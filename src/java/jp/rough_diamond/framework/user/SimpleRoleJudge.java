/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.framework.user;

/**
 * ��ɃA�N�Z�X��������RoleJudge
 */
public class SimpleRoleJudge implements RoleJudge {
	@Override
	public boolean hasRole(String... role) {
		return true;
	}
}
