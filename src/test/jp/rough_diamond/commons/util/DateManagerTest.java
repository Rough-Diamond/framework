/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;


/**
 *
 */
public class DateManagerTest extends TestCase {
	@Override
	protected void setUp() throws Exception {
		//初期化ルーチンで重くなってるかもしれないので先読み
		Class.forName(DateManager.class.getName());
	}
	
	@Override
	protected void tearDown() throws Exception {
		DateManager.DM.setDate(null);
	}
	
	public void testNoneSetting() throws Exception {
		long system = System.currentTimeMillis();
		long wrapped = DateManager.DM.currentTimeMillis();
		System.out.println(system);
		System.out.println(wrapped);
		assertTrue("10ms以上誤差があります。", (Math.abs(system - wrapped) < 10L));
		
		Date sysDate = new Date();
		Date wrappedDate = DateManager.DM.newDate();
		System.out.println(sysDate + ":" + sysDate.getTime());
		System.out.println(wrappedDate + ":" + wrappedDate.getTime());
		assertTrue("10ms以上誤差があります。", (Math.abs(sysDate.getTime() - wrappedDate.getTime()) < 10L));
		
		Calendar sysCal = Calendar.getInstance();
		Calendar wrappedCal = DateManager.DM.newCalendar();
		System.out.println(sysCal + ":" + sysDate.getTime());
		System.out.println(wrappedCal + ":" + wrappedDate.getTime());
		assertTrue("10ms以上誤差があります。", (Math.abs(sysCal.getTimeInMillis() - wrappedCal.getTimeInMillis()) < 10L));
	}
	
	public void testAfterSetting() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		//500msにしたのは誤差吸収のため（500mずれることはあり得ないでしょう。あればバグだ！）
		Date d = sdf.parse("2011/04/06 13:12:11.500");
		DateManager.DM.setDate(d);
		Thread.sleep(1000);
		Date d2 = DateManager.DM.newDate();
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		assertEquals("返却値が誤っています。", "2011/04/06 13:12:12", sdf2.format(d2));
	}
	
	public void testAfterNullSetting() throws Exception {
		DateManager.DM.setDate(null);
		testNoneSetting();
	}
}
