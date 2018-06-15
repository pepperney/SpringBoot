package com.pepper.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "api.security")
@PropertySource("classpath:config/config.properties")
@Configuration
public class ApiConfig {

    @Value("${api.security.timestamp.need}")
    private boolean needValidateTimestamp;

    @Value("${api.security.timestamp.milliseconds}")
    private long timestampMilliseconds;

    private List<String> securityChains = new ArrayList<>();

    public boolean isNeedValidateTimestamp() {
        return needValidateTimestamp;
    }

    public void setNeedValidateTimestamp(boolean needValidateTimestamp) {
        this.needValidateTimestamp = needValidateTimestamp;
    }

    public long getTimestampMilliseconds() {
        return timestampMilliseconds;
    }

    public void setTimestampMilliseconds(long timestampMilliseconds) {
        this.timestampMilliseconds = timestampMilliseconds;
    }

    public List<String> getSecurityChains() {
        return securityChains;
    }

    public void setSecurityChains(List<String> securityChains) {
        this.securityChains = securityChains;
    }
}
