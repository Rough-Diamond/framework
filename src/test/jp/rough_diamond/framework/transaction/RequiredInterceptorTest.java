/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.framework.transaction;

import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;

import junit.framework.TestCase;


/**
 *
 */
public class RequiredInterceptorTest extends TestCase {
	public void testShapeUp() throws Exception {
		Set<Class<?>> set = new HashSet<Class<?>>();
		set.add(String.class);
		set.add(AbstractList.class);
		set.add(LinkedList.class);
		set.add(ArrayList.class);
		set.add(AbstractMap.class);
		set.add(TreeMap.class);
		Set<Class<?>> ret = RequiredInterceptor.shapeUp(set);
		assertEquals("�ԋp��������Ă��܂��B", 3, ret.size());
		assertTrue("�^�C�v������Ă��܂��B", ret.contains(String.class));
		assertTrue("�^�C�v������Ă��܂��B", ret.contains(AbstractList.class));
		assertTrue("�^�C�v������Ă��܂��B", ret.contains(AbstractMap.class));
	}
}
