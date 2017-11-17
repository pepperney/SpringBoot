package com.pepper.boot.consts;

import java.util.Objects;

public enum SystemCode {
	
	SYSTEM_SUCCESS			("200","success"),
	CHECK_UNAUTHORIZED   	("401","unauthorized"),
	CHECK_FORBIDDEN			("403","forbidden"),
	SYSTEM_ERROR			("500","系统异常"),
	TOKEN_ERROR				("611","token异常"),
	FILE_NOT_FOUND			("612","找不到文件"),
	CONNECT_TIMEOUT			("613","连接超时"),
	CHECK_TIMESTAMP_FAIL	("614","时间戳异常"),
	CHECK_SIGN_FAIL			("615","签名异常"),
	CHECK_TOKEN_EXPIRE		("616","登录验证已过期，请重新登录");
	
	
	
	
	private String code;
	
	private String message;

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
	
	SystemCode(String code,String message){
		this.code = code;
		this.message = message;
	}
	
	public static SystemCode getByCode(String code){
		for(SystemCode _enum : values()){
			if(Objects.equals(code, _enum.getCode())){
				return _enum;
			}
		}
		return null;
	}
}
