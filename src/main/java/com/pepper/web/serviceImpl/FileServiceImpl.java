package com.pepper.web.serviceImpl;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.pepper.web.service.FileService;

@Service("fileService")
public class FileServiceImpl implements FileService {

	private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

	@Override
	public ResponseEntity<byte[]> download(String filename, HttpServletRequest request) {
		ResponseEntity<byte[]> file = null;
		ServletContext sc = request.getSession().getServletContext();
		String sourceUrl = sc.getRealPath(filename);// 设定文件保存的目录
		logger.debug("------------------------>  sourceUrl is : " + sourceUrl);
		if (null != filename && !"".equals(filename.trim())) {
			File sourceFile = new File(sourceUrl);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			try {
				headers.setContentDispositionFormData("attachment", new String(filename.getBytes("UTF-8"), "ISO8859-1"));
				file = new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(sourceFile), headers, HttpStatus.OK);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}



}
