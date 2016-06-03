/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.util;

import java.util.Calendar;
import java.util.Date;

/**
 * CalendarオブジェクトをJSTLより使用しやすくするためのラッパークラス
**/
public class DateHelper {
    public DateHelper(Date date) {
        cal = Calendar.getInstance();
        cal.setTime(date);
    }

    public DateHelper(Calendar cal) {
        this.cal = (Calendar)cal.clone();
    }

    public int getDayOfMonth() {
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public int getWeekInt() {
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    private Calendar cal;
}