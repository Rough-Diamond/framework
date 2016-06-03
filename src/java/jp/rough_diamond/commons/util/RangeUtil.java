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
 * �͈̓`�F�b�N���s�����[�e�B���e�B
**/
public class RangeUtil {
    /**
     * ���t�͈̓`�F�b�N
    **/
    public static boolean isIncludeDay(Date start, Date end, Date target) {
        start = initDate(start).getTime();
        Calendar endCal = initDate(end);
        endCal.add(Calendar.DAY_OF_MONTH, 1);
        end = endCal.getTime();
        return isIncludeDate(start, end, target);
    }
    
    /**
     * �����͈̓`�F�b�N
    **/
    public static boolean isIncludeMinute(Date start, Date end, Date target) {
        start = initMinute(start).getTime();
        Calendar endCal = initMinute(end);
        endCal.add(Calendar.MINUTE, 1);
        end = endCal.getTime();
        target = initMinute(target).getTime();
        return isIncludeDate(start, end, target);
    }

    private static boolean isIncludeDate(Date start, Date end, Date target) {
        return (
                (start.compareTo(target) <= 0) &&
                (target.compareTo(end) < 0));
    }
    
    private static Calendar initDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        initDate(cal);
        return cal;
    }
    
    private static void initDate(Calendar cal) {
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }
    
    private static Calendar initMinute(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        initMinute(cal);
        return cal;
    }
    
    private static void initMinute(Calendar cal) {
        cal.set(Calendar.YEAR, 0);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }
}