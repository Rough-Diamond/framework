/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.transaction;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AnnotationTransactionManager extends TransactionManager {
	private final static Log log = LogFactory.getLog(AnnotationTransactionManager.class);
	
	public Object invoke(MethodInvocation arg0) throws Throwable {
		log.debug(">>AnnotationTransactionManager#invoke");
		try {
			Method m = arg0.getMethod();
			TransactionAttribute ta = m.getAnnotation(TransactionAttribute.class);
			TransactionAttributeType tat = (ta == null) 
					? TransactionAttributeType.REQUIRED : ta.value();
			return tat.doIt(arg0);
		} finally {
			log.debug("<<AnnotationTransactionManager#invoke");
		}
	}
}
