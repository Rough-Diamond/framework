/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.lang;

import java.io.Serializable;

/**
 * �͈�
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
     * �w�肳�ꂽ�l���ő�l/�ŏ��l�Ƃ���Range�I�u�W�F�N�g�𐶐�
     * @param value
     * @return
     */
    public static <T extends Comparable<T>> Range<T> getRange(T value) {
        return new Range<T>(value, value);
    }
    
    /**
     * @return max ��߂��܂��B
     */
    public T getMax() {
        return max;
    }

    /**
     * @return min ��߂��܂��B
     */
    public T getMin() {
        return min;
    }
    
    /**
     * min��max����v���Ă���ꍇ��true
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
     * �͈͂�������
     * @return
     */
    public boolean isInfiniteBigness() {
        return (getMax() == null);
    }
    
    /**
     * �͈͂�������
     * @return
     */
    public boolean isInfiniteSmallness() {
        return (getMin() == null);
    }
    
    /**
     * �͈͂�����
     * @return
     */
    public boolean isInfiniteRange() {
        return (isInfiniteSmallness() && isInfiniteBigness());
    }
    
    /**
     * �l���ő�l�ƈ�v���Ă���ꍇ��true
     * @param value
     * @return
     */
    public boolean isMaximum(T value) {
        return (!isInfiniteBigness() && compare(getMax(), value) == 0);
    }
    
    /**
     * �l���ŏ��l�ƈ�v���Ă���ꍇ��true
     * @param value
     * @return
     */
    public boolean isMinimum(T value) {
        return (!isInfiniteSmallness() && compare(getMin(), value) == 0);
    }
    
    /**
     * �l���͈͂Ɋ܂܂�Ă���ꍇ��true
     * @param value
     * @return
     */
    public boolean isIncludion(T value) {
        return (!isOver(value) && !isLess(value));
    }
    
    /**
     * �l���͈͂𒴂��Ă���ꍇ��true
     * @param value
     * @return
     */
    public boolean isOver(T value) {
        return (!isInfiniteBigness() && compare(getMax(), value) < 0);
    }

    /**
     * �l���͈͂ɖ����Ȃ��ꍇ��true
     * @param value
     * @return
     */
    public boolean isLess(T value) {
        return (!isInfiniteSmallness() && compare(getMin(), value) > 0);
    }
    
    /**
     * �w�肳�ꂽ�͈͂��S�Ĕ͈͂Ɋ܂܂�Ă���ꍇ��true��ԋp����
     * null���w�肳�ꂽ�ꍇ��false��ԋp����
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
     * �w�肳�ꂽ�͈͂��A�ꕔ���ł��d�Ȃ�ꍇ��true��ԋp����
     * null���w�肳�ꂽ�ꍇ��false��ԋp����
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
     * �������r����
     * �\�[�g�͈ȉ��̃\�[�g���ɏ]��
     *
     *�@�P�D�������`������͍ő�Ƃ݂Ȃ�
     *�@�Q�D�ŏ��l���ׂď������ق���������
     *�@�@�@�i�������A�� < min�j
     *�@�R�D�ő�l���ׂď������ق���������
     *�@�@�@�i�������Amax < ���j
     *�@�S�D�ŏ��l���ő�l������̏ꍇ�͓���Ƃ݂Ȃ�
     * 
     * @param o
     * @return
     */
    public int compareTo(Range<T> o) {
        //�ǂ��炩�������͈̔͂ł���ꍇ
        if (this.isInfiniteRange()) {
            return o.isInfiniteRange() ? 0 : 1;
        } else if (o.isInfiniteRange()) {
            return -1;
        }      
        //�������ł����Ė�����łȂ��̏ꍇ
        if (this.isInfiniteSmallness()) {
            return o.isInfiniteSmallness() ? compare(this.getMax(), o.getMax()) : -1;
        }
        //������ł����Ė������łȂ��ꍇ
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
        //������ł��Ȃ��������ł��Ȃ��ꍇ
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
