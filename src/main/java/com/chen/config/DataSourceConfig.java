package com.chen.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.chen.framework.datasource.DataSourceType;
import com.chen.framework.datasource.DynamicRoutingDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.druid.master")
    public DataSource primaryDataSource() {
        return new DruidDataSource();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.druid.slave")
    public DataSource slaveDataSource() {
        return new DruidDataSource();
    }

    @Bean
    @Primary
    public DataSource dataSource(DataSource primaryDataSource, DataSource slaveDataSource) {
        DynamicRoutingDataSource routingDataSource = new DynamicRoutingDataSource();
        Map<Object, Object> targets = new HashMap<>();
        targets.put(DataSourceType.PRIMARY, primaryDataSource);
        targets.put(DataSourceType.SLAVE, slaveDataSource);
        routingDataSource.setTargetDataSources(targets);
        routingDataSource.setDefaultTargetDataSource(primaryDataSource);
        return routingDataSource;
    }
}
