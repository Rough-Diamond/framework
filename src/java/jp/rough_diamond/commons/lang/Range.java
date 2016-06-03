/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.lang;

import java.io.Serializable;

/**
 * 範囲
 */
public class Range<T extends Comparable<T>> 
        implements Serializable, Comparable<Range<T>> {
    private static final long serialVersionUID = 1L;

    private final T min;
    private final T max;
    
    public Range(T min, T max) {
        this.min = min;
        this.max = max;
        
        if(min != null && max != null) {
            if(min.compareTo(max) > 0) {
                throw new RuntimeException();
            }
        }
    }

    /**
     * 指定された値を最大値/最小値とするRangeオブジェクトを生成
     * @param value
     * @return
     */
    public static <T extends Comparable<T>> Range<T> getRange(T value) {
        return new Range<T>(value, value);
    }
    
    /**
     * @return max を戻します。
     */
    public T getMax() {
        return max;
    }

    /**
     * @return min を戻します。
     */
    public T getMin() {
        return min;
    }
    
    /**
     * minとmaxが一致している場合にtrue
     * @return
     */
    public boolean isMuch() {
        if(getMin() != null) {
            return (getMin().equals(getMax()));
        } else {
            return false;
        }
    }

    /**
     * 範囲が無限大
     * @return
     */
    public boolean isInfiniteBigness() {
        return (getMax() == null);
    }
    
    /**
     * 範囲が無限小
     * @return
     */
    public boolean isInfiniteSmallness() {
        return (getMin() == null);
    }
    
    /**
     * 範囲が無限
     * @return
     */
    public boolean isInfiniteRange() {
        return (isInfiniteSmallness() && isInfiniteBigness());
    }
    
    /**
     * 値が最大値と一致している場合にtrue
     * @param value
     * @return
     */
    public boolean isMaximum(T value) {
        return (!isInfiniteBigness() && compare(getMax(), value) == 0);
    }
    
    /**
     * 値が最小値と一致している場合にtrue
     * @param value
     * @return
     */
    public boolean isMinimum(T value) {
        return (!isInfiniteSmallness() && compare(getMin(), value) == 0);
    }
    
    /**
     * 値が範囲に含まれている場合にtrue
     * @param value
     * @return
     */
    public boolean isIncludion(T value) {
        return (!isOver(value) && !isLess(value));
    }
    
    /**
     * 値が範囲を超えている場合にtrue
     * @param value
     * @return
     */
    public boolean isOver(T value) {
        return (!isInfiniteBigness() && compare(getMax(), value) < 0);
    }

    /**
     * 値が範囲に満たない場合にtrue
     * @param value
     * @return
     */
    public boolean isLess(T value) {
        return (!isInfiniteSmallness() && compare(getMin(), value) > 0);
    }
    
    /**
     * 指定された範囲が全て範囲に含まれている場合にtrueを返却する
     * nullが指定された場合はfalseを返却する
     * @param range
     * @return
     */
    public boolean isIncludion(Range<T> range) {
    	if(range ==null) {
    		return false;
    	}
    	return isIncludion(range.min) && isIncludion(range.max);
    }

    /**
     * 指定された範囲が、一部分でも重なる場合にtrueを返却する
     * nullが指定された場合はfalseを返却する
     * @param range
     * @return
     */
    public boolean isOverlap(Range<T> range) {
    	if(range ==null) {
    		return false;
    	}
    	return range.isIncludion(min) || range.isIncludion(max) || isIncludion(range.min);
    }
    
    /**
     * 順序を比較する
     * ソートは以下のソート順に従う
     *
     *　１．無限小～無限大は最大とみなす
     *　２．最小値を比べて小さいほうが小さい
     *　　　（ただし、∞ < min）
     *　３．最大値を比べて小さいほうが小さい
     *　　　（ただし、max < ∞）
     *　４．最小値も最大値も同一の場合は同一とみなす
     * 
     * @param o
     * @return
     */
    public int compareTo(Range<T> o) {
        //どちらかが無限の範囲である場合
        if (this.isInfiniteRange()) {
            return o.isInfiniteRange() ? 0 : 1;
        } else if (o.isInfiniteRange()) {
            return -1;
        }      
        //無限小であって無限大でないの場合
        if (this.isInfiniteSmallness()) {
            return o.isInfiniteSmallness() ? compare(this.getMax(), o.getMax()) : -1;
        }
        //無限大であって無限小でない場合
        if (this.isInfiniteBigness()) {
            if (o.isInfiniteSmallness()) {
                return 1;
            } else {
                int tmpMin = compare(this.getMin(), o.getMin());
                if (tmpMin == 0) {
                    return o.isInfiniteBigness() ? 0 : 1;
                }
                return tmpMin;
            }
        }
        //無限大でもなく無限小でもない場合
        if (o.isInfiniteSmallness()) {
            return 1;
        } else {
            int tmpMin = compare(this.getMin(), o.getMin());
            if (tmpMin == 0) {
                if (o.isInfiniteBigness()) {
                    return -1;
                }
                return compare(this.getMax(), o.getMax());
            }
            return tmpMin;
        }
    }

	@Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
    	if(o == null) {
    		return false;
    	}
    	if(!(o instanceof Range)) {
    		return false;
    	}
    	
    	return (compareTo((Range)o) == 0);
    }
    
	@Override
	public int hashCode() {
		return min.hashCode() + max.hashCode();
	}
	
    private int compare(T thisProperty, T otherProperty) {
        if(isNeedReverse(thisProperty.getClass(), otherProperty.getClass())) {
            return otherProperty.compareTo(thisProperty) * -1;
        } else {
            return thisProperty.compareTo(otherProperty);
        }
    }
    
    @SuppressWarnings("unchecked")
    private boolean isNeedReverse(Class thisClass, Class otherClass) {
        return otherClass.isAssignableFrom(thisClass);
    }
}
