/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.extractor;

/**
 * NotEq�i{@literal <>}�jCondition
 */
@SuppressWarnings("unchecked")
public class NotEq<T extends Value> extends ValueHoldingCondition<T> {
	private static final long serialVersionUID = 1L;

	/**
	 * NotEq({@literal <>}�jCondition�𐶐�����
	 * @param propertyName	�v���p�e�B�� null�̏ꍇ��NullPointerException�𑗏o����
     * @param target    �v���p�e�B��ێ����Ă���G���e�B�e�B�N���X
     * @param aliase    �G���e�B�e�B�̕ʖ�
	 * @param value		�l null�̏ꍇ��NullPointerException�𑗏o����
	 * @deprecated		NotEq(T, Object)�̎g�p�𐄏����܂�
	 */
	@Deprecated
	public NotEq(String propertyName, Class target, String aliase, Object value) {
		super(propertyName, target, aliase, value);
	}

	/**
	 * NotEq({@literal <>}�jCondition�𐶐�����
	 * @param label		��r�Ώƃ��x��
	 * @param value		�l null�̏ꍇ��NullPointerException�𑗏o����
	 */
	public NotEq(T label, Object value) {
		super(label, value);
	}
}
