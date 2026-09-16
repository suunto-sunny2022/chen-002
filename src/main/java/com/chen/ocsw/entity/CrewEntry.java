package com.chen.ocsw.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("t_ocsw_crew_entry")
public class CrewEntry implements OcswRecord {
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
    private String crewCode;
    private Integer qualified;
    private Integer plannedCount;
    private Integer actualCount;
    private java.time.LocalDateTime enteredAt;
}
