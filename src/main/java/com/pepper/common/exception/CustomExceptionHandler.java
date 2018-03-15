package com.pepper.common.exception;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.google.common.collect.Maps;
import com.pepper.common.consts.SystemCode;
import com.pepper.web.model.CommonResp;

@ControllerAdvice
public class CustomExceptionHandler {

	// 签名异常
	public static final Integer TOKEN_ERROR_CODE = 612;
	// 系统异常
	public static final Integer SYSTEM_ERROR_CODE = 500;

	private static final Logger log = LoggerFactory.getLogger(CustomExceptionHandler.class);

	private static MappingJackson2JsonView jackson2JsonView = new MappingJackson2JsonView();

	@ExceptionHandler(CustomException.class)
	public void msdExceptionHandle(CustomException ex, HttpServletRequest request, HttpServletResponse response) {
		log.error("======>业务异常[code=" + ex.getCode() + "]", ex);

		// token异常
		if (SystemCode.TOKEN_ERROR.getCode().equals(ex.getCode())) {
			dealExceptionResponse(TOKEN_ERROR_CODE, new CommonResp(ex.getErrorCode(), ex.getMessage()), request, response);
			return;
		}

		dealExceptionResponse(SYSTEM_ERROR_CODE, new CommonResp(ex.getErrorCode(), ex.getMessage()), request, response);
	}

	@ExceptionHandler(Exception.class)
	public void exceptionHandle(Exception e, HttpServletRequest request, HttpServletResponse response) {
		log.error("======>系统异常", e);
		dealExceptionResponse(SYSTEM_ERROR_CODE, new CommonResp(SystemCode.SYSTEM_ERROR), request, response);

	}

	/**
	 * 公共异常处理
	 * 
	 * @param httpStatusCode
	 * @param commonResp
	 * @param request
	 * @param response
	 */
	public void dealExceptionResponse(int httpStatusCode, CommonResp commonResp, HttpServletRequest request, HttpServletResponse response) {
		if (!StringUtils.isEmpty(httpStatusCode)) {
			response.setStatus(httpStatusCode);
		} else {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		if (!ObjectUtils.isEmpty(commonResp)) {
			try {
				jackson2JsonView.render(getErrorMap(commonResp), request, response);
			} catch (Exception e) {
				log.warn("Jackson to json error.", e);
			}
		}
	}

	public Map<String, String> getErrorMap(CommonResp commonResp) {
		Map<String, String> errorMap = Maps.newConcurrentMap();
		if (!ObjectUtils.isEmpty(commonResp)) {
			errorMap.put("code", commonResp.getCode());
			errorMap.put("message", commonResp.getMessage());
		}
		return errorMap;
	}

}
