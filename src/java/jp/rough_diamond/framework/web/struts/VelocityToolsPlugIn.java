/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.web.struts;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;
import org.apache.velocity.tools.view.ToolboxManager;
import org.apache.velocity.tools.view.servlet.ServletToolboxManager;

public class VelocityToolsPlugIn implements PlugIn {
	private final static Log log = LogFactory.getLog(VelocityToolsPlugIn.class);
	
	private String toolBoxFileName;
	private static ToolboxManager manager;
	
	public void destroy() {
		toolBoxFileName = null;
		destory2();
	}

	private static void destory2() {
		manager = null;
		
	}
	
	public void init(ActionServlet arg0, ModuleConfig arg1) throws ServletException {
		if(toolBoxFileName == null) {
			log.debug("toolbox.xmlÇÃéwíËÇ™ñ≥Ç¢ÇΩÇﬂToolBoxÇê∂ê¨ÇµÇ‹ÇπÇÒÅB");
			return;
		}
		log.debug(toolBoxFileName);
		init2(arg0);
	}

	private void init2(ActionServlet arg0) {
		manager = ServletToolboxManager.getInstance(arg0.getServletContext(), toolBoxFileName);
	}
	
	public String getToolBoxFileName() {
		return toolBoxFileName;
	}

	public void setToolBoxFileName(String toolBoxFileName) {
		this.toolBoxFileName = toolBoxFileName;
	}

	public static ToolboxManager getManager() {
		return manager;
	}
}
