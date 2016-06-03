/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.user;

/**
 * ユーザーオブジェクトの変更を通委されるObserver
 */
public interface UserChangeListener {
    /**
     * Notify
     * @param   oldUser 旧ユーザー
     * @param   newUser 新ユーザー
     */
    public void notify(Object oldUser, Object newUser);
}
