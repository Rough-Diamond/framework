/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.service.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import jp.rough_diamond.commons.service.BasicService;
import jp.rough_diamond.commons.util.PropertyUtils;
import jp.rough_diamond.framework.transaction.TransactionManager;
import jp.rough_diamond.framework.transaction.hibernate.HibernateUtils;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.type.Type;

public class BasicServiceInterceptor extends InterceptorDecoratorBase {
	private static final long serialVersionUID = 1L;
	private static ThreadLocal<Boolean> NO_CACHE = new ThreadLocal<Boolean>() {
		@Override
		protected Boolean initialValue() {
			return Boolean.FALSE;
		}
	};

	public static void setNoCache(boolean noCache) {
		NO_CACHE.set(noCache);
	}

	public static boolean isNoCache() {
		return NO_CACHE.get();
	}

	private final static String POST_LOAD_OBJECTS = "postLoadedObjects";

	@SuppressWarnings("unchecked")
	static void startLoad(boolean noCache) {
		setNoCache(noCache);
		Map map = TransactionManager.getTransactionContext();
		Set<Object> set = new LinkedHashSet<Object>();
		map.put(POST_LOAD_OBJECTS, set);
	}
	
	static List<Object> popPostLoadedObjects() {
		Set<Object> set = getLoadedObjectSet();
		if(set == null) {
			return new ArrayList<Object>();
		} else {
			TransactionManager.getTransactionContext().remove(POST_LOAD_OBJECTS);
			return new ArrayList<Object>(set);
		}
	}
	
	static void addPostLoadObject(Object o) {
		Set<Object> set = getLoadedObjectSet();
		if(set != null) {
			set.add(o);
		}
	}

	@SuppressWarnings("unchecked")
	private static Set<Object> getLoadedObjectSet() {
		Map map = TransactionManager.getTransactionContext();
		Set<Object> set = (Set<Object>)map.get(POST_LOAD_OBJECTS);
		return set;
	}
	
	@Override
	public boolean onLoad(Object arg0, Serializable arg1, Object[] arg2, String[] arg3, Type[] arg4) {
		Set<Object> set = getLoadedObjectSet();
		if(set == null) {
			((HibernateBasicService)BasicService.getService(
					)).firePostLoadDirect(makeObject(arg0, arg2, arg3));
		}
		addPostLoadObject(arg0);
		return super.onLoad(arg0, arg1, arg2, arg3, arg4);
	}

	Object makeObject(Object arg0, Object[] arg2, String[] arg3) {
		for(int i = 0 ; i < arg2.length ; i++) {
			try {
				PropertyUtils.setProperty(arg0, arg3[i], arg2[i]);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return arg0;
	}

	@Override
	public String onPrepareStatement(String sql) {
		return super.onPrepareStatement(translateSql(sql));
	}
	
	final static Set<Class<? extends Dialect>> REVERSE_NULL_ORDER_DIALECTS;
	static {
		Set<Class<? extends Dialect>> set = new HashSet<Class<? extends Dialect>>();
		set.add(H2Dialect.class);
		set.add(H2DialectExt.class);
		REVERSE_NULL_ORDER_DIALECTS = Collections.unmodifiableSet(set);
	}
	
	Map<String, String> translateResult = new HashMap<String, String>();
	String translateSql(String sql) {
		if(!REVERSE_NULL_ORDER_DIALECTS.contains(HibernateUtils.getDialect().getClass())) {
			return sql;
		}
		if(translateResult.containsKey(sql)) {
			return translateResult.get(sql);
		}
		//Ç†Ç≠Ç‹Ç≈RDFÇÃExtractorÇÃèoóÕÇÃÇ›ÇëzíËÇ∑ÇÈ
		final String ORDER_BY_TOKEN = "order by";

		int orderByStart = sql.toLowerCase().lastIndexOf(ORDER_BY_TOKEN);
		if (orderByStart == -1) {
			translateResult.put(sql, sql);
			return sql;
		}
		StringBuilder sb = new StringBuilder(sql.length() + 100);
		sb.append(sql.substring(0, orderByStart + 1 + ORDER_BY_TOKEN.length()));
		String afterOrderBy = sql.substring(sb.length());
		StringTokenizer tokenizer = new StringTokenizer(afterOrderBy, ", ", true);
		while(tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			sb.append(token);
			String tokenLow = token.trim().toLowerCase();
			if(tokenLow.equals("desc")) {
				sb.append(" NULLS FIRST");
			} else if(tokenLow.equals("asc")){
				sb.append(" NULLS LAST");
			}
		}
		translateResult.put(sql, sb.toString());
		return sb.toString();
	}
}
