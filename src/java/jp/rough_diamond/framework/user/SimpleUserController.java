/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.framework.user;

/**
 * JavaVM���Ƀ��[�U�[���Ǘ����郆�[�U�[�R���g���[���[
 */
public class SimpleUserController extends UserController {
	private User user;
	@Override
	public User getUser() {
		return user;
	}

	@Override
	public void setUser(User user) {
		this.user = user;
	}
}
