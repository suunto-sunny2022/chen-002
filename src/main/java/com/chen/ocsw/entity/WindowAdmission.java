package com.chen.ocsw.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@TableName("t_ocsw_window_plan")
public class WindowAdmission implements OcswRecord {
    @com.baomidou.mybatisplus.annotation.TableId(type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Long id;
    private String businessNo;
    private Long windowId;
    private String status;
    @com.baomidou.mybatisplus.annotation.TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private String operator;
    private String remarks;
    @com.baomidou.mybatisplus.annotation.TableField(
        fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT, select = false)
    private LocalDateTime createdAt;
    @com.baomidou.mybatisplus.annotation.TableField(
        fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE, select = false)
    private LocalDateTime updatedAt;
    private String windowNo;
    private String sectionCode;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxCrew;
    private String requestedBy;
}
