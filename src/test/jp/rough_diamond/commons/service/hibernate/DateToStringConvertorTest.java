/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.service.hibernate;

import org.hibernate.dialect.PostgreSQLDialect;

import junit.framework.TestCase;


/**
 *
 */
public class DateToStringConvertorTest extends TestCase {
	public void testFormatConvertWhenPostgreSQL() {
		DateToStringConvertor convertor = DateToStringConvertor.getConvertor(PostgreSQLDialect.class); 
		String format = convertor.translateFormat("yyyyMMdd");
		assertEquals("変換結果に誤りがあります。", "YYYYMMDD", format);
		format = convertor.translateFormat("yyyyMMddHHmmssSSS");
		assertEquals("変換結果に誤りがあります。", "YYYYMMDDHH24MISSMS", format);
		format = convertor.translateFormat("yyyyMdHmsS");
		assertEquals("変換結果に誤りがあります。", "YYYYFMMMFMDDFMHH24FMMIFMSSFMMS", format);
		format = convertor.translateFormat("yyyy/MM/dd");
		assertEquals("変換結果に誤りがあります。", "YYYY/MM/DD", format);
	}
}
