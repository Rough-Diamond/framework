/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.extractor;

/**
 * IsNull������\��Condition 
 */
@SuppressWarnings("unchecked")
public class IsNull<T extends Value> extends LabelHoldingCondition<T> {
	private static final long serialVersionUID = 1L;

	/**
	 * IsNull������\��Condition�𐶐�����
	 * @param propertyName �v���p�e�B�� null�̏ꍇ��NullPointerException�𑗏o����
     * @param target    �v���p�e�B��ێ����Ă���G���e�B�e�B�N���X
     * @param aliase    �G���e�B�e�B�̕ʖ�
	 * @deprecated		IsNull(Value)�̎g�p�𐄏����܂�
	 */
	@Deprecated
	public IsNull(String propertyName, Class target, String aliase) {
		super(propertyName, target, aliase);
	}

	/**
	 * IsNull������\��Condition�𐶐�����
	 * @param label		��r�Ώƃ��x��
	 * @param value		�l null�̏ꍇ��NullPointerException�𑗏o����
	 */
	public IsNull(T label) {
		super(label);
	}
}
