/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.service.hibernate;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jp.rough_diamond.commons.extractor.DateToString;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.MySQLInnoDBDialect;
import org.hibernate.dialect.MySQLMyISAMDialect;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.dialect.Oracle9iDialect;
import org.hibernate.dialect.PostgreSQLDialect;

public class DateToStringConvertor {
	static String[] keys = {
		"yyyy", 
		"SSS",
		"yy", "MM", "dd", "HH", "mm", "ss",
		"y", "M", "d", "H", "m", "s", "S"}; 
	static final Map<Class<? extends Dialect>, DateToStringConvertor> CONVERTOR_MAP;
	static {
		Map<Class<? extends Dialect>, DateToStringConvertor> map = new HashMap<Class<? extends Dialect>, DateToStringConvertor>();
		//MySQL
		DateToStringConvertor convertor = new DateToStringConvertor(
				"to_char({0}, ''{1}'')",
				"YYYY", 
				"MS", 
				"YY", "MM", "DD", "HH24", "MI", "SS",
				"Y", "FMMM", "FMDD", "FMHH24", "FMMI", "FMSS", "FMMS");
		map.put(PostgreSQLDialect.class, convertor);
		//Oracle(任意桁数ミリ秒怪しい）
		convertor = new DateToStringConvertor(
				"to_char({0}, ''{1}'')",
				"YYYY", 
				"FF3", 
				"YY", "MM", "DD", "HH24", "MI", "SS",
				"Y", "FMMM", "FMDD", "FMHH24", "FMMI", "FMSS", "FMFF3");
		map.put(Oracle8iDialect.class, convertor);
		map.put(Oracle9iDialect.class, convertor);
		map.put(Oracle10gDialect.class, convertor);
		//H2(H2Dialectにはformatdatetimeは無いが将来サポートされると思われるので・・・）
		convertor = new DateToStringConvertor(
				"formatdatetime({0}, ''{1}'')", keys){
			@Override
			String translateFormat(String format) {
				return format;
			}			
		};
		map.put(H2Dialect.class, convertor);
		map.put(H2DialectExt.class, convertor);
		//MySQL(ミリ秒は返せない。0抜きは分、ミリ秒はできない。年を１ケタで返せない)
		convertor = new DateToStringConvertor(
				"date_format({0}, ''{1}'')",
				"%Y", 
				"%f", 
				"%y", "%m", "%d", "%H", "%i", "%s",
				"%y", "%c", "%e", "%k", "%i", "%S", "%f");
		map.put(MySQLDialect.class, convertor);
		map.put(MySQLInnoDBDialect.class, convertor);
		map.put(MySQL5Dialect.class, convertor);
		map.put(MySQL5InnoDBDialect.class, convertor);
		map.put(MySQLMyISAMDialect.class, convertor);

		CONVERTOR_MAP = Collections.unmodifiableMap(map);
	}

	final String format;
	final String[] after;
	private DateToStringConvertor(String format, String... after) {
		if(keys.length != after.length) {
			throw new RuntimeException("parameter unmuch.");
		}
		this.format = format;
		this.after = after;
	}
	
	static DateToStringConvertor getConvertor(Class<? extends Dialect> dialectType) {
		return CONVERTOR_MAP.get(dialectType);
	}

	@SuppressWarnings("unchecked")
	String convert(Extractor2HQL generator, DateToString v) {
		return MessageFormat.format(format, 
				Extractor2HQL.VALUE_MAKE_STRATEGY_MAP.get(v.value.getClass()).makeValue(generator, v.value),
				translateFormat(v.format));
	}
	
	private Map<String, String> formatCache = new HashMap<String, String>();
	
	String translateFormat(String format) {
		if(formatCache.containsKey(format)) {
			return formatCache.get(format);
		}
		StringBuilder sb = new StringBuilder();
		int index = 0;
		while(index < format.length()) {
			boolean changeIt = false;
			for(int i = 0 ; i < keys.length ; i++) {
				if(format.startsWith(keys[i], index)){
					sb.append(this.after[i]);
					index+= keys[i].length();
					changeIt = true;
					break;
				}
			}
			if(!changeIt) {
				sb.append(format.charAt(index++));
			}
		}
		formatCache.put(format, sb.toString());
		return translateFormat(format);
	}
}
