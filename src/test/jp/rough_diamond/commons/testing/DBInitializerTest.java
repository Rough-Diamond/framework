/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.testing;

import jp.rough_diamond.framework.service.ServiceLocator;
import junit.framework.TestCase;

/**
 *
 */
public class DBInitializerTest extends TestCase {
	public void testGetResourceNames_�A�m�e�[�V���������\�b�h�̃I�[�o�[���C�h�����Ă��Ȃ�() throws Exception{
		try {
			ServiceLocator.getService(DBInitializer1.class).getResourceNames();
			fail("��O���������Ă��܂���B");
		} catch(RuntimeException ex) {
		}
	}

	public void testGetResourceNames_�A�m�e�[�V������Excel�t�@�C�����΃p�X�Ŏw��() throws Exception {
		String[] resourceNames = ServiceLocator.getService(DBInitializer2.class).getResourceNames();
		assertEquals("�ԋp��������Ă��܂��B", 1, resourceNames.length);
		assertEquals("���\�[�X��������Ă��܂��B", "jp/rough_diamond/commons/testdata/NUMBERING.xls", resourceNames[0]);
	}

	public void testGetResourceNames_�A�m�e�[�V�����ɑ���DBInitializer���΃p�X�Ŏw��() throws Exception {
		String[] resourceNames = ServiceLocator.getService(DBInitializer3.class).getResourceNames();
		assertEquals("�ԋp��������Ă��܂��B", 1, resourceNames.length);
		assertEquals("���\�[�X��������Ă��܂��B", "jp/rough_diamond/commons/testdata/NUMBERING.xls", resourceNames[0]);
	}
	
	public void testGetResourceNames_�A�m�e�[�V�����ɑ���DBInitializer�ȊO�̃N���X�����΃p�X�Ŏw��() throws Exception {
		try {
			ServiceLocator.getService(DBInitializer4.class).getResourceNames();
			fail("��O���������Ă��܂���B");
		} catch(RuntimeException ex) {
		}
	}
	
	public void testGetResourceNames_�A�m�e�[�V������Excel�t�@�C���𑊑΃p�X�Ŏw��() throws Exception {
		String[] resourceNames = ServiceLocator.getService(DBInitializer5.class).getResourceNames();
		assertEquals("�ԋp��������Ă��܂��B", 1, resourceNames.length);
		assertEquals("���\�[�X��������Ă��܂��B", "jp/rough_diamond/commons/testdata/NUMBERING.xls", resourceNames[0]);
	}
	
	public void testGetResourceNames_�A�m�e�[�V������DBInitializer�N���X�𑊑΃p�X�Ŏw��() throws Exception {
		String[] resourceNames = ServiceLocator.getService(DBInitializer6.class).getResourceNames();
		assertEquals("�ԋp��������Ă��܂��B", 1, resourceNames.length);
		assertEquals("���\�[�X��������Ă��܂��B", "jp/rough_diamond/commons/testdata/NUMBERING.xls", resourceNames[0]);
	}
	
	public static class DBInitializer1 extends DBInitializer {
	}
	
	@ResourceNames(resources = {"jp/rough_diamond/commons/testdata/NUMBERING.xls"})
	public static class DBInitializer2 extends DBInitializer {
	}
	
	@ResourceNames(resources = {"jp.rough_diamond.commons.testing.DBInitializerTest$DBInitializer2"})
	public static class DBInitializer3 extends DBInitializer {
	}

	@ResourceNames(resources = {"jp.rough_diamond.commons.testing.DBInitializerTest$FakeService"})
	public static class DBInitializer4 extends DBInitializer {
	}

	@ResourceNames(resources = {"../testdata/NUMBERING.xls"})
	public static class DBInitializer5 extends DBInitializer {
	}

	@ResourceNames(resources = {"../testing/DBInitializerTest$DBInitializer3"})
	public static class DBInitializer6 extends DBInitializer {
	}
	
	public static class FakeService {}
}
