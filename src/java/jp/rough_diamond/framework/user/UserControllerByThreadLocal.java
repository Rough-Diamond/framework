/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.user;

public class UserControllerByThreadLocal extends UserController {
	private static ThreadLocal<User> tl = new ThreadLocal<User>();
	
	public User getUser() {
		return tl.get();
	}

	public void setUser(User user) {
        Object old = getUser();
		tl.set(user);
        if(user != old) {
            //�I�u�W�F�N�g�I�ɓ��l�ł��Q�ƒl�ɕω�������Βʒm����
            //�I�u�W�F�N�g�I�ɓ������ǂ����̔��f��Listener�ɂ䂾�˂�
            notify(old, user);
        }
//        if(old != null) {
//            if(!old.equals(user)) {
//                notify(old, user);
//            }
//        } else if(user != null) {
//            notify(old, user);
//        }
	}
}
