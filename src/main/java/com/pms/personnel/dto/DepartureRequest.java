package com.pms.personnel.dto;

import javax.validation.constraints.NotBlank;

/**
 * 离职通知请求（OA -> PMS）。
 */
public class DepartureRequest {

    /** OA 侧离职事件唯一 ID（幂等键）。 */
    @NotBlank(message = "eventId 不能为空")
    private String eventId;

    /** 人员唯一标识（OA 工号）。 */
    @NotBlank(message = "employeeId 不能为空")
    private String employeeId;

    /** 租户标识。 */
    @NotBlank(message = "tenantId 不能为空")
    private String tenantId;

    /** 离职生效时间，格式 yyyy-MM-dd HH:mm:ss。 */
    @NotBlank(message = "departureTime 不能为空")
    private String departureTime;

    /** 离职原因。 */
    private String reason;

    /** OA 侧操作人。 */
    private String operator;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
