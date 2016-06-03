/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.service;

/**
 * �T�[�r�X���擾���邽�߂̃C���^�t�F�[�X
 */
public interface ServiceFinder {
	/**
	 * �T�[�r�X���擾����
	 * @param <T>				�ԋp����C���X�^���X�̃^�C�v
	 * @param cl				�T�[�r�X�̊�_�ƂȂ�C���X�^���X�̃^�C�v
	 * @param defaultClass		DI�R���e�i�ɐݒ肪�����ꍇ�Ɏ��̉�����C���X�^���X�̃^�C�v	
     * @return      �T�[�r�X 
	 */
    public <T extends Service> T getService(Class<T> cl, Class<? extends T> defaultClass);
}
