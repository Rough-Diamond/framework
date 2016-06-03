/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.util.jobmonitor;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import junit.framework.TestCase;

/**
 *
 */
public class ContextListenerTest extends TestCase {
	public void testGetMonitorNameWhenSpecficContextParam() throws Exception {
		IMocksControl mockC = EasyMock.createControl();
		ServletContext sc = (ServletContext)mockC.createMock(ServletContext.class);
		ServletContextEvent sce = new ServletContextEvent(sc);
		sc.getInitParameter("monitorName");
		EasyMock.expectLastCall().andReturn("hogehoge");
		mockC.replay();
		ContextListener listener = new ContextListener();
		assertEquals("返却値が誤っています。", "hogehoge", listener.getMonitorName(sce));
	}

	public void testGetMonitorNameWhenNonspecficContextParam() throws Exception {
		IMocksControl mockC = EasyMock.createControl();
		ServletContext sc = (ServletContext)mockC.createMock(ServletContext.class);
		ServletContextEvent sce = new ServletContextEvent(sc);
		ContextListener listener = new ContextListener();
		assertEquals("返却値が誤っています。", "jobMonitor", listener.getMonitorName(sce));
	}
	
	public void testInit() throws Exception {
		ContextListener listener = new ContextListener();
		IMocksControl mockC = EasyMock.createControl();
		ServletContext sc = (ServletContext)mockC.createMock(ServletContext.class);
		ServletContextEvent sce = new ServletContextEvent(sc);
		try {
			sc.getInitParameter("monitorName");
			EasyMock.expectLastCall().andReturn("hogeMonitor");
			mockC.replay();
			listener.contextInitialized(sce);
			assertNotNull("ジョブモニタが初期化されていません。", listener.monitor);
		} finally {
			listener.contextDestroyed(sce);
			assertNull("ジョブモニタが解放されていません。", listener.monitor);
		}
	}
}