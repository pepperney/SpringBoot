package com.pepper.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class HttpUtil {

	private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

	public static final String DEFAULT_CHARSET = "UTF-8";

	private static RestTemplate restTemplate;

	@Autowired
	private RestTemplate myRestTemplate;

	@PostConstruct
	public void init() {
		restTemplate = myRestTemplate;
	}

	@Bean
	public RestTemplate myRestTemplate() {
		HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		httpRequestFactory.setConnectionRequestTimeout(3000);//请求超时时间3s
		httpRequestFactory.setConnectTimeout(3000);//连接超时时间3s
		httpRequestFactory.setReadTimeout(3000);//读取超时时间3s
		return new RestTemplate(httpRequestFactory);
	}

	/**
	 * 发送HTTP_GET请求
	 * @param reqURL
	 * @param params
	 * @param heads
	 * @return
	 */
	public static String get(String reqURL, Map<String, String> params, Map<String, String> heads) {
		HttpHeaders requestHeaders = new HttpHeaders();
		if (null != heads) {
			for (Map.Entry<String, String> entry : heads.entrySet()) {
				requestHeaders.add(entry.getKey(), entry.getValue());
			}
		}
		HttpEntity<Object> requestEntity = new HttpEntity<>(null, requestHeaders);
		String queryString = "";
		if (null != params) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				try {
					queryString += "&" + entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), DEFAULT_CHARSET);
				} catch (UnsupportedEncodingException e) {
					logger.error("{}", e.getMessage(), e);
				}
			}
		}
		reqURL = reqURL + (StringUtils.isNotBlank(queryString) ? "?" + queryString.substring(1) : "");
		ResponseEntity<String> response = restTemplate.exchange(reqURL, HttpMethod.GET, requestEntity, String.class);
		return response.getBody();
	}

	/**
	 * 发送HTTP_POST请求
	 */
	public static String post(String reqURL, String reqData) {
		return post(reqURL, reqData, null);
	}

	public static String post(String reqURL, String reqData, String contentType) {
		HttpHeaders requestHeaders = new HttpHeaders();
		if (!StringUtils.isBlank(contentType)) {
			requestHeaders.set(HttpHeaders.CONTENT_TYPE, contentType);
		}
		HttpEntity<String> entity = new HttpEntity<String>(reqData, requestHeaders);
		ResponseEntity<String> response = restTemplate.exchange(reqURL, HttpMethod.POST, entity, String.class);
		return response.getBody();
	}

	public static String post(String reqURL, Map<String, String> params) {
		return post(reqURL, params, null);
	}

	public static String post(String reqURL, Map<String, String> params, Map<String, String> heads) {
		HttpHeaders requestHeaders = new HttpHeaders();
		if (null != heads) {
			for (Map.Entry<String, String> entry : heads.entrySet()) {
				requestHeaders.add(entry.getKey(), entry.getValue());
			}
		}
		LinkedMultiValueMap<String, String> linkedMultiValueMap = new LinkedMultiValueMap<String, String>();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			linkedMultiValueMap.add(entry.getKey(), entry.getValue());
		}
		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(linkedMultiValueMap, requestHeaders);
		ResponseEntity<String> response = restTemplate.exchange(reqURL, HttpMethod.POST, entity, String.class);
		return response.getBody();
	}

}
