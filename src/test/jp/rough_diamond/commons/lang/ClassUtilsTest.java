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
	public void test���ʂ̃N���X�̃��\�[�X�����擾�ł��邱��() {
		String result = ClassUtils.translateResourceName(ClassUtilsTest.class);
		assertEquals("�ԋp�l������Ă��܂��B",
				"jp/rough_diamond/commons/lang/ClassUtilsTest.class",
				result);
		checkResourceName(result);
	}

	public void test�C���i�[�N���X�̃��\�[�X�����擾�ł��邱��() {
		String result = ClassUtils.translateResourceName(Dummy.class);
		assertEquals("�ԋp�l������Ă��܂��B",
				"jp/rough_diamond/commons/lang/ClassUtilsTest$Dummy.class",
				result);
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(result);
		assertNotNull("���������\�[�X���ł͂���܂���B", is);
	}
	
	public void test���d�C���i�[�N���X�̃��\�[�X�����擾�ł��邱��() {
		String result = ClassUtils.translateResourceName(Dummy.ClassInDummy.class);
		assertEquals("�ԋp�l������Ă��܂��B",
				"jp/rough_diamond/commons/lang/ClassUtilsTest$Dummy$ClassInDummy.class",
				result);
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(result);
		assertNotNull("���������\�[�X���ł͂���܂���B", is);
	}
	
	public void test���\�N���X�̃��\�[�X�����擾�ł��邱��() {
		String result = ClassUtils.translateResourceName(ClassUtilsTestDummy2.class);
		assertEquals("�ԋp�l������Ă��܂��B",
				"jp/rough_diamond/commons/lang/ClassUtilsTestDummy2.class",
				result);
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(result);
		assertNotNull("���������\�[�X���ł͂���܂���B", is);
	}
	
	public void test�N���X�ɑΉ�����URL���������擾�ł��Ă��邱��() {
		assertTrue("URL������Ă��܂��B", 
				ClassUtils.getClassURL(ClassUtilsTest.class).toString().endsWith("ClassUtilsTest.class"));
	}
	
	private void checkResourceName(String result) {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(result);
		assertNotNull("���������\�[�X���ł͂���܂���B", is);
	}
	
	static class Dummy { 
		class ClassInDummy { }
	}
}

class ClassUtilsTestDummy2 { }