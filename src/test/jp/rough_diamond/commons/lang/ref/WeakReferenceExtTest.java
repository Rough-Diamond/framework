/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.lang.ref;

import jp.rough_diamond.commons.lang.ref.WeakReferenceExt;
import junit.framework.TestCase;

public class WeakReferenceExtTest extends TestCase {
	public void testEquals() {
		WeakReferenceExt<String> target = new WeakReferenceExt<String>("xyz");
		assertFalse(target.equals(null));

		assertTrue(target.equals(target));
		
		WeakReferenceExt<String> r1 = new WeakReferenceExt<String>("xyz");
		assertTrue(target.equals(r1));
		assertTrue(r1.equals(target));
		r1.clear();
		assertFalse(target.equals(r1));
		assertFalse(r1.equals(target));

		
		WeakReferenceExt<String> r2 = new WeakReferenceExt<String>("xyzz");
		assertFalse(target.equals(r2));
		assertFalse(r2.equals(target));

		target.clear();
		assertFalse(target.equals(r1));
	}
}
