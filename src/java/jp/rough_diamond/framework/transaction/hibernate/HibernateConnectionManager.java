/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.transaction.hibernate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;

import jp.rough_diamond.commons.lang.StringUtils;
import jp.rough_diamond.framework.transaction.ConnectionManager;
import jp.rough_diamond.framework.transaction.VersionUnmuchException;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Settings;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.persister.entity.EntityPersister;

@SuppressWarnings("unchecked")
public class HibernateConnectionManager extends ConnectionManager {
	private final static Log log = LogFactory.getLog(HibernateConnectionManager.class);
	
	static ThreadLocal<Stack<Session>> tl = new ThreadLocal<Stack<Session>>() {
        protected Stack<Session> initialValue() {
            return new Stack<Session>();
        }
    };
    

	static ThreadLocal<Map<Session, Transaction>> transactionMap = new ThreadLocal<Map<Session, Transaction>>() {
		protected Map<Session, Transaction> initialValue() {
			return new HashMap<Session, Transaction>();
		}
	};
	
	private SessionFactory sessionFactory;
	private SessionFactory getSessionFactory() {
		init();
		return sessionFactory;
	}

    private Configuration config;
    private Configuration getConfig2() {
    	init();
        return config;
    }
    
	private synchronized void init() {
		if(sessionFactory == null) {
			init2();
		}
	}
	
	private void init2() {
		if(log.isDebugEnabled()) {
			log.debug("セッションファクトリーを初期化します。:" + this);
		}
		loadConfigration();
		addingProperties();
		if(getInterceptor() != null) {
			config.setInterceptor(getInterceptor());
		}
		setListeners();
		buildSessionFactory();
        log.debug("セッションスタックを初期化します。");
	}

	protected Configuration addingProperties() {
		String addingPropertyFileName = getAddingPropertyFileName();
		if(!StringUtils.isBlank(addingPropertyFileName)) {
			InputStream is = null;
			IOException ioe = null;
			try {
				File f = new File(addingPropertyFileName);
				if(f.exists()) {
					try {
						log.debug("プロパティファイルをファイルから読み込みます。");
						is = new FileInputStream(f);
					} catch (FileNotFoundException e) {
						// 存在チェックしているから基本ありえない
						throw new RuntimeException(e);
					}
				} else {
					log.debug("プロパティファイルをCLASSPATHから読み込みます。");
					is = this.getClass().getClassLoader().getResourceAsStream(addingPropertyFileName);
				}
				Properties prop = new Properties();
				prop.load(is);
				config.addProperties(prop);
			} catch (IOException e) {
				ioe = e;
				throw new RuntimeException(e);
			} finally {
				if(is != null) {
					try {
						is.close();
					} catch (IOException e) {
						log.debug(e.getMessage(), e);
						if(ioe == null) {
							throw new RuntimeException(e);
						}
					}
				}
			}
		}
		return config;
	}

	/**
	 * 
	 */
	protected Configuration loadConfigration() {
		config = newConfiguration();
		if(getHibernateConfigName() == null) {
			log.debug("デフォルト設定ファイルで初期化します。");
			config.configure();
		} else {
			if(log.isDebugEnabled()) {
				log.debug("設定ファイル名：" + getHibernateConfigName());
			}
			config.configure(getHibernateConfigName());
		}
		return config;
	}
	
	protected Configuration newConfiguration() {
		return new Configuration();
	}
	
	protected void buildSessionFactory() {
		sessionFactory = config.buildSessionFactory();
	}
	
	protected void setListeners() {
		Map<String, List<String>> listenersMap;
		if(this.listenersMap == null) {
			listenersMap = new HashMap<String, List<String>>();
		} else {
			listenersMap = new HashMap<String, List<String>>(this.listenersMap);
		}

		Set<String> tmp = new HashSet<String>();
		List<String> flushListeners = listenersMap.get("flush-entity");
		if(flushListeners == null) {
			flushListeners = new ArrayList<String>();
			listenersMap.put("flush-entity", flushListeners);
		}
		tmp.clear();
		tmp.addAll(flushListeners);
		if(!tmp.contains(FlushListener.FlushListenerInner.class.getName())) {
			flushListeners.add(FlushListener.FlushListenerInner.class.getName());
		}

		List<String> autoFlushListeners = listenersMap.get("auto-flush");
		if(autoFlushListeners == null) {
			autoFlushListeners = new ArrayList<String>();
			listenersMap.put("auto-flush", autoFlushListeners);
		}
		tmp.clear();
		tmp.addAll(autoFlushListeners);
		if(!tmp.contains(FlushListener.AutoFlushListenerInner.class.getName())) {
			autoFlushListeners.add(FlushListener.AutoFlushListenerInner.class.getName());
		}

		List<String> saveListeners = listenersMap.get("save");
		if(saveListeners == null) {
			saveListeners = new ArrayList<String>();
			listenersMap.put("save", saveListeners);
		}
		tmp.clear();
		tmp.addAll(saveListeners);
		if(!tmp.contains(SaveOrUpdateListener.SaveListenerInner.class.getName())) {
			saveListeners.add(SaveOrUpdateListener.SaveListenerInner.class.getName());
		}
		
		List<String> updateListeners = listenersMap.get("update");
		if(updateListeners == null) {
			updateListeners = new ArrayList<String>();
			listenersMap.put("update", updateListeners);
		}
		tmp.clear();
		tmp.addAll(updateListeners);
		if(!tmp.contains(SaveOrUpdateListener.UpdateListenerInner.class.getName())) {
			updateListeners.add(SaveOrUpdateListener.UpdateListenerInner.class.getName());
		}

		List<String> saveOrUpdateListeners = listenersMap.get("save-update");
		if(saveOrUpdateListeners == null) {
			saveOrUpdateListeners = new ArrayList<String>();
			listenersMap.put("save-update", saveOrUpdateListeners);
		}
		tmp.clear();
		tmp.addAll(saveOrUpdateListeners);
		if(!tmp.contains(SaveOrUpdateListener.SaveOrUpdateListenerInner.class.getName())) {
			saveOrUpdateListeners.add(SaveOrUpdateListener.SaveOrUpdateListenerInner.class.getName());
		}

		for(Map.Entry<String, List<String>> entry : listenersMap.entrySet()) {
			List<String> list = entry.getValue();
			if(list != null) {
				config.setListeners(entry.getKey(), list.toArray(new String[list.size()]));
			}
		}
	}
	
	protected boolean isTransactionBegining(MethodInvocation mi) {
        init();
		return (tl.get().size() != 0);
	}

	@SuppressWarnings("deprecation")
	public Connection getCurrentConnection(MethodInvocation mi) {
		Session session = getSession2();
		if(session == null) {
			return null;
		} else {
			return getSession2().connection();
		}
	}

    public static Configuration getConfig() {
        HibernateConnectionManager hcm = 
            (HibernateConnectionManager)getConnectionManager();
        return hcm.getConfig2();
    }

    public static Session getCurrentSession() {
		HibernateConnectionManager hcm = 
			(HibernateConnectionManager)getConnectionManager();
		return hcm.getSession2();
	}

    public static void rebuildSessionFactory() {
        HibernateConnectionManager hcm = 
            (HibernateConnectionManager)getConnectionManager();
        hcm.rebuildSessionFactory2();
    }
    
    public static Settings getSettings() {
		HibernateConnectionManager hcm = 
			(HibernateConnectionManager)getConnectionManager();
		if(hcm == null) {
			return null;
		}
		return ((SessionFactoryImplementor)hcm.sessionFactory).getSettings();
    }
    
	protected void rebuildSessionFactory2() {
		buildSessionFactory();
    }

    private Session getSession2() {
        init();
        if(tl.get().isEmpty()) {
        	return null;
        } else {
        	return tl.get().peek();
        }
	}

	volatile static int accessCounter = 0;
    public void beginTransaction(MethodInvocation mi) {
		Session session = getSessionFactory().openSession();
		log.info("--- DBセッションが確保できました。 ");
		Transaction t = session.beginTransaction();
		transactionMap.get().put(session, t);
		tl.get().push(session);
        
		String methodName = mi.getMethod().getName();
		if(log.isInfoEnabled()) {
			log.info("トランザクションを開始します : " + mi.getMethod().getDeclaringClass().getName() + "#" + methodName);
		}
		accessCounter++;
		if(log.isInfoEnabled()) {
			log.info("transaction accessCounter : " + accessCounter);
		}
	}

    public void rollback(MethodInvocation mi) {
        init();
		String methodName = mi.getMethod().getName();
		if(log.isInfoEnabled()) {
			log.info("ロールバックします : " + mi.getMethod().getDeclaringClass().getName() + "#" + methodName);
		}

		Session session = (Session)((Stack)tl.get()).pop();
		try {
			Transaction t = (Transaction)((Map)transactionMap.get()).get(session);
			t.rollback();
		} catch(Exception e) {
			log.warn("ロールバック中に例外が発生しましたが無視します。", e);
		} finally {
            ((Map)transactionMap.get()).remove(session);
			accessCounter--;
			session.close();
		}
	}

	public void commit(MethodInvocation mi) throws VersionUnmuchException {
        init();
		String methodName = mi.getMethod().getName();
		if(log.isInfoEnabled()) {
			log.info("コミットします : " + mi.getMethod().getDeclaringClass().getName() + "#" + methodName);
		}

		Session session = (Session)((Stack)tl.get()).peek();
		Transaction t = (Transaction)((Map)transactionMap.get()).get(session);
		try {
			t.commit();
			session.close();
			((Stack)tl.get()).pop();
            ((Map)transactionMap.get()).remove(session);
		} catch(StaleObjectStateException e) {
			Class[] exceptionTypes = mi.getMethod().getExceptionTypes();
			for(int i = 0 ; i < exceptionTypes.length ; i++) {
				Class exceptionType = exceptionTypes[i];
				if(VersionUnmuchException.class.isAssignableFrom(exceptionType)) {
					log.debug(e.getMessage(), e);
					throw new VersionUnmuchException(e.getMessage());
				}
			}
			throw e;
		} finally{
			accessCounter--;
		}
	}

	/* (non-Javadoc)
	 * @see jp.rough_diamond.framework.transaction.ConnectionManager#clearCache()
	 */
	@Override
	public void clearCache() {
    	SessionFactory sf = getSessionFactory();
    	if(sf instanceof SessionFactoryImplementor) {
    		SessionFactoryImplementor sfi = (SessionFactoryImplementor)sf;
    		sfi.evictQueries();
    		String[] regions = sfi.getStatistics().getSecondLevelCacheRegionNames();
    		for(String region : regions) {
    			if(sfi.getClassMetadata(region) != null) {
    				EntityPersister p = sfi.getEntityPersister(region);
    				if(p.hasCache()) {
    					p.getCache().clear();
    				}
    			}
    		}
    	}
	}

	private String hibernateConfigName;
	public String getHibernateConfigName() {
		return hibernateConfigName;
	}

	//DI用！！
	public void setHibernateConfigName(String hibernateConfigName) {
		this.hibernateConfigName = hibernateConfigName;
	}

	private String addingPropertyFileName;
	public String getAddingPropertyFileName() {
		return addingPropertyFileName;
	}

	//DI用！！
	public void setAddingPropertyFileName(String addingPropertyFileName) {
		this.addingPropertyFileName = addingPropertyFileName;
	}
	
	private Interceptor interceptor;
	public Interceptor getInterceptor() {
		return interceptor;
	}

	//DI用！！
	public void setInterceptor(Interceptor interceptor) {
		this.interceptor = interceptor;
	}
	
	private Map<String, List<String>> listenersMap;
	public Map<String, List<String>> getListenersMap() {
		return listenersMap;
	}

	//DI用！！
	public void setListenersMap(Map<String, List<String>> listenersMap) {
		this.listenersMap = listenersMap;
	}
}
