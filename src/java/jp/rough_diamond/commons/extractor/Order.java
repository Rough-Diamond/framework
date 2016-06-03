/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.extractor;

import java.io.Serializable;

/**
 * ソート条件
 */
@SuppressWarnings("unchecked")
abstract public class Order<T extends Value> implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ソート対象プロパティ名
	 * @deprecated 将来削除する予定です
	 */
	@Deprecated
	public final String propertyName;
	
    /**
     * ターゲットクラス
	 * @deprecated 将来削除する予定です
	 */
	@Deprecated
    public final Class target;
    
    /**
     * エイリアス
	 * @deprecated 将来削除する予定です
	 */
	@Deprecated
    public final String aliase;
    
	/**
	 * ラベル
	 */
	public final T label;

	/**
	 * ソート条件を生成する
	 * @param propertyName プロパティ名 nullの場合はNullPointerExceptionを送出する
     * @param target    プロパティを保持しているエンティティクラス
     * @param aliase    エンティティの別名
     * @deprecated
	 */
    @Deprecated
	public Order(String propertyName, Class target, String aliase) {
		propertyName.getClass();		//NOP NullPointerExceptionを送出させたい場合
		this.propertyName = propertyName;
        this.target = target;
        this.aliase = aliase;
        this.label = (T)new Property(target, aliase, propertyName);
	}

	/**
	 * ソート条件を生成する
	 * @param propertyName プロパティ名 nullの場合はNullPointerExceptionを送出する
     * @param target    プロパティを保持しているエンティティクラス
     * @param aliase    エンティティの別名
	 */
	@SuppressWarnings("deprecation")
	protected Order(T label) {
    	label.getClass();		//NOP NullPointerExceptionを送出させたい場合
		this.propertyName = null;
        this.target = null;
        this.aliase = null;
        this.label = label;
	}

	/**
	 * Ascオブジェクトを取得する
	 * @param propertyName	プロパティ名
	 * @return Ascオブジェクト
     * @deprecated
	 */
    @Deprecated
	public static Order<Property> asc(String propertyName) {
		return asc(propertyName, null, null);
	}
	
    /**
     * Ascオブジェクトを取得する
     * @param propertyName  プロパティ名
     * @param target    プロパティを保持しているエンティティクラス
     * @param aliase    エンティティの別名
     * @return Ascオブジェクト
     * @deprecated
	 */
    @Deprecated
    public static Order<Property> asc(String propertyName, Class target, String aliase) {
        return new Asc(propertyName, target, aliase);
    }
    
    /**
     * Ascオブジェクトを取得する
     * @param label  ラベル
     * @return Ascオブジェクト
     */
    public static <T extends Value> Order<T> asc(T label) {
        return new Asc(label);
    }
    
	/**
	 * Descオブジェトを取得する
	 * @param propertyName プロパティ名
	 * @return	Descオブジェクト
     * @deprecated
	 */
    @Deprecated
	public static Order<Property> desc(String propertyName) {
		return desc(propertyName, null, null);
	}

    /**
     * Descオブジェトを取得する
     * @param propertyName プロパティ名
     * @param target    プロパティを保持しているエンティティクラス
     * @param aliase    エンティティの別名
     * @return  Descオブジェクト
     * @deprecated
	 */
    @Deprecated
    public static Order<Property> desc(String propertyName, Class target, String aliase) {
        return new Desc(propertyName, target, aliase);
    }

    /**
     * Descオブジェクトを取得する
     * @param label  ラベル
     * @return Descオブジェクト
     */
    public static <T extends Value> Order<T> desc(T label) {
        return new Desc(label);
    }
}
