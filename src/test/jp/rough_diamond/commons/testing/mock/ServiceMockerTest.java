/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.testing.mock;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.mockito.cglib.proxy.Factory;

import jp.rough_diamond.commons.di.DIContainer;
import jp.rough_diamond.commons.di.DIContainerFactory;
import jp.rough_diamond.commons.service.BasicService;
import jp.rough_diamond.framework.es.EnterpriseService;
import jp.rough_diamond.framework.service.Service;
import jp.rough_diamond.framework.service.ServiceLocator;
import jp.rough_diamond.framework.service.ServiceLocatorLogic;
import junit.framework.TestCase;

/**
 *
 */
public class ServiceMockerTest extends TestCase {
	public void testDIContainerReplace() {
		ServiceMocker slmm = new ServiceMocker();
		DIContainer org = DIContainerFactory.getDIContainer();
		slmm.initialize();
		DIContainer mockDI = DIContainerFactory.getDIContainer();
		assertFalse("DIContainer�̍����ւ����s���Ă��܂���", org == mockDI);
		slmm.cleanUp();
		assertTrue("���ɖ߂��Ă��܂���", org == DIContainerFactory.getDIContainer());
	}
	
	public void testServiceLocatorLogic() throws Exception {
		ServiceMocker slmm = new ServiceMocker();
		slmm.initialize();
		try {
			DIContainer mockDI = DIContainerFactory.getDIContainer();
			ServiceLocatorLogic sll = (ServiceLocatorLogic)mockDI.getObject(ServiceLocator.SERVICE_LOCATOR_KEY);
			assertEquals("�ԋp�N���X������Ă��܂��B", ServiceMocker.ServiceLocatorLogicExt.class, sll.getClass());
		} finally {
			slmm.cleanUp();
		}
	}
	
	public void test�����̃��[���ŃT�[�r�X���������ԋp����邱��() throws Exception {
		ServiceMocker slmm = new ServiceMocker();
		slmm.initialize();
		try {
			BasicService bs = BasicService.getService();
			assertNotNull("�����̏����̎ז������Ă��܂���B", bs);
		} finally {
			slmm.cleanUp();
		}
	}
	
	public void testMock�I�u�W�F�N�g���ԋp����Ă��邱��() throws Exception {
		ServiceMocker slmm = new ServiceMocker();
		slmm.initialize();
		try {
			slmm.mockAllEnterprise();
			Service2 s2 = ServiceLocator.getService(Service2.class);
			assertTrue("mock�C���^�t�F�[�X��������܂���B", 
					new HashSet<Class<?>>(Arrays.asList(
							s2.getClass().getInterfaces())).contains(Factory.class));
		} finally {
			slmm.cleanUp();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void testIsMockTarget() throws Exception {
		Set<Class<? extends Service>> set = new HashSet<Class<? extends Service>>(
				Arrays.asList(Service.class));
		assertTrue("�ԋp�l������Ă��܂��B", ServiceMocker.isMockTarget(set, new Service1()));	
		assertTrue("�ԋp�l������Ă��܂��B", ServiceMocker.isMockTarget(set, new Service11()));	
		assertTrue("�ԋp�l������Ă��܂��B", ServiceMocker.isMockTarget(set, new Service2()));
		
		set = new HashSet<Class<? extends Service>>(Arrays.asList(Service11.class, Service2.class));
		assertFalse("�ԋp�l������Ă��܂��B", ServiceMocker.isMockTarget(set, new Service1()));	
		assertTrue("�ԋp�l������Ă��܂��B", ServiceMocker.isMockTarget(set, new Service11()));	
		assertTrue("�ԋp�l������Ă��܂��B", ServiceMocker.isMockTarget(set, new Service2()));
	}
	
	public void testSetReturnValueBySignature() {
		ServiceMocker slmm = new ServiceMocker();
		slmm.initialize();
		try {
			slmm.mockAllEnterprise();
			Service2 s2 = ServiceLocator.getService(Service2.class);
			slmm.setReturnValueBySignature(s2, "bar", 1000);
			assertEquals("�ԋp�l������Ă��܂��B", 1000, s2.bar().intValue());
			assertEquals("�ԋp�l������Ă��܂��B", 1000, s2.bar().intValue());
		} finally {
			slmm.cleanUp();
		}
	}
	
	public void testGetArguments() throws Exception {
		ServiceMocker slmm = new ServiceMocker();
		slmm.initialize();
		try {
			slmm.mockAllEnterprise();
			Service2 s2 = ServiceLocator.getService(Service2.class);
			s2.foo2("1", "2");
			s2.foo2("3", "4");
			s2.foo2("5", "6");
			Object[][] ret = slmm.getArguments(s2, "foo2", String.class, String.class);
			assertEquals("�z��v�f��������Ă��܂��B", 3, ret.length);
			assertEquals("�z��v�f��������Ă��܂��B", 2, ret[0].length);
			assertEquals("�z��v�f��������Ă��܂��B", 2, ret[1].length);
			assertEquals("�z��v�f��������Ă��܂��B", 2, ret[2].length);
			assertEquals("�ԋp�l������Ă��܂��B", "1", ret[0][0]);
			assertEquals("�ԋp�l������Ă��܂��B", "2", ret[0][1]);
			assertEquals("�ԋp�l������Ă��܂��B", "3", ret[1][0]);
			assertEquals("�ԋp�l������Ă��܂��B", "4", ret[1][1]);
			assertEquals("�ԋp�l������Ă��܂��B", "5", ret[2][0]);
			assertEquals("�ԋp�l������Ă��܂��B", "6", ret[2][1]);
		} finally {
			slmm.cleanUp();
		}
	}
	
	public void testServiceMocker�������ւ����Ƃ���mock���N���[���A�b�v����邱��() throws Exception {
		ServiceMocker slmm = new ServiceMocker();
		slmm.initialize();
		try {
			slmm.mockAllEnterprise();
			Service2 s2 = ServiceLocator.getService(Service2.class);
			s2.func("123");
			assertEquals("�Ăяo���񐔂�����Ă��܂��B", 1, slmm.getArguments(s2, "func", String.class).length);
		} finally {
			slmm.cleanUp();
		}
		slmm.initialize();
		try {
			slmm.mockAllEnterprise();
			Service2 s2 = ServiceLocator.getService(Service2.class);
			s2.func("xyz");
			assertEquals("�Ăяo���񐔂�����Ă��܂��B", 1, slmm.getArguments(s2, "func", String.class).length);
		} finally {
			slmm.cleanUp();
		}
	}
	
	public static class Service1 implements Service { }
	public static class Service11 extends Service1{ }
	public static class Service2 implements EnterpriseService { 
		public void foo(String xyz) {
			System.out.println(xyz);
		}
		public void foo2(String str1, String str2) {
			
		}
		public void func(String str) {
		}

		public Integer bar() {
			return 10;
		}
	}
}
