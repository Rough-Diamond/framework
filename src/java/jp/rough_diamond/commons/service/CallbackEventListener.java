/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import jp.rough_diamond.commons.resource.Messages;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * �R�[���o�b�N�C�x���g���X�i�[
 */
@SuppressWarnings("unchecked")
abstract class CallbackEventListener implements Comparable<CallbackEventListener>{
	private static Log log = LogFactory.getLog(CallbackEventListener.class);
	final Class annotationType;
	final Method method;

	CallbackEventListener(Method m, Class annotationType) {
		this.method = m;
		this.annotationType = annotationType;
	}
	
	abstract void callback(Object eventSource, CallbackEventType type) 
						throws IllegalAccessException, InvocationTargetException;
	abstract Messages validate(Object eventSource, WhenVerifier when) 
						throws IllegalArgumentException, IllegalAccessException, InvocationTargetException;
	
	abstract boolean isSelfCallback();

	int getPriority() {
        try {
            Annotation a = method.getAnnotation(annotationType);
            return (Integer)a.getClass().getMethod("priority").invoke(a);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
	}
	
	public int compareTo(CallbackEventListener other) {
        try {
        	//�D��x�̍�����
        	int p1 = this.getPriority();
        	int p2 = other.getPriority();
            if(p1 == p2) {
            	//���ȃR�[���o�b�N�̕����D��
            	p1 = this.isSelfCallback() ? 1 : 0;
            	p2 = other.isSelfCallback() ? 1 : 0;
            	if(p1 == p2) {
            		//���O�Ń\�[�g
            		String s1 = this.method.toString();
            		String s2 = other.method.toString();
            		return s1.compareTo(s2);
            	}
            }
            return p2 - p1;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
	}
	static boolean isEventListener(Object listener, Class srcType, Method m, Class annotationType) {
		Annotation annotation = m.getAnnotation(annotationType);
		//�R�[���o�b�N�A�m�e�[�V�������Ȃ����\�b�h�Ȃ̂őΏۊO
	    if(annotation == null) {
	    	return false;
	    }
		List<Class<?>> paramType = makeParamTypes(listener, srcType, m);
		if(paramType == null) {
			return false;
		}
		if(paramType.size() == 0) {
			return true;
		} else if(isAssignableFroms(paramType, CallbackEventType.class)) {
			return true;
		} else if(CallbackEventType.VERIFIER.getAnnotation() == annotationType) {
			if(isAssignableFroms(paramType, WhenVerifier.class) ||
			   isAssignableFroms(paramType, CallbackEventType.class, WhenVerifier.class)) {
				return true;
			}
		}
		if(log.isDebugEnabled()) {
			log.debug(m.toString() + "�͈����^�C�v������Ă��邽�߃R�[���o�b�N���\�b�h�Ƃ��ĔF�����܂���B");
		}
		return false;
	}

	static boolean isAssignableFroms(List<Class<?>> targetTypes, Class... baseTypes) {
		if(targetTypes.size() != baseTypes.length) {
			return false;
		}
		for(int i = 0 ; i < targetTypes.size() ; i++) {
			if(!targetTypes.get(i).isAssignableFrom(baseTypes[i])){
				return false;
			}
		}
		return true;
	}
	
	static List<Class<?>> makeParamTypes(Object listener,
			Class srcType, Method m) {
		List<Class<?>> paramType;
		if(listener == null) {
			paramType = Arrays.asList(m.getParameterTypes());
		} else {
			if(m.getParameterTypes().length == 0) {
				if(log.isDebugEnabled()) {
					log.debug(m.toString() + "�͈������Ȃ��̂ŃR�[���o�b�N���\�b�h�Ƃ��ĔF�����܂���B");
				}
				return null;
			}
			if(!m.getParameterTypes()[0].isAssignableFrom(srcType)) {
				if(log.isDebugEnabled()) {
					log.debug(m.toString() + "�͑�P�����̃^�C�v���s��v�Ȃ̂ŃR�[���o�b�N���\�b�h�Ƃ��ĔF�����܂���B");
				}
				return null;
			}
			paramType = Arrays.asList(m.getParameterTypes());
			paramType = paramType.subList(1, paramType.size());
		}
		return paramType;
	}
	
	static class SelfEventListener extends CallbackEventListener {
		SelfEventListener(Method m, Class annotationType) {
			super(m, annotationType);
		}
		@Override
		void callback(Object eventSource, CallbackEventType type)
				throws IllegalAccessException, InvocationTargetException {
        	if(log.isDebugEnabled()) {
        		log.debug(String.format("CallBack EventType[%s]:%s(%s)#%s()", 
        				type, eventSource.getClass().getName(), method.getDeclaringClass().getName(), method.getName()));
        	}
        	if(method.getParameterTypes().length == 0) {
        		method.invoke(eventSource);
        	} else {
        		method.invoke(eventSource, type);
        	}
		}
		@Override
		Messages validate(Object eventSource, WhenVerifier when) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        	if(log.isDebugEnabled()) {
        		log.debug(String.format("CallBack EventType[Verifier]:%s(%s)#%s()", 
        				eventSource.getClass().getName(), method.getDeclaringClass().getName(), method.getName()));
        	}
        	if(method.getParameterTypes().length == 0) {
        		return (Messages)method.invoke(eventSource);
        	} else {
        		return (Messages)method.invoke(eventSource, when);
        	}
		}
		
		@Override
		boolean isSelfCallback() {
			return true;
		}
	};
	
	static class EventAdapter extends CallbackEventListener {
		final Object listener;
		EventAdapter(Object listener, Method m, Class annotationType) {
			super(m, annotationType);
			this.listener = listener;
		}
		@Override
		void callback(Object eventSource, CallbackEventType type)
				throws IllegalAccessException, InvocationTargetException {
        	if(log.isDebugEnabled()) {
        		log.debug(String.format("CallBack EventType[%s]:%s(%s)#%s(%s)", 
        				type, listener.getClass().getName(), method.getDeclaringClass().getName(), method.getName(), eventSource.getClass().getName()));
        	}
        	if(method.getParameterTypes().length == 1) {
        		if(log.isDebugEnabled()) {
	        		log.debug(method.getParameterTypes()[0].getName());
	        		log.debug(eventSource.getClass().getName());
        		}
        		method.invoke(listener, eventSource);
        	} else {
        		method.invoke(listener, eventSource, type);
        	}
		}
		@Override
		Messages validate(Object eventSource, WhenVerifier when)
				throws IllegalArgumentException, IllegalAccessException,
				InvocationTargetException {
        	if(log.isDebugEnabled()) {
        		log.debug(String.format("CallBack EventType[Verifier]:%s(%s)#%s(%s)", 
        				listener.getClass().getName(), method.getDeclaringClass().getName(), method.getName(), eventSource.getClass().getName()));
        	}
        	List<Class<?>> paramTypes = Arrays.asList(method.getParameterTypes());
        	if(isAssignableFroms(paramTypes, eventSource.getClass())) {
        		return (Messages)method.invoke(listener, eventSource);
        	} else if(isAssignableFroms(paramTypes, eventSource.getClass(), CallbackEventType.class)) {
        		return (Messages)method.invoke(listener, eventSource, CallbackEventType.VERIFIER);
        	} else if(isAssignableFroms(paramTypes, eventSource.getClass(), WhenVerifier.class)) {
        		return (Messages)method.invoke(listener, eventSource, when);
        	} else {
        		return (Messages)method.invoke(listener, eventSource, CallbackEventType.VERIFIER, when);
        	}
		}

		@Override
		boolean isSelfCallback() {
			return false;
		}
	}
}
