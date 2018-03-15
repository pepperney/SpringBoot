package com.pepper.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

/**
 * 转json去除属性
 * @author pei.nie
 *
 */
public class JsonFilter {

	private static final Logger logger = LoggerFactory.getLogger(JsonFilter.class);

	/**
	 * 过滤对象中的部分属性
	 * @param obj
	 * @param filterStrs
	 * @return
	 */
	public static String toString(Object obj, final String... filterStrs) {
		String result = "";
		try {
			SimplePropertyPreFilter preFilter = new SimplePropertyPreFilter();
			for (String str : filterStrs) {
				preFilter.getExcludes().add(str);
			}
			result = filtStr(JSON.toJSONString(obj, preFilter));//object是Java对象
		} catch (Exception e) {
			logger.error("JsonFilter.toString()过滤属性转json异常", e);
		}
		return result;
	}

	public static String filtStr(String str) {
		String res = str.replaceAll("(\\d{2})\\d{12}(\\d{4})", "$1************$2").replaceAll("(\\d{2})\\d{5}(\\d{4})", "$1*****$2");
		return call(res);
	}

	public static String call(String res) {
		String regexp = "0\\d{2,3}-\\d{7,8}";
		Pattern p = Pattern.compile(regexp);
		Matcher m = p.matcher(res);
		String call = "";
		String rep = "";
		while (m.find()) {
			call = m.group(0);
			rep = m.group(0);
		}
		if (StringUtils.isNotBlank(call)) {
			int s = call.replace("-", "").length() - 4;
			rep = rep.replace("-", "").replaceAll("(\\d{0})\\d{" + s + "}(\\d{4})", "$1*****$2");
		}
		return res.replace(call, rep);
	}

	/**
	 * 过滤手机号
	 * @param phone
	 * @return
	 */
	public static String filtMobile(String phone) {
		return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
	}

	public static void main(String[] args) {
		String dd = "{\"amount\":7300,\"applyNo\":\"MSXF20************075987940\",\"busType\":\"0\",\"conName1\":\"联系人未知\",\"conPhone1\":\"021-12345678\",\"cooperatorId\":\"80000040\",\"idCard\":\"44************7032\",\"outBusNo\":\"QFQ58b7b310a6057\",\"phone\":\"13728964977\",\"productNo\":\"2305\",\"reqMap\":{},\"uuId\":\"909da07792364332b2980e5898dffe38\"}";
		String dds = "{\"conPhone1\":\"17700000000\"}";
		System.out.println(filtStr(dd));
		System.out.println(filtMobile(dds));
	}
}
