/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.lang;

import java.io.InputStream;

import jp.rough_diamond.commons.lang.ClassUtils;
import junit.framework.TestCase;

public class ClassUtilsTest extends TestCase {
	public void test普通のクラスのリソース名が取得できること() {
		String result = ClassUtils.translateResourceName(ClassUtilsTest.class);
		assertEquals("返却値が誤っています。",
				"jp/rough_diamond/commons/lang/ClassUtilsTest.class",
				result);
		checkResourceName(result);
	}

	public void testインナークラスのリソース名が取得できること() {
		String result = ClassUtils.translateResourceName(Dummy.class);
		assertEquals("返却値が誤っています。",
				"jp/rough_diamond/commons/lang/ClassUtilsTest$Dummy.class",
				result);
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(result);
		assertNotNull("正しいリソース名ではありません。", is);
	}
	
	public void test多重インナークラスのリソース名が取得できること() {
		String result = ClassUtils.translateResourceName(Dummy.ClassInDummy.class);
		assertEquals("返却値が誤っています。",
				"jp/rough_diamond/commons/lang/ClassUtilsTest$Dummy$ClassInDummy.class",
				result);
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(result);
		assertNotNull("正しいリソース名ではありません。", is);
	}
	
	public void test非代表クラスのリソース名が取得できること() {
		String result = ClassUtils.translateResourceName(ClassUtilsTestDummy2.class);
		assertEquals("返却値が誤っています。",
				"jp/rough_diamond/commons/lang/ClassUtilsTestDummy2.class",
				result);
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(result);
		assertNotNull("正しいリソース名ではありません。", is);
	}
	
	public void testクラスに対応するURLが正しく取得できていること() {
		assertTrue("URLが誤っています。", 
				ClassUtils.getClassURL(ClassUtilsTest.class).toString().endsWith("ClassUtilsTest.class"));
	}
	
	private void checkResourceName(String result) {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(result);
		assertNotNull("正しいリソース名ではありません。", is);
	}
	
	static class Dummy { 
		class ClassInDummy { }
	}
}

class ClassUtilsTestDummy2 { }