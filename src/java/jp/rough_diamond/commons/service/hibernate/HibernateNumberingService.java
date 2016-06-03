/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.service.hibernate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.metadata.ClassMetadata;

import jp.rough_diamond.commons.extractor.Condition;
import jp.rough_diamond.commons.extractor.Extractor;
import jp.rough_diamond.commons.service.BasicService;
import jp.rough_diamond.commons.service.NumberingService;
import jp.rough_diamond.framework.transaction.hibernate.HibernateUtils;

public class HibernateNumberingService extends NumberingService {
	private final GetNumberStrategy strategy;
	
	public HibernateNumberingService() {
		this(false);
	}
	
	public HibernateNumberingService(int cashSize) {
		this(cashSize, false);
	}
	
	public HibernateNumberingService(boolean skipCheckPK) {
		this(1, skipCheckPK);
	}
	
	public HibernateNumberingService(int cashSize, boolean skipCheckPK) {
		super(cashSize);
		strategy = (skipCheckPK) 
			? new SkipCheckPKStrategy()
			: new CheckPKStrategy();
	}
	
    /**
     * 指定されたクラスに対応するテーブルの主キーで
     * 利用されていない最適なナンバーを返却する
     * その際に利用されるキーはエンティティ名となる（Hibernate依存）
     * @param entityClass
     * @return  ナンバー
     */
	@Override
    public synchronized <T> Serializable getNumber(Class<T> entityClass) {
		NumberGenerateInfo info = getGenerateInfo(entityClass);
		return strategy.getPK(info);
    }
	
	@SuppressWarnings("unchecked")
	NumberGenerateInfo getGenerateInfo(Class<?> cl) {
		NumberGenerateInfo ret = genMap.get(cl);
		if(ret != null) {
			return ret;
		}
        Session session = HibernateUtils.getSession();
        SessionFactory sf = session.getSessionFactory();
        ClassMetadata cm = sf.getClassMetadata(cl);
        Supplimenter supplimenter = NUMBERING_ALLOWED_CLASSES.get(cm.getIdentifierType().getReturnedClass());
        if(supplimenter == null) {
            throw new RuntimeException("主キーが自動採番対象オブジェクトではありません");
        }
        Configuration config = HibernateUtils.getConfig();
        PersistentClass pc = config.getClassMapping(cm.getMappedClass(EntityMode.POJO).getName());
        Property prop = pc.getIdentifierProperty();
        Iterator<Column> iter = prop.getColumnIterator();
        int length = -1;
        if(iter.hasNext()) {
        	Column col = iter.next();
        	length = col.getLength();
        } else {
        	throw new RuntimeException("なんか変");
        }
        ret = new NumberGenerateInfo(cl, cm.getEntityName(), length, supplimenter, prop.getName());
        genMap.put(cl, ret);
        return ret;
	}
	
	static class NumberGenerateInfo {
		final Class<?> entityClass;
		final String entityName;
		final int length;
		final Supplimenter supplimenter;
		final String identifierName;
		NumberGenerateInfo(Class<?> entityClass, String entityName, int length, Supplimenter supplimenter, String identifierName) {
			this.entityClass = entityClass;
			this.entityName = entityName;
			this.length = length;
			this.supplimenter = supplimenter;
			this.identifierName = identifierName;
		}
	}
	
	Map<Class<?>, NumberGenerateInfo> genMap = new HashMap<Class<?>, NumberGenerateInfo>();
	
	interface GetNumberStrategy {
		Serializable getPK(NumberGenerateInfo info);
	}
	class SkipCheckPKStrategy implements GetNumberStrategy {
		@Override
		public Serializable getPK(NumberGenerateInfo info) {
	        long ret = getNumber(info.entityName);
            return info.supplimenter.suppliment(ret, info.length);
		}
	}
	class CheckPKStrategy implements GetNumberStrategy {
		@Override
		public Serializable getPK(NumberGenerateInfo info) {
	        long ret = getNumber(info.entityName);
	        long stop = ret - 1;
	        BasicService bs = BasicService.getService();
	        while(ret != stop) {
	            Serializable ser = info.supplimenter.suppliment(ret, info.length);
	        	Extractor ex = new Extractor(info.entityClass);
	        	ex.add(Condition.eq(new jp.rough_diamond.commons.extractor.Property(info.identifierName), ser));
	        	if(bs.getCountByExtractor(ex) == 0) {
	        		return ser;
	        	}
	            ret = getNumber(info.entityName);
	        }
	        throw new RuntimeException("いっぱいいっぱいです");
		}
	}
}
