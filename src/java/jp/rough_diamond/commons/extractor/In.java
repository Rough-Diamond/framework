/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.extractor;

import java.util.Collection;

/**
 * In(��܁jCondition
 */
@SuppressWarnings("unchecked")
public class In<T extends Value> extends ValueHoldingCondition<T> {
	private static final long serialVersionUID = 1L;

	/**
	 * In(��܁jCondition�𐶐�����
	 * @param propertyName	�v���p�e�B�� null�̏ꍇ��NullPointerException�𑗏o����
     * @param target    �v���p�e�B��ێ����Ă���G���e�B�e�B�N���X
     * @param aliase    �G���e�B�e�B�̕ʖ�
	 * @param values		�l�Q null�̏ꍇ��NullPointerException�𑗏o����
	 * @deprecated		In(T, Object...)�̎g�p�𐄏����܂�
	 */
	@Deprecated
	public In(String propertyName, Class target, String aliase, Collection values) {
		super(propertyName, target, aliase, values);
	}

	/**
	 * In(��܁jCondition�𐶐�����
	 * @param label		��r�Ώƃ��x��
	 * @param value		�l null�̏ꍇ��NullPointerException�𑗏o����
	 */
	public In(T label, Collection value) {
		super(label, value);
	}
}
