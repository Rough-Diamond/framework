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
 * �����̐ݒ���R���g���[������}�l�[�W��
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
			log.debug("���t�R���g���[���N���X�F" + DM.getClass().getName());
		}
	}

	protected long baseTimeStamp = System.currentTimeMillis();
	protected long settingTimeStamp = baseTimeStamp;
	/**
	 * ���ݎ������w�肳�ꂽ�����ƔF������
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
	 * ���݂̃^�C���X�^���v���w�肳�ꂽ�^�C���X�^���v�ƔF������
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
			System.out.println(String.format("%s��%s�Ƃ��ď������܂��B", new Date(this.settingTimeStamp), new Date(this.baseTimeStamp)));
		}
	}
	
	/**
	 * �^�C���X�^���v��ԋp����BSystem.currentTimeMillis�̑��
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
	 * Date�I�u�W�F�N�g��ԋp����Bnew Date()�̑��
	 * @return
	 */
	public Date newDate() {
		return new Date(currentTimeMillis());
	}
	
	/**
	 * Calendar�I�u�W�F�N�g��ԋp����BCalendar.getInstance()�̑��
	 * @return
	 */
	public Calendar newCalendar() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(currentTimeMillis());
		return cal;
	}
}
