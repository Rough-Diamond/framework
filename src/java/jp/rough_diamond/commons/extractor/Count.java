/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.extractor;

/**
 * �J�E���g
 */
public class Count extends SummaryFunction {
	public final boolean distinct;
	public Count(Value value) {
		this(value, false);
	}
	
	public Count() {
		this(new Property("*"));
	}
	
	public Count(Value value, boolean distinct) {
		super(value);
		this.distinct = distinct;
	}
}
