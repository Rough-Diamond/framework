/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import jp.rough_diamond.commons.di.CompositeDIContainer;
import jp.rough_diamond.commons.di.DIContainer;
import jp.rough_diamond.commons.di.DIContainerFactory;
import jp.rough_diamond.commons.di.MapDIContainer;
import junit.framework.TestCase;

public class SimpleServiceLocatorLogicTest extends TestCase {
	private DIContainer orgDI;
	private Map<Object, Object> map;
	private Map<Class<?>, Service> serviceMap;
	private SimpleServiceLocatorLogic logic;
	@Override
	protected void setUp() {
		orgDI = DIContainerFactory.getDIContainer();
		map = new HashMap<Object, Object>();
		map.put(ServiceLocator.SERVICE_FINDER_KEY, new SimpleServiceFinder());
		MapDIContainer mapDI = new MapDIContainer(map);
		CompositeDIContainer newDI = new CompositeDIContainer(
				Arrays.asList(new DIContainer[]{mapDI, orgDI}));
		DIContainerFactory.setDIContainer(newDI);
		logic = (SimpleServiceLocatorLogic)SimpleServiceLocatorLogic.getServiceLocatorLogic();
		serviceMap = logic.serviceMap;
		logic.serviceMap = new HashMap<Class<?>, Service>();
	}

	@Override
	protected void tearDown() {
		DIContainerFactory.setDIContainer(orgDI);
		logic.serviceMap = serviceMap;
	}
	
	//����܂ł̃��[��
	public void testErrorWhenOldVersion() throws Exception {
		TestInterface o = ServiceLocator.getService(TestInterface.class);
		assertEquals("�C���X�^���X�^�C�v������Ă��܂��B", TestInterface.class, o.getClass());
	}
	
	//�f�t�H���g�^�C�v���^�����Ă���DI�ɐݒ肪�����ꍇ�̓f�t�H���g�^�C�v�ŃC���X�^���X������邱��
	public void testGetDefaultClassInstance() throws Exception {
		TestInterface o = ServiceLocator.getService(TestInterface.class, TestInterfaceImpl1.class);
		assertEquals("�C���X�^���X�^�C�v������Ă��܂��B", TestInterfaceImpl1.class, o.getClass());
	}
	
	//DI�ɒ�`����Ă���ꍇ�̓f�t�H���g�ł͂Ȃ���`����Ă���C���X�^���X���ԋp����邱��
	public void testGetSpecificateClassInstance1() throws Exception {
		Map<Object, Object> tmp = new HashMap<Object, Object>();
		tmp.put(TestInterface.class.getName(), new TestInterfaceImpl2());
		MapDIContainer mapDI = new MapDIContainer(tmp);
		CompositeDIContainer newDI = new CompositeDIContainer(
				Arrays.asList(new DIContainer[]{mapDI, DIContainerFactory.getDIContainer()}));
		DIContainerFactory.setDIContainer(newDI);
		TestInterface o = ServiceLocator.getService(TestInterface.class, TestInterfaceImpl1.class);
		assertEquals("�C���X�^���X�^�C�v������Ă��܂��B", TestInterfaceImpl2.class, o.getClass());
	}
	
	//DI�ɒ�`����Ă���ꍇ�͒�`����Ă���C���X�^���X���ԋp����邱��
	public void testGetSpecificateClassInstance2() throws Exception {
		Map<Object, Object> tmp = new HashMap<Object, Object>();
		tmp.put(TestInterface.class.getName(), new TestInterfaceImpl2());
		MapDIContainer mapDI = new MapDIContainer(tmp);
		CompositeDIContainer newDI = new CompositeDIContainer(
				Arrays.asList(new DIContainer[]{mapDI, DIContainerFactory.getDIContainer()}));
		DIContainerFactory.setDIContainer(newDI);
		TestInterface o = ServiceLocator.getService(TestInterface.class);
		assertEquals("�C���X�^���X�^�C�v������Ă��܂��B", TestInterfaceImpl2.class, o.getClass());
	}

	static class TestInterface implements Service {}
	static class TestInterfaceImpl1 extends TestInterface { }
	static class TestInterfaceImpl2 extends TestInterface { }
}
