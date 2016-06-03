/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class PropertyUtilsTest extends TestCase {
	public void testCollectionToCollection() throws Exception {
		Bean1 bean1 = new Bean1();
		bean1.setList(new ArrayList<String>(Arrays.asList(new String[]{"abc", "xyz"})));
		Bean1 bean2 = new Bean1();
		PropertyUtils.copyProperties(bean1, bean2);
		assertEquals("�R�s�[�Ɏ��s���Ă��܂��B", 2, bean2.getList().size());
		assertEquals("�R�s�[�Ɏ��s���Ă��܂��B", "abc", bean2.getList().get(0));
		assertEquals("�R�s�[�Ɏ��s���Ă��܂��B", "xyz", bean2.getList().get(1));
	}
	
	public void testCollectionToArray() throws Exception {
		Bean1 bean1 = new Bean1();
		bean1.setList(Arrays.asList(new String[]{"abc", "xyz"}));
		Bean2 bean2 = new Bean2();
		PropertyUtils.copyProperties(bean1, bean2);
		assertEquals("�R�s�[�Ɏ��s���Ă��܂��B", 2, bean2.getList().length);
		assertEquals("�R�s�[�Ɏ��s���Ă��܂��B", "abc", bean2.getList()[0]);
		assertEquals("�R�s�[�Ɏ��s���Ă��܂��B", "xyz", bean2.getList()[1]);
	}
	
	@SuppressWarnings("unchecked")
	public void testDescribeFromDest() throws Exception {
		Bean3 bean3 = new Bean3();
		Bean4 bean4 = new Bean4();
		bean3.setA("123");
		bean4.setA("456");

		Map map = PropertyUtils.describeFromDest(bean3, bean4);
		System.out.println(map);
		assertEquals("�ԋp��������Ă��܂��B", 2, map.size());
		assertEquals("�l������Ă��܂��B", "123", map.get("a"));
		assertEquals("�l������Ă��܂��B", "xyz", map.get("b"));
		
		map = PropertyUtils.describeFromDest(bean4, bean3);
		System.out.println(map);
		assertEquals("�ԋp��������Ă��܂��B", 2, map.size());
		assertEquals("�l������Ă��܂��B", "456", map.get("a"));
		assertEquals("�l������Ă��܂��B", "abc", map.get("c"));
	}
	
	public void testSkipProperty�A�m�e�[�V�������@�\���Ă��鎖() throws Exception {
		Bean4 bean4 = new Bean4();
		Bean5 bean5 = new Bean5();
		bean5.setA("xyz");

		assertNull("�����l���z��O�ł��B", bean4.getA());
		PropertyUtils.copyProperties(bean5, bean4);
		assertNull("�l���R�s�[����Ă��܂��B", bean4.getA());
	}
	
	public static class Bean1 {
		List<String> list;

		public List<String> getList() {
			return list;
		}

		public void setList(List<String> list) {
			this.list = list;
		}
	}
	
	public static class Bean2 {
		String[] list;

		public String[] getList() {
			return list;
		}

		public void setList(String[] list) {
			this.list = list;
		}
	}
	
	public static class Bean3 {
		private String a,b,c;

		public String getA() {
			return a;
		}

		public void setA(String a) {
			this.a = a;
		}

		public String getB() {
			return "xyz";
		}

		public void setC(String c) {
			this.c = c;
		}
		
		public String getX() {
			throw new RuntimeException("�Ăяo����Ă��܂��B");
		}
		
		void foo() {
			b = c;
			c = b;
		}
	}
	
	public static class Bean4 {
		private String a,b,c;
		public String getA() {
			return a;
		}

		public void setA(String a) {
			this.a = a;
		}

		public String getC() {
			return "abc";
		}

		public void setB(String b) {
			this.b = b;
		}

		public String getY() {
			throw new RuntimeException("�Ăяo����Ă��܂��B");
		}

		void foo() {
			b = c;
			c = b;
		}
	}
	
	public static class Bean5 extends Bean4 {
		@Override
		@PropertyUtils.SkipProperty
		public String getA() {
			return super.getA();
		}
	}
	
	public static void main(String[] args) {
		int[] array = new int[1];
		Array.set(array, 0, Integer.valueOf(1));
		System.out.println(array[0]);
	}
}
