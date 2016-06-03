/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.extractor;

import java.io.Serializable;

/**
 * �\�[�g����
 */
@SuppressWarnings("unchecked")
abstract public class Order<T extends Value> implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * �\�[�g�Ώۃv���p�e�B��
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
	 * �\�[�g�����𐶐�����
	 * @param propertyName �v���p�e�B�� null�̏ꍇ��NullPointerException�𑗏o����
     * @param target    �v���p�e�B��ێ����Ă���G���e�B�e�B�N���X
     * @param aliase    �G���e�B�e�B�̕ʖ�
     * @deprecated
	 */
    @Deprecated
	public Order(String propertyName, Class target, String aliase) {
		propertyName.getClass();		//NOP NullPointerException�𑗏o���������ꍇ
		this.propertyName = propertyName;
        this.target = target;
        this.aliase = aliase;
        this.label = (T)new Property(target, aliase, propertyName);
	}

	/**
	 * �\�[�g�����𐶐�����
	 * @param propertyName �v���p�e�B�� null�̏ꍇ��NullPointerException�𑗏o����
     * @param target    �v���p�e�B��ێ����Ă���G���e�B�e�B�N���X
     * @param aliase    �G���e�B�e�B�̕ʖ�
	 */
	@SuppressWarnings("deprecation")
	protected Order(T label) {
    	label.getClass();		//NOP NullPointerException�𑗏o���������ꍇ
		this.propertyName = null;
        this.target = null;
        this.aliase = null;
        this.label = label;
	}

	/**
	 * Asc�I�u�W�F�N�g���擾����
	 * @param propertyName	�v���p�e�B��
	 * @return Asc�I�u�W�F�N�g
     * @deprecated
	 */
    @Deprecated
	public static Order<Property> asc(String propertyName) {
		return asc(propertyName, null, null);
	}
	
    /**
     * Asc�I�u�W�F�N�g���擾����
     * @param propertyName  �v���p�e�B��
     * @param target    �v���p�e�B��ێ����Ă���G���e�B�e�B�N���X
     * @param aliase    �G���e�B�e�B�̕ʖ�
     * @return Asc�I�u�W�F�N�g
     * @deprecated
	 */
    @Deprecated
    public static Order<Property> asc(String propertyName, Class target, String aliase) {
        return new Asc(propertyName, target, aliase);
    }
    
    /**
     * Asc�I�u�W�F�N�g���擾����
     * @param label  ���x��
     * @return Asc�I�u�W�F�N�g
     */
    public static <T extends Value> Order<T> asc(T label) {
        return new Asc(label);
    }
    
	/**
	 * Desc�I�u�W�F�g���擾����
	 * @param propertyName �v���p�e�B��
	 * @return	Desc�I�u�W�F�N�g
     * @deprecated
	 */
    @Deprecated
	public static Order<Property> desc(String propertyName) {
		return desc(propertyName, null, null);
	}

    /**
     * Desc�I�u�W�F�g���擾����
     * @param propertyName �v���p�e�B��
     * @param target    �v���p�e�B��ێ����Ă���G���e�B�e�B�N���X
     * @param aliase    �G���e�B�e�B�̕ʖ�
     * @return  Desc�I�u�W�F�N�g
     * @deprecated
	 */
    @Deprecated
    public static Order<Property> desc(String propertyName, Class target, String aliase) {
        return new Desc(propertyName, target, aliase);
    }

    /**
     * Desc�I�u�W�F�N�g���擾����
     * @param label  ���x��
     * @return Desc�I�u�W�F�N�g
     */
    public static <T extends Value> Order<T> desc(T label) {
        return new Desc(label);
    }
}
