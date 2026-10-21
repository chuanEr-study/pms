package com.pms.personnel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pms.personnel.entity.Personnel;
import com.pms.personnel.repository.DepartureEventRepository;
import com.pms.personnel.repository.PersonnelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PersonnelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonnelRepository personnelRepository;

    @Autowired
    private DepartureEventRepository eventRepository;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
        personnelRepository.deleteAll();

        Personnel p = new Personnel();
        p.setTenantId("T001");
        p.setEmployeeId("E10086");
        p.setName("张三");
        p.setStatus(Personnel.STATUS_ACTIVE);
        p.setCreatedAt(LocalDateTime.now());
        p.setUpdatedAt(LocalDateTime.now());
        personnelRepository.save(p);
    }

    private String body(String eventId, String employeeId) throws Exception {
        Map<String, Object> m = new HashMap<>();
        m.put("eventId", eventId);
        m.put("employeeId", employeeId);
        m.put("tenantId", "T001");
        m.put("departureTime", "2026-10-21 18:00:00");
        m.put("reason", "个人原因");
        m.put("operator", "admin");
        return objectMapper.writeValueAsString(m);
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder auth(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder builder) {
        return builder.header("X-App-Key", "test-app-key").header("X-App-Secret", "test-app-secret");
    }

    @Test
    void departureSuccess() throws Exception {
        mockMvc.perform(auth(post("/api/personnel/departure"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("EVT-1", "E10086")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.employeeId").value("E10086"))
                .andExpect(jsonPath("$.data.status").value("DEPARTED"));

        Personnel p = personnelRepository.findByTenantIdAndEmployeeId("T001", "E10086").orElse(null);
        org.junit.jupiter.api.Assertions.assertNotNull(p);
        org.junit.jupiter.api.Assertions.assertEquals(Personnel.STATUS_DEPARTED, p.getStatus());
        org.junit.jupiter.api.Assertions.assertNotNull(p.getDepartureTime());
    }

    @Test
    void departureIdempotent() throws Exception {
        mockMvc.perform(auth(post("/api/personnel/departure"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("EVT-1", "E10086")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));

        mockMvc.perform(auth(post("/api/personnel/departure"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("EVT-1", "E10086")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.status").value("DEPARTED"));

        org.junit.jupiter.api.Assertions.assertEquals(1, eventRepository.count());
    }

    @Test
    void departureEventBindingMismatch() throws Exception {
        mockMvc.perform(auth(post("/api/personnel/departure"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("EVT-1", "E10086")))
                .andExpect(status().isOk());

        mockMvc.perform(auth(post("/api/personnel/departure"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("EVT-1", "E99999")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("40002"));
    }

    @Test
    void departureMissingField() throws Exception {
        Map<String, Object> m = new HashMap<>();
        m.put("employeeId", "E10086");
        m.put("tenantId", "T001");
        m.put("departureTime", "2026-10-21 18:00:00");

        mockMvc.perform(auth(post("/api/personnel/departure"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(m)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("40001"));
    }

    @Test
    void departurePersonNotFound() throws Exception {
        mockMvc.perform(auth(post("/api/personnel/departure"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("EVT-404", "E99999")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("40401"));
    }

    @Test
    void departureAuthFailed() throws Exception {
        mockMvc.perform(post("/api/personnel/departure")
                        .header("X-App-Key", "wrong")
                        .header("X-App-Secret", "wrong")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("EVT-1", "E10086")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("40101"));
    }

    @Test
    void departureMalformedJson() throws Exception {
        mockMvc.perform(auth(post("/api/personnel/departure"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("40001"));
    }
}
