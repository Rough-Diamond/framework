/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.transaction.hibernate;

import jp.rough_diamond.framework.transaction.AnnotationTransactionManager;
import jp.rough_diamond.framework.transaction.VersionUnmuchException;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.StaleObjectStateException;

public class HibernateAnotationTransactionManager extends AnnotationTransactionManager {
	private final static Log log = LogFactory.getLog(HibernateAnotationTransactionManager.class);
	
	@Override
	@SuppressWarnings("unchecked")
	public Object invoke(MethodInvocation mi) throws Throwable {
		try {
			return super.invoke(mi);
		} catch(StaleObjectStateException e) {
			Class[] exceptionTypes = mi.getMethod().getExceptionTypes();
			for(Class exceptionType : exceptionTypes) {
				if(VersionUnmuchException.class.isAssignableFrom(exceptionType)) {
					log.debug(e.getMessage(), e);
					throw new VersionUnmuchException(e.getMessage());
				}
			}
			throw e;
		}
	}
}
