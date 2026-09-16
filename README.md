# OCSW

铁路接触网天窗检修与送电闭环基线，采用 Java 17、Spring Boot 2.7.18、
MyBatis-Plus、Apache Shiro、Druid 多数据源和 Quartz。

## 数据库

使用 `doc/schema/ocsw.sql` 初始化 `chen_002`。开发配置中的数据库与 Druid
监控凭据均为 `CHANGE_ME`，运行前应通过环境变量提供：

- `SPRING_DATASOURCE_DRUID_MASTER_URL`、`SPRING_DATASOURCE_DRUID_MASTER_USERNAME`、
  `SPRING_DATASOURCE_DRUID_MASTER_PASSWORD`
- `SPRING_DATASOURCE_DRUID_SLAVE_URL`、`SPRING_DATASOURCE_DRUID_SLAVE_USERNAME`、
  `SPRING_DATASOURCE_DRUID_SLAVE_PASSWORD`
- `DRUID_MONITOR_USERNAME`、`DRUID_MONITOR_PASSWORD`

主从连接在本地基线中可指向同一数据库；证据读取仍通过
`@DataSource(DataSourceType.SLAVE)` 独立路由。

## 编译与运行

```bash
mvn -DskipTests compile
mvn spring-boot:run
```

登录后以 `POST` 调用以下接口，写操作均要求 `ocsw:write` 权限：

- `/api/ocsw/window-admissions`
- `/api/ocsw/outage-confirmations`
- `/api/ocsw/grounding-interlocks`
- `/api/ocsw/crew-entries`
- `/api/ocsw/defect-closures`
- `/api/ocsw/window-extensions`
- `/api/ocsw/evacuation-checks`
- `/api/ocsw/grounding-removals`
- `/api/ocsw/energization-reviews`
- `/api/ocsw/lifecycle-reconciliations`

Quartz 每分钟扫描已过有效期的停电命令，不调用外部服务。
