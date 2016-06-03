/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.lang;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * 配列ユーティリティ
 */
public class ArrayUtils extends org.apache.commons.lang.ArrayUtils {
    /**
     * 指定された配列を指定されたサイズで分割して返却する
     * @param <T>
     * @param array
     * @param size
     * @return
     */
    public static int[][] separateArray(int[] array, int size) {
        return (int[][])separateArray2(array, size);
    }

    /**
     * 指定された配列を指定されたサイズで分割して返却する
     * @param <T>
     * @param array
     * @param size
     * @return
     */
    public static long[][] separateArray(long[] array, int size) {
        return (long[][])separateArray2(array, size);
    }

    /**
     * 指定された配列を指定されたサイズで分割して返却する
     * @param <T>
     * @param array
     * @param size
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T[][] separateArray(T[] array, int size) {
        return (T[][])separateArray2(array, size);
    }

    @SuppressWarnings("unchecked")
    private static Object separateArray2(Object array, int size) {
        if(array == null) {
            return null;
        }
        if(size <= 0) {
            throw new IllegalArgumentException();
        }
        Class arrayType = array.getClass();
        Class cl = arrayType.getComponentType();
        int arrayLength = Array.getLength(array);
        int count = arrayLength / size;
        if(arrayLength % size != 0) {
            count++;
        }
        List<Object> ret = new ArrayList<Object>();
        for(int i = 0 ; i < count ; i++) {
            int startIndex = (i * size);
            int lastIndex = Math.min(startIndex + size, arrayLength);
            Object tmp = Array.newInstance(cl, lastIndex - startIndex);
            for(int j = startIndex ; j < lastIndex ; j++) {
                Array.set(tmp, j - startIndex, Array.get(array, j));
            }
            ret.add(tmp);
        }
        Object[] retArray = (Object[])Array.newInstance(arrayType, count);
        return ret.toArray(retArray);
    }
}
