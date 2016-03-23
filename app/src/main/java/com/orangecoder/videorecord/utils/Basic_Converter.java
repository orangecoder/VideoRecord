package com.orangecoder.videorecord.utils;


public class Basic_Converter {

//	static final String UpperCaseHex = "0123456789ABCDEF";
	public static final String LowerCaseHex = "0123456789abcdef";
//	static final char HEX_DIGITS_UPPERCASE[]=UpperCaseHex.toCharArray();
	public static final char HEX_DIGITS_LOWERCASE[]=LowerCaseHex.toCharArray();

	 /**
	 * 字符总数0-9a-zA-Z\_\- a-z: 10-35 A-Z: 36-61 _: 62 -: 63
	 */
	 final static int MAX64 = 64;

	/** 加密之后的字符集 */
	private static final char dic64[] = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_-".toCharArray();

//	/** 把指定的十六进制字符串转换为64进制字符串 */
//	final static String converHexTo64(String str) {
//		 
//		final int lens = str.length();
//		
//		
//		int  tempLen=3-(lens%3);
//		 
//		tempLen%=3;
//		
//		int countLens=lens+tempLen;
//		
//		StringBuilder temSb=new StringBuilder();
//		for (int i = 0; i < tempLen; i++) {
//			temSb.append('0');			
//		}
//		
//		System.err.println(str.length());
//		str= temSb.append(str).toString();
//		
//		System.err.println(str+"=>"+str.length());
//		
//		StringBuilder buffer = new StringBuilder((lens << 1) / 3);
//		int dec = 0;
//		byte chr = 0;
//		int end = 0;
//
//		for (int i = 0; i < countLens; i += 3) {
//			end = (i + 3) < countLens ? (i + 3) : countLens;
//			String tmp = str.substring(i, end);
//			dec = Integer.parseInt(tmp, 16);
//			chr = (byte) ((dec < MAX64) ? 0 : (dec >>> 6));//如果小于64，则高位补0
//			buffer.append(dic64[chr]);
//
//			buffer.append(dic64[dec & 63]);
//		}
//
//		return buffer.toString();
//	}

	/**
	 * <p>
	 * 十六进制字符转换为byte
	 * </p>
	 * java的byte是-127到128之间<br/>
	 * 0~F十六进制字符表示的范围是0到15<br/>
	 * 
	 * @param c
	 *            0~F 十六进制字符
	 * @return 0~15
	 */
	public static byte hexCharToByte(char c) {
		try {
			c = Character.toLowerCase(c);
		} catch (Throwable e) {
		}
		return (byte) LowerCaseHex.indexOf(c);
	}

	
	/**
	 * 将byte转换为hex String
	 * @param b
	 * @return
	 */
	public static String bytesToHexString(byte[] b) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS_LOWERCASE[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS_LOWERCASE[b[i] & 0x0f]);
		}
		return sb.toString();
	}
}
