/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jp.rough_diamond.commons.di.DIContainerFactory;

/**
 * ServiceLocatorのデフォルト実装
 */
@SuppressWarnings("unchecked")
public class SimpleServiceLocatorLogic extends ServiceLocatorLogic {
	private final static Log log = LogFactory.getLog(SimpleServiceLocatorLogic.class);
	Map<Class<?>, Service> serviceMap = new HashMap<Class<?>, Service>();

	private final static String DEFAULT_SERVICE_FINDER_NAME = "jp.rough_diamond.framework.transaction.ServiceFinder";

	@Override
	public <T extends Service> T getService(Class<T> cl, Class<? extends T> defaultClass) {
		T service = (T)serviceMap.get(cl);
		if(service == null) {
			findService(cl, defaultClass);
			service = (T)serviceMap.get(cl);
		}
		if(service == null) {
			log.warn("サービスが取得できません。");
			throw new RuntimeException();
		}
		return service;
	}

    protected synchronized <T extends Service> void findService(Class<T> cl, Class<? extends T> defaultClass) {
    	if(serviceMap.get(cl) == null) {
        	T service = getFinder().getService(cl, defaultClass);
        	serviceMap.put(cl, service);
    	}
    }

    protected ServiceFinder getFinder() {
    	ServiceFinder finder = (ServiceFinder)DIContainerFactory.getDIContainer().getObject(ServiceLocator.SERVICE_FINDER_KEY);
    	if(finder == null) {
    		finder = getDefaultFinder();
    	}
    	return finder;
    }
    
    private static ServiceFinder defaultFinder = null;
	static ServiceFinder getDefaultFinder() {
		if(defaultFinder == null) {
			createFinder();
		}
		return defaultFinder;
	}
	
	static synchronized void createFinder() {
		try {
			if(defaultFinder == null) {
				Class cl = Class.forName(DEFAULT_SERVICE_FINDER_NAME);
				defaultFinder = (ServiceFinder)cl.newInstance();
			}
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
