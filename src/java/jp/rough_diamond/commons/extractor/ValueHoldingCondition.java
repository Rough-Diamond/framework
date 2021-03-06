/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.extractor;

/**
 * プロパティ名と値を保持するCondition
 */
@SuppressWarnings("unchecked")
abstract public class ValueHoldingCondition<T extends Value> extends LabelHoldingCondition<T> {
	private static final long serialVersionUID = 1L;
	/**
	 * 値
	 */
	public final Object value;
	
	/**
	 * プロパティ名と値を保持するConditionを生成する
	 * @param propertyName	プロパティ名 nullの場合はNullPointerExceptionを送出する
     * @param target    プロパティを保持しているエンティティクラス
     * @param aliase    エンティティの別名
	 * @param value		値 nullの場合はNullPointerExceptionを送出する
	 * @deprecated		ValueHoldingCondition(Property, Object)の使用を推奨します
	 */
	@Deprecated
	public ValueHoldingCondition(String propertyName, Class target, String aliase, Object value) {
		super(propertyName, target, aliase);
		value.getClass();		//NOP	NullPointerExceptionを送出させたい場合
		this.value = value;
	}
	
	public ValueHoldingCondition(T label, Object value) {
		super(label);
		value.getClass();		//NOP	NullPointerExceptionを送出させたい場合
		this.value = value;
	}
}
