package com.pms.personnel.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDateTime;

/**
 * 人员（成员）。多租户：以 (tenant_id, employee_id) 唯一标识一名人员。
 */
@Entity
@Table(name = "personnel",
        uniqueConstraints = @UniqueConstraint(name = "uk_personnel_tenant_employee",
                columnNames = {"tenant_id", "employee_id"}))
public class Personnel {

    /** 状态：在职。 */
    public static final String STATUS_ACTIVE = "ACTIVE";

    /** 状态：离职。 */
    public static final String STATUS_DEPARTED = "DEPARTED";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 租户标识（假设：与 employeeId 共同唯一定位人员）。 */
    @Column(name = "tenant_id", nullable = false, length = 64)
    private String tenantId;

    /** 人员唯一标识（假设：采用 OA 工号作为跨系统稳定主键）。 */
    @Column(name = "employee_id", nullable = false, length = 64)
    private String employeeId;

    @Column(name = "name", length = 128)
    private String name;

    /** 人员状态：ACTIVE / DEPARTED。 */
    @Column(name = "status", nullable = false, length = 32)
    private String status = STATUS_ACTIVE;

    /** 离职生效时间。 */
    @Column(name = "departure_time")
    private LocalDateTime departureTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
