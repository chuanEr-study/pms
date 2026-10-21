package com.pms.personnel.dto;

/**
 * 离职通知处理结果。
 */
public class DepartureResult {

    private String employeeId;

    private String status;

    public DepartureResult() {
    }

    public DepartureResult(String employeeId, String status) {
        this.employeeId = employeeId;
        this.status = status;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
