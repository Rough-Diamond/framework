/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.web.filter.session_historical;

import java.io.IOException;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HistoricalSessionRemover implements HttpSessionListener {
	private static Log log = LogFactory.getLog(HistoricalSessionRemover.class);
	
    public void sessionCreated(HttpSessionEvent se) {
    }

    public void sessionDestroyed(HttpSessionEvent se) {
        log.debug("HistoricalSessionRemover#sessionDestroyed");
        try {
            SessionHistoricalFilter.removeTimeoutSessionHistorical(se.getSession());
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
}