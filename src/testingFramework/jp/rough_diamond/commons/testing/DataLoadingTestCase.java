/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.testing;

import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import jp.rough_diamond.commons.di.AbstractDIContainer;
import jp.rough_diamond.commons.di.CompositeDIContainer;
import jp.rough_diamond.commons.di.DIContainer;
import jp.rough_diamond.commons.di.DIContainerFactory;
import jp.rough_diamond.commons.di.MapDIContainer;
import jp.rough_diamond.commons.service.BasicService;
import jp.rough_diamond.commons.service.hibernate.H2DialectExt;
import jp.rough_diamond.commons.util.PropertyUtils;
import jp.rough_diamond.framework.service.Service;
import jp.rough_diamond.framework.service.ServiceFinder;
import jp.rough_diamond.framework.service.ServiceLocator;
import jp.rough_diamond.framework.service.ServiceLocatorLogic;
import jp.rough_diamond.framework.service.SimpleServiceLocatorLogic;
import jp.rough_diamond.framework.transaction.ConnectionManager;
import jp.rough_diamond.framework.transaction.hibernate.HibernateConnectionManager;
import junit.framework.TestCase;

/**
 * DBUnitを利用してデータをローディングしているテストケース
 */
public abstract class DataLoadingTestCase extends TestCase {
	private final static Log log = LogFactory.getLog(DataLoadingTestCase.class);
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setUpDB();
    }

    public static void setUpDB() throws Exception {
    	initializer.initialize();
        DBInitializer.clearModifiedClasses();
        DIContainer org = DIContainerFactory.getDIContainer();
        DIContainerFactory.setDIContainer(new DIContainerExt(org));
    }
    
    @Override
    protected void tearDown() throws Exception {
    	try {
    		cleanUpDB();
    	} finally {
    		super.tearDown();
    	}
    }
    
    public static void cleanUpDB() throws Exception {
    	DIContainer di = DIContainerFactory.getDIContainer();
    	if(di instanceof DIContainerExt) {
	        DIContainerExt ext = (DIContainerExt)di;
	        DIContainerFactory.setDIContainer(ext.org);
    	}
        DBInitializer.clearModifiedData();
        initializer.cleanUp();
    }
    
    static class DIContainerExt extends AbstractDIContainer {
    	DIContainer org;
    	DIContainerExt(DIContainer org) {
    		this.org = org;
    	}
    	static ServiceLocatorLogic sllExt;
		@SuppressWarnings("unchecked")
		@Override
		public synchronized <T> T getObject(Class<T> arg0, Object arg1) {
			if(arg1.equals(ServiceLocator.SERVICE_LOCATOR_KEY)) {
				synchronized(this) {
					if(sllExt == null) {
						log.debug("ServiceLocatorLogicの取得要求です");
						DIContainer current = DIContainerFactory.getDIContainer();
						DIContainerFactory.setDIContainer(org);
						try {
							ServiceLocatorLogic orgLogic = ServiceLocatorLogic.getServiceLocatorLogic();
							sllExt = (ServiceLocatorLogic)new ServiceLocatorLogicExt(orgLogic);
						} finally {
							DIContainerFactory.setDIContainer(current);
						}
					}
					return (T)sllExt;
				}
			}
			return org.getObject(arg0, arg1);
		}

		@Override
		public <T> T getSource(Class<T> arg0) {
			return org.getSource(arg0);
		}
    }
    
    static class ServiceLocatorLogicExt extends SimpleServiceLocatorLogic {
    	private ServiceLocatorLogic org;
    	ServiceLocatorLogicExt(ServiceLocatorLogic org) {
    		this.org = org;
    	}
		@Override
    	public <T extends Service> T getService(Class<T> cl, Class<? extends T> defaultClass) {
    		if(cl.equals(BasicService.class)) {
    			return super.getService(cl, defaultClass);
    		} else {
    			return org.getService(cl, defaultClass);
    		}
    	}

    	protected ServiceFinder getFinder() {
        	return new ServiceFinder() {
				@SuppressWarnings("unchecked")
				@Override
				public <T extends Service> T getService(Class<T> cl, Class<? extends T> defaultClass) {
					log.debug("BasicServiceを作成します");
					BasicService service = (BasicService)org.getService(cl, defaultClass);
					ProxyFactory pf = new ProxyFactory(service);
					MethodInterceptor mi = new MethodInterceptor() {
						@Override
						public Object invoke(MethodInvocation arg0) throws Throwable {
							log.debug("call deleteAll");
							Object ret = arg0.proceed();
							DBInitializer.addModifiedClasses((Class)arg0.getArguments()[0]);
							return ret;
						}
					};
					NameMatchMethodPointcut pc = new NameMatchMethodPointcut();
					pc.addMethodName("deleteAll");
					pf.addAdvisor(new DefaultPointcutAdvisor(pc, mi));
					pf.setOptimize(true);
					return (T)pf.getProxy();
				}
        	};
        }
    }

    static Initializer initializer = Initializer.INIT;
    static enum Initializer {
    	INIT {
			@Override
			void initialize() {
				try {
					orgDI = DIContainerFactory.getDIContainer();
					replaceDI = orgDI;
					String useInMemoryDB = (String)DIContainerFactory.getDIContainer().getObject("useInMemoryDBWhenTest");
					Boolean b = Boolean.valueOf(useInMemoryDB);
					if(!b) {
						return;
					}
					ConnectionManager cm = ConnectionManager.getConnectionManager();
					if(!(cm instanceof HibernateConnectionManager)) {
						return;
					}
					HibernateConnectionManager hcm = (HibernateConnectionManager)cm;
					InMemoryHibernateConnectionManager hcm2 = new InMemoryHibernateConnectionManager();
					PropertyUtils.copyProperties(hcm, hcm2);
					Map<Object, Object> map = new HashMap<Object, Object>();
					map.put(ConnectionManager.CONNECTION_MANAGER_KEY, hcm2);
					replaceDI = new CompositeDIContainer(new MapDIContainer(map),
							DIContainerFactory.getDIContainer());
					DIContainerFactory.setDIContainer(replaceDI);
				} finally {
					initializer = NULL;
				}
			}
		},
    	NULL {
			@Override
			void initialize() {
				DIContainerFactory.setDIContainer(replaceDI);
			}
		},
    	;
		static DIContainer replaceDI;
		static DIContainer orgDI;
    	abstract void initialize();
    	void cleanUp() {
			DIContainerFactory.setDIContainer(orgDI);
    	}
    }
    
    static class InMemoryHibernateConnectionManager extends HibernateConnectionManager {
    	@Override
    	protected Configuration addingProperties() {
    		Configuration cfg = super.addingProperties();
    		cfg.setProperty(Environment.DRIVER, 				"org.h2.Driver");
    		cfg.setProperty(Environment.URL, 					"jdbc:h2:mem:mymemdb");
    		cfg.setProperty(Environment.USER, 					"SA");
    		cfg.setProperty(Environment.PASS, 					"");
    		cfg.setProperty(Environment.DIALECT, 				H2DialectExt.class.getName());
    		cfg.setProperty(Environment.DEFAULT_SCHEMA, 		"PUBLIC");
    		cfg.setProperty(Environment.HBM2DDL_AUTO, 			"create");
    		cfg.setProperty(Environment.STATEMENT_BATCH_SIZE, 	"0");
    		return cfg;
    	}
    	
    	@Override
    	protected Configuration newConfiguration() {
    		return new ConfigurationExt();
    	}
    }
    
    static class ConfigurationExt extends Configuration {
		private static final long serialVersionUID = 6832918783093002571L;
		@Override
		public String[] generateSchemaCreationScript(Dialect dialect) throws HibernateException {
			String[] ret = super.generateSchemaCreationScript(dialect);
			for(int i = 0 ; i < ret.length ; i++) {
				if(ret[i].indexOf("foreign key") >= 0 && !ret[i].endsWith("on delete cascade")) {
					ret[i] = ret[i] + " on delete cascade";
				}
			}
			return ret;
		}
    }
}
