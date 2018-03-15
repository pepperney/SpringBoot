package com.pepper.config;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "api.security")
public class SecurityProperties {
	
    private boolean enabled;
    private String appKey = "appKey";
    private String callId = "callId";
    private String sign = "sign";
    private String timestamp = "timestamp";
    private String token = "token";
    private String deviceInfo = "deviceInfo";

    public final static String xClient = "X-Client";
    public final static String xToken = "X-Token";
    public final static String xAPIVersion = "X-API-Version";
    public final static String xApplicationId = "X-Application-Id";

    private long timestampMilliseconds = 10 * 60 * 1000;

    private List<String> securityChains = new ArrayList<String>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<String> getSecurityChains() {
        return securityChains;
    }

    public void setSecurityChains(List<String> securityChains) {
        this.securityChains = securityChains;
    }

    public long getTimestampMilliseconds() {
        return timestampMilliseconds;
    }

    public void setTimestampMilliseconds(long timestampMilliseconds) {
        this.timestampMilliseconds = timestampMilliseconds;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getxClient() {
        return xClient;
    }


    public String getxToken() {
        return xToken;
    }


    public String getxAPIVersion() {
        return xAPIVersion;
    }


    public String getxApplicationId() {
        return xApplicationId;
    }


    @Override
    public String toString() {
        return "SecurityProperties{" +
                "enabled=" + enabled +
                ", appKey='" + appKey + '\'' +
                ", callId='" + callId + '\'' +
                ", sign='" + sign + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", token='" + token + '\'' +
                ", deviceInfo='" + deviceInfo + '\'' +
                ", xClient='" + xClient + '\'' +
                ", xToken='" + xToken + '\'' +
                ", xAPIVersion='" + xAPIVersion + '\'' +
                ", xApplicationId='" + xApplicationId + '\'' +
                ", timestampMilliseconds=" + timestampMilliseconds +
                ", securityChains=" + securityChains +
                '}';
    }

    public String getAppKey(HttpServletRequest request){
    	if(request.getParameterMap().containsKey(this.appKey)){
             return request.getParameter(this.appKey);
    	}else{
             return request.getHeader(this.appKey);
    	}
    }
    public String getCallId(HttpServletRequest request){
    	if(request.getParameterMap().containsKey(this.callId)){
             return request.getParameter(this.callId);
    	}else{
             return request.getHeader(this.callId);
    	}
    }
}