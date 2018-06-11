package com.pepper.common.consts;

public class Const {
	
	/** global redis key prefix */
	public static final String PREFIX = "system_";
	/** redis key for token*/
	public static final String TOKEN_KEY = PREFIX+"_user_token_";
	
	
	/** redis key expire time */
	public static final long EXPIRE_TIME_USERID = 5 * 60;
	/** token expire time  */
	public static final long EXPIRE_TIME_TOKEN = 7 * 24 * 60 *60;
	
	/** conmon response code success*/
	public static final String SUCCESS = "0";
	/** conmon response code fail*/
	public static final String FAIL = "-1";

	/** rabbitMQ exchage which is named "exchange_test" */
	public static final String MQ_EXCHANGE_TEST = "exchange_test";
}
