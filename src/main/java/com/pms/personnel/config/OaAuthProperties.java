package com.pms.personnel.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

/**
 * OA 应用凭证配置。
 *
 * <p>假设：OA -> PMS 采用共享静态应用凭证，通过 HTTP 头 X-App-Key / X-App-Secret 传递；
 * 待确认项若确定其它鉴权方式（IP 白名单 / 网关鉴权），再替换此实现。
 *
 * <p>凭证必填且启动时 fail-fast（为空即拒绝启动），由环境变量/配置中心注入。
 */
@Component
@Validated
@ConfigurationProperties(prefix = "pms.oa")
public class OaAuthProperties {

    @NotBlank
    private String appKey;

    @NotBlank
    private String appSecret;

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
}
