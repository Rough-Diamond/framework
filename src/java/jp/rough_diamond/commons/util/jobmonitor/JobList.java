/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.util.jobmonitor;

import java.util.ArrayList;
import java.util.List;

/**
 * ï°êîÇÃJobÇèWçáÇµÇΩÇ‡ÇÃ 
 */
public class JobList implements Job {
	private final List<Job> jobList;
	public JobList(List<Job> list) {
		jobList = new ArrayList<Job>(list); 
	}
	
	@Override
	public void run() {
		for(Job job : jobList) {
			job.run();
		}
	}
}
