/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.transaction;

import org.aopalliance.intercept.MethodInvocation;

public class NopInterceptor extends TransactionInterceptor {

	public Object invoke(MethodInvocation arg0) throws Throwable {
		return arg0.proceed();
	}
}
