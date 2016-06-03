/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.testing;

import jp.rough_diamond.commons.entity.Numbering;
import jp.rough_diamond.commons.service.BasicService;
import jp.rough_diamond.commons.testdata.NumberingLoader;
import jp.rough_diamond.commons.testdata.UnitLoader;

public class DataLoadingTestCaseTest extends DataLoadingTestCase {

	protected void setUp() throws Exception {
		super.setUp();
		Loader.load(UnitLoader.class);
		Loader.load(NumberingLoader.class);
	}

	public void testWhenInsert() throws Exception {
		Numbering n = new Numbering();
		n.setId("akls;djglakjlkasj");
		n.setNextNumber(1L);
		BasicService.getService().insert(n);
	}
	
	public void testWhenUpdate() throws Exception {
		Numbering entiry = BasicService.getService().findByPK(Numbering.class, "hoge");
		entiry.setNextNumber(System.currentTimeMillis());
		BasicService.getService().update(entiry);
	}
	
	public void testWhenDelete() throws Exception {
		Numbering entiry = BasicService.getService().findByPK(Numbering.class, "hoge");
		BasicService.getService().delete(entiry);
	}
	
	public void testWhenDeleteAll() throws Exception {
		BasicService.getService().deleteAll(Numbering.class);
	}
	
	protected void tearDown() throws Exception {
		try {
			assertTrue("Numberingがロールバック対象になっていません。", DBInitializer.modifiedClasses.contains(Numbering.class));
		} finally {
			super.tearDown();
		}
	}
}
