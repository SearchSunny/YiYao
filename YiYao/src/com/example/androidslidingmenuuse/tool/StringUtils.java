package com.example.androidslidingmenuuse.tool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	// 去除字符串中的空格、回车、换行符、制表符
	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}
	/**
	 * 判断字符串是否为空或null
	 * @param str
	 * @return
	 */
	public static boolean isNullColums(String str) {
		if (str == null || str.trim().equals("") || str.trim().equals("null")) {
			return true;
		}
		return false;
	}
	
}
