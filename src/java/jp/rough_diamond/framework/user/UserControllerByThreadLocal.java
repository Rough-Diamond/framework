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
            //オブジェクト的に等値でも参照値に変化があれば通知する
            //オブジェクト的に等価かどうかの判断はListenerにゆだねる
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
