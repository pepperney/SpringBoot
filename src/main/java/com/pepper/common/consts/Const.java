package com.pepper.common.consts;

public interface Const {


	// ######################################### 标识码 #########################################
	/**
	 * conmon response code success
	 */
	String SUCCESS = "0";
	/**
	 * conmon response code fail
	 */
	String FAIL = "-1";


	// ######################################### 格式 #########################################
	/**
	 * 日期时间格式：yyyy-MM-dd HH:mm:ss
	 */
	String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	/**
	 * 日期格式：yyyy-MM-dd
	 */
	String DATE_PATTERN = "yyyy-MM-dd";


	// ######################################### 时间单位 #########################################
	/**
	 * 时间单位：秒--1000ms
	 */
	int SECONDS = 1000;
	/**
	 * 时间单位：分--60s
	 */
	int MINUTES = 60 * SECONDS;
	/**
	 * 时间单位：时--60min
	 */
	int HOURS = 60 * MINUTES;
	/**
	 * 时间单位：天--24h
	 */
	int DAYS = 24 * HOURS;

	// ######################################### 其他常量  #########################################
	/**
	 * rabbitMQ exchage which is named "exchange_test"
	 */
	String MQ_EXCHANGE_TEST = "exchange_test";
	/**
	 * 日志中的请求id
	 */
	String MDC_KEY = "REQUEST_ID";
}
