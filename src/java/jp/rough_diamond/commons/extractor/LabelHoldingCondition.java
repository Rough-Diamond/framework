/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.extractor;

/**
 * プロパティ名を保持するコンディション
 */
@SuppressWarnings("unchecked")
public abstract class LabelHoldingCondition<T extends Value> extends Condition<T> {
	private static final long serialVersionUID = 1L;

	/**
	 * プロパティ名
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
	 * プロパティ名を保持するConditionを生成する
	 * @param propertyName	プロパティ名 nullの場合はNullPointerExceptionを送出する
     * @param target    プロパティを保持しているエンティティクラス
     * @param aliase    エンティティの別名
     * @deprecated
	 */
    @Deprecated
	public LabelHoldingCondition(String propertyName, Class target, String aliase) {
    	Property p = new Property(target, aliase, propertyName);
		this.propertyName = p.property;
		propertyName.length();	//NOP nullなら強制的に例外を送出させたいので
        this.target = p.target;
        this.aliase = p.aliase;
        this.label = (T)p;
	}
    
    @SuppressWarnings("deprecation")
	public LabelHoldingCondition(T label) {
    	this.label = label;
    	if(label instanceof Property) {
    		Property p = (Property)label;
			this.propertyName = p.property;
			propertyName.length();	//NOP nullなら強制的に例外を送出させたいので
	        this.target = p.target;
	        this.aliase = p.aliase;
    	} else {
    		this.propertyName = null;
	        this.target = null;
	        this.aliase = null;
    	}
    }
}
