/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.service;

/**
 * �T�[�r�X���擾���郉�b�p�[�N���X
 * ��x�擾�ł����N���X�̓L���b�V�������B
 */
@SuppressWarnings("unchecked")
public class ServiceLocator {
	public final static String SERVICE_LOCATOR_KEY = "serviceLocator"; 
	public final static String SERVICE_FINDER_KEY = "serviceFinder";

	/**
     * �T�[�r�X���擾����
	 * @param <T>	�ԋp����C���X�^���X�̃^�C�v
     * @param cl    �擾�ΏۃT�[�r�X�N���X
     * @return      �T�[�r�X 
     */
	public static <T extends Service> T getService(Class<T> cl) {
		return getService(cl, cl);
    }	
	
	/**
	 * �T�[�r�X���擾����
	 * @param <T>				�ԋp����C���X�^���X�̃^�C�v
	 * @param cl				�T�[�r�X�̊�_�ƂȂ�C���X�^���X�̃^�C�v
	 * @param defaultClassName	DI�R���e�i�ɐݒ肪�����ꍇ�Ɏ��̉�����C���X�^���X�̃^�C�v��	
     * @return      �T�[�r�X 
	 */
	public static <T extends Service> T getService(Class<T> cl, String defaultClassName) {
		Class<? extends T> defaultClass;
		try {
			defaultClass = (Class<? extends T>)Class.forName(defaultClassName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return getService(cl, defaultClass);
	}
	
	/**
	 * �T�[�r�X���擾����
	 * @param <T>				�ԋp����C���X�^���X�̃^�C�v
	 * @param cl				�T�[�r�X�̊�_�ƂȂ�C���X�^���X�̃^�C�v
	 * @param defaultClass		DI�R���e�i�ɐݒ肪�����ꍇ�Ɏ��̉�����C���X�^���X�̃^�C�v	
     * @return      �T�[�r�X 
	 */
	public static <T extends Service> T getService(Class<T> cl, Class<? extends T> defaultClass) {
    	return ServiceLocatorLogic.getServiceLocatorLogic().getService(cl, defaultClass);
	}
}
