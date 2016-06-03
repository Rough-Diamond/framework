package jp.rough_diamond.commons.testing;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.rough_diamond.commons.lang.ClassUtils;
import jp.rough_diamond.framework.service.Service;
import jp.rough_diamond.framework.service.ServiceLocator;
import jp.rough_diamond.framework.transaction.ConnectionManager;
import jp.rough_diamond.framework.transaction.hibernate.HibernateUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.operation.DeleteAllOperation;
import org.hibernate.Interceptor;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.mapping.PersistentClass;

@SuppressWarnings("unchecked")
abstract public class DBInitializer implements Service {
    private final static Log log = LogFactory.getLog(DBInitializer.class);
    private static List<DBInitializer> list;
    private static Map<String, Class> entityMap;
    private static Map<Class, Set<DBInitializer>> initializerObjects; 
    static Set<Class> modifiedClasses;
    private static TmpService service;
    
    private final DatabaseOperation INSERT = new InsertOperationExt(this);
    
    public final static String TEST_DATA_CONTROLER = "RDF_TEST_DATA_CONTROLER";
    
    public DBInitializer() {
    	init();
    }
    
    static boolean isInit = false;
    synchronized static void init() {
    	if(!isInit) {
	    	try {
		        service = ServiceLocator.getService(TmpService.class); 
		        service.init();
		        list = new ArrayList<DBInitializer>();
		        initializerObjects = new HashMap<Class, Set<DBInitializer>>();
		        modifiedClasses = new HashSet<Class>();
		        isInit = true;
	    	} catch(Exception e) {
	    		throw new ExceptionInInitializerError(e);
	    	}
    	}
    }
    
    static void clearModifiedClasses() {
    	init();
        modifiedClasses.clear();
    }
    
    /**
     * 更新対象クラス名を追加する
     * 基本的には呼ばなくて良い
     * @param cl
     */
    public static void addModifiedClasses(Class cl) {
    	init();
        modifiedClasses.add(cl);
    }
    
    static void clearModifiedData() throws Exception {
    	init();
        int minimumIndex = Integer.MAX_VALUE;
        for(Class cl : modifiedClasses) {
            Set<DBInitializer> set = initializerObjects.get(cl);
            if(set == null) {
                continue;
            }
            for(int i = 0 ; i < list.size() ; i++) {
                if(set.contains(list.get(i))) {
                    minimumIndex = Math.min(minimumIndex, i);
                    break;
                }
            }
        }
        if(minimumIndex >= list.size()) {
            return;
        }
        for(int i = list.size() -1 ; i >= minimumIndex ; i--) {
            service.clearData(list.get(i));
        }
        list = list.subList(0, minimumIndex);
        ConnectionManager.getConnectionManager().clearCache();
    }
    
	public static class TmpService implements Service {
        public void replaceInterceptor() {
            Configuration config = HibernateUtils.getConfig();
            Interceptor org = config.getInterceptor();
            Interceptor tmp = new InterceptorImpl(org);
            config.setInterceptor(tmp);
            HibernateUtils.rebuildSessionFactory();
            
            entityMap = new HashMap<String, Class>();
            Iterator iterator = HibernateUtils.getConfig().getClassMappings();
            while(iterator.hasNext()) {
                PersistentClass pc = (PersistentClass)iterator.next();
                entityMap.put(pc.getTable().getName().toUpperCase(), pc.getMappedClass());
            }
        }
        
        public void init() throws SQLException {
        	replaceInterceptor();
        	createTestDataTable();
        }
        
        void createTestDataTable() throws SQLException {
        	Connection con = ConnectionManager.getConnectionManager().getCurrentConnection(null);
        	String schema = HibernateUtils.getConfig().getProperty(Environment.DEFAULT_SCHEMA);
        	if(!DatabaseUtils.isExistsTable(con, schema, TEST_DATA_CONTROLER)) {
        		Statement stmt = con.createStatement();
        		try {
        			stmt.execute(
        					String.format("create table %s(name varchar(4000), test_table varchar(32), ts varchar(20))",
        							TEST_DATA_CONTROLER));
        		} finally {
        			stmt.close();
        		}
        	}
        }
        
        public void clearData(DBInitializer initializer) throws Exception {
            initializer.delete();
        }
    }
    
    public void delete() throws Exception {
    	if(getDriverName().toUpperCase().indexOf("MYSQL") != -1) {
    		execute(DatabaseOperation.TRUNCATE_TABLE, getResourceNames());
    	} else {
        	execute(DatabaseOperation.DELETE_ALL, getResourceNames());
    	}
    }

    public void load() throws Exception {
        if(isAlreadyLoading()) {
            log.debug("既にロードされているため処理を行いません。");
            return;
        }
        String[] resourceNames = getResourceNames();
        execute(INSERT, resourceNames);
        list.add(this);
    }
    
    void addInitializedObject(String name) {
        Class cl = entityMap.get(name.toUpperCase());
        Set<DBInitializer> set = initializerObjects.get(cl);
        if(set == null) {
            set = new HashSet<DBInitializer>();
            initializerObjects.put(cl, set);
        }
        set.add(this);
    }
    
    public void cleanInsert() throws Exception {
        if(isAlreadyLoading()) {
            log.debug("既にロードされているため処理を行いません。");
            return;
        }
        delete();
        load();
    }
    
    private boolean isAlreadyLoading() {
        for(DBInitializer target : list) {
            if(target == this) {
                return true;
            }
        }
        return false;
    }
    
    private String[] resources = null;
    protected String[] getResourceNames() {
    	if(resources != null) {
    		return resources;
    	}
    	ResourceNames rn = this.getClass().getAnnotation(ResourceNames.class);
    	if(rn == null) {
    		throw new RuntimeException("ResourceNameアノテーションを付与するか、getResourceNamesをオーバーライドしてください");
    	}
    	List<String> resources = new ArrayList<String>();
    	for(String resource : rn.resources()) {
    		resources.addAll(getResources(resource));
    	}
    	this.resources = resources.toArray(new String[resources.size()]);
    	return this.resources;
    }
    
	Collection<? extends String> getResources(String resource) {
		if(resource.endsWith(".xls") || resource.endsWith(".xml")) {
			return Arrays.asList(getResource(resource));
		} else {
			return getResourcesByClass(resource);
		}
	}

	List<String> getResourcesByClass(String className) {
		List<String> ret = new ArrayList();
		//絶対パスで他DBInitializerが指定されているか？
		try {
			Class<DBInitializer> cl = (Class<DBInitializer>)Class.forName(className);
			DBInitializer target = ServiceLocator.getService(cl);
			ret.addAll(Arrays.asList(target.getResourceNames()));
			return ret;
		} catch (ClassNotFoundException e) {
		}
		try {
			className = getResourcePath(className).replace('/', '.');
			Class<DBInitializer> cl = (Class<DBInitializer>)Class.forName(className);
			DBInitializer target = ServiceLocator.getService(cl);
			ret.addAll(Arrays.asList(target.getResourceNames()));
			return ret;
		} catch (ClassNotFoundException e) {
		}
		throw new RuntimeException(className + "に対応するDBInitializerが存在しません。");
	}
	
	String getResource(String resource) {
		//絶対パスのリソースが指定されているか？
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(resource);
		if(is != null) {
			return resource;
		}
		//指定されていない場合は相対パスで探しに行く
		resource = getResourcePath(resource);
		is = this.getClass().getClassLoader().getResourceAsStream(resource);
		if(is != null) {
			return resource;
		}
		throw new RuntimeException(resource + "に対応するリソースが存在しません。");
	}

	//ディレクトリトラバースにしたのは、getCanonicalPathが異様に遅いため
	String getResourcePath(String resource) {
		String classPath = ClassUtils.translateResourceName(this.getClass());
		File f = new File(classPath).getParentFile();
		resource = resource.replace('\\', '/');
		String[] pathes = resource.split("\\/");
		for(String path : pathes) {
			if("..".equals(path)) {
				f = f.getParentFile();
			} else {
				f = new File(f, path);
			}
		}
		return f.getPath().replace('\\', '/');
	}
	
	private String getDriverName() {
		String driverClassName = HibernateUtils.getConfig().getProperty(Environment.DRIVER);
		return driverClassName;
	}

	private static Map<String, IDataSet> datasetMap = new HashMap<String, IDataSet>();
    protected void execute(DatabaseOperation operation, String... resourceNames) throws Exception {
    	String schema = HibernateUtils.getConfig().getProperty(Environment.DEFAULT_SCHEMA);
    	String driverClassName = getDriverName();
    	
		String[] tmpArray = new String[resourceNames.length];
        System.arraycopy(resourceNames, 0, tmpArray, 0, tmpArray.length);
        List<String> tmp = Arrays.asList(tmpArray);
        if(operation instanceof DeleteAllOperation) {
            Collections.reverse(tmp);
        }
        ConnectionManager cm = ConnectionManager.getConnectionManager();
        //TODO nullでよいかなぁ。。。
        IDatabaseConnection idc = new DatabaseConnection(cm.getCurrentConnection(null), schema);
        if(driverClassName.toUpperCase().indexOf("ORACLE") != -1) {
        	//Oracleの拡張
        	idc.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new OracleDataTypeFactory());
        }
        for(String name : tmp) {
        	try {
        		if(log.isDebugEnabled()) {
        			log.debug(name + ":" + operation.getClass().getName());
        		}
        		IDataSet dataset = datasetMap.get(name);
        		if(dataset == null) {
	        		if(name.endsWith("xls")) {
	        			dataset = new XlsDataSet(
	    	                    this.getClass().getClassLoader().getResourceAsStream(name)); 
	        		} else if(name.endsWith(".xml")) {
	        			dataset = new XmlDataSet(
	    	                    this.getClass().getClassLoader().getResourceAsStream(name)); 
	        		} else {
	        			throw new RuntimeException();
	        		}
	        		dataset = new DataSetProxy(name, dataset);
	        		datasetMap.put(name, dataset);
        		}
	            operation.execute(idc, dataset);
        	} catch(RuntimeException e) {
        		log.warn("以下のリソースを実行中に例外が発生しました：[" + name + "]");
        		throw e;
        	}
        }
    }
}
