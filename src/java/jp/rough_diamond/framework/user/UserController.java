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
 * ユーザー情報の管理を行う
 */
public abstract class UserController {
	/**
	 * ユーザーオブジェクトを取得する
	 * @return ユーザーオブジェクト
	 */
	abstract public User getUser();
	
	/**
	 * ユーザーオブジェクトを設定する
	 * @param user ユーザー
	 */
	abstract public void setUser(User user);
	
    private LinkedHashSet<UserChangeListener> listeners = new LinkedHashSet<UserChangeListener>();

    /**
     * ユーザー変更通知リスナを登録する
     * @param listener
     */
    public void addListener(UserChangeListener listener) {
        listeners.add(listener);
    }
    
    /**
     * ユーザー変更通知リスナを抹消する
     * @param listener
     */
    public void removeListener(UserChangeListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * ユーザー変更通知リスナへ変更要求を通知する
     * @param oldUser
     * @param newUser
     */
    protected void notify(Object oldUser, Object newUser) {
        for(UserChangeListener listener : listeners) {
            listener.notify(oldUser, newUser);
        }
    }
    
    /**
     * ユーザー管理オブジェクトを取得する
     * @return ユーザー管理オブジェクト
     */
	public static UserController getController() {
		return (UserController)DIContainerFactory.getDIContainer().getObject("userController");
	}
}
