package com.pepper.boot.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

import com.pepper.boot.consts.SystemCode;
import com.pepper.boot.exception.CustomException;

public class MD5Utils {

	private static final String ALGORITHM = "MD5";
	private static final String CHARSET = "UTF-8";
	private static final int BUFFERSIZE = 8192;

	public static String md5(String input) {

		try {
			MessageDigest md5 = MessageDigest.getInstance(ALGORITHM);
			byte[] md5Bytes = md5.digest(input.getBytes(CHARSET));
			return byte2hex(md5Bytes);
		} catch (Exception e) {
			throw new CustomException(SystemCode.FILE_NOT_FOUND, e.getMessage());
		}
	}

	private static String byte2hex(byte[] b) {

		String str = "";
		String stmp = "";
		int length = b.length;
		for (int n = 0; n < length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				str = str + "0" + stmp;
			} else {
				str = str + stmp;
			}
			if (n < length - 1) {
				str = str + "";
			}
		}
		return str.toLowerCase();
	}

	public static String md5File(String filename) {

		try {
			return md5(new FileInputStream(filename));
		} catch (FileNotFoundException e) {
			throw new CustomException(SystemCode.FILE_NOT_FOUND, e.getMessage());
		}
	}

	public static String md5(InputStream inputStream) {

		BufferedInputStream bufferedInputStream = null;
		MessageDigest md = null;
		try {
			byte[] buffer = new byte[BUFFERSIZE];
			int i = 0;
			bufferedInputStream = new BufferedInputStream(inputStream, BUFFERSIZE);
			md = MessageDigest.getInstance(ALGORITHM);
			while ((i = bufferedInputStream.read(buffer)) != -1) { // >0
				md.update(buffer, 0, i);
			}
			byte[] md5Bytes = md.digest();
			return byte2hex(md5Bytes);
		} catch (Exception e) {
			throw new CustomException(SystemCode.FILE_NOT_FOUND, e.getMessage());
		} finally {
			if (bufferedInputStream != null) {
				try {
					bufferedInputStream.close();
				} catch (IOException e) {
					throw new CustomException(SystemCode.FILE_NOT_FOUND, e.getMessage());
				}
			}
		}
	}

}
