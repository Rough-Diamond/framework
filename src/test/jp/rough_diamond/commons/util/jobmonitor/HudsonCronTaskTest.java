/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.util.jobmonitor;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

public class HudsonCronTaskTest extends TestCase {
	public void testGetNextTimestamp() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

		HudsonCronTask.Schedule s = new HudsonCronTask.Schedule("30 * * * *");
		Date cur = sdf.parse("2009-01-10 23:29:59.999");
		Date next = s.getNextTimestamp(cur);
		assertEquals("次回実行日時が誤っています。", "2009-01-10 23:30:00.000", sdf.format(next));
		cur = sdf.parse("2009-01-10 23:30:59.999");
		next = s.getNextTimestamp(cur);
		assertEquals("次回実行日時が誤っています。", "2009-01-11 00:30:00.000", sdf.format(next));

		s = new HudsonCronTask.Schedule("30 12 * * *");
		cur = sdf.parse("2009-01-31 12:29:59.999");
		next = s.getNextTimestamp(cur);
		assertEquals("次回実行日時が誤っています。", "2009-01-31 12:30:00.000", sdf.format(next));
	}
	
	public void testExecuteWhenPastNextTimestamp() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

		JobExt ext = new JobExt();
		HudsonCronTask task = new HudsonCronTask("30 * * * *", ext);

		Date next = sdf.parse("2008-10-10 11:30:00.000");
		task.nextTimestamp = next;
		task.executeWhenPastNextTimestamp(sdf.parse("2008-10-10 11:29:59.000"));
		assertFalse("実行されています。", ext.isCalled);
		task.executeWhenPastNextTimestamp(sdf.parse("2008-10-10 11:30:00.000"));
		assertTrue("実行されていません。", ext.isCalled);
		assertFalse("次回実行時間が更新されていません。", next.equals(task.nextTimestamp));
	}

	static class JobExt implements Job {
		boolean isCalled = false;
		@Override
		public void run() {
			isCalled = true;
		}
	}
}
