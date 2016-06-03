/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.testing;


import jp.rough_diamond.commons.entity.ScalableNumber;
import jp.rough_diamond.commons.entity.Unit;
import jp.rough_diamond.commons.extractor.Extractor;
import jp.rough_diamond.commons.service.BasicService;
import jp.rough_diamond.commons.testdata.NumberingLoader;
import jp.rough_diamond.commons.testdata.UnitLoader;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class DataLodingTestCaseWithJunit4Test {
	private long unitCount;
	private Extractor ex;
	private String status;

	@Before
	public void setUp() throws Exception {
		DataLoadingTestCase.setUpDB();
		Loader.load(UnitLoader.class);
		Loader.load(NumberingLoader.class);
		ex = new Extractor(Unit.class);
		unitCount = BasicService.getService().getCountByExtractor(ex);
		status = "dontProcess";
	}

	@After
	public void tearDown() throws Exception {
		DataLoadingTestCase.cleanUpDB();
		if("dontProcess".equals(status)) {
			return;
		}
		if("insertUnit".equals(status)) {
			Assert.assertEquals("データが消去されていません。", 0L, BasicService.getService().getCountByExtractor(ex));
		} else if ("none".equals(status)) {
			Assert.assertEquals("データが消去されていません。", unitCount, BasicService.getService().getCountByExtractor(ex));
		}
	}
	
	@Test
	public void testWhenInsert() throws Exception {
		Unit newUnit = new Unit();
		newUnit.setName("新規ユニット");
		newUnit.setRate(new ScalableNumber("1"));
		newUnit.setBase(newUnit);
		newUnit.setScale(0);
		newUnit.save();
		status = "insertUnit";
	}
	
	@Test
	public void testNon() throws Exception {
		status = "none";
	}
}
