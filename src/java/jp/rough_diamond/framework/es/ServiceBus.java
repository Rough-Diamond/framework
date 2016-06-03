/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.es;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.MuleServer;
import org.mule.api.MuleContext;

import jp.rough_diamond.commons.di.DIContainerFactory;

public class ServiceBus {
	private final static Log log = LogFactory.getLog(ServiceBus.class);
	
	private ThreadLocal<WeakReference<Map<String, Object>>> parameters = new ThreadLocal<WeakReference<Map<String,Object>>>();
	public void addProperties(Map<String, Object> parameters) {
		this.parameters.set(new WeakReference<Map<String, Object>>(parameters));
	}

	public void addProperty(String key, Object value) {
		Map<String, Object> parameters = popParameters();
		parameters.put(key, value);
		addProperties(parameters);
	}
	
	Map<String, Object> popParameters() {
		WeakReference<Map<String, Object>> referent = parameters.get();
		if(referent == null) {
			return new HashMap<String, Object>();
		}
		this.parameters.remove();
		Map<String, Object> parameters = referent.get();
		return (parameters == null) ? new HashMap<String, Object>() : parameters;
	}
	
	public static ServiceBus getInstance() {
		ServiceBus bus = (ServiceBus)DIContainerFactory.getDIContainer().getObject("serviceBus");
		bus.init();
		return bus;
	}
	
	public void dispose() {
		log.info("MuleServerを停止します。");
		getContext().dispose();
	}

	private String config;
	public void setConfig(String config) {
		this.config = config;
	}
	
	public MuleContext getContext() {
		return MuleServer.getMuleContext();
	}
	
	synchronized void init() {
		MuleContext context = getContext();
		if(context == null) {
			startMule();
		}
	}

	private Object muleStartMonitor = new Object();
	private volatile boolean isStart = false;
	private void startMule() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					log.info("MuleServerを開始します。");
					MuleServer server;
					if(config == null) {
						server = new MuleServer();
					} else {
						server = new MuleServer(new String[]{"-config", config});
					}
					server.start(false, true);
				} catch(Exception e) {
					throw new RuntimeException(e);
				} finally {
					synchronized(muleStartMonitor) {
						isStart = true;
						muleStartMonitor.notifyAll();
					}
				}
			}
		});
		synchronized(muleStartMonitor) {
			isStart = false;
			t.setDaemon(true);
			t.start();
			try {
				while(true) {
					muleStartMonitor.wait(100);
					if(isStart) {
						break;
					}
				}
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
