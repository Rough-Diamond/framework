/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.service.hibernate;

import jp.rough_diamond.framework.service.Service;
import jp.rough_diamond.framework.service.ServiceLocator;
import junit.framework.TestCase;

/**
 *
 */
public class BasicServiceInterceptorTest extends TestCase {
	public void testTranslateSql() throws Exception {
		ServiceLocator.getService(BasicServiceInterceptorTestService.class).testTranslateSql();
	}
	
	public static class BasicServiceInterceptorTestService implements Service {
		public void testTranslateSql() throws Exception {
//XXX H2 Only			
//			String sql = new BasicServiceInterceptor().translateSql("select unit0_.BASE_UNIT_ID as col_0_0_, sum(unit0_.RATE_VALUE) as col_1_0_ from PUBLIC.UNIT unit0_ where unit0_.ID<=? group by unit0_.BASE_UNIT_ID order by sum(unit0_.RATE_VALUE) asc limit ?");
//			assertEquals("select unit0_.BASE_UNIT_ID as col_0_0_, sum(unit0_.RATE_VALUE) as col_1_0_ from PUBLIC.UNIT unit0_ where unit0_.ID<=? group by unit0_.BASE_UNIT_ID order by sum(unit0_.RATE_VALUE) asc NULLS LAST limit ?", sql);
		}
	}
}
