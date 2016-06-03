/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.es;


import jp.rough_diamond.framework.service.Service;
import junit.framework.TestCase;

public class ServiceFinderTest extends TestCase {

	public void testIsTarget() throws Exception {
		// TODO テスト方法確立後に再度テスト
//		assertFalse(ServiceFinder.isTarget(Service1.class));
//		assertTrue(ServiceFinder.isTarget(Service2.class));
//		assertFalse(ServiceFinder.isTarget(Service3.class));
//		
//		ServiceFinder sf = new ServiceFinder();
//		Service2 s = sf.getService(Service2.class);
//		s.hoge("xyz");
//		s.poge();
	}
	
	public static interface Service1 extends Service { }
	public static interface Service2 extends EnterpriseService {
		@ServiceConnecter(serviceName="hoge")
		public void hoge(String xyz);
		public void poge();
	}
	public static class Service3 implements EnterpriseService { }
}
