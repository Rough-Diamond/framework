/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.framework.transaction;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;

/**
 * トランザクションマネージャの簡易実装
 * JDK 5.0以前のバージョンでは本マネージャを使用すること
 * トランザクション属性は、setTransactionMapに渡された情報に基づいて行われる。
 * transactionMapは、キーはクラス名、値はトランザクション属性文字列
 * （REQUIRED or REQUIRED_NEW or NOP)を指定する。
 * 省略された場合は、「REQUIRED」である
 */
@SuppressWarnings("unchecked")
public class SimpleTransactionManager extends TransactionManager {
	private final static Map TRANSACTION_INTERCEPTORS;
	static {
		Map tmp = new HashMap();
		tmp.put("REQUIRED", RequiredInterceptor.class);
		tmp.put("REQUIRED_NEW", RequiredNewInterceptor.class);
		tmp.put("NOP", NopInterceptor.class);
		TRANSACTION_INTERCEPTORS = Collections.unmodifiableMap(tmp);
	}
	
	protected Map getTransactionInterceptors() {
		return TRANSACTION_INTERCEPTORS;
	}
	
    
	private Map transactionMap = new HashMap();
	
	/**
	 * トランザクション属性マップをセットする（DI用）
	 * キーはクラス名、値はトランザクション属性文字列（REQUIRED or REQUIRED_NEW or NOP)
	 * @param map
	 */
	public void setTransactionMap(Map map) {
		this.transactionMap = map;
	}
	
	/**
	 * トランザクション属性マップを取得する
	 * @return
	 */
	public Map getTransactionMap() {
		return transactionMap;
	}

	public Object invoke(MethodInvocation arg0) throws Throwable {
		String className = arg0.getThis().getClass().getName();
		String transactionAttr = (String)transactionMap.get(className);
		if(transactionAttr == null) {
			transactionAttr = "REQUIRED";
		}
		Class cl = (Class)getTransactionInterceptors().get(transactionAttr);
		TransactionInterceptor ti = (TransactionInterceptor)cl.newInstance();
        return ti.invoke(arg0);
	}
}
