/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.service;

import jp.rough_diamond.commons.di.DIContainerFactory;

/**
 * ServiceLocatorの実装インタフェース
 */
@SuppressWarnings("unchecked")
abstract public class ServiceLocatorLogic {
	public final static String DEFAULT_SERVICE_LOCATOR_NAME = "jp.rough_diamond.framework.service.SimpleServiceLocatorLogic";

	public static ServiceLocatorLogic getServiceLocatorLogic() {
		ServiceLocatorLogic logic = (ServiceLocatorLogic)DIContainerFactory.getDIContainer(
												).getObject(ServiceLocator.SERVICE_LOCATOR_KEY);
    	if(logic == null) {
    		logic = getDefaultLogic();
    	}
    	return logic;
	}
	
    static ServiceLocatorLogic defaultFinder = null;
	static ServiceLocatorLogic getDefaultLogic() {
		if(defaultFinder == null) {
			createLogic();
		}
		return defaultFinder;
	}
	
	private synchronized static void createLogic() {
		try {
			if(defaultFinder == null) {
				Class cl = Class.forName(DEFAULT_SERVICE_LOCATOR_NAME);
				defaultFinder = (ServiceLocatorLogic)cl.newInstance();
			}
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * サービスの取得
	 * @param <T>
	 * @param cl
	 * @param defaultClass
	 * @return
	 */
	abstract public <T extends Service> T getService(Class<T> cl, Class<? extends T> defaultClass);
}
