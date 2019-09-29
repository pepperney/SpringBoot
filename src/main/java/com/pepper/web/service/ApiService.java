package com.pepper.web.service;

public interface ApiService {

    void doRetry();

    void logValue(Object param);

    String cacheValue(String args);

    Integer addCount(String requestId);
}
