-- =============================================================
-- 生产环境 MySQL 建表/索引脚本
-- 与实体 Personnel / DepartureEvent 的 @UniqueConstraint 保持一致。
-- H2 开发/测试环境由 JPA ddl-auto=update 自动建表，无需执行本脚本。
-- =============================================================

CREATE TABLE IF NOT EXISTS personnel (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    tenant_id      VARCHAR(64)  NOT NULL,
    employee_id    VARCHAR(64)  NOT NULL,
    name           VARCHAR(128) NULL,
    status         VARCHAR(32)  NOT NULL,
    departure_time DATETIME     NULL,
    created_at     DATETIME     NULL,
    updated_at     DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_personnel_tenant_employee (tenant_id, employee_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS departure_event (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    event_id    VARCHAR(128) NOT NULL,
    tenant_id   VARCHAR(64)  NOT NULL,
    employee_id VARCHAR(64)  NOT NULL,
    reason      VARCHAR(500) NULL,
    operator    VARCHAR(128) NULL,
    created_at  DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_departure_event_event_id (event_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
