/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.sample.esb.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import jp.rough_diamond.framework.es.DynamicEndPointRouter;
import jp.rough_diamond.framework.es.ServiceBus;
import jp.rough_diamond.framework.service.ServiceLocator;
import junit.framework.TestCase;

/**
 *
 */
public class ChangeEndPointTest extends TestCase {
	protected void setUp() throws Exception {
		super.setUp();
		ServiceBus.getInstance();
	}

	public void testEndPoint���w�肵�Ȃ��ꍇ�͐���ɑ��M�ł��邱��() throws Exception {
		SampleService service = ServiceLocator.getService(SampleService.class);
		assertEquals("�ԋp�l������Ă��܂��B", "Hello Eiji Yamane.", service.sayHello3("Eiji Yamane"));
	}
	
	public void testEndPoint������Ďw�肵���ꍇ�ɃG���[���������邱��() throws Exception {
		setEndPoint(false);
		SampleService service = ServiceLocator.getService(SampleService.class);
		try {
			service.sayHello3("Eiji Yamane");
			fail("��O���������Ă��܂���B");
		} catch(Exception e) {
			e.printStackTrace();
		}
		try {
			assertEquals("�ԋp�l������Ă��܂��B", "Hello Eiji Yamane.", service.sayHello3("Eiji Yamane"));
		} catch(Exception e) {
			e.printStackTrace();
			fail("EndPoint�̏�񂪃��Z�b�g����Ă��܂���");
		}
	}

	public void testEndPoint�𐳂����w�肵���ꍇ�ɃG���[���������Ȃ�����() throws Exception {
		setEndPoint(true);
		SampleService service = ServiceLocator.getService(SampleService.class);
		assertEquals("�ԋp�l������Ă��܂��B", "Hello Eiji Yamane.", service.sayHello3("Eiji Yamane"));
	}
	
	private void setEndPoint(boolean isTruth) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("cxfrouting.properties");
		Properties p = new Properties();
		p.load(is);
		long port = Long.parseLong(p.getProperty("server.port")) + ((isTruth) ? 0 : 1);
		String url = String.format(
				"http://localhost:%d/services/SampleService", port);
		System.out.println(url);
		params.put(DynamicEndPointRouter.ENDPOINT_KEY, url);
		ServiceBus.getInstance().addProperties(params);
	}
}
