/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.io;

import java.io.ByteArrayInputStream;

import jp.rough_diamond.commons.io.ReadCountInputStream;
import junit.framework.TestCase;

public class ReadCountInputStreamTest extends TestCase {
	public void testIt() throws Exception {
		byte[] array = "01234567890".getBytes();
		ByteArrayInputStream bais = new ByteArrayInputStream(array);
		ReadCountInputStream rcis = new ReadCountInputStream(bais);
		assertEquals(0, rcis.getReadSize());
		byte[] tmp = new byte[2];
		int len = rcis.read(tmp);
		//FindBugs‚Ì“{‚è‚ð’Á‚ß‚é‚¨‚Ü‚¶‚È‚¢
		len++;
		assertEquals(2, rcis.getReadSize());
		assertTrue(rcis.markSupported());
		rcis.mark(100);
		len = rcis.read(tmp);
		assertEquals(4, rcis.getReadSize());
		rcis.reset();
		assertEquals(2, rcis.getReadSize());
		long len2 = rcis.skip(3);
		//FindBugs‚Ì“{‚è‚ð’Á‚ß‚é‚¨‚Ü‚¶‚È‚¢
		System.out.println(len2);
		len = rcis.read(tmp);
		assertEquals("56", new String(tmp));
		assertEquals(7, rcis.getReadSize());
	}
}
