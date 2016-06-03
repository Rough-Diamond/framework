/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.service.hibernate;

import java.io.Serializable;
import java.util.Iterator;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.EntityMode;
import org.hibernate.Interceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;

/**
 * HibernateのInterceptorを拡張するInterceptor
 * すべての処理を指定されたInterceptorへ委譲する。
 * 拡張するメソッドだけオーバーライドすればよい。
 * 引数省略時はEmptyInterceotorへ委譲する。
 */
public class InterceptorDecoratorBase implements Interceptor {
	protected final Interceptor base;
	public InterceptorDecoratorBase() {
		this(EmptyInterceptor.INSTANCE);
	}
	
	public InterceptorDecoratorBase(Interceptor base) {
		this.base = base;
	}
	
	@Override
	public void afterTransactionBegin(Transaction arg0) {
		base.afterTransactionBegin(arg0);
	}

	@Override
	public void afterTransactionCompletion(Transaction arg0) {
		base.afterTransactionCompletion(arg0);
	}

	@Override
	public void beforeTransactionCompletion(Transaction arg0) {
		base.beforeTransactionCompletion(arg0);
	}

	@Override
	public int[] findDirty(Object arg0, Serializable arg1, Object[] arg2,
			Object[] arg3, String[] arg4, Type[] arg5) {
		return base.findDirty(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	@Override
	public Object getEntity(String arg0, Serializable arg1)
			throws CallbackException {
		return base.getEntity(arg0, arg1);
	}

	@Override
	public String getEntityName(Object arg0) throws CallbackException {
		return base.getEntityName(arg0);
	}

	@Override
	public Object instantiate(String arg0, EntityMode arg1, Serializable arg2)
			throws CallbackException {
		return base.instantiate(arg0, arg1, arg2);
	}

	@Override
	public Boolean isTransient(Object arg0) {
		return base.isTransient(arg0);
	}

	@Override
	public void onCollectionRecreate(Object arg0, Serializable arg1)
			throws CallbackException {
		base.onCollectionRecreate(arg0, arg1);
	}

	@Override
	public void onCollectionRemove(Object arg0, Serializable arg1)
			throws CallbackException {
		base.onCollectionRemove(arg0, arg1);
	}

	@Override
	public void onCollectionUpdate(Object arg0, Serializable arg1)
			throws CallbackException {
		base.onCollectionUpdate(arg0, arg1);
	}

	@Override
	public void onDelete(Object arg0, Serializable arg1, Object[] arg2,
			String[] arg3, Type[] arg4) throws CallbackException {
		base.onDelete(arg0, arg1, arg2, arg3, arg4);
	}

	@Override
	public boolean onFlushDirty(Object arg0, Serializable arg1, Object[] arg2,
			Object[] arg3, String[] arg4, Type[] arg5) throws CallbackException {
		return base.onFlushDirty(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	@Override
	public boolean onLoad(Object arg0, Serializable arg1, Object[] arg2,
			String[] arg3, Type[] arg4) throws CallbackException {
		return base.onLoad(arg0, arg1, arg2, arg3, arg4);
	}

	@Override
	public String onPrepareStatement(String arg0) {
		return base.onPrepareStatement(arg0);
	}

	@Override
	public boolean onSave(Object arg0, Serializable arg1, Object[] arg2,
			String[] arg3, Type[] arg4) throws CallbackException {
		return base.onSave(arg0, arg1, arg2, arg3, arg4);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void postFlush(Iterator arg0) throws CallbackException {
		base.postFlush(arg0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void preFlush(Iterator arg0) throws CallbackException {
		base.preFlush(arg0);
	}
}
