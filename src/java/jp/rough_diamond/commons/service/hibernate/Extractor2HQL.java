/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.service.hibernate;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import jp.rough_diamond.commons.extractor.And;
import jp.rough_diamond.commons.extractor.Avg;
import jp.rough_diamond.commons.extractor.Case;
import jp.rough_diamond.commons.extractor.CombineCondition;
import jp.rough_diamond.commons.extractor.Condition;
import jp.rough_diamond.commons.extractor.Count;
import jp.rough_diamond.commons.extractor.DateToString;
import jp.rough_diamond.commons.extractor.Desc;
import jp.rough_diamond.commons.extractor.Eq;
import jp.rough_diamond.commons.extractor.ExtractValue;
import jp.rough_diamond.commons.extractor.Extractor;
import jp.rough_diamond.commons.extractor.FreeFormat;
import jp.rough_diamond.commons.extractor.Function;
import jp.rough_diamond.commons.extractor.Ge;
import jp.rough_diamond.commons.extractor.Gt;
import jp.rough_diamond.commons.extractor.In;
import jp.rough_diamond.commons.extractor.InnerJoin;
import jp.rough_diamond.commons.extractor.IsNotNull;
import jp.rough_diamond.commons.extractor.IsNull;
import jp.rough_diamond.commons.extractor.Join;
import jp.rough_diamond.commons.extractor.LabelHoldingCondition;
import jp.rough_diamond.commons.extractor.Le;
import jp.rough_diamond.commons.extractor.Like;
import jp.rough_diamond.commons.extractor.Lt;
import jp.rough_diamond.commons.extractor.Max;
import jp.rough_diamond.commons.extractor.Min;
import jp.rough_diamond.commons.extractor.NotEq;
import jp.rough_diamond.commons.extractor.NotIn;
import jp.rough_diamond.commons.extractor.Or;
import jp.rough_diamond.commons.extractor.Order;
import jp.rough_diamond.commons.extractor.Property;
import jp.rough_diamond.commons.extractor.RegularExp;
import jp.rough_diamond.commons.extractor.Sum;
import jp.rough_diamond.commons.extractor.SummaryFunction;
import jp.rough_diamond.commons.extractor.Value;
import jp.rough_diamond.commons.extractor.ValueHoldingCondition;
import jp.rough_diamond.commons.lang.StringUtils;
import jp.rough_diamond.commons.util.PropertyUtils;
import jp.rough_diamond.framework.transaction.hibernate.HibernateConnectionManager;
import jp.rough_diamond.framework.transaction.hibernate.HibernateUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.EntityMode;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Settings;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.engine.query.HQLQueryPlan;
import org.hibernate.impl.SessionFactoryImpl;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;
import org.hibernate.type.TypeFactory;

/**
 * ExtractorオブジェクトからHibernateのHQLを生成する
 * 本クラスは、Service層以外での動作保障はしない 
 */
@SuppressWarnings("unchecked")
public class Extractor2HQL {
    private final static Log log = LogFactory.getLog(Extractor2HQL.class);

    private final Extractor extractor;
    private StringBuilder builder;
    private int patemeterIndex;
    private boolean usingGroupBy = false;
    private int defaultMaxFetchDepth;
    
    private Extractor2HQL(Extractor extractor) {
        this.extractor = extractor;
        builder = new StringBuilder();
        patemeterIndex = 0;
        if(extractor.getFetchDepth() < 0) {
	        Settings settings = HibernateConnectionManager.getSettings();
	        if(settings == null) {
	        	throw new RuntimeException("connectionManagerにはHibernateConnectionManagerを指定してください。");
	        }
	        this.defaultMaxFetchDepth = settings.getMaximumFetchDepth();
        } else {
        	this.defaultMaxFetchDepth = extractor.getFetchDepth();
        }
    }
    
    /**
     * ExtractorオブジェクトからHibernateHQLを生成する
     * @param extractor 抽出条件格納オブジェクト
     * @return              HibernateのHQL
     */
    public static Query extractor2Query(Extractor extractor, LockMode lockMode) {
        Extractor2HQL tmp = new Extractor2HQL(extractor);
        Query q = tmp.makeQuery();
        tmp.setParameter(q, tmp.extractor);
        int offset = extractor.getOffset();
        if(offset > 0) {
            q.setFirstResult(offset);
        }
        int limit = extractor.getLimit();
        if(limit > 0) {
            q.setMaxResults(limit);
        }
        if(extractor.getValues().size() == 0) {
        	q.setLockMode(getAlias(extractor.target, extractor.targetAlias), lockMode);
        } else {
        	Type[] returnTypes = q.getReturnTypes();
        	List<ExtractValue> evs = extractor.getValues();
        	for(int i = 0 ; i < evs.size() ; i++) {
        		ExtractValue ev = evs.get(i);
        		if(ev.returnType != null) {
        			Type t = TypeFactory.basic(ev.returnType.getName());
           			returnTypes[i] = (t == null) ? returnTypes[i] : t;
        		}
        	}
        }
        if(extractor.getFetchSize() != Extractor.DEFAULT_FETCH_SIZE) {
        	q.setFetchSize(extractor.getFetchSize());
        }
        return q;
    }

	public static Query extractor2CountQuery(Extractor extractor) {
        Extractor2HQL tmp = new Extractor2HQL(extractor);
        Query q = tmp.makeCountQuery();
        tmp.setParameter(q, tmp.extractor);
        return q;
	}

	/**
	 * Extractorオブジェクトから件数取得用のPreparedStatementを生成/返却する
	 * こっちは、extractor.getValues().size() > 0の場合にのみ呼び出すこと。
	 * それ以外の場合はsubqueryを用いるため、性能劣化の原因になります。
	 * @param extractor
	 * @return
	 */
	public static PreparedStatement extractor2PreparedStatement(Extractor extractor) {
		try {
	        Extractor2HQL tmp = new Extractor2HQL(extractor);
	        Query q = tmp.makeQuery(false);
	        SessionImplementor session = (SessionImplementor)HibernateUtils.getSession();
			SessionFactoryImpl sfi = (SessionFactoryImpl)HibernateUtils.getSession().getSessionFactory();
			HQLQueryPlan hqp = sfi.getQueryPlanCache().getHQLQueryPlan(q.getQueryString(), false, session.getEnabledFilters());
			String sql = String.format("select count(*) from (%s) x", hqp.getSqlStrings()[0]);
			log.debug(sql);
			PreparedStatement pstmt;
			pstmt = session.connection().prepareStatement(sql);
			tmp.setParameter(pstmt, tmp.extractor);
	        return pstmt;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	/**
     * 抽出値からMapのリストを生成
     * @param list
     * @return
     * @deprecated
     */
	@Deprecated
    public static List<Map<String, Object>> makeMap(Extractor extractor, List<Object> list) {
        List<ExtractValue> values = extractor.getValues();
        return makeMap(values, list);
    }
    
    static List<Map<String, Object>> makeMap(List<ExtractValue> values, List<Object> list) {
    	List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
        MakeMapStrategy strategy = (values.size() == 1) ? SingleStrategy.INSTANCE : MultiStrategy.INSTANCE;
        for(Object row : list) {
        	Map<String, Object> rowMap = strategy.getMap(row, values);
            ret.add(rowMap);
        }
        return ret;
    }
    
    public static <T> List<T> makeList(final Class<T> returnType, Extractor extractor, List<Object> list) {
    	List<ExtractValue> values = extractor.getValues();
    	if(extractor.getValues().size() == 0) {
    		values = new ArrayList<ExtractValue>(){
				private static final long serialVersionUID = 1L;
			{
				add(new ExtractValue("entity", new Wrapper(returnType)));
    		}};
    	}
    	List<Map<String, Object>> mapList = makeMap(values, list);
    	if(returnType.isAssignableFrom(Map.class)) {
    		return (List<T>) mapList;
    	}
    	List<T> retList = new ArrayList<T>(mapList.size());
    	if(extractor.target.isAssignableFrom(returnType) && extractor.getValues().size() == 0) {
    		for(Map<String, Object> map : mapList) {
    			retList.add((T)map.values().toArray()[0]);
    		}
    	} else {
	    	for(Map<String, Object> map : mapList) {
	   			retList.add(makeInstance(returnType, map));
	    	}
    	}
    	return retList;
    }
   
    static <T> T makeInstance(Class<T> returnType, Map<String, Object> map) {
    	if(map.size() == 1) {
    		List<?> list = new ArrayList<Object>(map.values());
    		if(returnType.isInstance(list.get(0))) {
    			return (T)list.get(0);
    		}
    	}
    	T ret = tryingConstructorInjection(returnType, map);
    	if(ret == null) {
    		ret = tryingSetterInjection(returnType, map);
    	}
    	BasicServiceInterceptor.addPostLoadObject(ret);
		return ret;
    }

    static <T> T tryingSetterInjection(Class<T> returnType, Map<String, Object> map) {
    	try {
			T ret = returnType.newInstance();
			for(Map.Entry<String, Object> entry : map.entrySet()) {
				PropertyUtils.setProperty(ret, entry.getKey(), entry.getValue());
			}
			return ret;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
    
    static <T> T tryingConstructorInjection(Class<T> returnType, Map<String, Object> map) {
    	Object[] array = map.values().toArray();
    	Constructor<?>[] consts = returnType.getConstructors();
    	for(Constructor<?> con : consts) {
    		try {
				T ret = (T)con.newInstance(array);
				return ret;
			} catch (IllegalArgumentException e) {
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
    	}
    	return null;
    }

    private static interface MakeMapStrategy {
    	public Map<String, Object> getMap(Object target, List<ExtractValue> values);
    }
    
    private static class SingleStrategy implements MakeMapStrategy {
    	private final static MakeMapStrategy INSTANCE = new SingleStrategy();
		public Map<String, Object> getMap(Object target, List<ExtractValue> values) {
			Map<String, Object> ret = new LinkedHashMap<String, Object>();
			Object value = target;
			if((values.get(0).value instanceof SummaryFunction) && value == null) {
				value = translateZeroValue(values.get(0));
			} else if(value.getClass().isArray()) {
				value = Array.get(value, 0);
			}
			ret.put(values.get(0).key, value);
			return ret;
		}
    	
    }
    
    private static Object translateZeroValue(ExtractValue value) {
    	try {
	    	if(value.returnType == null) {
	    		return 0L;
	    	} else {
	    		return value.returnType.getConstructor(String.class).newInstance("0");
	    	}
    	} catch(Exception e) {
    		throw new RuntimeException(e);
    	}
    }
    
    private static class MultiStrategy implements MakeMapStrategy {
    	private final static MakeMapStrategy INSTANCE = new MultiStrategy();
		public Map<String, Object> getMap(Object target, List<ExtractValue> values) {
			Object[] row = (Object[])target;
			Map<String, Object> rowMap = new LinkedHashMap<String, Object>();
			for(int i = 0 ; i < values.size() ; i++) {
				Object value = ((values.get(i).value instanceof SummaryFunction) && row[i] == null) ? translateZeroValue(values.get(i)) : row[i]; 
				rowMap.put(values.get(i).key, value);
			}
			return rowMap;
		}
    	
    }
    
	private Query makeCountQuery() {
        builder.append("select ");
        if(extractor.getValues().size() == 0) {
        	builder.append("count(");
        	if(extractor.isDistinct()) {
            	builder.append("distinct ");
        	}
        	builder.append(getAlias(extractor.target, extractor.targetAlias));
        	ClassMetadata cm = HibernateUtils.getSession().getSessionFactory().getClassMetadata(extractor.target);
        	builder.append(".");
        	builder.append(cm.getIdentifierPropertyName());
        	builder.append(")");
        } else {
        	builder.append("count(*)");
        }
        makeFromCouse(false);
        makeWhereCouse();
        String hql = builder.toString();
        log.debug(hql);
        Query query = HibernateUtils.getSession().createQuery(hql);
        return query;
	}

	private Query makeQuery() {
		return makeQuery(true);
	}
	
    private Query makeQuery(boolean withOrderBy) {
        String hql = makeQueryString(withOrderBy);
        return HibernateUtils.getSession().createQuery(hql);
    }

	private String makeQueryString(boolean withOrderBy) {
		makeSelectCouse();
        makeFromCouse(true);
        makeWhereCouse();
        makeGroupByCouse();
        makeHavingCouse();
        if(withOrderBy) {
        	makeOrderByCouse();
        }
        String hql = builder.toString();
        log.debug(hql);
		return hql;
	}
    
	private void makeHavingCouse() {
		if(extractor.getHavingIterator().size() == 0) {
			return;
		}
        String joinString = "";
        builder.append(" having ");
        for(Condition<? extends Value> con : extractor.getHavingIterator()) {
            builder.append(joinString);
            makeCondition(con);
            joinString = " and ";
        }
	}

	private void makeGroupByCouse() {
		if(!usingGroupBy) {
			return;
		}
		String delimitor = " group by ";
		for(ExtractValue ev : extractor.getValues()) {
			delimitor = makeGroupByCouse(ev.value, delimitor);
		}
	}

	private String makeGroupByCouse(Object value, String delimitor) {
		if(value instanceof SummaryFunction) {
			return delimitor;
		}
		if(value instanceof FreeFormat) {
			FreeFormat ff = (FreeFormat)value;
			if(ff.includeSummaryFunction()) {
				for(Object o : ff.values) {
					delimitor = makeGroupByCouse(o, delimitor);
				}
			} else {
				builder.append(delimitor);
				builder.append(VALUE_MAKE_STRATEGY_MAP.get(value.getClass()).makeValue(this, (Value)value));
				delimitor = ", ";
			}
		} else {
			builder.append(delimitor);
			builder.append(VALUE_MAKE_STRATEGY_MAP.get(value.getClass()).makeValue(this, (Value)value));
			delimitor = ", ";
		}
		return delimitor;
	}
	
	private void makeOrderByCouse() {
        if(extractor.getOrderIterator().size() == 0) {
            return;
        }
        builder.append(" order by ");
        String delimitor = "";
        for(Order<? extends Value> order : extractor.getOrderIterator()) {
            builder.append(delimitor);
            String property = VALUE_MAKE_STRATEGY_MAP.get(order.label.getClass()).makeValue(this, order.label);
            builder.append(property);
            if(order instanceof Desc) {
                builder.append(" desc");
            } else {
                builder.append(" asc");
            }
            delimitor = ",";
        }
    }

    private void setParameter(Object query, Extractor extractor) {
    	for(ExtractValue ev : extractor.getValues()) {
    		setParameter(query, ev.value);
    	}
    	List<Condition<? extends Value>> list = new ArrayList<Condition<? extends Value>>(extractor.getConditionIterator());
        for(Condition<? extends Value> con : list) {
       		setParameter(query, con);
        }
        if(usingGroupBy) {
        	for(ExtractValue ev : extractor.getValues()) {
        		setParameterGroupByCouse(query, ev.value);
        	}
        }
        for(Condition<? extends Value> con : extractor.getHavingIterator()) {
       		setParameter(query, con);
        }
        for(Order<? extends Value> v : extractor.getOrderIterator()) {
       		setParameter(query, v.label);
        }
    }
    
    private void setParameterGroupByCouse(Object query, Object value) {
		if(value instanceof SummaryFunction) {
			return;
		}
		if(value instanceof FreeFormat) {
			FreeFormat ff = (FreeFormat)value;
			if(ff.includeSummaryFunction()) {
				for(Object o : ff.values) {
					setParameterGroupByCouse(query, o);
				}
			} else {
				setParameter(query, ff);
			}
		} else if(value instanceof Value){
			Value v = (Value)value;
			setParameter(query, v);
		}
    }
    
	private void setParameter(Object query, Value value) {
		if(value instanceof Extractor) {
			setParameter(query, (Extractor)value);
		} else if(value instanceof Case) {
			Case c = (Case)value;
			if(c.condition instanceof Condition) {
				setParameter(query, (Condition)c.condition);
			} else {
				setParameter(query, (Value)c.condition);
			}
		} else if(value instanceof Function) {
			setParameter(query, ((Function)value).value);
		} else if(value instanceof FreeFormat) {
			for(Object o : ((FreeFormat)value).values) {
				if(o instanceof Value) {
					setParameter(query, (Value)o);
				}
			}
		}
	}

	private void setParameter(Object query, Condition<? extends Value> con) {
    	ConditionStrategy<Condition> strategy = (ConditionStrategy<Condition>) CONDITION_STRATEGY_MAP2.get(con.getClass()); 
    	strategy.setParameter(query, this, con);
    }
    
    private void makeWhereCouse() {
        if(extractor.getConditionIterator().size() + extractor.getInnerJoins().size() == 0) {
            return;
        }
        String joinString = "";
        builder.append(" where ");
        for(Condition<? extends Value> con : extractor.getConditionIterator()) {
            builder.append(joinString);
            makeCondition(con);
            joinString = " and ";
        }
/*
        Set<String> set = new HashSet<String>();
        set.add(getAlias(extractor.target, extractor.targetAlias));
        //deletedinDBを強制的にチェック
        for(InnerJoin join : extractor.getInnerJoins()) {
            if(LogicalDeleteEntity.class.isAssignableFrom(join.target)) {
                String aliase = getAlias(join.target, join.targetAlias);
                if(!set.contains(aliase)) {
                    set.add(aliase);
                    builder.append(joinString);
                    builder.append(aliase);
                    builder.append(".deletedInDB");
                    builder.append("=");
                    builder.append("'");
                    builder.append(HibernateUtils.BOOLEAN_CHAR_F);
                    builder.append("'");
                    joinString = " and ";
                }
            }
            if(LogicalDeleteEntity.class.isAssignableFrom(join.joined)) {
                String aliase = getAlias(join.joined, join.joinedAlias);
                if(!set.contains(aliase)) {
                    set.add(aliase);
                    builder.append(joinString);
                    builder.append(aliase);
                    builder.append(".deletedInDB");
                    builder.append("=");
                    builder.append("'");
                    builder.append(HibernateUtils.BOOLEAN_CHAR_F);
                    builder.append("'");
                    joinString = " and ";
                }
            }
        }
*/
        for(InnerJoin join : extractor.getInnerJoins()) {
            builder.append(joinString);
            builder.append(getAlias(join.target, join.targetAlias));
            addProperty(join.targetProperty);
            builder.append("=");
            builder.append(getAlias(join.joined, join.joinedAlias));
            addProperty(join.joinedProperty);
            joinString = " and ";
        }
    }

    private void makeCondition(Condition<? extends Value> con) {
        ConditionStrategy<Condition> strategy = (ConditionStrategy<Condition>) CONDITION_STRATEGY_MAP2.get(con.getClass());
        String where = strategy.makeWhereCouse(this, con);
        builder.append(where);
    }
    
    private void addProperty(String targetProperty) {
        if(targetProperty == null || "".equals(targetProperty)) {
            return;
        }
        builder.append(".");
        builder.append(targetProperty);
    }
    
    private void makeSelectCouse() {
        if(extractor.getValues().size() == 0) {
        	Class target = extractor.target;
        	ClassMetadata cm = HibernateUtils.getSession().getSessionFactory().getClassMetadata(target);
//        	while(cm == null && target != Object.class) {
//        		target = target.getSuperclass();
//        		cm = HibernateUtils.getSession().getSessionFactory().getClassMetadata(target);
//        	}
        	if(cm == null) {
        		return;
        	}
        	builder.append("select ");
        	if(extractor.isDistinct()) {
        		builder.append("distinct ");
        	}
            builder.append(getAlias(target, extractor.targetAlias));
            for(Order<? extends Value> order : extractor.getOrderIterator()) {
            	if(isSkipSelect(order)) {
            		continue;
            	}
                String property = VALUE_MAKE_STRATEGY_MAP.get(order.label.getClass()).makeValue(this, order.label);
            	builder.append(",");
            	builder.append(property);
            }
        } else {
            builder.append("select ");
        	if(extractor.isDistinct()) {
        		builder.append("distinct ");
        	}
	        String delimitor = "";
	        for(ExtractValue v : extractor.getValues()) {
	            builder.append(delimitor);
	            String property = VALUE_MAKE_STRATEGY_MAP.get(v.value.getClass()).makeValue(this, v.value);
	            builder.append(property);
	            delimitor = ",";
	        }
        }
    }

    private boolean isSkipSelect(Order<? extends Value> order) {
    	if(!(order.label instanceof Property)) {
    		//プロパティ以外ならたぶんExtractValueとして加わっているのでスキップして良い
    		return true;
    	}
    	Property p = (Property)order.label;
    	return isSkipSelect(p);
    }
    
    private boolean isSkipSelect(Property p) {
    	if(p.target == null || p.target == this.extractor.target) {
    		//Fetchの深さがしていないか見てみる
    		int depth = 0;
    		StringTokenizer tokenizer = new StringTokenizer(p.property, ".");
    		ClassMetadata cm = HibernateUtils.getSession().getSessionFactory().getClassMetadata(extractor.target);
			StringBuilder propertyStack = new StringBuilder(); 
    		while(tokenizer.hasMoreTokens()) {
    			String token = tokenizer.nextToken();
    			propertyStack.append(token);
    			String property = propertyStack.toString();
    			if(log.isDebugEnabled()) {
    				log.debug("プロパティ名：" + property);
    			}
    			Type t = cm.getPropertyType(property);
    			if(t.isComponentType()) {
    				propertyStack.append(".");
    				continue;
    			} else if(t.isEntityType()) {
    				cm = HibernateUtils.getSession().getSessionFactory().getClassMetadata(t.getReturnedClass());
    				propertyStack.setLength(0);
    				depth++;
    			}
    		}
    		if(log.isDebugEnabled()) {
    			log.debug("プロパティの深さ：" + depth);
    		}
    		return (depth <= defaultMaxFetchDepth);
    	}
    	return false;
	}

	static interface ValueMaker<T extends Value> {
    	public String makeValue(Extractor2HQL generator, T v);
    }

    static class SummaryFunctionValueMaker<T extends SummaryFunction> implements ValueMaker<T> {
    	static final String format = "%s(%s)";
    	private final String prefix;
    	SummaryFunctionValueMaker(String prefix) {
    		this.prefix = prefix;
    	}
		@Override
		public String makeValue(Extractor2HQL generator, T v) {
			generator.usingGroupBy = true;
			return String.format(format, prefix, getValue(generator, v));
		}
    	
		protected String getValue(Extractor2HQL generator, T v) {
			return VALUE_MAKE_STRATEGY_MAP.get(v.value.getClass()).makeValue(generator, v.value);
		}
    }
    final static Map<Class<? extends Value>, ValueMaker> VALUE_MAKE_STRATEGY_MAP;
    static {
    	Map<Class<? extends Value>, ValueMaker> tmp = new HashMap<Class<? extends Value>, ValueMaker>();
    	tmp.put(Property.class, new ValueMaker<Property>() {
			@Override
			public String makeValue(Extractor2HQL generator, Property v) {
				return generator.getProperty(v.target, v.aliase, v.property);
			}
    	});
    	tmp.put(Max.class, new SummaryFunctionValueMaker<Max>("max"));
    	tmp.put(Min.class, new SummaryFunctionValueMaker<Min>("min"));
    	tmp.put(Sum.class, new SummaryFunctionValueMaker<Sum>("sum"));
    	tmp.put(Avg.class, new SummaryFunctionValueMaker<Avg>("avg"));
    	tmp.put(Count.class, new SummaryFunctionValueMaker<Count>("count") {
			@Override
			public String getValue(Extractor2HQL generator, Count v) {
				return (v.distinct ? "distinct " : "") + super.getValue(generator, v);
			}
    	});
    	tmp.put(FreeFormat.class, new ValueMaker<FreeFormat>() {
			@Override
			public String makeValue(Extractor2HQL generator, FreeFormat v) {
				int replaceIndex = 0;
				int length = v.format.length();
				StringBuilder ret = new StringBuilder();
				for(int i = 0 ; i < length ; i++) {
					char ch = v.format.charAt(i);
					if(ch == '?') {
						ret.append(replacetext(generator, v.values.get(replaceIndex++)));
					} else {
						ret.append(ch);
					}
				}
				return ret.toString();
			}

			String replacetext(Extractor2HQL generator, Object val) {
				if(val instanceof Value) {
					return VALUE_MAKE_STRATEGY_MAP.get(val.getClass()).makeValue(generator, (Value)val);
				} else if(val instanceof Number) {
					return val.toString();
				} else {
					return "'" + val.toString() + "'";
				}
			}
    	});
    	tmp.put(Extractor.class, new ValueMaker<Extractor>(){
			@Override
			public String makeValue(Extractor2HQL generator, Extractor ex) {
				if(ex.getValues().size() != 1) {
					throw new RuntimeException("サブクエリーの抽出情報は１つしか許可していません:" + ex.getValues().size());
				}
				Extractor2HQL gen = new Extractor2HQL(ex);
				String query = gen.makeQueryString(true);
				return String.format("(%s)", query);
			}
    	});
    	tmp.put(DateToString.class, new ValueMaker<DateToString>(){
			@Override
			public String makeValue(Extractor2HQL generator, DateToString v) {
				return DateToStringConvertor.getConvertor(HibernateUtils.getDialect().getClass()).convert(generator, v);
			}
    	});
    	tmp.put(Case.class, new ValueMaker<Case>() {
			@Override
			public String makeValue(Extractor2HQL generator, Case v) {
				String conText = null;
				if(v.condition instanceof Condition) {
					Condition con = (Condition)v.condition;
			        ConditionStrategy<Condition> conStrategy = (ConditionStrategy<Condition>) CONDITION_STRATEGY_MAP2.get(v.condition.getClass());
			        conText = conStrategy.makeWhereCouse(generator, con);
				} else {
					Value value = (Value)v.condition;
			        ValueMaker vm = VALUE_MAKE_STRATEGY_MAP.get(value.getClass());
			        conText = vm.makeValue(generator, value);
				}
		        ValueMaker vm = VALUE_MAKE_STRATEGY_MAP.get(v.thenValue.getClass());
				String thenText = vm.makeValue(generator, v.thenValue);
		        vm = VALUE_MAKE_STRATEGY_MAP.get(v.elseValue.getClass());
				String elseText = vm.makeValue(generator, v.elseValue);
				String ret = String.format(
						"case when %s then %s else %s end", 
						conText, thenText, elseText);
				return ret;
			}
    	});
    	VALUE_MAKE_STRATEGY_MAP = Collections.unmodifiableMap(tmp);
    }
    
    
//    private Set<Class> joinedEntity;
    private void makeFromCouse(boolean isFetch) {
        Set<String> aliases = new HashSet<String>();
        builder.append(" from ");
        ClassMetadata cm = HibernateUtils.getSession().getSessionFactory().getClassMetadata(extractor.target);
        if(cm == null) {
        	builder.append(extractor.target.getName());
        } else {
            builder.append(getAlias(extractor.target, null));
        }
        builder.append(" as ");
        builder.append(getAlias(extractor.target, extractor.targetAlias));
        if(extractor.getValues().size() == 0) {
            //参照オブジェクトのジョイン
//            joinedEntity = new HashSet<Class>();
            makeOuterJoin(extractor.target, getAlias(extractor.target, extractor.targetAlias), isFetch, 1);
        }
        aliases.add(getAlias(extractor.target, extractor.targetAlias));
/*
        for(InnerJoin join : extractor.getInnerJoins()) {
            if(!joinedEntity.contains(join.target)) {
                joinedEntity.set(join.target);
            }
        }
*/
        for(InnerJoin join : extractor.getInnerJoins()) {
            String aliase = getAlias(join.target, join.targetAlias);
            if(!aliases.contains(aliase)) {
                aliases.add(aliase);
                builder.append(", ");
                builder.append(getAlias(join.target, null));
                builder.append(" as ");
                builder.append(aliase);
            }
            aliase = getAlias(join.joined, join.joinedAlias);
            if(!aliases.contains(aliase)) {
                aliases.add(aliase);
                builder.append(", ");
                builder.append(getAlias(join.joined, null));
                builder.append(" as ");
                builder.append(aliase);
            }
        }
    }
    
    private void makeOuterJoin(Class target, String currentAliase, boolean isFetch, int depth) {
    	if(depth > this.defaultMaxFetchDepth) {
    		return;
    	}
    	depth++;
//        joinedEntity.add(target);
        Map<String, Class> map = getEntityRelationProperty(target);
        for(Map.Entry<String, Class> entry : map.entrySet()) {
            builder.append(" left outer join ");
            if(isFetch) {
            	builder.append("fetch ");
            }
            String property = currentAliase + "." + entry.getKey();
            builder.append(property);
            Class cl = entry.getValue();
            if(!cl.equals(target)) {    //再帰を避ける
                makeOuterJoin(entry.getValue(), property, isFetch, depth);
            }
        }
    }

    private Map<String, Class> getEntityRelationProperty(Class target) {
        Map<String, Class> ret = new HashMap<String, Class>();
        ClassMetadata cm = HibernateUtils.getSession().getSessionFactory().getClassMetadata(target);
        if(cm == null) {
        	return ret;
        }
        String[] names = cm.getPropertyNames();
        for(String name : names) {
            Type t = cm.getPropertyType(name);
            if(t.isEntityType()) {
                try {
                    ret.put(name, Class.forName(t.getName()));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return ret;
    }
    
    private String getProperty(Class cl, String aliase, String property) {
    	if(cl == null) {
    		cl = this.extractor.target;
    	}
    	if(StringUtils.isBlank(aliase)) {
    		aliase = extractor.targetAlias;
    	}
        aliase = getAlias(cl, aliase);
        if(property == null || "".equals(property)) {
            return aliase;
        } else if(property.equals("*")){
        	return "*";
        } else {
            return aliase + "." + property;
        }
    }
    
    private static String getAlias(Class cl, String alias) {
    	if(StringUtils.isBlank(alias)) {
            String name = cl.getName();
            return name.substring(name.lastIndexOf(".") + 1);
        } else {
            return alias;
        }
    }
    
    private static interface ConditionStrategy<T extends Condition> {
    	public String makeWhereCouse(Extractor2HQL generator, T condition);
    	public void setParameter(Object query, Extractor2HQL generator, T condition);
    }
    
    private static class CombineConditionStrategy<T extends CombineCondition> implements ConditionStrategy<T> {
    	private final String combinString;
    	CombineConditionStrategy(String combinString) {
    		this.combinString = " " + combinString + " ";
    	}

		@Override
		public String makeWhereCouse(Extractor2HQL generator, T condition) {
			StringBuilder sb = new StringBuilder();
            if(condition.getConditions().size() == 0) {
                return "";
            }
            sb.append("(");
            String combineString = "";
            for(Object o : condition.getConditions()) {
            	Condition con = (Condition)o;
                sb.append(combineString);
            	ConditionStrategy<Condition> strategy = (ConditionStrategy<Condition>) CONDITION_STRATEGY_MAP2.get(con.getClass());
            	sb.append(strategy.makeWhereCouse(generator, con));
                combineString = this.combinString;
            }
            sb.append(")");
            return sb.toString();
		}

		@Override
		public void setParameter(Object query, Extractor2HQL generator, T condition) {
            for(Object o : condition.getConditions()) {
            	Condition con = (Condition)o;
            	ConditionStrategy<Condition> strategy = (ConditionStrategy<Condition>) CONDITION_STRATEGY_MAP2.get(con.getClass());
            	strategy.setParameter(query, generator, con);
            }
		}
    }
    
    private static class LabelHoldingStrategy<T extends LabelHoldingCondition> implements ConditionStrategy<T> {
    	public String makeWhereCouse(Extractor2HQL generator, T condition) {
    		return String.format("%s %s %s", 
    				getLeftSide(generator, condition), 
    				getExpression(generator, condition), 
    				getRightSide(generator, condition));
    	}
    	
    	protected String getLeftSide(Extractor2HQL generator, T condition) {
    		return VALUE_MAKE_STRATEGY_MAP.get(condition.label.getClass()).makeValue(generator, condition.label);
    	}
    	
    	protected String getExpression(Extractor2HQL generator, T condition) {
    		return CONDITION_EXPRESSION_MAP.get(condition.getClass());
    	}
    	
    	protected String getRightSide(Extractor2HQL generator, T condition) {
    		return "";
    	}

		@Override
		public void setParameter(Object query, Extractor2HQL generator, T condition) {
		}
    }

    private static class ValueHoldingStrategy<T extends ValueHoldingCondition> extends LabelHoldingStrategy<T> {
    	@Override
    	protected String getRightSide(Extractor2HQL generator, T condition) {
    		if(condition.value instanceof Value) {
    			return VALUE_MAKE_STRATEGY_MAP.get(condition.value.getClass()).makeValue(generator, (Value)condition.value);
    		} else {
    			return "?";
    		}
    	}
		@Override
		public void setParameter(Object query, Extractor2HQL generator, T condition) {
			if(condition.value instanceof Extractor) {
	        	Extractor ex = (Extractor)condition.value;
	        	generator.setParameter(query, ex);
			} else if(!(condition.value instanceof Value)) {
				setParameter2(query, generator, condition.value);
			}
		}
		
	    protected void setParameter2(Object o, Extractor2HQL generator, Object value) {
	    	if(o instanceof Query) {
	    		Query query = (Query)o;
		        if(value instanceof Number) {
		            query.setParameter(generator.patemeterIndex, value, TypeFactory.basic(value.getClass().getName()));
		        } else {
		            query.setParameter(generator.patemeterIndex, value);
		        }
	    	} else {
	    		try {
		    		PreparedStatement pstmt = (PreparedStatement)o;
	    			SessionFactory sf = HibernateUtils.getSession().getSessionFactory();
	    	        ClassMetadata cm = sf.getClassMetadata(value.getClass());
	    	        if(cm != null) {
	    	        	//TODO 複合キーだとどうなる？
	    	        	value = cm.getIdentifier(value, EntityMode.POJO);
	    	        } else if ((value instanceof Date) && !value.getClass().getName().startsWith("java.sql.")) {
	    	        	value = new Timestamp(((Date)value).getTime());
	    	        }
    	        	pstmt.setObject(generator.patemeterIndex + 1, value);
	    		} catch(Exception e) {
	    			throw new RuntimeException(e);
	    		}
	    	}
	        generator.patemeterIndex++;
	    }
    }
    
    private static class CollectionValueHoldingStrategy<T extends ValueHoldingCondition> extends ValueHoldingStrategy<T> {
    	@Override
    	public String makeWhereCouse(Extractor2HQL generator, T condition) {
    		if(((Collection)condition.value).size() == 0) {
    			if(condition instanceof In) {
        			return "1=0";
    			} else if(condition instanceof NotIn) {
    				return "1=1";
    			} else {
    				throw new RuntimeException("予期しないタイプです。:" + condition.getClass().getName());
    			}
    		} else {
    			return super.makeWhereCouse(generator, condition);
    		}
    	}
    	
    	@Override
    	protected String getRightSide(Extractor2HQL generator, T condition) {
        	List<Object> values = new ArrayList<Object>((Collection)condition.value);
        	StringBuilder sb = new StringBuilder();
        	sb.append("(");
        	if(values.size() == 1 && values.get(0) instanceof Extractor) {
        		Extractor ex = (Extractor)values.get(0);
    			sb.append(VALUE_MAKE_STRATEGY_MAP.get(Extractor.class).makeValue(generator, ex));
        	} else {
	        	String delimiter = "";
	        	for(@SuppressWarnings("unused") Object o : (Collection)condition.value) {
	        		sb.append(delimiter);
	        		sb.append("?");
	        		delimiter = ", ";
	        	}
        	}
        	sb.append(")");
        	return sb.toString();
    	}
		@Override
		public void setParameter(Object query, Extractor2HQL generator, T condition) {
        	List<Object> values = new ArrayList<Object>((Collection)condition.value);
        	if(values.size() == 1 && values.get(0) instanceof Extractor) {
        		generator.setParameter(query, (Extractor)values.get(0));
        	} else if(!(condition.value instanceof Value)) {
            	Collection col = (Collection)condition.value;
            	for(Object o : col) {
            		setParameter2(query, generator, o);
            	}
			}
		}
    }

    private final static Map<Class<? extends Condition>, ConditionStrategy<? extends Condition>> CONDITION_STRATEGY_MAP2;
    static {
    	Map<Class<? extends Condition>, ConditionStrategy<? extends Condition>> tmp = 
    		new HashMap<Class<? extends Condition>, ConditionStrategy<? extends Condition>>();
    	tmp.put(And.class, new CombineConditionStrategy<And>("and"));
    	tmp.put(Or.class, new CombineConditionStrategy<Or>("or"));
    	tmp.put(Join.class, new CombineConditionStrategy<Join>(""){
			@Override
			public String makeWhereCouse(Extractor2HQL generator, Join condition) {
	               throw new RuntimeException("HQLではサポートしていません。（今のところ）");
			}
    	});
    	tmp.put(Eq.class, new ValueHoldingStrategy<Eq>());
    	tmp.put(Ge.class, new ValueHoldingStrategy<Ge>());
    	tmp.put(Gt.class, new ValueHoldingStrategy<Gt>());
    	tmp.put(In.class, new CollectionValueHoldingStrategy<In>());
    	tmp.put(IsNotNull.class, new LabelHoldingStrategy<IsNotNull>());
    	tmp.put(IsNull.class, new LabelHoldingStrategy<IsNull>());
    	tmp.put(Le.class, new ValueHoldingStrategy<Le>());
    	tmp.put(Like.class, new ValueHoldingStrategy<Like>());
    	tmp.put(Lt.class, new ValueHoldingStrategy<Lt>());
    	tmp.put(NotEq.class, new ValueHoldingStrategy<NotEq>());
    	tmp.put(NotIn.class, new CollectionValueHoldingStrategy<NotIn>());
    	tmp.put(RegularExp.class, new ValueHoldingStrategy<RegularExp>(){
    		@Override
        	protected String getLeftSide(Extractor2HQL generator, RegularExp condition) {
	        	Dialect dialect = HibernateUtils.getDialect();
	        	String tmpl = REGEXP_TEMPLATE.get(dialect.getClass());
	        	log.debug(tmpl);
	        	if(tmpl == null) {
	        		throw new RuntimeException("unsupported regular Expression in " + dialect.getClass().getName() + ".");
	        	}
	        	String property = super.getLeftSide(generator, condition);
	        	String value = super.getRightSide(generator, condition);
	        	return MessageFormat.format(tmpl, property, value);
    		}
    		@Override
        	protected String getRightSide(Extractor2HQL generator, RegularExp condition) {
    			return "0";
    		}

        	@Override
        	public String makeWhereCouse(Extractor2HQL generator, RegularExp condition) {
	        	Dialect dialect = HibernateUtils.getDialect();
	        	if(dialect == null) {
	        		throw new RuntimeException("dialect can't get.");
	        	}
	        	if(dialect instanceof MySQLDialect) {
	        		String ret = MessageFormat.format("{0} REGEXP {1}", 
	        				super.getLeftSide(generator, condition), super.getRightSide(generator, condition));
	        		return ret;
	        	} else {
	        		return super.makeWhereCouse(generator, condition);
	        	}
        	}
    	});
    	CONDITION_STRATEGY_MAP2 = Collections.unmodifiableMap(tmp);
    }
    
    private final static Map<Class<? extends Condition> , String> CONDITION_EXPRESSION_MAP;
    
    static {
        Map<Class<? extends Condition>, String> tmp = new HashMap<Class<? extends Condition>, String>();
        tmp.put(Eq.class, 			"=");
        tmp.put(Ge.class, 			">=");
        tmp.put(Gt.class, 			">");
        tmp.put(In.class, 			"in");
        tmp.put(IsNotNull.class, 	"is not null");
        tmp.put(IsNull.class, 		"is null");
        tmp.put(Le.class, 			"<=");
        tmp.put(Like.class, 		"like");
        tmp.put(Lt.class, 			"<");
        tmp.put(NotEq.class, 		"<>");
        tmp.put(NotIn.class, 		"not in");
        tmp.put(RegularExp.class, 	">");
        CONDITION_EXPRESSION_MAP = Collections.unmodifiableMap(tmp);
    }

    private final static Map<Class<? extends Dialect>, String> REGEXP_TEMPLATE;
    static {
        Map<Class<? extends Dialect>, String> tmp = new HashMap<Class<? extends Dialect>, String>();
        tmp.put(PostgreSQLDialect.class, "char_length(substring({0},{1}))");
        tmp.put(Oracle10gDialect.class, "REGEXP_INSTR({0}, {1})");
        REGEXP_TEMPLATE = Collections.unmodifiableMap(tmp);
    }
}
