/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.extractor;

/**
 *
 */
public class Case implements Value {
	public final Object condition;
	public final Value thenValue;
	public final Value elseValue;
	
	public Case(Value condition, Value thenValue, Value elseValue) {
		this.condition = condition;
		this.thenValue = thenValue;
		this.elseValue = elseValue;
	}

	public Case(Condition<? extends Value> condition, Value thenValue, Value elseValue) {
		this.condition = condition;
		this.thenValue = thenValue;
		this.elseValue = elseValue;
	}
}
