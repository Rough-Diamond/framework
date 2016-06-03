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
 * �g�����U�N�V�����}�l�[�W���̊ȈՎ���
 * JDK 5.0�ȑO�̃o�[�W�����ł͖{�}�l�[�W�����g�p���邱��
 * �g�����U�N�V���������́AsetTransactionMap�ɓn���ꂽ���Ɋ�Â��čs����B
 * transactionMap�́A�L�[�̓N���X���A�l�̓g�����U�N�V��������������
 * �iREQUIRED or REQUIRED_NEW or NOP)���w�肷��B
 * �ȗ����ꂽ�ꍇ�́A�uREQUIRED�v�ł���
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
	 * �g�����U�N�V���������}�b�v���Z�b�g����iDI�p�j
	 * �L�[�̓N���X���A�l�̓g�����U�N�V��������������iREQUIRED or REQUIRED_NEW or NOP)
	 * @param map
	 */
	public void setTransactionMap(Map map) {
		this.transactionMap = map;
	}
	
	/**
	 * �g�����U�N�V���������}�b�v���擾����
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
