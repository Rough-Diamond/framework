/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.service.hibernate;

import java.io.Serializable;

import jp.rough_diamond.commons.entity.Unit;
import jp.rough_diamond.commons.service.NumberingService;
import jp.rough_diamond.commons.testdata.NumberingLoader;
import jp.rough_diamond.commons.testdata.UnitLoader;
import jp.rough_diamond.commons.testing.DataLoadingTestCase;
import jp.rough_diamond.commons.testing.Loader;
import jp.rough_diamond.framework.service.Service;
import jp.rough_diamond.framework.service.ServiceLocator;

public class HibernateNumberingServiceTest extends DataLoadingTestCase {

	protected void setUp() throws Exception {
		super.setUp();
		Loader.load(UnitLoader.class);
		Loader.load(NumberingLoader.class);
	}
	
	public void testGetNumberByString() throws Exception {
		NumberingService service = ServiceLocator.getService(HibernateNumberingService.class);
		assertEquals("返却値が誤っています。", service.getNumber("yamane"), 1L);
		assertEquals("返却値が誤っています。", service.getNumber("jp.rough_diamond.commons.entity.Unit"), 3L);
		assertEquals("返却値が誤っています。", service.getNumber("hoge"), 11L);
	}
	
	public void testGetNumberByClass() throws Exception {
		TestGetNumberByClassService service = ServiceLocator.getService(TestGetNumberByClassService.class);

		//生成子の引数をチェックすることが目的なので、ServiceLocatorを経由せずに生成する
		//よい子はまねしちゃだめです。
		NumberingService nService = new HibernateNumberingService(true);
		assertEquals("返却値が誤っています。", service.doIt(nService), 3L);
		
		//生成子の引数をチェックすることが目的なので、ServiceLocatorを経由せずに生成する
		//よい子はまねしちゃだめです。
		nService = new HibernateNumberingService();
		assertEquals("返却値が誤っています。", service.doIt(nService), 6L);
	}
	
	public static class TestGetNumberByClassService implements Service {
		public Serializable doIt(NumberingService nService) {
			return nService.getNumber(Unit.class);
		}
	}
}
