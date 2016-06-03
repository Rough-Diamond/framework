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
		assertEquals("�ϊ����ʂɌ�肪����܂��B", "YYYYMMDD", format);
		format = convertor.translateFormat("yyyyMMddHHmmssSSS");
		assertEquals("�ϊ����ʂɌ�肪����܂��B", "YYYYMMDDHH24MISSMS", format);
		format = convertor.translateFormat("yyyyMdHmsS");
		assertEquals("�ϊ����ʂɌ�肪����܂��B", "YYYYFMMMFMDDFMHH24FMMIFMSSFMMS", format);
		format = convertor.translateFormat("yyyy/MM/dd");
		assertEquals("�ϊ����ʂɌ�肪����܂��B", "YYYY/MM/DD", format);
	}
}
