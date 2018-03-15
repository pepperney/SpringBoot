package com.pepper.common.exception;

import com.pepper.common.consts.SystemCode;

public class CustomException extends RuntimeException {

	private static final long serialVersionUID = 7393177901625809149L;

	private SystemCode systemCode;

	public CustomException(SystemCode systemCode) {
		super(systemCode.getMessage());

		this.systemCode = systemCode;
	}

	public CustomException(SystemCode systemCode, String message) {
		super(message);

		this.systemCode = systemCode;
	}

	public CustomException(SystemCode systemCode, String message, Throwable t) {
		super(message, t);

		this.systemCode = systemCode;
	}

	public String getCode() {
		return this.systemCode.getCode();
	}

	public SystemCode getErrorCode() {
		return this.systemCode;
	}
}
