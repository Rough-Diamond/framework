/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.transaction.hibernate;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Projections;
import org.hibernate.dialect.Dialect;
import org.hibernate.impl.SessionFactoryImpl;

public class HibernateUtils {
    public final static String BOOLEAN_CHAR_T = "Y";
    public final static String BOOLEAN_CHAR_F = "N";
    
	public static int getCountForCriteria(Criteria criteria) {
		Integer ret = (Integer)criteria.setProjection(Projections.rowCount()).uniqueResult();
		criteria.setProjection(null);
		criteria.setResultTransformer(Criteria.ROOT_ENTITY);
		return ret.intValue();
	}
	
    public static Configuration getConfig() {
        return HibernateConnectionManager.getConfig();
    }
    
	public static Session getSession() {
		return HibernateConnectionManager.getCurrentSession();
	}
    
    public static void rebuildSessionFactory() {
        HibernateConnectionManager.rebuildSessionFactory();
    }
    
    public static Dialect getDialect() {
    	Session session = getSession();
    	SessionFactory sf = session.getSessionFactory();
    	if(sf instanceof SessionFactoryImpl) {
    		SessionFactoryImpl sfi = (SessionFactoryImpl)sf;
    		return sfi.getDialect();
    	} else {
    		return Dialect.getDialect();
    	}
    }
}
