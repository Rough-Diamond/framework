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
 * �g�����U�N�V�����}�l�[�W��
 * JDK 5.0�ȑO�̃o�[�W�����ł͖{�}�l�[�W�����g�p���邱��
 * �g�����U�N�V���������́AsetTransactionMap�ɓn���ꂽ���Ɋ�Â��čs����B
 * transactionMap�́A�L�[�̓N���X���A�l�̓g�����U�N�V��������������
 * �iREQUIRED or REQUIRED_NEW or NOP)���w�肷��B
 * �ȗ����ꂽ�ꍇ�́A�uREQUIRED�v�ł���
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
	 * ���炩�̃g�����U�N�V���������ۂ���ԋp���� 
	 */
	public static boolean isInTransaction() {
		return !transactionContext.get().isEmpty();
	}
	
    /**
     * �g�����U�N�V�����Ɋ֘A����R���e�L�X�g�}�b�v���擾����
     */
    public static Map<Object, Object> getTransactionContext() {
        Stack<Map> stack2 = transactionContext.get();
        return (stack2.empty()) ? null : stack2.peek();
    }

    /**
	 * �g�����U�N�V�������J�n����Interceptor���X�^�b�N�ɐς�
	 * @param ti
	 */
    public static void pushTransactionBeginingInterceptor(TransactionInterceptor ti) {
        Stack stack = (Stack)transactionBeginingStack.get();
        stack.push(ti);
        Stack<Map> stack2 = transactionContext.get();
        stack2.push(new HashMap());
    }
    
    /**
     * �g�����U�N�V�������J�n����Interceptor���X�^�b�N���珜������
     *
     */
    public static void popTransactionBeginingInterceptor() {
        Stack stack = (Stack)transactionBeginingStack.get();
        stack.pop();
        Stack<Map> stack2 = transactionContext.get();
        stack2.pop();
    }
    
    /**
     * true�̏ꍇ�A���݂̃g�����U�N�V�����͕K�����[���o�b�N�����
     * @return
     */
    public static boolean isRollbackOnly() {
        Stack stack = (Stack)transactionBeginingStack.get();
        TransactionInterceptor ti = (TransactionInterceptor)stack.peek();
        return ti.isRollbackOnly();
    }
    
    /**
     * �Ăяo�������_�̃g�����U�N�V�����̓��[���o�b�N�I�����[�ƂȂ�
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
