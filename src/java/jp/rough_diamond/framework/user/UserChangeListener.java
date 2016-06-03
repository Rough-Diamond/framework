/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.user;

/**
 * ���[�U�[�I�u�W�F�N�g�̕ύX��ʈς����Observer
 */
public interface UserChangeListener {
    /**
     * Notify
     * @param   oldUser �����[�U�[
     * @param   newUser �V���[�U�[
     */
    public void notify(Object oldUser, Object newUser);
}
