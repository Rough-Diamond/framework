/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.extractor;

import java.util.Collection;

/**
 * NotIn(��W���jCondition
 */
@SuppressWarnings("unchecked")
public class NotIn<T extends Value> extends ValueHoldingCondition<T> {
	private static final long serialVersionUID = 1L;

	/**
	 * NotIn(��W���jCondition�𐶐�����
	 * @param propertyName	�v���p�e�B�� null�̏ꍇ��NullPointerException�𑗏o����
     * @param target    �v���p�e�B��ێ����Ă���G���e�B�e�B�N���X
     * @param aliase    �G���e�B�e�B�̕ʖ�
	 * @param values		�l�Q null�̏ꍇ��NullPointerException�𑗏o����
	 * @deprecated		NotIn(T, Object...)�̎g�p�𐄏����܂�
	 */
	@Deprecated
	public NotIn(String propertyName, Class target, String aliase, Collection values) {
		super(propertyName, target, aliase, values);
	}

	/**
	 * NotIn(��W���jCondition�𐶐�����
	 * @param label		��r�Ώƃ��x��
	 * @param value		�l null�̏ꍇ��NullPointerException�𑗏o����
	 */
	public NotIn(T label, Collection value) {
		super(label, value);
	}
}
