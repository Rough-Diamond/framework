/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.es;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * <p>
 * <a href="BaseListener.java.html"> <i>View Source </i> </a>
 * </p>
 */
public class MuleListener implements ServletContextListener {
	public void contextDestroyed(ServletContextEvent arg0) {
		ServiceBus.getInstance().dispose();
	}

	public void contextInitialized(ServletContextEvent arg0) {
        ServiceBus.getInstance();
	}
}