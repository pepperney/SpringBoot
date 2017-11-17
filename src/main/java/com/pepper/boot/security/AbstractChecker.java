package com.pepper.boot.security;


import javax.servlet.http.HttpServletRequest;

import com.pepper.boot.config.SecurityProperties;


public abstract class AbstractChecker implements Checker {
	
    protected SecurityProperties securityProperties;

    public AbstractChecker(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    public String getxApplicationId(HttpServletRequest request){
        return getFromHeaderOrParameter(request, securityProperties.getxApplicationId());
    }

    public String getxAPIVersion(HttpServletRequest request){
        return getFromHeaderOrParameter(request, securityProperties.getxAPIVersion());
    }

    public String getxToken(HttpServletRequest request){
        return getFromHeaderOrParameter(request, securityProperties.getxToken());
    }

    public String getxClient(HttpServletRequest request){
        return getFromHeaderOrParameter(request, securityProperties.getxClient());
    }

    public String getAppKey(HttpServletRequest request) {
        return getFromHeaderOrParameter(request, securityProperties.getAppKey());
    }


    public String getCallId(HttpServletRequest request) {
        return getFromHeaderOrParameter(request, securityProperties.getCallId());
    }


    public String getSign(HttpServletRequest request) {
        return getFromHeaderOrParameter(request, securityProperties.getSign());
    }

    public String getTimestamp(HttpServletRequest request) {
        return getFromHeaderOrParameter(request, securityProperties.getTimestamp());
    }

    public String getToken(HttpServletRequest request) {
        return getFromHeaderOrParameter(request, securityProperties.getToken());
    }

    public String getDeviceInfo(HttpServletRequest request) {
        return getFromHeaderOrParameter(request, securityProperties.getDeviceInfo());
    }

    public long getTimestampMilliseconds(){
        return securityProperties.getTimestampMilliseconds();
    }

    protected String getFromHeaderOrParameter(HttpServletRequest request, String key) {
        if (request.getParameterMap().containsKey(key)) {
            return request.getParameter(key);
        } else {
            return request.getHeader(key);
        }
    }

    protected String getRequestPath(HttpServletRequest request) {
        String url = request.getServletPath();

        if (request.getPathInfo() != null) {
            url += request.getPathInfo();
        }

        return url;
    }
}
