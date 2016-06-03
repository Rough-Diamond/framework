/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.user;

import java.util.LinkedHashSet;

import jp.rough_diamond.commons.di.DIContainerFactory;

/**
 * ���[�U�[���̊Ǘ����s��
 */
public abstract class UserController {
	/**
	 * ���[�U�[�I�u�W�F�N�g���擾����
	 * @return ���[�U�[�I�u�W�F�N�g
	 */
	abstract public User getUser();
	
	/**
	 * ���[�U�[�I�u�W�F�N�g��ݒ肷��
	 * @param user ���[�U�[
	 */
	abstract public void setUser(User user);
	
    private LinkedHashSet<UserChangeListener> listeners = new LinkedHashSet<UserChangeListener>();

    /**
     * ���[�U�[�ύX�ʒm���X�i��o�^����
     * @param listener
     */
    public void addListener(UserChangeListener listener) {
        listeners.add(listener);
    }
    
    /**
     * ���[�U�[�ύX�ʒm���X�i�𖕏�����
     * @param listener
     */
    public void removeListener(UserChangeListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * ���[�U�[�ύX�ʒm���X�i�֕ύX�v����ʒm����
     * @param oldUser
     * @param newUser
     */
    protected void notify(Object oldUser, Object newUser) {
        for(UserChangeListener listener : listeners) {
            listener.notify(oldUser, newUser);
        }
    }
    
    /**
     * ���[�U�[�Ǘ��I�u�W�F�N�g���擾����
     * @return ���[�U�[�Ǘ��I�u�W�F�N�g
     */
	public static UserController getController() {
		return (UserController)DIContainerFactory.getDIContainer().getObject("userController");
	}
}
