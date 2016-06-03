/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.util.jobmonitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * cronちっくなジョブモニター
 */
public class CronJobMonitor implements JobMonitor {
	Timer timer;
	final List<CrontabTask> tasks;
	final long period;
	public CronJobMonitor(List<CrontabTask> tasks) {
		this(tasks, 10000);	//10秒間隔
	}

	public CronJobMonitor(List<CrontabTask> tasks, long period) {
		this.tasks = new ArrayList<CrontabTask>(tasks);
		this.period = period;
	}
	
	@Override
	public synchronized void start() {
		if(timer != null) {
			return;
		}
		timer = new Timer(true);
		timer.schedule(new TimerTask(){
			@Override
			public void run() {
				for(CrontabTask task : tasks) {
					task.executeWhenPastNextTimestamp();
				}
			}
		}, 0, period);
	}

	@Override
	public synchronized void stop() {
		if(timer == null) {
			return;
		}
		timer.cancel();
		timer = null;
	}
}
