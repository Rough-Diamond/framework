/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.util;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ClassLoaderへの参照許可を選択できるPropertyUtilsBean
 */
public class ClassLoaderIgnoreablePropertyUtilsBean extends PropertyUtilsBean {
	private final static Log log = LogFactory.getLog(ClassLoaderIgnoreablePropertyUtilsBean.class);

	private final ThreadLocal<Boolean> isClassLoaderPopulate;
	
	/**
	 * ClassLoaderへの参照をデフォルトで許可しないPropertyUtilsBeanを生成します
	 */
	public ClassLoaderIgnoreablePropertyUtilsBean() {
		this(Boolean.FALSE);
	}

	/**
	 * ClassLoaderへの参照を選択するPropertyUtilsBeanを生成します
	 * @param defaultClassLoaderPopulate	trueの場合は通常のPropertyUtilsBeanと同様の振る舞いを行います
	 */
	public ClassLoaderIgnoreablePropertyUtilsBean(final Boolean defaultClassLoaderPopulate) {
		isClassLoaderPopulate = new ThreadLocal<Boolean>() {
			@Override
			protected Boolean initialValue() {
				return defaultClassLoaderPopulate;
			}
		};
	}

	@Override
	public Object getIndexedProperty(Object bean, String name, int index)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		return checkClassLoader(super.getIndexedProperty(bean, name, index));
	}

	@Override
	public Object getMappedProperty(Object bean, String name, String key)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		return checkClassLoader(super.getMappedProperty(bean, name, key));
	}

	@Override
	public Object getSimpleProperty(Object bean, String name)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		return checkClassLoader(super.getSimpleProperty(bean, name));
	}
	
	private Object checkClassLoader(Object returnValue) throws NoSuchMethodException {
		if(returnValue instanceof ClassLoader && !isClassLoaderPopulate.get()) {
			log.debug("ClassLoaderの返却をキャンセルします");
			throw new NoSuchMethodException("ClassLoaderの返却をキャンセルします");
		}
		return returnValue;
	}

	/**
	 * 呼び出したスレッドに対してClassLoaderへの参照の許可/禁止を指定します。
	 * @param b	trueの場合は通常のClassLoaderへの参照を許可します。
	 */
	public Boolean setClassLoaderPopulate(boolean b) {
		Boolean ret = isClassLoaderPopulate.get();
		isClassLoaderPopulate.set(b);
		return ret;
	}
}
