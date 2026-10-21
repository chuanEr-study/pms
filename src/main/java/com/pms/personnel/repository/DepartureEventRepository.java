package com.pms.personnel.repository;

import com.pms.personnel.entity.DepartureEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 离职通知处理记录仓储。
 */
@Repository
public interface DepartureEventRepository extends JpaRepository<DepartureEvent, Long> {

    /** 按事件 ID 查询处理记录（幂等判定）。 */
    Optional<DepartureEvent> findByEventId(String eventId);
}
