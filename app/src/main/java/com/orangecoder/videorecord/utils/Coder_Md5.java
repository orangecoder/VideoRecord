package com.orangecoder.videorecord.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;


public class Coder_Md5 {

	/**
	 * md5编码
	 * 
	 * @param val
	 * @return
	 */
	public static String md5(String val) {
		try {
			String result = null;
			if (val != null && val.length() > 0) {
				try {
					MessageDigest md5 = MessageDigest.getInstance("MD5");
					byte[] buff = val.getBytes();
					md5.update(buff, 0, buff.length);
					result = String.format("%032x", new BigInteger(1, md5.digest()));
				} catch (Throwable e) {
				}
			}
			return result;
		} catch (Throwable e) {
		}
		return "";
	}

	/**
	 * md5编码
	 * 
	 * @MD5实现过程
	 */
	public static String md5(byte[] source) {
		String s = null;
		char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(source);
			byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
										// 用字节表示就是 16 个字节
			char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
											// 所以表示成 16 进制需要 32 个字符
			int k = 0; // 表示转换结果中对应的字符位置
			for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
											// 转换成 16 进制字符的转换
				byte byte0 = tmp[i]; // 取第 i 个字节
				str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
															// >>>
															// 为逻辑右移，将符号位一起右移
				str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
			}
			s = new String(str); // 换后的结果转换为字符串
		} catch (Throwable e) {
		}
		return s;
	}

	/**
	 * 获取文件的Md5值
	 * 
	 * @param file
	 * @return
	 */
	public static String getMD5SUM(File file) {
		try {
			if (file == null) {
				return null;
			}

			if (!file.exists()) {
				return null;
			}
		} catch (Throwable e) {
		}

		InputStream fis = null;
		byte[] buffer = new byte[1024];
		int numRead = 0;
		MessageDigest md5;
		try {
			fis = new FileInputStream(file);
			md5 = MessageDigest.getInstance("MD5");
			while ((numRead = fis.read(buffer)) > 0) {
				md5.update(buffer, 0, numRead);
			}
			return Basic_Converter.bytesToHexString(md5.digest());
		} catch (Throwable e) {
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (Throwable e2) {
			}
		}
		return null;
	}
	
	/**
	 * 校验文件的md5
	 * 
	 * @param file
	 * @param md5Sum
	 * @return
	 */
	public static boolean checkMd5Sum(File file, String md5Sum) {
		try {
			String fileMd5 = getMD5SUM(file);
			if (fileMd5 == null) {
				return false;
			}
			fileMd5 = fileMd5.toLowerCase();

			if (md5Sum == null) {
				return false;
			}
			md5Sum = md5Sum.toLowerCase();
			return fileMd5.equals(md5Sum);
		} catch (Throwable e) {
		}
		return false;
	}

}
