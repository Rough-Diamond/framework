/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.rough_diamond.commons.io.DeleteFileWhenGC;
import junit.framework.TestCase;

public class DeleteFileWhenGCTest extends TestCase {
	public void testIt() throws Exception {
		File f = File.createTempFile("xxx", ".tmp");
		String name = f.getCanonicalPath();
		DeleteFileWhenGC f2 = new DeleteFileWhenGC(name);
		assertTrue(f2.exists());
		f2 = null;
		List<byte[]> list = new ArrayList<byte[]>();
		try {
			while(true) {
				long free = Runtime.getRuntime().freeMemory();
				int max = (int)Math.min((long)Integer.MAX_VALUE, free);
				byte[] tmp = new byte[max];
				list.add(tmp);
			}
		} catch(OutOfMemoryError e) {
		}
		list = null;
		System.gc();
		System.runFinalization();
		Thread.sleep(1000);
		f = new File(name);
		assertFalse(f.exists());
	}
}
