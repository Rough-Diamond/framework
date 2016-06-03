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

import jp.rough_diamond.commons.di.DIContainerFactory;

/**
 * 日時の設定をコントロールするマネージャ
 */
public class DateManager {
	private final static Log log = LogFactory.getLog(DateManager.class);
	
	public final static DateManager DM;
	static {
		DateManager dm = (DateManager)DIContainerFactory.getDIContainer().getObject("DateManager");
		if(dm == null) {
			DM = new DateManager();
		} else {
			DM = dm;
		}
		if(log.isDebugEnabled()) {
			log.debug("日付コントロールクラス：" + DM.getClass().getName());
		}
	}

	protected long baseTimeStamp = System.currentTimeMillis();
	protected long settingTimeStamp = baseTimeStamp;
	/**
	 * 現在時刻を指定された時刻と認識する
	 * @param d
	 */
	public void setDate(Date d) {
		if(d == null) {
			setTimeMillis(-1L);
		} else {
			setTimeMillis(d.getTime());
		}
	}

	/**
	 * 現在のタイムスタンプを指定されたタイムスタンプと認識する
	 * @param time
	 */
	public void setTimeMillis(long time) {
		if(time == -1L) {
			this.baseTimeStamp = System.currentTimeMillis();
			this.settingTimeStamp = this.baseTimeStamp;
		} else {
			this.baseTimeStamp = time;
			this.settingTimeStamp = System.currentTimeMillis();
		}
		if(log.isDebugEnabled()) {
			System.out.println(String.format("%sを%sとして処理します。", new Date(this.settingTimeStamp), new Date(this.baseTimeStamp)));
		}
	}
	
	/**
	 * タイムスタンプを返却する。System.currentTimeMillisの代替
	 * @return
	 */
	public Long currentTimeMillis() {
		if(baseTimeStamp == settingTimeStamp) {
			return System.currentTimeMillis();
		}
		Long sub = System.currentTimeMillis() - this.settingTimeStamp;
		return baseTimeStamp + sub;
	}
	
	/**
	 * Dateオブジェクトを返却する。new Date()の代替
	 * @return
	 */
	public Date newDate() {
		return new Date(currentTimeMillis());
	}
	
	/**
	 * Calendarオブジェクトを返却する。Calendar.getInstance()の代替
	 * @return
	 */
	public Calendar newCalendar() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(currentTimeMillis());
		return cal;
	}
}
