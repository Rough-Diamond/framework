/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnvReplacer {
	static Pattern p = Pattern.compile("\\{env\\.([^}]+)\\}");
	public static String replaceEnv(String text) {
		Matcher matcher = p.matcher(text);
		if(!matcher.find()) {
			return text;
		}
		StringBuilder sb = new StringBuilder();
		int lastIndex = 0;
		do {
			sb.append(text.substring(lastIndex, matcher.start()));
			String env = System.getenv(matcher.group(1));
			sb.append(env == null ? matcher.group(0) : env);
			lastIndex = matcher.end();
		} while (matcher.find());
		sb.append(text.substring(lastIndex, text.length()));
		return sb.toString();
	}
}
