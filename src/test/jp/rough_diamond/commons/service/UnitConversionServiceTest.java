/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.service;

import junit.framework.TestCase;

public class UnitConversionServiceTest extends TestCase {
	public void testGetInstance() {
		UnitConversionService service = UnitConversionService.getService();
		assertTrue("デフォルトのインスタンスタイプが誤っています。", service instanceof SimpleUnitConversionService);
	}
}
