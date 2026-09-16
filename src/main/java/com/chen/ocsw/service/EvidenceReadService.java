package com.chen.ocsw.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chen.framework.datasource.DataSource;
import com.chen.framework.datasource.DataSourceType;
import com.chen.ocsw.entity.EnergizationReview;
import com.chen.ocsw.mapper.EnergizationReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EvidenceReadService {
    private final EnergizationReviewMapper energizationReviewMapper;

    @DataSource(DataSourceType.SLAVE)
    @Transactional(readOnly = true)
    public long countApproved(String businessNo) {
        return energizationReviewMapper.selectCount(Wrappers.<EnergizationReview>lambdaQuery()
            .eq(EnergizationReview::getBusinessNo, businessNo)
            .eq(EnergizationReview::getConclusion, "PASS"));
    }
}
