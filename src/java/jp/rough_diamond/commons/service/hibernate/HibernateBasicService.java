/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.service.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.EntityMode;
import org.hibernate.LockMode;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.proxy.HibernateProxy;

import jp.rough_diamond.commons.extractor.ExtractValue;
import jp.rough_diamond.commons.extractor.Extractor;
import jp.rough_diamond.commons.extractor.Property;
import jp.rough_diamond.commons.resource.Messages;
import jp.rough_diamond.commons.resource.MessagesIncludingException;
import jp.rough_diamond.commons.service.BasicService;
import jp.rough_diamond.commons.service.CallbackEventType;
import jp.rough_diamond.commons.service.NumberingService;
import jp.rough_diamond.commons.service.RelationalChecker;
import jp.rough_diamond.commons.service.WhenVerifier;
import jp.rough_diamond.commons.service.annotation.Check;
import jp.rough_diamond.framework.transaction.VersionUnmuchException;
import jp.rough_diamond.framework.transaction.hibernate.FlushListener;
import jp.rough_diamond.framework.transaction.hibernate.HibernateConnectionManager;
import jp.rough_diamond.framework.transaction.hibernate.HibernateUtils;
import jp.rough_diamond.framework.transaction.hibernate.SaveOrUpdateListener;

public class HibernateBasicService extends BasicService {
    private final static Log log = LogFactory.getLog(HibernateBasicService.class);

    private LockMode getLockMode(RecordLock lock) {
    	switch (lock) {
		case NONE:
			return LockMode.NONE;
		case FOR_UPDATE:
			return LockMode.UPGRADE;
		case FOR_UPDATE_NOWAIT:
			return LockMode.UPGRADE_NOWAIT;
		default:
			throw new RuntimeException();
		}
    }

    private ThreadLocal<Boolean> isLoading = new ThreadLocal<Boolean>();
	@Override
    @SuppressWarnings("unchecked")
    public <T> T findByPK(Class<T> type, Serializable pk, boolean isNoCache, RecordLock lock) {
    	BasicServiceInterceptor.startLoad(isNoCache);
        try {
        	isLoading.set(Boolean.TRUE);
            T ret = (T)HibernateUtils.getSession().get(type, pk, getLockMode(lock));
        	isLoading.remove();
            if(ret != null) {
                List<Object> loadedObjects = BasicServiceInterceptor.popPostLoadedObjects();
                fireEvent(CallbackEventType.POST_LOAD, loadedObjects);
                if(isNoCache) {
    	            Session session = HibernateUtils.getSession();
    	            for(Object o : loadedObjects) {
    	            	session.evict(o);
    	            }
                }
            }
            return ret;
        } catch(Exception e) {
            throw new RuntimeException(e);
        } finally {
        	isLoading.remove();
        	BasicServiceInterceptor.setNoCache(false);
        	BasicServiceInterceptor.popPostLoadedObjects();
        }
    }

	@Override
    protected <T> List<T> findByExtractor(Class<T> type, Extractor extractor, boolean isNoCache, RecordLock lock) {
    	BasicServiceInterceptor.startLoad(isNoCache);
        try {
        	return findByExtractor2(type, extractor, isNoCache, lock);
        } catch(Exception e) {
            throw new RuntimeException(e);
        } finally {
        	isLoading.remove();
        	BasicServiceInterceptor.setNoCache(false);
        	BasicServiceInterceptor.popPostLoadedObjects();
        }
    }

	@Override
	public <T> long getCountByExtractor(Extractor extractor) {
		if(extractor.getValues().size() == 0) {
	        Query countQuery = Extractor2HQL.extractor2CountQuery(extractor);
	        if(extractor.isCachable()) {
	        	countQuery.setCacheable(true);
	        }
	        Number n = (Number)countQuery.list().get(0);
	        return n.longValue();
		} else {
			PreparedStatement pstmt = Extractor2HQL.extractor2PreparedStatement(extractor);
	        if(extractor.isCachable()) {
	        	log.info("this query doesn't cache!");
	        }
			SQLException sqlex = null;
			try {
				ResultSet rs = pstmt.executeQuery();
				try {
					rs.next();
					return rs.getLong(1);
				} finally {
					rs.close();
				}
			} catch (SQLException e) {
				sqlex = e;
				throw new RuntimeException(e);
			} finally {
				try {
					pstmt.close();
				} catch (SQLException e) {
					sqlex = (sqlex == null) ? e : sqlex;
					throw new RuntimeException(sqlex);
				}
			}
		}
	}
	
    @SuppressWarnings("unchecked")
    <T> List<T> findByExtractor2(Class<T> type, Extractor extractor, boolean isNoCache, RecordLock lock) throws VersionUnmuchException, MessagesIncludingException {
        List<T> list;
        if(extractor.getLimit() != 0) {
	        Query query = Extractor2HQL.extractor2Query(extractor, getLockMode(lock));
	        if(extractor.isCachable()) {
		        query.setCacheable(true);
	        }
        	isLoading.set(Boolean.TRUE);
        	List<T> listTmp = (List<T>) Extractor2HQL.makeList(type, extractor, query.list());
        	isLoading.remove();
        	list = new ArrayList<T>(listTmp.size());
        	for(T o : listTmp) {
        		if(isProxy(o)) {
        			try {
	        			ClassMetadata cm = getRealType((Class<?>)o.getClass());
	        			o = (T)HibernateUtils.getSession().get(Class.forName(cm.getEntityName()), 
	        								cm.getIdentifier(o, EntityMode.POJO), getLockMode(lock));
	        			BasicServiceInterceptor.addPostLoadObject(o);
        			} catch(ClassNotFoundException e) {
        				throw new RuntimeException(e);
        			}
        		}
        		list.add(o);
        	}
	        List<Object> loadedObjects = BasicServiceInterceptor.popPostLoadedObjects();
	        fireEvent(CallbackEventType.POST_LOAD, loadedObjects);
	        if(isNoCache) {
	        	Session session = HibernateUtils.getSession();
	        	for(Object o : loadedObjects) {
	        		session.evict(o);
	        	}
	        }
        } else {
        	list = new ArrayList<T>();
        }
        return list;
    }
    
	@Override
    @SuppressWarnings("unchecked")
    public <T> void insert(T... objects) throws MessagesIncludingException {
        try {
            SessionFactory sf = HibernateUtils.getSession().getSessionFactory();
            for(Object o : objects) {
                ClassMetadata cm = sf.getClassMetadata(o.getClass());
                if(cm.getIdentifier(o, EntityMode.POJO) == null &&
                        NumberingService.isAllowedNumberingType(cm.getIdentifierType().getReturnedClass())) {
                    NumberingService ns = NumberingService.getService();
                    cm.setIdentifier(o, ns.getNumber(getSuperMappedClass(o.getClass())), EntityMode.POJO);
                }
            }
            Messages msgs = new Messages();
            for(Object o : objects) {
                msgs.add(validate(o, WhenVerifier.INSERT));
            }
            if(!msgs.hasError()) {
                for(Object o : objects) {
                    try {
	                    List<Object> list = new ArrayList<Object>();
	                    list.add(o);
	               		fireEvent(CallbackEventType.PRE_PERSIST, list);
	                    msgs.add(checkUnique(o, WhenVerifier.INSERT));
                    } catch(MessagesIncludingException e) {
                    	msgs.add(e.getMessages());
                    }
                }
            }
        	if(!msgs.hasError()) {
                for(Object o : objects) {
            		HibernateUtils.getSession().save(o);
                    List<Object> list = new ArrayList<Object>();
                    list.add(o);
                    fireEvent(CallbackEventType.POST_PERSIST, list);
                }
        	}
        	if(msgs.hasError()) {
        		throw new MessagesIncludingException(msgs);
        	}
        } catch(MessagesIncludingException e) {
        	throw e;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

	@SuppressWarnings("unchecked")
	private Class getSuperMappedClass(Class cl) {
        PersistentClass pc = HibernateConnectionManager.getConfig().getClassMapping(cl.getName());
        while(pc.isInherited()) {
        	pc = pc.getSuperclass();
        }
        return pc.getMappedClass();
    }

	@Override
    public <T> void update(T... objects) throws VersionUnmuchException, MessagesIncludingException {
        boolean isLogicalDelete = isLogicalDelete(new Throwable());
        Messages msgs = new Messages();
        if(!isLogicalDelete) {
            for(Object o : objects) {
                msgs.add(validate(o, WhenVerifier.UPDATE));
            }
        }
        //論理削除でもユニークチェックはいるねん！！
        if(!msgs.hasError()) {
            for(Object o : objects) {
                try {
                    if(!isLogicalDelete) {
	                    List<Object> list = new ArrayList<Object>();
	                    list.add(o);
	               		fireEvent(CallbackEventType.PRE_UPDATE, list);
                    }
                    msgs.add(checkUnique(o, WhenVerifier.UPDATE));
                } catch(MessagesIncludingException e) {
                	msgs.add(e.getMessages());
                }
            }
        }
        if(!msgs.hasError()) {
            for(Object o : objects) {
//            	ClassMetadata cm = HibernateUtils.getSession().getSessionFactory().getClassMetadata(o.getClass());
//            	String[] props = cm.getPropertyNames();
//        		System.out.println(o.getClass().getName());
//            	for(String prop : props) {
//            		if(cm.getPropertyType(prop).isEntityType()) {
//                		System.out.println(prop + ":" + isProxy(cm.getPropertyValue(o, prop, HibernateUtils.getSession().getEntityMode())));
//            		}
//            	}
            	try {
            		HibernateUtils.getSession().update(o);
            	} catch(NonUniqueObjectException e) {
            		log.warn("ユニーク例外が発生しました。オブジェクトをクリアして再実行します。");
            		Session session = HibernateUtils.getSession();
                	ClassMetadata cm = HibernateUtils.getSession().getSessionFactory().getClassMetadata(o.getClass());
                	Object evictTarget = session.get(o.getClass(), cm.getIdentifier(o, session.getEntityMode()));
                	session.evict(evictTarget);
                	session.update(o);
            	}
                if(!isLogicalDelete) {
                    List<Object> list = new ArrayList<Object>();
                    list.add(o);
                    fireEvent(CallbackEventType.POST_UPDATE, list);
                }
            }
    	}
        if(msgs.hasError()) {
        	throw new MessagesIncludingException(msgs);
        }
    }

    private boolean isLogicalDelete(Throwable t) {
        StackTraceElement el = t.getStackTrace()[1];
        if(el.getClassName().endsWith(".BasicService") &&
                el.getMethodName().equals("delete")) {
            return true;
        } else {
            return false;
        }
    }

	@Override
    @SuppressWarnings("unchecked")
    public void deleteAll(Class cl) {
        Session session = HibernateUtils.getSession();
        SessionFactory sf = session.getSessionFactory();
        ClassMetadata cm = sf.getClassMetadata(cl);
        String hql = "delete from " + cm.getEntityName();
        log.debug(hql);
        Query query = session.createQuery(hql);
        int ret = query.executeUpdate();
        if(log.isDebugEnabled()) {
        	log.debug("削除件数：" + ret);
        }
    }

    @Override
    public void delete(Object... objects) throws VersionUnmuchException, MessagesIncludingException {
        Messages msgs = new Messages();
        RelationalChecker checker = RelationalChecker.getRerationalChecker();
        SessionFactory sf = HibernateUtils.getSession().getSessionFactory();
        for(Object o : objects) {
            try {
                checker.allowRemove(o);
                ClassMetadata cm = sf.getClassMetadata(o.getClass());
                if(cm.getIdentifier(o, EntityMode.POJO) == null) {
                    continue;
                }
//                if(o instanceof LogicalDeleteEntity) {
//                    LogicalDeleteEntity lde = (LogicalDeleteEntity)o;
//                    List<LogicalDeleteEntity> list = new ArrayList<LogicalDeleteEntity>();
//                    list.add(lde);
//                    lde.setDeleted(true);
//                    fireEvent(CallbackEventType.PRE_REMOVE, list);
//                    msgs.add(update(o));
//                    fireEvent(CallbackEventType.POST_REMOVE, list);
//                } else {
                    List<Object> list = new ArrayList<Object>();
                    list.add(o);
                    fireEvent(CallbackEventType.PRE_REMOVE, list);
                	try {
                        HibernateUtils.getSession().delete(o);
                	} catch(NonUniqueObjectException e) {
                		log.warn("ユニーク例外が発生しました。オブジェクトをクリアして再実行します。");
                		Session session = HibernateUtils.getSession();
                    	Object evictTarget = session.get(o.getClass(), cm.getIdentifier(o, session.getEntityMode()));
                    	session.evict(evictTarget);
                        HibernateUtils.getSession().delete(o);
                	}
                    fireEvent(CallbackEventType.POST_REMOVE, list);
//                }
            } catch(MessagesIncludingException e) {
                msgs.add(e.getMessages());
            }
        }
        if(msgs.hasError()) {
        	throw new MessagesIncludingException(msgs);
        }
    }

    @Override
    public Messages validate(Object o, WhenVerifier when) {
        Messages ret = super.validate(o, when);
    	try {
	        if(when == WhenVerifier.INSERT) {
	            RelationalChecker.getRerationalChecker().allowPersist(o);
	        } else {
	            RelationalChecker.getRerationalChecker().allowUpdate(o);
	        }
	        return ret;
        } catch(MessagesIncludingException e) {
            ret.add(e.getMessages());
        	return ret;
    	} catch(Exception ex) {
    		throw new RuntimeException(ex);
        }
    }

    @Override
	@SuppressWarnings("unchecked")
	protected List getMutchingObjects(Object o, Check check) {
    	Session session = HibernateUtils.getSession();
		session.evict(o);
		List list = super.getMutchingObjects(o, check);
		for(Object tmp : list) {
			session.evict(tmp);	//Hibernateの管理対象オブジェクトから除外（しないとうにゃうにゃする）
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see jp.rough_diamond.commons.service.BasicService#clearCache(java.lang.Object)
	 */
	@Override
	public void clearCache(Object o) {
		Session session = HibernateUtils.getSession();
		if(session != null) {
			session.evict(o);
		}
	}

	void firePostLoadDirect(Object o) {
		log.debug("予期しないload（lazy loading）が発生しました。POST_LOADイベントをFireします。");
		try {
			fireEvent(CallbackEventType.POST_LOAD, new ArrayList<Object>(Arrays.asList(o)));
		} catch (Exception e) {
            throw new RuntimeException(e);
		}
	}

	@Override
	protected Extractor getMutchingExtractor(Object o, Check check) {
		Extractor ex = super.getMutchingExtractor(o, check);
		ClassMetadata cm = HibernateUtils.getSession().getSessionFactory().getClassMetadata(o.getClass());
		ex.addExtractValue(new ExtractValue("pk", new Property(cm.getIdentifierPropertyName())));
		ex.setReturnType(cm.getIdentifierType().getReturnedClass());
		return ex;
	}
	
	@Override
	protected boolean compareUniqueObject(Object target, Object org) {
		ClassMetadata cm = HibernateUtils.getSession().getSessionFactory().getClassMetadata(target.getClass());
		Object pk = cm.getIdentifier(target, EntityMode.POJO);
		if(log.isDebugEnabled()) {
			log.debug(pk + ":" + org);
		}
		return pk.equals(org);
	}

	private ThreadLocal<Boolean> isUnitPropertyValidating = new ThreadLocal<Boolean>();
	@Override
    protected Messages unitPropertyValidate(Object o, WhenVerifier when) throws Exception {
		isUnitPropertyValidating.set(Boolean.TRUE);
		try {
			return super.unitPropertyValidate(o, when);
		} finally {
			isUnitPropertyValidating.remove();
		}
    }
    
    @SuppressWarnings("unchecked")
	@Override
	public <T> T replaceProxy(T proxy) {
		try {
			if(!isProxy(proxy)) {
				return proxy;
			}
			//フラッシュ中時点でProxyならProxyのままでよいはず
			if(FlushListener.isFlushing()) {
				return (T)proxy;
			}
			if(Boolean.TRUE.equals(isLoading.get())) {
				return (T)proxy;
			}
			if(Boolean.TRUE.equals(isUnitPropertyValidating.get())) {
				return (T)proxy;
			}
			if(Boolean.TRUE.equals(SaveOrUpdateListener.isSaveingOrUpdating())) {
				return (T)proxy;
			}
			ClassMetadata cm = getRealType((Class<?>)proxy.getClass());
			return (T)findByPK(Class.forName(cm.getEntityName()), cm.getIdentifier(proxy, EntityMode.POJO));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	static Map<Class<?>, ClassMetadata> realTypeMap = new HashMap<Class<?>, ClassMetadata>();
	@SuppressWarnings("unchecked")
	private ClassMetadata getRealType(Class<?> proxyType) {
		ClassMetadata ret = realTypeMap.get(proxyType);
		if(ret == null) {
			Class cl = proxyType;
			while(!cl.equals(Object.class) && ret == null) {
				ret = HibernateUtils.getSession().getSessionFactory().getClassMetadata(cl);
				cl = cl.getSuperclass();
			}
			if(ret == null) {
				throw new RuntimeException("ClassMetadata not found.");
			}
			realTypeMap.put(proxyType, ret);
		}
		return ret;
	}

	@Override
	protected ProxyChecker getProxyChecker() {
		return PROXY_CHECKER;
	}
	
	final static ProxyChecker PROXY_CHECKER = new HibernateProxyChecker();
	private static class HibernateProxyChecker implements ProxyChecker {
		@Override
		public boolean isProxy(Object target) {
			return (target instanceof HibernateProxy);
		}
	}
}
