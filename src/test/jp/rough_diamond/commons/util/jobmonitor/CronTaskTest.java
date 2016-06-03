/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.util.jobmonitor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

@SuppressWarnings("deprecation")
public class CronTaskTest extends TestCase {
	public void testScheduleConstructor() throws Exception {
		CronTask.Schedule s = new CronTask.Schedule("01 * * * *");
		assertEquals("��������Ă��܂��B", 1, s.minute);
		assertEquals("��������Ă��܂��B", -1, s.hour);
		assertEquals("��������Ă��܂��B", -1, s.day);
		assertEquals("��������Ă��܂��B", -1, s.month);
		assertEquals("�T������Ă��܂��B", -1, s.week);

		s = new CronTask.Schedule("02 4 * * *");
		assertEquals("��������Ă��܂��B", 2, s.minute);
		assertEquals("��������Ă��܂��B", 4, s.hour);
		assertEquals("��������Ă��܂��B", -1, s.day);
		assertEquals("��������Ă��܂��B", -1, s.month);
		assertEquals("�T������Ă��܂��B", -1, s.week);

		s = new CronTask.Schedule("22 4 * * 0");
		assertEquals("��������Ă��܂��B", 22, s.minute);
		assertEquals("��������Ă��܂��B", 4, s.hour);
		assertEquals("��������Ă��܂��B", -1, s.day);
		assertEquals("��������Ă��܂��B", -1, s.month);
		assertEquals("�T������Ă��܂��B", 0, s.week);

		s = new CronTask.Schedule("42 4 1 * 0");
		assertEquals("��������Ă��܂��B", 42, s.minute);
		assertEquals("��������Ă��܂��B", 4, s.hour);
		assertEquals("��������Ă��܂��B", 1, s.day);
		assertEquals("��������Ă��܂��B", -1, s.month);
		assertEquals("�T������Ă��܂��B", 0, s.week);
		
		s = new CronTask.Schedule("42 4 x * 0");
		assertEquals("��������Ă��܂��B", 42, s.minute);
		assertEquals("��������Ă��܂��B", 4, s.hour);
		assertEquals("��������Ă��܂��B", 99, s.day);
		assertEquals("��������Ă��܂��B", -1, s.month);
		assertEquals("�T������Ă��܂��B", 0, s.week);
		
		try {
			new CronTask.Schedule("02 4 * * a");
			fail("��O�����o����Ă��܂���B");
		} catch(Exception e) {}
		try {
			new CronTask.Schedule("* * * * *");
			fail("��O�����o����Ă��܂���B");
		} catch(Exception e) {}
		try {
			new CronTask.Schedule("0 * 1 * *");
			fail("��O�����o����Ă��܂���B");
		} catch(Exception e) {}
		try {
			new CronTask.Schedule("0 * * * 0");
			fail("��O�����o����Ă��܂���B");
		} catch(Exception e) {}
		try {
			new CronTask.Schedule("0 1 * 1 *");
			fail("��O�����o����Ă��܂���B");
		} catch(Exception e) {}
		try {
			new CronTask.Schedule("0 1 1 1 *");
		} catch(Exception e) {
			fail("��O�����o����Ă��܂��B");
		}
	}
	
	public void testRegExpToken() throws Exception {
		Pattern p = Pattern.compile(CronTask.Schedule.TOKEN);
		assertTrue("�}�b�`���Ă��܂���B", p.matcher("1").matches());
		assertTrue("�}�b�`���Ă��܂���B", p.matcher("01").matches());
		assertTrue("�}�b�`���Ă��܂���B", p.matcher("*").matches());
		assertFalse("�}�b�`���Ă��܂���B", p.matcher("1x").matches());
		Matcher m = p.matcher("1");
		m.matches();
		assertEquals("����Ă��܂��B", "1", m.group(1));
	}
	
	public void testGetNextTimestamp() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

		CronTask.Schedule s = new CronTask.Schedule("30 * * * *");
		Date cur = sdf.parse("2009-01-10 23:29:59.999");
		Date next = s.getNextTimestamp(cur);
		assertEquals("������s����������Ă��܂��B", "2009-01-10 23:30:00.000", sdf.format(next));
		cur = sdf.parse("2009-01-10 23:30:59.999");
		next = s.getNextTimestamp(cur);
		assertEquals("������s����������Ă��܂��B", "2009-01-11 00:30:00.000", sdf.format(next));

		s = new CronTask.Schedule("30 12 * * *");
		cur = sdf.parse("2009-01-31 12:29:59.999");
		next = s.getNextTimestamp(cur);
		assertEquals("������s����������Ă��܂��B", "2009-01-31 12:30:00.000", sdf.format(next));

		s = new CronTask.Schedule("30 12 x * *");
		cur = sdf.parse("2009-01-31 12:30:59.999");
		next = s.getNextTimestamp(cur);
		assertEquals("������s����������Ă��܂��B", "2009-02-28 12:30:00.000", sdf.format(next));

		s = new CronTask.Schedule("30 12 x 1 *");
		cur = sdf.parse("2009-01-31 12:30:59.999");
		next = s.getNextTimestamp(cur);
		assertEquals("������s����������Ă��܂��B", "2010-01-31 12:30:00.000", sdf.format(next));

		s = new CronTask.Schedule("30 12 x 1 1");
		cur = sdf.parse("2009-01-31 12:30:59.999");
		next = s.getNextTimestamp(cur);
		assertEquals("������s����������Ă��܂��B", "2011-01-31 12:30:00.000", sdf.format(next));
	}
	
	public void testExecuteWhenPastNextTimestamp() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

		JobExt ext = new JobExt();
		CronTask task = new CronTask("30 * * * *", ext);

		Date next = sdf.parse("2008-10-10 11:30:00.000");
		task.nextTimestamp = next;
		task.executeWhenPastNextTimestamp(sdf.parse("2008-10-10 11:29:59.000"));
		assertFalse("���s����Ă��܂��B", ext.isCalled);
		task.executeWhenPastNextTimestamp(sdf.parse("2008-10-10 11:30:00.000"));
		assertTrue("���s����Ă��܂���B", ext.isCalled);
		assertFalse("������s���Ԃ��X�V����Ă��܂���B", next.equals(task.nextTimestamp));
	}

	static class JobExt implements Job {
		boolean isCalled = false;
		@Override
		public void run() {
			isCalled = true;
		}
	}
}
