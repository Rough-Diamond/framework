/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.extractor;

/**
 * 昇順ソートを行うOrder
 */
@SuppressWarnings("unchecked")
public class Asc<T extends Value> extends Order<T> {
	private static final long serialVersionUID = 1L;

	/**
	 * 昇順ソートを行うOrderを生成する
	 * @param propertyName プロパティ名 nullの場合はNullPointerExceptionを送出する
     * @param target    プロパティを保持しているエンティティクラス
     * @param aliase    エンティティの別名
     * @deprecated
	 */
    @Deprecated
	public Asc(String propertyName, Class target, String aliase) {
		super(propertyName, target, aliase);
	}

    /**
	 * 昇順ソートを行うOrderを生成する
     * @param label	ラベル
     */
	public Asc(T label) {
		super(label);
	}
}
