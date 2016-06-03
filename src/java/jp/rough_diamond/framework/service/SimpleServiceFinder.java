/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.service;

import jp.rough_diamond.commons.di.DIContainerFactory;

public class SimpleServiceFinder implements ServiceFinder {
	@Override
	@SuppressWarnings("unchecked")
	public <T extends Service> T getService(Class<T> cl, Class<? extends T> defaultClass) {
		try {
			T ret = (T)DIContainerFactory.getDIContainer().getObject(cl.getName());
			if(ret == null) {
				ret = defaultClass.newInstance();
			}
			return ret;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}
