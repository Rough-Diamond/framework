/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.util.jobmonitor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.rough_diamond.commons.util.DateManager;

@Deprecated
public class CronTask implements CrontabTask {
	final Schedule schedule;
	Date nextTimestamp;
	final Job job;
	public CronTask(String scheduleString, Job job) {
		schedule = new Schedule(scheduleString);
		nextTimestamp = schedule.getNextTimestamp();
		this.job = job;
	}
	
	public void executeWhenPastNextTimestamp() {
		executeWhenPastNextTimestamp(DateManager.DM.newDate());
	}

	void executeWhenPastNextTimestamp(Date cur) {
		if(nextTimestamp.compareTo(cur) <= 0) {
			try {
				job.run();
			} finally {
				nextTimestamp = schedule.getNextTimestamp();
			}
		}
	}
	
	static class Schedule {
		final public int minute;
		final public int hour;
		final public int day;
		final public int month;
		final public int week;
		
		Schedule(String scheduleString) {
			Matcher m = p.matcher(scheduleString);
			if(!m.matches()) {
				throw new RuntimeException("schedule String syntax error:" + scheduleString);
			}
			minute = ("*".equals(m.group(1))) ? -1 : Integer.parseInt(m.group(1));
			hour = ("*".equals(m.group(2))) ? -1 : Integer.parseInt(m.group(2));
			day = initDay(m);
			month = ("*".equals(m.group(4))) ? -1 : Integer.parseInt(m.group(4));
			week = ("*".equals(m.group(5))) ? -1 : Integer.parseInt(m.group(5));
			validate(scheduleString);
		}
		
		int initDay(Matcher m) {
			if("*".equals(m.group(3))) {
				return -1;
			} else if("x".equals(m.group(3))) {
				return 99;
			} else {
				return Integer.parseInt(m.group(3));
			}
		}
		
		void validate(String scheduleString) {
			//論理チェック
			if(minute == -1) {
				throw new RuntimeException("schedule String error:" + scheduleString);
			}
			if(week != -1 && hour == -1) {
				//週が指定されているのに時間が未指定
				throw new RuntimeException("schedule String error:" + scheduleString);
			}
			if(day != -1 && hour == -1) {
				//日付が指定されているのに時間が未指定
				throw new RuntimeException("schedule String error:" + scheduleString);
			}
			if(month != -1 && (day == -1 && week == -1)) {
				//月が指定されているのに日付または週が未指定
				throw new RuntimeException("schedule String error:" + scheduleString);
			}
		}
		
		public Date getNextTimestamp() {
			return getNextTimestamp(DateManager.DM.newDate());
		}
		
		Date getNextTimestamp(Date d) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			Calendar ret = Calendar.getInstance();
			ret.setTime(d);
			ret.set(Calendar.MILLISECOND, 0);
			ret.set(Calendar.SECOND, 0);
			ret.set(Calendar.MINUTE, minute);
			if(hour == -1) {
				//毎時実行
				int inc = 0;
				if(minute <= cal.get(Calendar.MINUTE)) {
					inc++;
				}
				ret.add(Calendar.HOUR, inc);
				return ret.getTime();
			}
			ret.set(Calendar.HOUR_OF_DAY, hour);
			SimpleDateFormat sdf1 = new SimpleDateFormat("HHmm");
			String cur = sdf1.format(d);
			String next = sdf1.format(ret.getTime());
			if(next.compareTo(cur) <= 0) {
				ret.add(Calendar.DAY_OF_MONTH, 1);
			}
			if(day == -1 && week == -1){
				//毎日実行
				return ret.getTime();
			}
			while(true) {
				if(isValid(ret)) {
					return ret.getTime();
				}
				ret.add(Calendar.DAY_OF_MONTH, 1);
			}
		}
		
		boolean isValid(Calendar ret) {
			if(day == 99) {
				if(!isLastOfMonthDay(ret)) {
					return false;
				}
			} else if(day != -1 && day != ret.get(Calendar.DAY_OF_MONTH)) {
				return false;
			}
			if(week != -1 && weekList[week] != ret.get(Calendar.DAY_OF_WEEK)) {
				return false;
			}
			if(month != -1 && month != (ret.get(Calendar.MONTH) + 1)) {
				return false;
			}
			return true;
		}

		boolean isLastOfMonthDay(Calendar ret) {
			ret.add(Calendar.DAY_OF_MONTH, 1);
			try {
				return (ret.get(Calendar.DAY_OF_MONTH) == 1);
			} finally {
				ret.add(Calendar.DAY_OF_MONTH, -1);
			}
		}

		static String TOKEN = "(\\*|[0-9]*)";
		static String TOKEN2 = "(\\*|[0-9]*|x)";
		static Pattern p = Pattern.compile(TOKEN + "\\s" + TOKEN + "\\s" + TOKEN2 + "\\s" + TOKEN + "\\s" + TOKEN);
		static int[] weekList = new int[]{
			Calendar.SUNDAY,
			Calendar.MONDAY,
			Calendar.TUESDAY,
			Calendar.WEDNESDAY,
			Calendar.THURSDAY,
			Calendar.FRIDAY,
			Calendar.SATURDAY,
		};
	}
}
