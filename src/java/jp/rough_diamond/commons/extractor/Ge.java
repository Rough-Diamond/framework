/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.extractor;

/**
 * Ge(�����jCondition
 */
@SuppressWarnings("unchecked")
public class Ge<T extends Value> extends ValueHoldingCondition<T> {
	private static final long serialVersionUID = 1L;
	/**
	 * Ge(�����jCondition�𐶐�����
	 * @param propertyName	�v���p�e�B�� null�̏ꍇ��NullPointerException�𑗏o����
     * @param target    �v���p�e�B��ێ����Ă���G���e�B�e�B�N���X
     * @param aliase    �G���e�B�e�B�̕ʖ�
	 * @param value		�l null�̏ꍇ��NullPointerException�𑗏o����
	 * @deprecated		Ge(T, Object)�̎g�p�𐄏����܂�
	 */
	@Deprecated
	public Ge(String propertyName, Class target, String aliase, Object value) {
		super(propertyName, target, aliase, value);
	}

	/**
	 * Ge(�����jCondition�𐶐�����
	 * @param label		��r�Ώƃ��x��
	 * @param value		�l null�̏ꍇ��NullPointerException�𑗏o����
	 */
	public Ge(T label, Object value) {
		super(label, value);
	}
}
