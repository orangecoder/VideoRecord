package com.orangecoder.videorecord.utils;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Basic_StringUtil {

	public static boolean isNullOrEmpty(String str) {
		try {
			if (str == null) {
				return true;
			}
			return (str.trim().length() == 0);
		} catch (Throwable e) {
		}
		return true;
	}
	
	/**
	 * 获取非null的字符串，如果为null，则返回空串
	 * @param str
	 * @return
	 */
	public static String getNotNullStringButEmpty(String str) {
		try {
			if(str==null)
			{
				return "";
			}
			str=str.trim();
			return str;
		} catch (Throwable e) {
		}
		return "";
	}

	public static String getNOTNULLString(String str) {
		try {
			if (str == null) {
				return null;
			}
			if (str.length() == 0) {
				return null;
			}

			str = str.trim();
			if (str.length() == 0) {
				return null;
			}
			return str;
		} catch (Throwable e) {
		}
		return null;
	}

	public static boolean isEmail(String str) {
		try {
			// 待验证
			Pattern pattern = Pattern
					.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");

			Matcher matcher = pattern.matcher(str);
			return matcher.matches();
		} catch (Throwable e) {
		}
		return false;
	}

	public static String getString(Context context, int resourceId) {
		try {
			String res = context.getResources().getString(resourceId);
			return res;
		} catch (Throwable e) {
		}
		return null;
	}

	public static String formate(Context context, int resourceId, Object... formatargs) {
		try {
			String res = context.getResources().getString(resourceId, formatargs);
			return res;
		} catch (Throwable e) {
		}
		return null;
	}

	public static String formatBtiNum(int bignum) {
		try {
			return String.format("%,d", bignum);
		} catch (Throwable e) {
		}
		return String.valueOf(bignum);
	}

	public static String formatImageUri(String originUri, String pre) {
		try {
			if (originUri == null) {
				return originUri;
			}
			String origin = originUri.replaceAll("!.*\\?", "?");
			int index = origin.lastIndexOf('?');
			if (index < 0) {
				return origin;
			}
			String p1 = origin.substring(0, index);
			String p2 = origin.substring(index);
			return p1 + pre + p2;
		} catch (Throwable e) {
		}
		return originUri;
	}

	public static String getJsPrefix() {
		return "javascript:";
	}
	 

	public static String getNotEmptyStringElseReturnNull(String str) {
		try {
			if (str != null) {
				str = str.trim();

				if (str.length() > 0) {
					return str;
				}
			}
		} catch (Throwable e) {
		}
		return null;
	}

	public static String objectSerializableToString(Serializable object) {
		ByteArrayOutputStream baos = null;
		ObjectOutputStream os=null;
		try {
			if (object != null) {
				baos = new ByteArrayOutputStream();
				os=new ObjectOutputStream(baos);
				os.writeObject(object);
				os.flush();
				baos.flush();
				return baos.toString("UTF-8");
			}
		} catch (Throwable e) {
		} finally {
			try {
				if (os != null) {
					os.close();
				}
			} catch (Throwable e2) {
			}
			
			try {
				if (baos != null) {
					baos.close();
				}
			} catch (Throwable e2) {
			}
		}
		return null;
	}

}
