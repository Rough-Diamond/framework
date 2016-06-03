/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.testing;

import java.io.Serializable;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.CallbackException;
import org.hibernate.EntityMode;
import org.hibernate.Interceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;

@SuppressWarnings("unchecked")
class InterceptorImpl implements Interceptor {
    private final static Log log = LogFactory.getLog(InterceptorImpl.class);

    private Interceptor org;
    InterceptorImpl(Interceptor org) {
        this.org = org;
    }
    
    public boolean onLoad(Object arg0, Serializable arg1, Object[] arg2,
            String[] arg3, Type[] arg4) throws CallbackException {
//        log.debug("onLoad:" + arg0);
        return org.onLoad(arg0, arg1, arg2, arg3, arg4);
    }

    public boolean onFlushDirty(Object arg0, Serializable arg1, Object[] arg2,
            Object[] arg3, String[] arg4, Type[] arg5) throws CallbackException {
    	if(log.isDebugEnabled()) {
    		log.debug("更新します。:" + arg0.getClass().getName());
    	}
        DBInitializer.addModifiedClasses(arg0.getClass());
        return org.onFlushDirty(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    public boolean onSave(Object arg0, Serializable arg1, Object[] arg2,
            String[] arg3, Type[] arg4) throws CallbackException {
    	if(log.isDebugEnabled()) {
    		log.debug("永続化します。:" + arg0.getClass().getName());
    	}
        DBInitializer.addModifiedClasses(arg0.getClass());
        return org.onSave(arg0, arg1, arg2, arg3, arg4);
    }

    public void onDelete(Object arg0, Serializable arg1, Object[] arg2,
            String[] arg3, Type[] arg4) throws CallbackException {
    	if(log.isDebugEnabled()) {
    		log.debug("削除します。:" + arg0.getClass().getName());
    	}
        DBInitializer.addModifiedClasses(arg0.getClass());
        org.onDelete(arg0, arg1, arg2, arg3, arg4);
    }

    public void preFlush(Iterator arg0) throws CallbackException {
//        log.debug("preFlush:" + arg0);
        org.preFlush(arg0);
    }

    public void postFlush(Iterator arg0) throws CallbackException {
//        log.debug("postFlush:" + arg0);
        org.postFlush(arg0);
    }

    public Boolean isTransient(Object arg0) {
//        log.debug("isTransient:" + arg0);
        return org.isTransient(arg0);
    }

    public int[] findDirty(Object arg0, Serializable arg1, Object[] arg2,
            Object[] arg3, String[] arg4, Type[] arg5) {
//        log.debug("findDirty:" + arg0);
        return org.findDirty(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    public Object instantiate(String arg0, EntityMode arg1, Serializable arg2)
            throws CallbackException {
//        log.debug("instantiate:" + arg0);
        return org.instantiate(arg0, arg1, arg2);
    }

    public String getEntityName(Object arg0) throws CallbackException {
//        log.debug("getEntityName:" + arg0);
        return org.getEntityName(arg0);
    }

    public Object getEntity(String arg0, Serializable arg1)
            throws CallbackException {
//        log.debug("getEntity:" + arg0);
        return org.getEntity(arg0, arg1);
    }

    public void afterTransactionBegin(Transaction arg0) {
//        log.debug("afterTransactionBegin:" + arg0);
        org.afterTransactionBegin(arg0);
    }

    public void beforeTransactionCompletion(Transaction arg0) {
//        log.debug("beforeTransactionCompletion:" + arg0);
        org.beforeTransactionCompletion(arg0);
    }

    public void afterTransactionCompletion(Transaction arg0) {
//        log.debug("afterTransactionCompletion:" + arg0);
        org.afterTransactionCompletion(arg0);
    }

	public void onCollectionRecreate(Object arg0, Serializable arg1)
			throws CallbackException {
//      log.debug("onCollectionRecreate:" + arg0);
	}

	public void onCollectionRemove(Object arg0, Serializable arg1)
			throws CallbackException {
//      log.debug("onCollectionRemove:" + arg0 + ":" + arg1);
	}

	public void onCollectionUpdate(Object arg0, Serializable arg1)
			throws CallbackException {
//      log.debug("onCollectionUpdate:" + arg0);
	}

	public String onPrepareStatement(String arg0) {
//      log.debug("onPrepareStatement:" + arg0);
		return org.onPrepareStatement(arg0);
	}
}
