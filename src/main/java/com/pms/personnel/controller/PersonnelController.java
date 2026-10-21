package com.pms.personnel.controller;

import com.pms.common.Result;
import com.pms.personnel.dto.DepartureRequest;
import com.pms.personnel.dto.DepartureResult;
import com.pms.personnel.service.PersonnelService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 人员接口：接收 OA 离职通知。鉴权由 OaAuthInterceptor 前置完成。
 */
@RestController
@RequestMapping("/api/personnel")
public class PersonnelController {

    private final PersonnelService service;

    public PersonnelController(PersonnelService service) {
        this.service = service;
    }

    /** 接收 OA 离职通知，将人员状态置为「离职」。 */
    @PostMapping("/departure")
    public Result<DepartureResult> departure(@Valid @RequestBody DepartureRequest request) {
        return Result.ok(service.departure(request));
    }
}
