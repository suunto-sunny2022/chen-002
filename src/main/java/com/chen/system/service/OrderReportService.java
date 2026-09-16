package com.chen.system.service;

import com.chen.framework.datasource.DataSource;
import com.chen.framework.datasource.DataSourceType;
import com.chen.system.entity.OrderReport;
import com.chen.system.mapper.OrderReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@DataSource(DataSourceType.SLAVE)
public class OrderReportService {

    private final OrderReportMapper orderReportMapper;

    @Transactional(readOnly = true)
    public List<OrderReport> list() {
        return orderReportMapper.selectList(null);
    }
}
