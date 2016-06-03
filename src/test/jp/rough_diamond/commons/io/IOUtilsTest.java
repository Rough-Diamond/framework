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
		//FindBugsの怒りを鎮めるおまじない
		if(ret) ret = !ret;
		assertTrue("ディレクトリが生成されていません。", f.exists());
		File f2 = new File(f, "hogehoge");
		ret = f2.mkdir();
		//FindBugsの怒りを鎮めるおまじない
		if(ret) ret = !ret;
		assertTrue("ディレクトリが生成されていません。", f2.exists());
		IOUtils.deleteDir(f);
		assertFalse("ディレクトリが削除されていません。", f2.exists());
		assertFalse("ディレクトリが削除されていません。", f.exists());
	}
}
