/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.util;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 */
public class EmptyDateManager extends DateManager {
	private final static Log log = LogFactory.getLog(EmptyDateManager.class);
	
	@Override
	public Long currentTimeMillis() {
		return System.currentTimeMillis();
	}

	@Override
	public Calendar newCalendar() {
		return Calendar.getInstance();
	}

	@Override
	public Date newDate() {
		return new Date();
	}

	@Override
	public void setTimeMillis(long time) {
		log.debug("İ’è—v‹‚ğó‚¯‚Ü‚µ‚½‚ª–³‹‚µ‚Ü‚·");
	}
}
