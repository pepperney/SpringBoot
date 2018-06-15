package com.pepper.common.exception;

import com.pepper.common.consts.Code;

public class CustomException extends RuntimeException {

	private static final long serialVersionUID = 7393177901625809149L;

	private Code code;

	public CustomException(Code code) {
		super(code.getMsg());

		this.code = code;
	}

	public CustomException(Code code, String message) {
		super(message);

		this.code = code;
	}

	public CustomException(Code code, String message, Throwable t) {
		super(message, t);

		this.code = code;
	}

	public String getCode() {
		return this.code.getCode();
	}

	public String getMessage(){
		return this.code.getMsg();
	}

	public Code getErrorCode() {
		return this.code;
	}
}
