/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.lang;

import jp.rough_diamond.commons.lang.ArrayUtils;
import junit.framework.TestCase;

public class ArrayUtilsTest extends TestCase {
    public void testSeparateReturnNullWhen1stParameterIsNull() {
        String[][] ret = ArrayUtils.separateArray((String[])null, 100);
        assertNull("返却値がnullではありません。", ret);
    }

    public void testSeparateThrownExceptionWhen2ndParameterIsZeroOrNegativeValue() {
        String[] param = new String[]{"a", "b", "c", "d", "e"};
        try {
            ArrayUtils.separateArray(param, 0);
            fail("例外がスローされていません。");
        } catch(IllegalArgumentException e) {

        }
        try {
            ArrayUtils.separateArray(param, -1);
            fail("例外がスローされていません。");
        } catch(IllegalArgumentException e) {

        }
    }

    public void testSeparateRetunrnEmptyValueWhenParameterIsEmptyArray() {
        String[][] ret = ArrayUtils.separateArray(new String[0], 100);
        assertEquals("retのサイズが誤まっています。", ret.length, 0);
    }

    public void testSeparateNormalCase() {
        String[] param = new String[]{"a", "b", "c", "d", "e"};
        String[][] ret = ArrayUtils.separateArray(param, param.length);
        assertEquals("retのサイズが誤まっています。", ret.length, 1);
        ret = ArrayUtils.separateArray(param, 1);
        assertEquals("retのサイズが誤まっています。", ret.length, 5);
        ret = ArrayUtils.separateArray(param, 4);
        assertEquals("retのサイズが誤まっています。", ret.length, 2);
        ret = ArrayUtils.separateArray(param, 100);
        assertEquals("retのサイズが誤まっています。", ret.length, 1);
    }

    public void testSeparateNormalCaseWithInt() {
        int[][] ret = ArrayUtils.separateArray(new int[]{1, 2, 3}, 3);
        assertEquals("retのサイズが誤まっています。", ret.length, 1);
    }
}
