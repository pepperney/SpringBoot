package com.pepper.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.pepper.web.service.FileService;

@Controller
@RequestMapping("/file")
public class FileController {

	private Logger logger = LoggerFactory.getLogger(FileController.class);

	@Autowired
	private FileService fileService;

	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public ResponseEntity<byte[]> download(@RequestParam("filename") String filename, HttpServletRequest request) throws Exception {
		logger.debug("-----------------  download is start    -----------------");
		ResponseEntity<byte[]> file = fileService.download(filename, request);
		logger.debug("-----------------  download is end    -----------------");
		return file;
	}

	@RequestMapping(value = "/downloadLocal", method = RequestMethod.GET)
	public void downloadLocal(HttpServletRequest req, HttpServletResponse resp,String name) throws Exception {
		
		File file = new File(name);
		InputStream in = new FileInputStream(file);
		
		name = name.substring(name.lastIndexOf("\\")+1, name.length());
		name = URLEncoder.encode(name, "UTF-8");//对文件进行url编码
		resp.setContentType("application/force-download");//应用程序强制下载
		resp.setHeader("Content-Disposition", "attachment;filename=" + name); //设置响应头 
		resp.setContentLength(in.available());

		OutputStream out = resp.getOutputStream();
		byte[] b = new byte[1024];
		int len = 0;
		while ((len = in.read(b)) != -1) {
			out.write(b, 0, len);
		}
		out.flush();
		out.close();
		in.close();
	}

}
