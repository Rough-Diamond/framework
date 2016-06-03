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
 * ClassLoader�ւ̎Q�Ƌ���I���ł���PropertyUtilsBean
 */
public class ClassLoaderIgnoreablePropertyUtilsBean extends PropertyUtilsBean {
	private final static Log log = LogFactory.getLog(ClassLoaderIgnoreablePropertyUtilsBean.class);

	private final ThreadLocal<Boolean> isClassLoaderPopulate;
	
	/**
	 * ClassLoader�ւ̎Q�Ƃ��f�t�H���g�ŋ����Ȃ�PropertyUtilsBean�𐶐����܂�
	 */
	public ClassLoaderIgnoreablePropertyUtilsBean() {
		this(Boolean.FALSE);
	}

	/**
	 * ClassLoader�ւ̎Q�Ƃ�I������PropertyUtilsBean�𐶐����܂�
	 * @param defaultClassLoaderPopulate	true�̏ꍇ�͒ʏ��PropertyUtilsBean�Ɠ��l�̐U�镑�����s���܂�
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
			log.debug("ClassLoader�̕ԋp���L�����Z�����܂�");
			throw new NoSuchMethodException("ClassLoader�̕ԋp���L�����Z�����܂�");
		}
		return returnValue;
	}

	/**
	 * �Ăяo�����X���b�h�ɑ΂���ClassLoader�ւ̎Q�Ƃ̋���/�֎~���w�肵�܂��B
	 * @param b	true�̏ꍇ�͒ʏ��ClassLoader�ւ̎Q�Ƃ������܂��B
	 */
	public Boolean setClassLoaderPopulate(boolean b) {
		Boolean ret = isClassLoaderPopulate.get();
		isClassLoaderPopulate.set(b);
		return ret;
	}
}
