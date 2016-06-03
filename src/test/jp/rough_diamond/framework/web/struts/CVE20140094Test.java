/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.framework.web.struts;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;

import jp.rough_diamond.commons.util.ClassLoaderIgnoreablePropertyUtilsBean;
import junit.framework.TestCase;

/**
 *
 */
public class CVE20140094Test extends TestCase {
	public void testCalssLoaderAccess() throws Exception {
		BeanUtilsBean org = BeanUtilsBean.getInstance();
		try {
			BeanUtilsBean.setInstance(new BeanUtilsBean(new ConvertUtilsBean(), new ClassLoaderIgnoreablePropertyUtilsBean()));
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("this.this.this.cl.foo", "hoge");
			map.put("this.map(hoge)", "foo");
			
			SampleClass sample = new SampleClass();
			sample.getMap().put("hoge", "poge");
			BeanUtils.populate(sample, map);
			System.out.println(sample.getMap());
			assertEquals("ClassLoaderÇ™ëÄçÏÇ≥ÇÍÇƒÇ¢Ç‹Ç∑ÅB", sample.cl.foo, "");
		} finally {
			BeanUtilsBean.setInstance(org);
		}
	}

	public static class SampleClass {
		private ClassLoaderSample cl = new ClassLoaderSample();
		private Map<String, String> map = new HashMap<String, String>();
		
		public Map<String, String> getMap() {
			return map;
		}

		public void setMap(Map<String, String> map) {
			this.map = map;
		}

		public ClassLoaderSample getCl() {
			return cl;
		}

		public void setCl(ClassLoaderSample cl) {
			this.cl = cl;
		}
		
		public SampleClass getThis() {
			return this;
		}
	}
	
	public static class ClassLoaderSample extends ClassLoader {
		private String foo = "";

		public String getFoo() {
			return foo;
		}

		public void setFoo(String foo) {
			this.foo = foo;
		}
	}
}
