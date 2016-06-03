/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.extractor;

/**
 *�@���o����v���p�e�B���
 */
@SuppressWarnings("unchecked")
public class Property implements Value {
	public final Class target;
    public final String aliase;
    public final String property;

    /**
     * ���o�l���w�肷��
     * @param target
     * @param aliase
     * @param property
     */
    public Property(Class target, String aliase, String property) {
        this.target = target;
        this.aliase = aliase;
        this.property = property;
    }

    /**
     * ���o�l���w�肷��
     * @param target
     * @param aliase
     * @param property
     */
    public Property(String property) {
    	this(null, null, property);
    }
}
