package com.pms.personnel.service;

import com.pms.common.BizException;
import com.pms.common.IdempotentConflictException;
import com.pms.common.Result;
import com.pms.personnel.dto.DepartureRequest;
import com.pms.personnel.dto.DepartureResult;
import com.pms.personnel.entity.DepartureEvent;
import com.pms.personnel.entity.Personnel;
import com.pms.personnel.repository.DepartureEventRepository;
import com.pms.personnel.repository.PersonnelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * 人员服务：接收 OA 离职通知，将人员状态更新为「离职」。
 */
@Service
public class PersonnelService {

    private static final Logger log = LoggerFactory.getLogger(PersonnelService.class);

    /** 离职生效时间格式（假设：约定为 yyyy-MM-dd HH:mm:ss）。 */
    private static final DateTimeFormatter DEPARTURE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final PersonnelRepository personnelRepository;

    private final DepartureEventRepository eventRepository;

    public PersonnelService(PersonnelRepository personnelRepository,
                            DepartureEventRepository eventRepository) {
        this.personnelRepository = personnelRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * 处理离职通知。
     * 幂等：以 eventId 为幂等键，event_id 唯一索引兜底；状态更新与幂等记录在同一本地事务内完成。
     */
    @Transactional
    public DepartureResult departure(DepartureRequest request) {
        DepartureEvent processed = eventRepository.findByEventId(request.getEventId()).orElse(null);
        if (processed != null) {
            validateBinding(processed, request);
            log.info("重复离职事件，幂等返回。eventId={}, tenantId={}, employeeId={}",
                    request.getEventId(), request.getTenantId(), request.getEmployeeId());
            return new DepartureResult(request.getEmployeeId(), Personnel.STATUS_DEPARTED);
        }

        Personnel personnel = personnelRepository
                .findByTenantIdAndEmployeeId(request.getTenantId(), request.getEmployeeId())
                .orElseThrow(() -> BizException.notFound("40401", "人员不存在"));

        // 先落幂等记录，唯一索引作为并发兜底：冲突说明另一请求已处理，识别为幂等成功。
        DepartureEvent event = new DepartureEvent();
        event.setEventId(request.getEventId());
        event.setTenantId(request.getTenantId());
        event.setEmployeeId(request.getEmployeeId());
        event.setReason(request.getReason());
        event.setOperator(request.getOperator());
        event.setCreatedAt(LocalDateTime.now());
        try {
            eventRepository.saveAndFlush(event);
        } catch (DataIntegrityViolationException e) {
            log.info("并发重复离职事件，幂等返回。eventId={}", request.getEventId());
            throw new IdempotentConflictException(
                    Result.ok(new DepartureResult(request.getEmployeeId(), Personnel.STATUS_DEPARTED)));
        }

        personnel.setStatus(Personnel.STATUS_DEPARTED);
        personnel.setDepartureTime(parseDepartureTime(request.getDepartureTime()));
        personnel.setUpdatedAt(LocalDateTime.now());
        personnelRepository.save(personnel);

        log.info("离职通知处理完成。eventId={}, tenantId={}, employeeId={}",
                request.getEventId(), request.getTenantId(), request.getEmployeeId());
        return new DepartureResult(request.getEmployeeId(), Personnel.STATUS_DEPARTED);
    }

    /** 校验幂等命中时 eventId 与 (tenantId, employeeId) 的绑定关系。 */
    private void validateBinding(DepartureEvent event, DepartureRequest request) {
        boolean tenantOk = Objects.equals(event.getTenantId(), request.getTenantId());
        boolean employeeOk = Objects.equals(event.getEmployeeId(), request.getEmployeeId());
        if (!tenantOk || !employeeOk) {
            throw BizException.badRequest("40002", "事件与人员不匹配");
        }
    }

    private LocalDateTime parseDepartureTime(String value) {
        try {
            return LocalDateTime.parse(value, DEPARTURE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw BizException.badRequest("40001", "departureTime 格式非法，应为 yyyy-MM-dd HH:mm:ss");
        }
    }
}
