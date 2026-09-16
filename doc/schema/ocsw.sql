CREATE DATABASE IF NOT EXISTS chen_002
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;
USE chen_002;

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    salt VARCHAR(64) NOT NULL,
    role VARCHAR(32) NOT NULL DEFAULT 'user',
    status INT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
INSERT INTO sys_user (username, password, salt, role, status)
VALUES ('admin', 'd4bc92e15d1f1e96a157fec088faec7beeaa5d350cdcc5605ede1ef2487a81b3',
        'chen', 'admin', 1)
ON DUPLICATE KEY UPDATE role = VALUES(role), status = VALUES(status);

CREATE TABLE IF NOT EXISTS order_report (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(64) NOT NULL UNIQUE,
    customer_name VARCHAR(128) NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS t_ocsw_section_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    business_no VARCHAR(64),
    window_id BIGINT,
    section_code VARCHAR(64) NOT NULL,
    max_parallel_windows INT NOT NULL,
    min_gap_minutes INT NOT NULL,
    enabled INT NOT NULL,
    status VARCHAR(32) NOT NULL,
    operator VARCHAR(64) NOT NULL,
    remarks VARCHAR(500),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_section_rule_code (section_code, enabled)
);

CREATE TABLE IF NOT EXISTS t_ocsw_window_plan (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    business_no VARCHAR(64),
    window_id BIGINT,
    window_no VARCHAR(64) NOT NULL,
    section_code VARCHAR(64) NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    max_crew INT NOT NULL,
    status VARCHAR(32) NOT NULL,
    requested_by VARCHAR(64),
    operator VARCHAR(64) NOT NULL,
    remarks VARCHAR(500),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_window_section_time (section_code, start_time, end_time),
    INDEX idx_window_business (business_no)
);

CREATE TABLE IF NOT EXISTS t_ocsw_outage_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    business_no VARCHAR(64),
    window_id BIGINT NOT NULL,
    order_no VARCHAR(64) NOT NULL,
    issued_at DATETIME,
    expires_at DATETIME,
    confirmed INT NOT NULL,
    confirmed_by VARCHAR(64),
    status VARCHAR(32) NOT NULL,
    operator VARCHAR(64) NOT NULL,
    remarks VARCHAR(500),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_outage_window (window_id),
    INDEX idx_outage_expiry (status, expires_at)
);

CREATE TABLE IF NOT EXISTS t_ocsw_grounding (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    business_no VARCHAR(64),
    window_id BIGINT NOT NULL,
    point_code VARCHAR(64) NOT NULL,
    sequence_no INT NOT NULL,
    installed INT NOT NULL,
    installed_at DATETIME,
    removed INT NOT NULL,
    removed_at DATETIME,
    first_reviewer VARCHAR(64),
    second_reviewer VARCHAR(64),
    status VARCHAR(32) NOT NULL,
    operator VARCHAR(64) NOT NULL,
    remarks VARCHAR(500),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_grounding_window_sequence (window_id, sequence_no)
);

CREATE TABLE IF NOT EXISTS t_ocsw_crew_entry (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    business_no VARCHAR(64),
    window_id BIGINT NOT NULL,
    crew_code VARCHAR(64) NOT NULL,
    qualified INT NOT NULL,
    planned_count INT NOT NULL,
    actual_count INT NOT NULL,
    entered_at DATETIME,
    status VARCHAR(32) NOT NULL,
    operator VARCHAR(64) NOT NULL,
    remarks VARCHAR(500),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_crew_window (window_id)
);

CREATE TABLE IF NOT EXISTS t_ocsw_defect (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    business_no VARCHAR(64),
    window_id BIGINT NOT NULL,
    defect_code VARCHAR(64) NOT NULL,
    severity VARCHAR(32),
    found_at DATETIME,
    retest_result VARCHAR(32),
    status VARCHAR(32) NOT NULL,
    operator VARCHAR(64) NOT NULL,
    remarks VARCHAR(500),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_defect_window (window_id),
    INDEX idx_defect_code (defect_code)
);

CREATE TABLE IF NOT EXISTS t_ocsw_extension (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    business_no VARCHAR(64),
    window_id BIGINT NOT NULL,
    requested_end DATETIME NOT NULL,
    approved_end DATETIME,
    reason VARCHAR(500),
    status VARCHAR(32) NOT NULL,
    operator VARCHAR(64) NOT NULL,
    remarks VARCHAR(500),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_extension_window (window_id)
);

CREATE TABLE IF NOT EXISTS t_ocsw_evacuation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    business_no VARCHAR(64),
    window_id BIGINT NOT NULL,
    people_expected INT NOT NULL,
    people_out INT NOT NULL,
    tools_expected INT NOT NULL,
    tools_out INT NOT NULL,
    checked_at DATETIME,
    status VARCHAR(32) NOT NULL,
    operator VARCHAR(64) NOT NULL,
    remarks VARCHAR(500),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_evacuation_window (window_id)
);

CREATE TABLE IF NOT EXISTS t_ocsw_energization_review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    business_no VARCHAR(64),
    window_id BIGINT NOT NULL,
    conclusion VARCHAR(32) NOT NULL,
    reviewer VARCHAR(64),
    reviewed_at DATETIME,
    certificate_no VARCHAR(64),
    status VARCHAR(32) NOT NULL,
    operator VARCHAR(64) NOT NULL,
    remarks VARCHAR(500),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_energization_window (window_id),
    INDEX idx_energization_certificate (certificate_no)
);

CREATE TABLE IF NOT EXISTS t_ocsw_lifecycle_event (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    business_no VARCHAR(64),
    window_id BIGINT NOT NULL,
    event_type VARCHAR(32) NOT NULL,
    event_time DATETIME NOT NULL,
    valid INT NOT NULL,
    status VARCHAR(32) NOT NULL,
    operator VARCHAR(64) NOT NULL,
    remarks VARCHAR(500),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_lifecycle_window_time (window_id, event_time)
);

INSERT INTO t_ocsw_section_rule
    (business_no, window_id, section_code, max_parallel_windows, min_gap_minutes,
     enabled, status, operator, remarks, created_at, updated_at)
VALUES
    ('RULE-DEFAULT', NULL, 'DEFAULT', 2, 15, 1, 'ENABLED', 'seed',
     '默认区段规则', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('RULE-S001', NULL, 'S001', 2, 10, 1, 'ENABLED', 'seed',
     '验收区段基础规则', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
