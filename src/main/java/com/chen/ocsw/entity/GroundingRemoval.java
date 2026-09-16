package com.chen.ocsw.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("t_ocsw_grounding")
public class GroundingRemoval implements OcswRecord {
    @com.baomidou.mybatisplus.annotation.TableId(type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Long id;
    private String businessNo;
    private Long windowId;
    private String status;
    @com.baomidou.mybatisplus.annotation.TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private String operator;
    private String remarks;
    @com.baomidou.mybatisplus.annotation.TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private java.time.LocalDateTime createdAt;
    @com.baomidou.mybatisplus.annotation.TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE)
    private java.time.LocalDateTime updatedAt;
    private String pointCode;
    private Integer sequenceNo;
    private Integer installed;
    private java.time.LocalDateTime installedAt;
    private Integer removed;
    private java.time.LocalDateTime removedAt;
    private String firstReviewer;
    private String secondReviewer;
}
