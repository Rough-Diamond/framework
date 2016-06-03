/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.extractor;

/**
 * �v���p�e�B����ێ�����R���f�B�V����
 */
@SuppressWarnings("unchecked")
public abstract class LabelHoldingCondition<T extends Value> extends Condition<T> {
	private static final long serialVersionUID = 1L;

	/**
	 * �v���p�e�B��
	 * @deprecated �����폜����\��ł�
	 */
	@Deprecated
	public final String propertyName;
	
    /**
     * �^�[�Q�b�g�N���X
	 * @deprecated �����폜����\��ł�
	 */
	@Deprecated
    public final Class target;
    
    /**
     * �G�C���A�X
	 * @deprecated �����폜����\��ł�
	 */
	@Deprecated
    public final String aliase;
    
	/**
	 * ���x��
	 */
	public final T label;
	
	/**
	 * �v���p�e�B����ێ�����Condition�𐶐�����
	 * @param propertyName	�v���p�e�B�� null�̏ꍇ��NullPointerException�𑗏o����
     * @param target    �v���p�e�B��ێ����Ă���G���e�B�e�B�N���X
     * @param aliase    �G���e�B�e�B�̕ʖ�
     * @deprecated
	 */
    @Deprecated
	public LabelHoldingCondition(String propertyName, Class target, String aliase) {
    	Property p = new Property(target, aliase, propertyName);
		this.propertyName = p.property;
		propertyName.length();	//NOP null�Ȃ狭���I�ɗ�O�𑗏o���������̂�
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
			propertyName.length();	//NOP null�Ȃ狭���I�ɗ�O�𑗏o���������̂�
	        this.target = p.target;
	        this.aliase = p.aliase;
    	} else {
    		this.propertyName = null;
	        this.target = null;
	        this.aliase = null;
    	}
    }
}
