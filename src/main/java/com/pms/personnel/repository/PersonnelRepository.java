package com.pms.personnel.repository;

import com.pms.personnel.entity.Personnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 人员仓储。
 */
@Repository
public interface PersonnelRepository extends JpaRepository<Personnel, Long> {

    /** 按 (tenantId, employeeId) 唯一定位人员。 */
    Optional<Personnel> findByTenantIdAndEmployeeId(String tenantId, String employeeId);
}
