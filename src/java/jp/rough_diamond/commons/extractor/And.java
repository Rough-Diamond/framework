/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.extractor;

import java.util.Collection;

/**
 * And������\��Condition
 */
public class And<T extends Value> extends CombineCondition<T> {
	private static final long serialVersionUID = 1L;

	/**aaa
	 * And�I�u�W�F�N�g�𐶐�����
	 * ��������钆�g�͊k�Ƃ���
	 */
	public And() {
		super();
	}
	
	/**
	 * And�I�u�W�F�N�g�𐶐�����
	 * @param conditions	Condition�Q nulll�̏ꍇ��NullPointerException���X���[����
	 */
	public And(Collection<Condition<T>> conditions) {
		super(conditions);
	}
}
