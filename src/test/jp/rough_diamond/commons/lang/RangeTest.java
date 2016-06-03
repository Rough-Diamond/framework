/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.lang;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import jp.rough_diamond.commons.lang.Range;
import junit.framework.TestCase;

public class RangeTest extends TestCase {
    public void test両方の指定があるケースでの包含チェックが行えること() throws Exception {
        Range<Integer> range = new Range<Integer>(0, 100);
        assertFalse("返却値が誤っています。", range.isIncludion(-1));
        assertTrue("返却値が誤っています。", range.isIncludion(0));
        assertTrue("返却値が誤っています。", range.isIncludion(100));
        assertFalse("返却値が誤っています。", range.isIncludion(101));
    }

    public void test最小値のみ指定があるケースでの包含チェックが行えること() throws Exception {
        Range<Integer> range = new Range<Integer>(0, null);
        assertFalse("返却値が誤っています。", range.isIncludion(-1));
        assertTrue("返却値が誤っています。", range.isIncludion(0));
        assertTrue("返却値が誤っています。", range.isIncludion(100));
        assertTrue("返却値が誤っています。", range.isIncludion(101));
    }

    public void test最大値のみ指定があるケースでの包含チェックが行えること() throws Exception {
        Range<Integer> range = new Range<Integer>(null, 100);
        assertTrue("返却値が誤っています。", range.isIncludion(-1));
        assertTrue("返却値が誤っています。", range.isIncludion(0));
        assertTrue("返却値が誤っています。", range.isIncludion(100));
        assertFalse("返却値が誤っています。", range.isIncludion(101));
    }

    public void test無期限のケースでの包含チェックが行えること() throws Exception {
        Range<Integer> range = new Range<Integer>(null, null);
        assertTrue("返却値が誤っています。", range.isIncludion(-1));
        assertTrue("返却値が誤っています。", range.isIncludion(0));
        assertTrue("返却値が誤っています。", range.isIncludion(100));
        assertTrue("返却値が誤っています。", range.isIncludion(101));
    }
    
    public void test突っ込むオブジェクトが異なるケースで比較が正しく行われること() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date d1 = sdf.parse("20060101");
        Date d2 = new Timestamp(sdf.parse("20060102").getTime());
        Date d3 = sdf.parse("20060103");
        
        Range<Date> r1 = Range.getRange(d1);
        Range<Date> r2 = Range.getRange(d2);
        Range<Date> r3 = Range.getRange(d3);

        List<Range<Date>> list = new ArrayList<Range<Date>>();
        list.add(r1);
        list.add(r2);
        list.add(r3);
        Collections.shuffle(list);
        
        Collections.sort(list);
        
        assertEquals("ソートされていません。", list.get(0), r1);
        assertEquals("ソートされていません。", list.get(1), r2);
        assertEquals("ソートされていません。", list.get(2), r3);
    }
    
    public void test正しくソートが行えること() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/DD");
        Date d3 = sdf.parse("05/03");
        Date d5 = sdf.parse("05/05");
        Date d10 = sdf.parse("05/10");
        
        Range<Date> r1 = new Range<Date>(null, d3);
        Range<Date> r2 = new Range<Date>(null, d5);
        Range<Date> r3 = new Range<Date>(d3, d3);
        Range<Date> r4 = new Range<Date>(d3, d5);
        Range<Date> r5 = new Range<Date>(d3, d10);
        Range<Date> r6 = new Range<Date>(d3, null);
        Range<Date> r7 = new Range<Date>(d5, d5);
        Range<Date> r8 = new Range<Date>(null, null);
        
        List<Range<Date>> list = new ArrayList<Range<Date>>();
        list.add(r4);
        list.add(r1);
        list.add(r8);
        list.add(r3);
        list.add(r2);
        list.add(r6);
        list.add(r5);
        list.add(r7);
        
        Collections.sort(list);
        
        assertEquals("正しくソートされていない１", list.get(0), r1);
        assertEquals("正しくソートされていない２", list.get(1), r2);
        assertEquals("正しくソートされていない３", list.get(2), r3);
        assertEquals("正しくソートされていない４", list.get(3), r4);
        assertEquals("正しくソートされていない５", list.get(4), r5);
        assertEquals("正しくソートされていない６", list.get(5), r6);
        assertEquals("正しくソートされていない７", list.get(6), r7);
        assertEquals("正しくソートされていない８", list.get(7), r8);
        
    }
    
    public void testIsIncludionRange() {
    	Range<Integer> target = new Range<Integer>(3, 8);
    	assertFalse(target.isIncludion((Range<Integer>)null));

    	assertTrue(target.isIncludion(new Range<Integer>(4, 7)));
    	assertTrue(target.isIncludion(new Range<Integer>(3, 8)));

    	assertFalse(target.isIncludion(new Range<Integer>(1, 3)));
    	assertFalse(target.isIncludion(new Range<Integer>(1, 4)));
    	assertFalse(target.isIncludion(new Range<Integer>(7, 10)));
    	assertFalse(target.isIncludion(new Range<Integer>(8, 10)));
    	assertFalse(target.isIncludion(new Range<Integer>(1, 10)));
    }

    public void testIsOverlap() {
    	Range<Integer> target = new Range<Integer>(3, 8);
    	assertFalse(target.isOverlap((Range<Integer>)null));

    	assertTrue(target.isOverlap(new Range<Integer>(4, 7)));
    	assertTrue(target.isOverlap(new Range<Integer>(3, 8)));

    	assertTrue(target.isOverlap(new Range<Integer>(1, 3)));
    	assertTrue(target.isOverlap(new Range<Integer>(1, 4)));
    	assertTrue(target.isOverlap(new Range<Integer>(7, 10)));
    	assertTrue(target.isOverlap(new Range<Integer>(8, 10)));
    	assertTrue(target.isOverlap(new Range<Integer>(1, 10)));

    	assertFalse(target.isOverlap(new Range<Integer>(1, 2)));
    	assertFalse(target.isOverlap(new Range<Integer>(9, 10)));
    }
}
