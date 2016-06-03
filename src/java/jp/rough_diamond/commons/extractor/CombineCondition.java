/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.extractor;

import java.util.ArrayList;
import java.util.Collection;

/**
 * ������Condition����������Condition
 */
public abstract class CombineCondition<T extends Value> extends Condition<T> {
	private static final long serialVersionUID = 1L;
	private final Collection<Condition<T>> conditions;

	/**
	 * CombineCondition�I�u�W�F�N�g�𐶐�����
	 * ��������钆�g�͊k�Ƃ���
	 */
	public CombineCondition() {
		conditions = new ArrayList<Condition<T>>();
	}
	
	/**
	 * CombineCondition�I�u�W�F�N�g�𐶐�����
	 * @param conditions	Condition�Q nulll�̏ꍇ��NullPointerException���X���[����
	 */
	public CombineCondition(Collection<Condition<T>> conditions) {
		conditions.size();	//NOP null�Ȃ狭���I�ɗ�O�𑗏o���������̂�
		this.conditions = conditions;
	}

	/**
	 * ��������Condition�̃C���e�[�^��ԋp����
	 * @return ��������Condition��Iterator
	 */
	public Collection<Condition<T>> getConditions() {
		return conditions;
	}
	
	/**
	 * ������������𖖔��ɒǉ�����
	 * @param condition	����
	 */
	public CombineCondition<T> add(Condition<T> condition) {
		conditions.add(condition);
		return this;
	}

	/**
	 * �������������擾����
	 * @return ������������
	 */
	public int getSize() {
		return conditions.size();
	}
}
