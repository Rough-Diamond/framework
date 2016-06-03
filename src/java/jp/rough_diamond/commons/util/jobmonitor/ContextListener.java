package jp.rough_diamond.commons.util.jobmonitor;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jp.rough_diamond.commons.di.DIContainerFactory;
import jp.rough_diamond.commons.util.jobmonitor.JobMonitor;

public class ContextListener implements ServletContextListener {
	JobMonitor monitor;
	private final static Log log = LogFactory.getLog(ContextListener.class);
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if(monitor != null) {
			try {
				monitor.stop();
			} finally {
				monitor = null;
			}
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		startMonitor(sce);
	}
	
	void startMonitor(ServletContextEvent sce) {
		String monitorName = getMonitorName(sce);
		if(log.isDebugEnabled()) {
			log.debug("Job Monitor DI Name:" + monitorName);
		}
		monitor = (JobMonitor)DIContainerFactory.getDIContainer(
				).getObject(monitorName);
		monitor.start();
	}

	String getMonitorName(ServletContextEvent sce) {
		String ret = sce.getServletContext().getInitParameter("monitorName");
		return (ret == null) ? "jobMonitor" : ret;
	}
}
