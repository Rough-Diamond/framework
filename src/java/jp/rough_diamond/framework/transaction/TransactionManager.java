/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.transaction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.aopalliance.intercept.MethodInterceptor;

/**
 * トランザクションマネージャ
 * JDK 5.0以前のバージョンでは本マネージャを使用すること
 * トランザクション属性は、setTransactionMapに渡された情報に基づいて行われる。
 * transactionMapは、キーはクラス名、値はトランザクション属性文字列
 * （REQUIRED or REQUIRED_NEW or NOP)を指定する。
 * 省略された場合は、「REQUIRED」である
 */
@SuppressWarnings("unchecked")
abstract public class TransactionManager implements MethodInterceptor {
    private static ThreadLocal transactionBeginingStack = new ThreadLocal() {
        protected Object initialValue() {
            return new Stack();
        }
    };
    
    private static ThreadLocal<Stack<Map>> 
    	transactionContext = new ThreadLocal<Stack<Map>>() {
    		protected Stack<Map> initialValue() {
    			return new Stack<Map>();
    	}
    };

	/**
	 * 何らかのトランザクション内か否かを返却する 
	 */
	public static boolean isInTransaction() {
		return !transactionContext.get().isEmpty();
	}
	
    /**
     * トランザクションに関連するコンテキストマップを取得する
     */
    public static Map<Object, Object> getTransactionContext() {
        Stack<Map> stack2 = transactionContext.get();
        return (stack2.empty()) ? null : stack2.peek();
    }

    /**
	 * トランザクションを開始したInterceptorをスタックに積む
	 * @param ti
	 */
    public static void pushTransactionBeginingInterceptor(TransactionInterceptor ti) {
        Stack stack = (Stack)transactionBeginingStack.get();
        stack.push(ti);
        Stack<Map> stack2 = transactionContext.get();
        stack2.push(new HashMap());
    }
    
    /**
     * トランザクションを開始したInterceptorをスタックから除去する
     *
     */
    public static void popTransactionBeginingInterceptor() {
        Stack stack = (Stack)transactionBeginingStack.get();
        stack.pop();
        Stack<Map> stack2 = transactionContext.get();
        stack2.pop();
    }
    
    /**
     * trueの場合、現在のトランザクションは必ずロールバックされる
     * @return
     */
    public static boolean isRollbackOnly() {
        Stack stack = (Stack)transactionBeginingStack.get();
        TransactionInterceptor ti = (TransactionInterceptor)stack.peek();
        return ti.isRollbackOnly();
    }
    
    /**
     * 呼び出した時点のトランザクションはロールバックオンリーとなる
     */
    public static void setRollBackOnly() {
        Stack stack = (Stack)transactionBeginingStack.get();
        TransactionInterceptor ti = (TransactionInterceptor)stack.peek();
        ti.setRollbackOnly();
    }

	/**
	 * @param class1
	 */
	public static void addModifiedTemporaryType(Class<? extends Object> cl) {
		Set<Class<?>> set = getModifiedTemporaryTypes();
		set.add(cl);
	}

	static Set<Class<?>> getModifiedTemporaryTypes() {
		final String key = TransactionManager.class.getName() + "_temporaryTypes";
		Map<Object, Object> map = getTransactionContext();
		Set<Class<?>> ret = (Set<Class<?>>)map.get(key);
		if(ret == null) {
			ret = new HashSet<Class<?>>();
			map.put(key, ret);
		}
		return ret;
	}
	
	
}
