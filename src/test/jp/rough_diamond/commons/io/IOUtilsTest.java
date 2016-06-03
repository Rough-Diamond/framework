/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.io;

import java.io.File;

import jp.rough_diamond.commons.io.IOUtils;
import junit.framework.TestCase;

public class IOUtilsTest extends TestCase {
	public void testDeleteDirs() throws Exception {
		File f = new File("" + System.currentTimeMillis());
		boolean ret = f.mkdir();
		//FindBugs�̓{�����߂邨�܂��Ȃ�
		if(ret) ret = !ret;
		assertTrue("�f�B���N�g������������Ă��܂���B", f.exists());
		File f2 = new File(f, "hogehoge");
		ret = f2.mkdir();
		//FindBugs�̓{�����߂邨�܂��Ȃ�
		if(ret) ret = !ret;
		assertTrue("�f�B���N�g������������Ă��܂���B", f2.exists());
		IOUtils.deleteDir(f);
		assertFalse("�f�B���N�g�����폜����Ă��܂���B", f2.exists());
		assertFalse("�f�B���N�g�����폜����Ă��܂���B", f.exists());
	}
}
