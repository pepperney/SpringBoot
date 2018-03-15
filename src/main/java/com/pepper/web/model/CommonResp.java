package com.pepper.web.model;

import java.io.Serializable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.pepper.common.consts.SystemCode;
import com.pepper.common.util.JsonUtil;

public class CommonResp implements Serializable {

	private static final long serialVersionUID = -8081367116849826277L;

	private String code;

	private String message;

	public CommonResp() {

	}

	public CommonResp(SystemCode code) {
		this.setErrorCode(code);
	}

	public CommonResp(SystemCode code, String message) {
		setErrorCode(code);
		setMessage(message);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setErrorCode(SystemCode code) {
		this.code = code.getCode();
		this.message = code.getMessage();
	}

	public String asJson() {
		return JsonUtil.toJson(this);
	}

	public static <T> ResponseEntity<T> returnOKResult(T data) {
		return new ResponseEntity<T>(data, HttpStatus.OK);
	}

	public static ResponseEntity<Object> returnOK() {
		return new ResponseEntity<Object>(HttpStatus.OK);
	}

	public static ResponseEntity<Object> returnErr(SystemCode code) {
		return returnErr(code, 610);
	}

	public static ResponseEntity<Object> returnErrResult(SystemCode code, String message) {
		return returnErrResult(code, message, 610);
	}

	public static ResponseEntity<Object> returnErr(SystemCode code, Integer statusCode) {
		return new ResponseEntity<Object>(new CommonResp(code), HttpStatus.valueOf(statusCode));
	}

	public static ResponseEntity<Object> returnErrResult(SystemCode code, String message, Integer statusCode) {
		return new ResponseEntity<Object>(new CommonResp(code, message), HttpStatus.valueOf(statusCode));
	}

}