/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.service;

import jp.rough_diamond.commons.entity.Numbering;
import jp.rough_diamond.commons.service.NumberingService.CachingStrategy;
import jp.rough_diamond.commons.testdata.NumberingLoader;
import jp.rough_diamond.commons.testdata.UnitLoader;
import jp.rough_diamond.commons.testing.DataLoadingTestCase;
import jp.rough_diamond.commons.testing.Loader;
import jp.rough_diamond.framework.service.ServiceLocator;

public class NumberingServiceTest extends DataLoadingTestCase {

	protected void setUp() throws Exception {
		super.setUp();
		Loader.load(UnitLoader.class);
		Loader.load(NumberingLoader.class);
	}

	public void testGetNumberInNonCasingStrategy() throws Exception {
		NumberingService.CachingStrategy strategy = ServiceLocator.getService(NonCachingStrategyExt.class, NonCachingStrategyExt.class);
		
		assertEquals("返却値が誤っています。", strategy.getNumber("yamane"), 1L);
		Numbering numbering = BasicService.getService().findByPK(Numbering.class, "yamane");
		assertEquals("返却値が誤っています。", numbering.getNextNumber().longValue(), 1L);
		assertEquals("返却値が誤っています。", strategy.getNumber("yamane"), 2L);
		numbering = BasicService.getService().findByPK(Numbering.class, "yamane");
		assertEquals("返却値が誤っています。", numbering.getNextNumber().longValue(), 2L);
		assertEquals("返却値が誤っています。", strategy.getNumber("yamane"), 3L);
		numbering = BasicService.getService().findByPK(Numbering.class, "yamane");
		assertEquals("返却値が誤っています。", numbering.getNextNumber().longValue(), 3L);
		assertEquals("返却値が誤っています。", strategy.getNumber("yamane"), 4L);
		numbering = BasicService.getService().findByPK(Numbering.class, "yamane");
		assertEquals("返却値が誤っています。", numbering.getNextNumber().longValue(), 4L);
		assertEquals("返却値が誤っています。", strategy.getNumber("yamane"), 5L);
		numbering = BasicService.getService().findByPK(Numbering.class, "yamane");
		assertEquals("返却値が誤っています。", numbering.getNextNumber().longValue(), 5L);
		assertEquals("返却値が誤っています。", strategy.getNumber("yamane"), 6L);
		numbering = BasicService.getService().findByPK(Numbering.class, "yamane");
		assertEquals("返却値が誤っています。", numbering.getNextNumber().longValue(), 6L);
		assertEquals("返却値が誤っています。", strategy.getNumber("yamane"), 7L);
		numbering = BasicService.getService().findByPK(Numbering.class, "yamane");
		assertEquals("返却値が誤っています。", numbering.getNextNumber().longValue(), 7L);
		assertEquals("返却値が誤っています。", strategy.getNumber("yamane"), 8L);
		numbering = BasicService.getService().findByPK(Numbering.class, "yamane");
		assertEquals("返却値が誤っています。", numbering.getNextNumber().longValue(), 8L);
		assertEquals("返却値が誤っています。", strategy.getNumber("yamane"), 9L);
		numbering = BasicService.getService().findByPK(Numbering.class, "yamane");
		assertEquals("返却値が誤っています。", numbering.getNextNumber().longValue(), 9L);
		assertEquals("返却値が誤っています。", strategy.getNumber("yamane"), 10L);
		numbering = BasicService.getService().findByPK(Numbering.class, "yamane");
		assertEquals("返却値が誤っています。", numbering.getNextNumber().longValue(), 10L);
		assertEquals("返却値が誤っています。", strategy.getNumber("yamane"), 11L);
		numbering = BasicService.getService().findByPK(Numbering.class, "yamane");
		assertEquals("返却値が誤っています。", numbering.getNextNumber().longValue(), 11L);
	}
	
	public static class NonCachingStrategyExt extends NumberingService.NonCachingStrategy {}
	
	public void testGetNumberInCasingStrategy() throws Exception {
		NumberingService.NumberCachingStrategy strategy = ServiceLocator.getService(NumberCachingStrategyExt.class, NumberCachingStrategyExt.class);

		assertEquals("返却値が誤っています。", strategy.getNumber("yamane"), 1L);
		Numbering numbering = BasicService.getService().findByPK(Numbering.class, "yamane");
		assertEquals("返却値が誤っています。", numbering.getNextNumber().longValue(), 10L);
		assertEquals("返却値が誤っています。", strategy.getNumber("yamane"), 2L);
		numbering = BasicService.getService().findByPK(Numbering.class, "yamane");
		assertEquals("返却値が誤っています。", numbering.getNextNumber().longValue(), 10L);
		assertEquals("返却値が誤っています。", strategy.getNumber("yamane"), 3L);
		numbering = BasicService.getService().findByPK(Numbering.class, "yamane");
		assertEquals("返却値が誤っています。", numbering.getNextNumber().longValue(), 10L);
		assertEquals("返却値が誤っています。", strategy.getNumber("yamane"), 4L);
		numbering = BasicService.getService().findByPK(Numbering.class, "yamane");
		assertEquals("返却値が誤っています。", numbering.getNextNumber().longValue(), 10L);
		assertEquals("返却値が誤っています。", strategy.getNumber("yamane"), 5L);
		numbering = BasicService.getService().findByPK(Numbering.class, "yamane");
		assertEquals("返却値が誤っています。", numbering.getNextNumber().longValue(), 10L);
		assertEquals("返却値が誤っています。", strategy.getNumber("yamane"), 6L);
		numbering = BasicService.getService().findByPK(Numbering.class, "yamane");
		assertEquals("返却値が誤っています。", numbering.getNextNumber().longValue(), 10L);
		assertEquals("返却値が誤っています。", strategy.getNumber("yamane"), 7L);
		numbering = BasicService.getService().findByPK(Numbering.class, "yamane");
		assertEquals("返却値が誤っています。", numbering.getNextNumber().longValue(), 10L);
		assertEquals("返却値が誤っています。", strategy.getNumber("yamane"), 8L);
		numbering = BasicService.getService().findByPK(Numbering.class, "yamane");
		assertEquals("返却値が誤っています。", numbering.getNextNumber().longValue(), 10L);
		assertEquals("返却値が誤っています。", strategy.getNumber("yamane"), 9L);
		numbering = BasicService.getService().findByPK(Numbering.class, "yamane");
		assertEquals("返却値が誤っています。", numbering.getNextNumber().longValue(), 10L);
		assertEquals("返却値が誤っています。", strategy.getNumber("yamane"), 10L);
		numbering = BasicService.getService().findByPK(Numbering.class, "yamane");
		assertEquals("返却値が誤っています。", numbering.getNextNumber().longValue(), 10L);
		assertEquals("返却値が誤っています。", strategy.getNumber("yamane"), 11L);
		numbering = BasicService.getService().findByPK(Numbering.class, "yamane");
		assertEquals("返却値が誤っています。", numbering.getNextNumber().longValue(), 20L);
	}
	
	public static class NumberCachingStrategyExt extends NumberingService.NumberCachingStrategy {
		@Override
		protected int getCashSize() {
			return 10;
		}
		@Override
		protected CachingStrategy getStrategy() {
			return this;
		}
	}
}
