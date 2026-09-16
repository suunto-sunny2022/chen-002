package com.chen.system.controller;

import com.chen.common.ApiResponse;
import com.chen.system.entity.OrderReport;
import com.chen.system.service.OrderReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports/orders")
@RequiredArgsConstructor
public class OrderReportController {

    private final OrderReportService orderReportService;

    @GetMapping
    public ApiResponse<List<OrderReport>> list() {
        return ApiResponse.ok(orderReportService.list());
    }
}
