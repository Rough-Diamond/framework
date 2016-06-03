/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.di;

/**
 * DI�R���e�i�̎������B������C���^�t�F�[�X�ł��B<br />
 */
public interface DIContainer {
	/**
	 * DI�����I�u�W�F�N�g���擾���܂��B
	 * @param key	�L�[
	 * @return DI�I�u�W�F�N�g
	 */
	public Object getObject(Object key);
	
	/**
	 * ����DIContainer�����b�v���Ă�����̂�ԋp���܂��B
	 * @return ���ۂ�DIContainer
	 */
	public Object getSource();

	/**
	 * DI�����I�u�W�F�N�g���擾����
	 * @param type	DI�����I�u�W�F�N�g�̃^�C�v
	 * @param key	�L�[
	 * @return		DI�����I�u�W�F�N�g
	 */
	public <T> T getObject(Class<T> type, Object key);
	
	/**
	 * ����DIContainer�����b�v���Ă�����̂�ԋp����
	 * @param type ���̂̃^�C�v
	 * @return DI�R���e�i����
	 */
	public <T> T getSource(Class<T> type);
}
