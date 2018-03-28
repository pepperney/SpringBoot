package com.pepper.web.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

public interface FileService {
	/**
	 * 根据文件地址下载文件
	 * @param path
	 */
	public ResponseEntity<byte[]> download(String filename, HttpServletRequest request);
	
	
	
}
