/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.web.struts;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.RequestProcessor;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.config.PlugInConfig;
import org.apache.struts.tiles.TilesPlugin;
import org.apache.struts.tiles.TilesUtil;
import org.apache.struts.tiles.TilesUtilImpl;

/**
 * Tilesプラグインの拡張
 *
 */
public class TilesPlugInExt extends TilesPlugin {
	private final static Log log = LogFactory.getLog(TilesPlugInExt.class);
	
	/**
	 * 再読み込み間隔（秒）
	 */
	private int reloadPeriod = -1;
	
	public int getReloadPeriod() {
		return reloadPeriod;
	}

	public void setReloadPeriod(int reloadPeriod) {
		this.reloadPeriod = reloadPeriod;
	}

	@Override
	public void init(ActionServlet servlet, ModuleConfig moduleConfig) throws ServletException {
		init2(servlet, moduleConfig);
		super.init(servlet, moduleConfig);
		lastAccessTime = System.currentTimeMillis();
	}

	
	@Override
	public void destroy() {
		if(timer != null) {
			timer.cancel();
		}
		super.destroy();
	}

	private boolean 		isInit = false;
	private File			configFile;
	private long			lastAccessTime;
	private Timer			timer;
	private ActionServlet	servlet;
	private ModuleConfig	config;
	
	void init2(ActionServlet servlet, ModuleConfig moduleConfig) {
		if(!isInit) {
			this.servlet = servlet;
			this.config = moduleConfig;
			PlugInConfig[] configs = moduleConfig.findPlugInConfigs();
			for(PlugInConfig pConfig : configs) {
				if(this.getClass().getName().equals(pConfig.getClassName())) {
					String configName = (String)pConfig.getProperties().get("definitions-config");
					String realPath = servlet.getServletContext().getRealPath(configName);
					configFile = new File(realPath);
				}
			}
			int reloadPeriod = getReloadPeriod() * 1000;
			if(reloadPeriod > 0) {
				timer = new Timer(true);
				timer.schedule(new ConfigChecker(), reloadPeriod, reloadPeriod);
			}
			isInit = true;
		}
	}
	
	private class ConfigChecker extends TimerTask {
		@Override
		public void run() {
			try {
				if(lastAccessTime < configFile.lastModified()) {
					log.debug("Tilesの情報を更新します。");
					servlet.getServletContext().removeAttribute(TilesUtilImpl.DEFINITIONS_FACTORY);
					TilesUtilExt.resetTilesImpl();
					init(servlet, config);
		            String key = Globals.REQUEST_PROCESSOR_KEY + config.getPrefix();
		            RequestProcessor rp = (RequestProcessor)servlet.getServletContext().getAttribute(key);
		            if(rp instanceof BaseTilesRequestProcessor) {
		            	((BaseTilesRequestProcessor)rp).initDefinitionsMapping();
		            } else {
		            	rp.init(servlet, config);
		            }
				}
			} catch(Exception e) {
				log.warn("例外が発生しましたが無視します。", e);
			}
		}
	}
	
	private static class TilesUtilExt extends TilesUtil {
		public static void resetTilesImpl() {
			TilesUtil.testReset();
		}
	}
}
