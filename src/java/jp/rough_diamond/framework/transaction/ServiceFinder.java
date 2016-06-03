/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.transaction;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import jp.rough_diamond.commons.di.DIContainerFactory;
import jp.rough_diamond.framework.service.Service;
import jp.rough_diamond.framework.service.SimpleServiceFinder;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;

/**
 * ＤＢアクセスサービスを生成するFinder
 * @version 1.0
 */
public class ServiceFinder extends SimpleServiceFinder {
	@SuppressWarnings("unchecked")
	public <T extends Service> T getService(Class<T> cl, Class<? extends T> defaultClass) {
		init();
		try {
			T base = super.getService(cl, defaultClass);
			ProxyFactory pf = new ProxyFactory(base);
			pf.addAdvisor(interceptor);
			pf.setOptimize(true);
			return (T)pf.getProxy();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	
	private static boolean isInit = false;
	private static void init() {
		if(!isInit) {
			init2();
		}
	}

	private synchronized static void init2() {
		if(!isInit) {
			mi = (MethodInterceptor)DIContainerFactory.getDIContainer().getObject("transactionInterceptor");
			interceptor = new ServiceAdvisor();
			interceptor.setAdvice(mi);
			isInit = true;
		}
	}
	
	static MethodInterceptor mi;
	static StaticMethodMatcherPointcutAdvisor interceptor;
	
	private final static class ServiceAdvisor extends StaticMethodMatcherPointcutAdvisor {
		public final static long serialVersionUID = -1L;
		
		@SuppressWarnings("unchecked")
		public boolean matches(Method arg0, Class arg1) {
			if(Service.class.isAssignableFrom(arg1) && 
					(arg0.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC) {
				return true;
			} else {
				return false;
			}
		}
	}
}
