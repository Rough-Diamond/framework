/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.transaction;

import java.util.HashSet;
import java.util.Set;

import jp.rough_diamond.commons.service.BasicService;

import org.aopalliance.intercept.MethodInterceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

abstract public class TransactionInterceptor implements MethodInterceptor {
    private final static Log log = LogFactory.getLog(TransactionInterceptor.class);

    protected boolean rollbackOnly = Boolean.FALSE;
    
    public boolean isRollbackOnly() {
        return rollbackOnly; 
    }

	public void setRollbackOnly() {
        log.debug("ロールバックおんりぃ～");
        rollbackOnly = Boolean.TRUE;
    }
	
	protected void removeTemporary() {
		Set<Class<?>> types = shapeUp(TransactionManager.getModifiedTemporaryTypes());
		for(Class<?> type : types) {
			BasicService.getService().deleteAll(type);
		}
	}
	
	static Set<Class<?>> shapeUp(Set<Class<?>> set) {
		//XXX ちょいとダサいがまぁそんなにテンポラリーが多いとは思わないし良いか？
		Set<Class<?>> tmp = new HashSet<Class<?>>(set);
		for(Class<?> cl : set) {
			Class<?> tmpCl = cl.getSuperclass();
			tmpCl = (tmpCl == null) ? Object.class : tmpCl;
			while(tmpCl != Object.class) {
				if(tmp.contains(tmpCl)) {
					tmp.remove(cl);
					break;
				}
				tmpCl = tmpCl.getSuperclass();
			}
		}
		return tmp;
	}
}
