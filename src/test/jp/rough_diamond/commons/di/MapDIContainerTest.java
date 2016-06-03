/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.di;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class MapDIContainerTest extends TestCase {
	private MapDIContainer 	container;
	@SuppressWarnings("unchecked")
	private Map				map;

	@SuppressWarnings("unchecked")
	@Override
	protected void setUp() {
		map = new HashMap();
		map.put("foo", "bar");
		container = new MapDIContainer(map);
	}
	
	public void testIt() {
		assertEquals("don't much", "bar", container.getObject("foo"));
		assertNull("don't null", container.getObject("hoge"));
		assertEquals("don't much", this.map, container.getSource());
	}
}
