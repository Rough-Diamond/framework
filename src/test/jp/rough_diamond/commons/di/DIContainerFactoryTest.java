/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.di;

import junit.framework.TestCase;

public class DIContainerFactoryTest extends TestCase {
	private DIContainer old;
	@Override
	protected void setUp() throws Exception {
		old = DIContainerFactory.getDIContainer();
	}
	@Override
	protected void tearDown() throws Exception {
		DIContainerFactory.setDIContainer(old);
	}
	
	public void testGetDefaultDIContainer() {
		DIContainer container = DIContainerFactory.getDIContainer();
		assertEquals("class unmuch.", container.getClass(), SpringFramework.class);
	}

	public void testSetAndGetDIContainer() {
		DIContainer container = new NullDIContainer();
		DIContainerFactory.setDIContainer(container);
		assertTrue("instance unmuch.", container == DIContainerFactory.getDIContainer());
	}
	
	
	public static class NullDIContainer implements DIContainer {

		public Object getObject(Object key) {
			return null;
		}

		public Object getSource() {
			return null;
		}
		
		@SuppressWarnings("unchecked")
		public <T> T getObject(Class<T> type, Object key) {
			return (T)getObject(key);
		}
		
		@SuppressWarnings("unchecked")
		public <T> T getSource(Class<T> type) {
			return (T)getSource();
		}
	}
}
