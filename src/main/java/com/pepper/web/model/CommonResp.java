package com.pepper.web.model;

import java.io.Serializable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.pepper.common.consts.Code;
import com.pepper.common.util.JsonUtil;

public class CommonResp implements Serializable {

	private static final long serialVersionUID = -8081367116849826277L;

	private String code;

	private String message;

	public CommonResp() {

	}

	public CommonResp(Code code) {
		this.setErrorCode(code);
	}

	public CommonResp(Code code, String message) {
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

	public void setErrorCode(Code code) {
		this.code = code.getCode();
		this.message = code.getMsg();
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

	public static ResponseEntity<Object> returnErr(Code code) {
		return returnErr(code, 610);
	}

	public static ResponseEntity<Object> returnErrResult(Code code, String message) {
		return returnErrResult(code, message, 610);
	}

	public static ResponseEntity<Object> returnErr(Code code, Integer statusCode) {
		return new ResponseEntity<Object>(new CommonResp(code), HttpStatus.valueOf(statusCode));
	}

	public static ResponseEntity<Object> returnErrResult(Code code, String message, Integer statusCode) {
		return new ResponseEntity<Object>(new CommonResp(code, message), HttpStatus.valueOf(statusCode));
	}

}