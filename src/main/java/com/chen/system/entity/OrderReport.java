package com.chen.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_report")
public class OrderReport {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private String customerName;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}
