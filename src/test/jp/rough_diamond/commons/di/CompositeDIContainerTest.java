/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.di;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class CompositeDIContainerTest extends TestCase {
	public void testIt() throws Exception {
		Map<Object, Object> map1 = new HashMap<Object, Object>();
		map1.put("a", "123");
		map1.put("b", "456");
		map1.put("c", "789");

		Map<Object, Object> map2 = new HashMap<Object, Object>();
		map2.put("a", "987");
		map2.put("d", "654");
		
		DIContainer container = new CompositeDIContainer(Arrays.asList(
				(DIContainer)new MapDIContainer(map1),
				(DIContainer)new MapDIContainer(map2)));
		assertEquals("123", container.getObject("a"));
		assertEquals("456", container.getObject("b"));
		assertEquals("789", container.getObject("c"));
		assertEquals("654", container.getObject("d"));
	}
}
