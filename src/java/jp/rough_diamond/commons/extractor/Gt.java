/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.extractor;

/**
 * Gt(＞）Condition
 */
@SuppressWarnings("unchecked")
public class Gt<T extends Value> extends ValueHoldingCondition<T> {
	private static final long serialVersionUID = 1L;

	/**
	 * Gt(＞）Conditionを生成する
	 * @param propertyName	プロパティ名 nullの場合はNullPointerExceptionを送出する
     * @param target    プロパティを保持しているエンティティクラス
     * @param aliase    エンティティの別名
	 * @param value		値 nullの場合はNullPointerExceptionを送出する
	 * @deprecated		Gt(T, Object)の使用を推奨します
	 */
	@Deprecated
	public Gt(String propertyName, Class target, String aliase, Object value) {
		super(propertyName, target, aliase, value);
	}

	/**
	 * Gt(＞）Conditionを生成する
	 * @param label		比較対照ラベル
	 * @param value		値 nullの場合はNullPointerExceptionを送出する
	 */
	public Gt(T label, Object value) {
		super(label, value);
	}
}
