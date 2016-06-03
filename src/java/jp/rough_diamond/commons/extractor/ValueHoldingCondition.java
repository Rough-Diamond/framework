/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.extractor;

/**
 * �v���p�e�B���ƒl��ێ�����Condition
 */
@SuppressWarnings("unchecked")
abstract public class ValueHoldingCondition<T extends Value> extends LabelHoldingCondition<T> {
	private static final long serialVersionUID = 1L;
	/**
	 * �l
	 */
	public final Object value;
	
	/**
	 * �v���p�e�B���ƒl��ێ�����Condition�𐶐�����
	 * @param propertyName	�v���p�e�B�� null�̏ꍇ��NullPointerException�𑗏o����
     * @param target    �v���p�e�B��ێ����Ă���G���e�B�e�B�N���X
     * @param aliase    �G���e�B�e�B�̕ʖ�
	 * @param value		�l null�̏ꍇ��NullPointerException�𑗏o����
	 * @deprecated		ValueHoldingCondition(Property, Object)�̎g�p�𐄏����܂�
	 */
	@Deprecated
	public ValueHoldingCondition(String propertyName, Class target, String aliase, Object value) {
		super(propertyName, target, aliase);
		value.getClass();		//NOP	NullPointerException�𑗏o���������ꍇ
		this.value = value;
	}
	
	public ValueHoldingCondition(T label, Object value) {
		super(label);
		value.getClass();		//NOP	NullPointerException�𑗏o���������ꍇ
		this.value = value;
	}
}
