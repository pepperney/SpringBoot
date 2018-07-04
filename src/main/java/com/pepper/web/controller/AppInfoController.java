package com.pepper.web.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 获取服务器信息的接口
 * @author pei.nie
 *
 */
@RestController
public class AppInfoController {
	
	private static Logger log = LoggerFactory.getLogger(AppInfoController.class);

	@RequestMapping(value = "/appInfo", method = RequestMethod.GET)
	public Map<String, String> home() {
		DecimalFormat df = new DecimalFormat("0.00");

		Map<String, String> info = new HashMap<String, String>();
		// 显示JVM总内存
		long totalMem = Runtime.getRuntime().totalMemory();

		info.put("totalMemory", df.format(totalMem / 1000000F) + " MB");

		// 显示JVM尝试使用的最大内存
		long maxMem = Runtime.getRuntime().maxMemory();
		info.put("maxMemory", df.format(maxMem / 1000000F) + " MB");

		// 空闲内存
		long freeMem = Runtime.getRuntime().freeMemory();
		info.put("freeMemory", df.format(freeMem / 1000000F) + " MB");

		// 主机ip
		String ip = getHostIp(getInetAddress());
		info.put("ip", ip == null ? "unknownIp" : ip);

		// 端口
		String port = System.getProperty("tomcat.port");
		info.put("port", port == null ? "0000" : port);

		return info;
	}

	private String getHostIp(InetAddress netAddress) {
		if (null == netAddress) {
			return null;
		}
		String ip = netAddress.getHostAddress(); // get the ip address
		return ip;
	}

	private InetAddress getInetAddress() {
		try {
			return InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			log.info("unknown host!");
		}
		return null;
	}
}
