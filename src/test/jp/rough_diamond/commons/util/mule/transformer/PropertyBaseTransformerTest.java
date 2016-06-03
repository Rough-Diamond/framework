/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.util.mule.transformer;

import junit.framework.TestCase;

/**
 *
 */
public class PropertyBaseTransformerTest extends TestCase {
	public void test_001_2009年10月2日GW山本さんからの指摘不具合再現試験() throws Exception {
		Parent001A src = new Parent001A();
		Parent001B dest = (Parent001B) new PropertyBaseTransformerExt(new Parent001B()).transform(src);
		assertEquals("コピーできていません。", "xyz", dest.child.getName());
	}
	

	public static class Parent001A {
		public Child001A getBean() {
			Child001A ret = new Child001A();
			ret.setName("xyz");
			return ret;
		}
	}
	
	public static class Child001A {
		String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public static class Parent001B {
		Child001B child;
		public void setBean(Child001B bean) {
			this.child = bean;
		}
	}
	
	public static class Child001B {
		String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
	
	public static class PropertyBaseTransformerExt extends PropertyBaseTransformer {
		private Object target;
		PropertyBaseTransformerExt(Object o) {
			this.target = o;
		}
		@Override
		protected Object newTransformObject() {
			return target;
		}
	}
}
