package com.pms.personnel.config;

import com.pms.common.BizException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * OA 应用凭证拦截器：在参数绑定之前完成鉴权，保证未授权请求优先返回 401。
 */
@Component
public class OaAuthInterceptor implements HandlerInterceptor {

    private final OaAuthProperties properties;

    public OaAuthInterceptor(OaAuthProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String appKey = request.getHeader("X-App-Key");
        String appSecret = request.getHeader("X-App-Secret");
        if (!constantTimeEquals(properties.getAppKey(), appKey)
                || !constantTimeEquals(properties.getAppSecret(), appSecret)) {
            throw BizException.unauthorized("40101", "应用凭证校验失败");
        }
        return true;
    }

    /** 恒时比较，避免时序侧信道。 */
    private boolean constantTimeEquals(String expected, String actual) {
        if (expected == null || actual == null) {
            return false;
        }
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8));
    }
}
