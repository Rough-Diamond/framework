/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.testing.mock;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import jp.rough_diamond.commons.di.DIContainer;
import jp.rough_diamond.commons.di.DIContainerFactory;
import jp.rough_diamond.framework.es.EnterpriseService;
import jp.rough_diamond.framework.service.Service;
import jp.rough_diamond.framework.service.ServiceLocator;
import jp.rough_diamond.framework.service.ServiceLocatorLogic;
import static org.mockito.Mockito.*;

public class ServiceMocker {
	DIContainer orginalDI;
	ServiceLocatorLogicExt 	sll;
	ServiceLocatorLogic		orginalSLL;
	Set<Class<? extends Service>> mockServices;
	
	public void initialize() {
		orginalSLL = ServiceLocatorLogic.getServiceLocatorLogic(); 
		orginalDI = DIContainerFactory.getDIContainer();
		sll = new ServiceLocatorLogicExt();
		mockServices = new HashSet<Class<? extends Service>>();
		DIContainer di = mock(DIContainer.class, new Answer<Object>(){
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				return invocation.getMethod().invoke(orginalDI, invocation.getArguments());
			}
		});
		when(di.getObject(ServiceLocator.SERVICE_LOCATOR_KEY)).thenAnswer(new Answer<ServiceLocatorLogic>() {
			@Override
			public ServiceLocatorLogic answer(InvocationOnMock invocation) {
				return sll;
			}
		});
		DIContainerFactory.setDIContainer(di);
	}
	
	public void cleanUp() {
		DIContainerFactory.setDIContainer(orginalDI);
	}
	
	public void mockAllEnterprise() {
		addMockService(EnterpriseService.class);
	}
	
	public void setReturnValueBySignature(Object mock, String methodName, Object returnValue, Class<?>... paramTypes) {
		try {
			Method m = mock.getClass().getDeclaredMethod(methodName, paramTypes);
			Object[] params = new Object[paramTypes.length];
			for(int i = 0 ; i < paramTypes.length ; i++) {
				params[i] = any(paramTypes[i]);
			}
			when(m.invoke(mock, params)).thenReturn(returnValue);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Object[][] getArguments(Object mock, String methodName, Class... paramTypes) {
		try {
			if(paramTypes.length == 0) {
				return new Object[0][0];
			}
			Object verificate = verify(mock, atMost(Integer.MAX_VALUE));
			Method m = verificate.getClass().getDeclaredMethod(methodName, paramTypes);
			Object[] params = new Object[paramTypes.length];
			ArgumentCaptor[] captures = new ArgumentCaptor[paramTypes.length];
			for(int i = 0 ; i < paramTypes.length ; i++) {
				captures[i] = ArgumentCaptor.forClass(paramTypes[i]);
				params[i] = captures[i].capture();
			}
			m.invoke(verificate, params);
			Object[][] ret = new Object[captures[0].getAllValues().size()][];
			for(int i = 0 ; i < ret.length ; i++) {
				ret[i] = new Object[captures.length];
				for(int j = 0 ; j < ret[i].length ; j++) {
					ret[i][j] = captures[j].getAllValues().get(i);
				}
			}
			return ret;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void addMockService(Class<? extends Service> serviceType) {
		mockServices.add(serviceType);
	}
	
	static boolean isMockTarget(Set<Class<? extends Service>> mockTypes, Object target) {
		for(Class<? extends Service> type : mockTypes) {
			if(type.isAssignableFrom(target.getClass())) {
				return true;
			}
		}
		return false;
	}

	class ServiceLocatorLogicExt extends ServiceLocatorLogic {
		Map<Class<? extends Service>, Service> mockMap = 
					new HashMap<Class<? extends Service>, Service>();
		@Override
		public <T extends Service> T getService(Class<T> cl, Class<? extends T> defaultClass) {
			ServiceLocatorLogic tmp = (ServiceLocatorLogic)orginalDI.getObject(ServiceLocator.SERVICE_LOCATOR_KEY);
			T ret;
			if(sll == null) {
				ret = tmp.getService(cl, defaultClass);
			} else {
				ret = orginalSLL.getService(cl, defaultClass);
			}
			if(isMockTarget(mockServices, ret)) {
				ret = getMock(cl);
			}
			return ret;
		}
		
		@SuppressWarnings("unchecked")
		<T extends Service> T getMock(Class<T> cl) {
			Class<? extends Service> type = cl;
			Service ret = mockMap.get(type);
			if(ret == null) {
				ret = mock(cl);
				mockMap.put(type, ret);
			}
			return (T)ret;
		}
	}
}
