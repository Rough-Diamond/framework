/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.extractor;

/**
 * �O���G���e�B�e�B�ƌ������邽�߂̏���
 * �O���G���e�B�e�B�̃v���p�e�B�𒊏o�����ɉ�����ꍇ�́A
 * �{�I�u�W�F�N�g�ɏ�����ǉ����Ă������� 
 */
public class Join extends CombineCondition<Property> {
	private static final long serialVersionUID = 1L;

	/**
	 * �G���e�B�e�B��
	 */
	public final String entityName;
	
	/**
	 * �O���G���e�B�e�B�ƌ������邽�߂̏����𐶐�����
	 * @param entityName �G���e�B�e�B�� null�̏ꍇ��NullPointerException�𑗏o����
	 */
	public Join(String entityName) {
		super();
		entityName.getClass();	//NOP null�Ȃ狭���I�ɗ�O�𑗏o���������̂�
		this.entityName = entityName;
	}
}
