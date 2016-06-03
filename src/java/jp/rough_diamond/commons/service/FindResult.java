/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.service;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * �����ƃI�u�W�F�N�g�Q��ێ�����ValueObject
 */
public class FindResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * �I�u�W�F�N�g�Q
     */
    public final List<T>    list;
    
	/**
     * ����
     */
    public final long       count;

    /**
     * �I�u�W�F�N�g�𐶐�����
     * @param list  �I�u�W�F�N�g�Q
     * @param count ����
     */
    public FindResult(List<T> list, long count) {
        this.list = Collections.unmodifiableList(list);
        this.count = count;
    }

    /**
     * �I�u�W�F�N�g�Q��ԋp����
     * @return
     */
    public List<T> getList() {
		return list;
	}

    /**
     * ������ԋp����
     * @return
     */
	public long getCount() {
		return count;
	}
}
