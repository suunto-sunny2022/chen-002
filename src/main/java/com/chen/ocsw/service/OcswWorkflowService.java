package com.chen.ocsw.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chen.ocsw.dto.OcswCommand;
import com.chen.ocsw.dto.OcswResult;
import com.chen.ocsw.entity.*;
import com.chen.ocsw.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OcswWorkflowService {
    private final WindowAdmissionMapper admissionMapper;
    private final OutageConfirmationMapper outageMapper;
    private final GroundingInterlockMapper groundingMapper;
    private final CrewEntryMapper crewMapper;
    private final DefectClosureMapper defectMapper;
    private final WindowExtensionMapper extensionMapper;
    private final EvacuationCheckMapper evacuationMapper;
    private final GroundingRemovalMapper removalMapper;
    private final EnergizationReviewMapper energizationMapper;
    private final LifecycleReconciliationMapper reconciliationMapper;
    private final SectionRuleMapper sectionRuleMapper;
    private final EvidenceReadService evidenceReadService;

    @Transactional
    public OcswResult admitWindow(OcswCommand command) {
        String sectionCode = text(command.getSectionCode(), command.getLineCode());
        LocalDateTime startTime = time(command.getStartTime(), command.getStartAt());
        LocalDateTime endTime = time(command.getEndTime(), command.getEndAt());
        long conflicts = admissionMapper.selectCount(Wrappers.<WindowAdmission>lambdaQuery()
            .eq(WindowAdmission::getSectionCode, sectionCode)
            .lt(WindowAdmission::getStartTime, endTime)
            .gt(WindowAdmission::getEndTime, startTime)
            .ne(WindowAdmission::getStatus, "REJECTED"));
        SectionRule rule = sectionRuleMapper.selectOne(Wrappers.<SectionRule>lambdaQuery()
            .eq(SectionRule::getSectionCode, sectionCode)
            .eq(SectionRule::getEnabled, 1)
            .last("LIMIT 1"));
        Integer capacity = integer(command.getMaxCrew(), command.getQuantity());
        int maxParallel = rule == null ? 2 : rule.getMaxParallelWindows();
        boolean accepted = conflicts < maxParallel && capacity <= 4;
        WindowAdmission entity = base(new WindowAdmission(), command,
            accepted ? "ADMITTED" : "REJECTED");
        entity.setWindowNo(text(command.getWindowNo(), command.getReferenceNo()));
        entity.setSectionCode(sectionCode);
        entity.setStartTime(startTime);
        entity.setEndTime(endTime);
        entity.setMaxCrew(capacity);
        entity.setRequestedBy(command.getRequestedBy());
        admissionMapper.insert(entity);
        return result(entity, accepted ? "天窗准入" : "天窗未准入");
    }

    @Transactional
    public OcswResult confirmOutage(OcswCommand command) {
        WindowAdmission admission = admissionMapper.selectById(command.getRelatedId());
        LocalDateTime expiresAt = time(command.getExpiresAt(), command.getEndAt());
        boolean valid = admission != null && "ADMITTED".equals(admission.getStatus())
            && expiresAt != null && expiresAt.isAfter(LocalDateTime.now());
        OutageConfirmation entity = base(new OutageConfirmation(), command,
            valid && Boolean.TRUE.equals(command.getConfirmed()) ? "CONFIRMED" : "INVALID");
        entity.setWindowId(command.getWindowId());
        entity.setOrderNo(text(command.getOrderNo(), command.getReferenceNo()));
        entity.setIssuedAt(time(command.getIssuedAt(), command.getStartAt()));
        entity.setExpiresAt(expiresAt);
        entity.setConfirmed(Boolean.TRUE.equals(command.getConfirmed()) ? 1 : 0);
        entity.setConfirmedBy(command.getConfirmedBy());
        outageMapper.insert(entity);
        return result(entity, valid ? "停电命令已记录" : "停电命令无效");
    }

    @Transactional
    public OcswResult interlockGrounding(OcswCommand command) {
        OutageConfirmation outage = outageMapper.selectById(command.getRelatedId());
        int groundingCount = integer(command.getSequenceNo(), command.getQuantity());
        boolean released = outage != null && "CONFIRMED".equals(outage.getStatus())
            && groundingCount > 0 && Boolean.TRUE.equals(command.getConfirmed());
        GroundingInterlock entity = base(new GroundingInterlock(), command,
            released ? "GROUNDING_READY" : "LOCKED");
        entity.setPointCode(text(command.getPointCode(), command.getReferenceNo()));
        entity.setSequenceNo(groundingCount);
        entity.setInstalled(released ? 1 : 0);
        entity.setInstalledAt(released ? time(command.getInstalledAt(), LocalDateTime.now()) : null);
        entity.setRemoved(0);
        entity.setFirstReviewer(text(command.getFirstReviewer(), command.getReviewer()));
        groundingMapper.insert(entity);
        return result(entity, released ? "接地防护已解锁" : "接地防护闭锁");
    }

    @Transactional
    public OcswResult enterCrew(OcswCommand command) {
        GroundingInterlock grounding = groundingMapper.selectById(command.getRelatedId());
        int declared = integer(command.getActualCount(), command.getQuantity());
        int qualified = Boolean.TRUE.equals(command.getQualified()) ? declared
            : value(command.getSecondaryQuantity());
        int required = integer(command.getPlannedCount(), command.getExpectedCount());
        boolean entered = grounding != null && grounding.getInstalled() == 1
            && declared >= required && qualified >= required;
        CrewEntry entity = base(new CrewEntry(), command, entered ? "ON_SITE" : "DENIED");
        entity.setCrewCode(text(command.getCrewCode(), command.getReferenceNo()));
        entity.setQualified(qualified >= required ? 1 : 0);
        entity.setPlannedCount(required);
        entity.setActualCount(declared);
        entity.setEnteredAt(entered ? time(command.getEnteredAt(), LocalDateTime.now()) : null);
        crewMapper.insert(entity);
        return result(entity, entered ? "作业组允许入场" : "作业组核对未通过");
    }

    @Transactional
    public OcswResult closeDefect(OcswCommand command) {
        CrewEntry crew = crewMapper.selectById(command.getRelatedId());
        String retestResult = text(command.getRetestResult(),
            Boolean.TRUE.equals(command.getConfirmed()) ? "PASS" : "PENDING");
        boolean passed = crew != null && "ON_SITE".equals(crew.getStatus())
            && "PASS".equalsIgnoreCase(retestResult);
        DefectClosure entity = base(new DefectClosure(), command,
            passed ? "CLOSED" : "RETEST_REQUIRED");
        entity.setDefectCode(text(command.getDefectCode(), command.getReferenceNo()));
        entity.setSeverity(text(command.getSeverity(), command.getResult()));
        entity.setFoundAt(time(command.getFoundAt(),
            command.getStartAt() == null ? LocalDateTime.now() : command.getStartAt()));
        entity.setRetestResult(retestResult);
        defectMapper.insert(entity);
        return result(entity, passed ? "缺陷复测闭环" : "缺陷等待复测");
    }

    @Transactional
    public OcswResult extendWindow(OcswCommand command) {
        WindowAdmission admission = admissionMapper.selectById(command.getRelatedId());
        LocalDateTime requestedEnd = time(command.getRequestedEnd(), command.getEndAt());
        long adjacent = admission == null ? 1 : admissionMapper.selectCount(
            Wrappers.<WindowAdmission>lambdaQuery()
                .eq(WindowAdmission::getSectionCode, admission.getSectionCode())
                .ge(WindowAdmission::getStartTime, requestedEnd)
                .ne(WindowAdmission::getId, admission.getId()));
        boolean approved = admission != null && adjacent == 0
            && requestedEnd != null && requestedEnd.isAfter(admission.getEndTime());
        WindowExtension entity = base(new WindowExtension(), command,
            approved ? "EXTENDED" : "REJECTED");
        entity.setRequestedEnd(requestedEnd);
        entity.setApprovedEnd(approved
            ? time(command.getApprovedEnd(), requestedEnd) : command.getApprovedEnd());
        entity.setReason(text(command.getReason(), command.getResult()));
        extensionMapper.insert(entity);
        return result(entity, approved ? "延点获批" : "延点冲突");
    }

    @Transactional
    public OcswResult checkEvacuation(OcswCommand command) {
        CrewEntry crew = crewMapper.selectById(command.getRelatedId());
        int expectedPersons = command.getPeopleExpected() != null ? command.getPeopleExpected()
            : crew == null ? value(command.getExpectedCount()) : crew.getActualCount();
        int evacuatedPersons = integer(command.getPeopleOut(), command.getActualCount());
        int expectedTools = integer(command.getToolsExpected(), command.getQuantity());
        int evacuatedTools = integer(command.getToolsOut(), command.getSecondaryQuantity());
        boolean cleared = crew != null && evacuatedPersons == expectedPersons
            && evacuatedTools == expectedTools;
        EvacuationCheck entity = base(new EvacuationCheck(), command,
            cleared ? "CLEARED" : "MISSING");
        entity.setPeopleExpected(expectedPersons);
        entity.setPeopleOut(evacuatedPersons);
        entity.setToolsExpected(expectedTools);
        entity.setToolsOut(evacuatedTools);
        entity.setCheckedAt(time(command.getCheckedAt(), LocalDateTime.now()));
        evacuationMapper.insert(entity);
        return result(entity, cleared ? "人员工具清点完成" : "撤离清点不一致");
    }

    @Transactional
    public OcswResult removeGrounding(OcswCommand command) {
        GroundingInterlock grounding = groundingMapper.selectById(command.getRelatedId());
        EvacuationCheck evacuation = evacuationMapper.selectById(command.getSecondaryId());
        int sequence = integer(command.getSequenceNo(), command.getQuantity());
        boolean removed = grounding != null && evacuation != null && "CLEARED".equals(evacuation.getStatus())
            && sequence <= grounding.getSequenceNo() && command.getReviewer() != null;
        GroundingRemoval entity = base(new GroundingRemoval(), command,
            removed ? "REMOVED" : "BLOCKED");
        entity.setPointCode(grounding == null
            ? text(command.getPointCode(), command.getReferenceNo()) : grounding.getPointCode());
        entity.setSequenceNo(sequence);
        entity.setInstalled(grounding == null ? 0 : grounding.getInstalled());
        entity.setInstalledAt(grounding == null ? null : grounding.getInstalledAt());
        entity.setRemoved(removed ? 1 : 0);
        entity.setRemovedAt(removed ? time(command.getRemovedAt(), LocalDateTime.now()) : null);
        entity.setFirstReviewer(grounding == null ? command.getFirstReviewer()
            : grounding.getFirstReviewer());
        entity.setSecondReviewer(text(command.getSecondReviewer(), command.getReviewer()));
        removalMapper.insert(entity);
        return result(entity, removed ? "接地拆除已复核" : "接地拆除被阻止");
    }

    @Transactional
    public OcswResult reviewEnergization(OcswCommand command) {
        GroundingRemoval removal = removalMapper.selectById(command.getRelatedId());
        OutageConfirmation outage = outageMapper.selectById(command.getSecondaryId());
        int evidenceCount = value(command.getQuantity());
        boolean approved = removal != null && removal.getRemoved() == 1 && outage != null
            && evidenceCount >= 2 && Boolean.TRUE.equals(command.getApproved());
        EnergizationReview entity = base(new EnergizationReview(), command,
            approved ? "ENERGIZED" : "REVIEW_PENDING");
        entity.setConclusion(text(command.getConclusion(), approved ? "PASS" : "REJECT"));
        entity.setReviewer(command.getReviewer());
        entity.setReviewedAt(time(command.getReviewedAt(), LocalDateTime.now()));
        entity.setCertificateNo(text(command.getCertificateNo(), command.getReferenceNo()));
        energizationMapper.insert(entity);
        return result(entity, approved ? "送电复核通过" : "送电复核未通过");
    }

    @Transactional
    public OcswResult reconcileLifecycle(OcswCommand command) {
        List<LifecycleReconciliation> histories = reconciliationMapper.selectList(
            Wrappers.<LifecycleReconciliation>lambdaQuery()
                .eq(LifecycleReconciliation::getBusinessNo, command.getBusinessNo())
                .orderByAsc(LifecycleReconciliation::getCreatedAt));
        String previous = histories.isEmpty() ? "APPLIED"
            : histories.get(histories.size() - 1).getEventType();
        long approvedEvidence = evidenceReadService.countApproved(command.getBusinessNo());
        String calculated = approvedEvidence > 0 ? "ENERGIZED"
            : value(command.getQuantity()) > 0 ? "WORKING" : previous;
        LifecycleReconciliation entity = base(new LifecycleReconciliation(), command,
            "RECONCILED");
        entity.setEventType(text(command.getEventType(), calculated));
        entity.setEventTime(time(command.getEventTime(), LocalDateTime.now()));
        entity.setValid(command.getValid() == null || command.getValid() ? 1 : 0);
        reconciliationMapper.insert(entity);
        return result(entity, "生命周期已按证据重算为" + calculated);
    }

    @Transactional
    public int expireOutageCommands(LocalDateTime now) {
        List<OutageConfirmation> commands = outageMapper.selectList(
            Wrappers.<OutageConfirmation>lambdaQuery()
                .eq(OutageConfirmation::getStatus, "CONFIRMED")
                .lt(OutageConfirmation::getExpiresAt, now));
        commands.forEach(command -> {
            command.setStatus("EXPIRED");
            outageMapper.updateById(command);
        });
        return commands.size();
    }

    private int value(Integer value) {
        return value == null ? 0 : value;
    }

    private int integer(Integer preferred, Integer fallback) {
        return preferred == null ? value(fallback) : preferred;
    }

    private String text(String preferred, String fallback) {
        return preferred == null ? fallback : preferred;
    }

    private LocalDateTime time(LocalDateTime preferred, LocalDateTime fallback) {
        return preferred == null ? fallback : preferred;
    }

    private <T extends OcswRecord> T base(T entity, OcswCommand command, String status) {
        entity.setBusinessNo(command.getBusinessNo());
        entity.setWindowId(command.getWindowId());
        entity.setStatus(status);
        entity.setRemarks(command.getRemarks());
        return entity;
    }

    private OcswResult result(OcswRecord entity, String message) {
        return new OcswResult(entity.getId(), entity.getBusinessNo(), entity.getStatus(), message);
    }
}
