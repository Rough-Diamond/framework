/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.es;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.ExceptionPayload;
import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.transport.NullPayload;

import jp.rough_diamond.commons.util.mule.transformer.JAXBElementToObject;
import jp.rough_diamond.framework.service.Service;

public class ServiceFinder implements
		jp.rough_diamond.framework.service.ServiceFinder {
	private final static Log log = LogFactory.getLog(ServiceFinder.class);
	
	@SuppressWarnings("unchecked")
	public <T extends Service> T getService(Class<T> cl, Class<? extends T> defaultClass) {
		if(!isTarget(defaultClass)) {
			return null;
		}
		T ret = (T)Proxy.newProxyInstance(defaultClass.getClassLoader(), new Class[]{defaultClass}, getInvocationHandler());
		return ret;
	}
	
	InvocationHandler getInvocationHandler() {
		return ih;
	}
	
	InvocationHandler ih = new InvocationHandler() {
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			try {
				init();
				String serviceName = method.getName();
				ServiceConnecter sc = method.getAnnotation(ServiceConnecter.class);
				String inboundName = null;
				if(sc != null) {
					serviceName = sc.serviceName();
					inboundName = sc.inboundName().trim();
					if(inboundName.length() == 0) {
						inboundName = null;
					}
				}
				MuleMessage msg = makeMessage(args);
				msg.addProperties(ServiceBus.getInstance().popParameters());
				if(log.isDebugEnabled()) {
					log.debug(String.format("serviceName=%s, inboundName=%s", serviceName, inboundName));
				}
				MuleMessage result = client.sendDirect(serviceName, inboundName, msg);
				ExceptionPayload exceptionPayload = result.getExceptionPayload();
				if(exceptionPayload != null) {
					throwException(method, exceptionPayload.getException());
				}
				Object returnPayload = result.getPayload();
				if(returnPayload instanceof NullPayload) {
					return null;
				} else if(method.getReturnType() != Void.TYPE) {
					JAXBElementToObject transformer = new JAXBElementToObject();
					Type t = method.getGenericReturnType();
					return transformer.transform(returnPayload, t);
//					if(t instanceof Class) {
//						return transformer.transform(returnPayload, method.getReturnType());
//					} else {
//						ParameterizedType pt = (ParameterizedType)t;
//						return transformer.transform(returnPayload, method.getReturnType(), (Class<?>)pt.getActualTypeArguments()[0]);
//					}
				} else {
					return null;
				}
			} catch(MuleException e) {
				throw new RuntimeException(e);
			}
		}

		void throwException(Method method, Throwable exception) throws Throwable {
			Class<?> exceptionType = exception.getClass();
			Class<?>[] methodExceptionTypes = method.getExceptionTypes();
			for(Class<?> methodExceptionType : methodExceptionTypes) {
				if(methodExceptionType.isAssignableFrom(exceptionType)) {
					throw exception;
				}
			}
			throw new RuntimeException(exception);
		}
	};
	
	private MuleClient client;
	synchronized void init() {
		try {
			if(client == null) {
				MuleContext context = ServiceBus.getInstance().getContext();
				client = new MuleClient(context);
			}
		} catch(MuleException e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("deprecation")
	static MuleMessage makeMessage(Object[] args) {
		if(args == null || args.length == 0) {
			return new DefaultMuleMessage(new Object[0]);
		} else if(args.length != 1) {
			return new DefaultMuleMessage(args);
		} else if(args[0] instanceof MuleMessage) {
			return (MuleMessage)args[0];
		} else {
			return new DefaultMuleMessage(args[0]);
		}
	}

	static <T extends Service> boolean isTarget(Class<T> cl) {
		if(EnterpriseService.class.isAssignableFrom(cl) && cl.isInterface()) {
			return true;
		}
		return false;
	}
}
