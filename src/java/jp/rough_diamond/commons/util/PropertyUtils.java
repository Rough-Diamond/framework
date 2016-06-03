/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.util;

import java.beans.PropertyDescriptor;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * プロパティユーティリティ
 */
public class PropertyUtils extends org.apache.commons.beanutils.PropertyUtils {
	private final static Log log = LogFactory.getLog(PropertyUtils.class);
	
	/**
	 * getterメソッドにこのアノテーションが付与されていれば、プロパティのコピーは行わない
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public static @interface SkipProperty { };
	
	private static ThreadLocal<Stack<?>> copyStack = new ThreadLocal<Stack<?>>(){
		@Override
		protected Stack<?> initialValue() {
			return new Stack<Object>();
		}
	};
	
	@SuppressWarnings("unchecked")
	public static void copyProperties(Object src, Object dest) {
		Stack stack = copyStack.get();
		if(stack.contains(src)) {
			log.debug("既にコピー対象のオブジェクト（相互参照しているオブジェクト）です。コピーは行いません。");
			return;
		}
		stack.push(src);
		try {
			log.debug(dest.getClass().getName());
			Map in = describeFromDest(src, dest);
			log.debug(in);
			for(Object o : in.entrySet()) {
				Map.Entry entry = (Map.Entry)o;
				Object inValue = entry.getValue();
				String propName = (String)entry.getKey();
				if(log.isDebugEnabled()) {
					log.debug("copy target propertyName:" + propName);
				}
				Method m = getSetterMethod(dest, propName);
				if(inValue == null) {
					m.invoke(dest, (Object)null);
				} else {
					Class outType = m.getParameterTypes()[0];
					if(Collection.class.isAssignableFrom(outType) && outType.isAssignableFrom(inValue.getClass()) && doneCollectionCopy(src, dest, propName, (Collection)inValue)) {
						//doneCollectionCopy内でコピーを実行しているのでここでは実装無し
					} else if (Collection.class.isAssignableFrom(inValue.getClass()) && outType.isArray()) {
						copyCollection2Array(src, dest, propName, (Collection)inValue);
					} else if(outType.isAssignableFrom(inValue.getClass())) {
						m.invoke(dest, inValue);
					} else {
						Object outValue = null;
						Method getter = getGetterMethod(dest, propName);
						if(getter != null) {
							outValue = getter.invoke(dest, (Object[])null);
						}
						if(outValue == null) {
							outValue = outType.newInstance();
							m.invoke(dest, outValue);
						}
						copyProperties(inValue, outValue);
					}
				}
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		} finally {
			stack.pop();
		}
	}

	@SuppressWarnings("unchecked")
	static Map describeFromDest(Object src, Object dest) throws Exception {
		PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(dest);
		Map ret = new HashMap();
		for(int i = 0 ; i < pds.length ; i++) {
			String propName = pds[i].getName();
			Method getter = getGetterMethod(src, propName);
			if(isTargetProperty(pds[i], getter)) {
				ret.put(pds[i].getName(), getter.invoke(src));
			}
		}
		return ret;
	}
	
	static boolean isTargetProperty(PropertyDescriptor descPD, Method getter) {
		if(getter == null) {
			return false;
		}
		if(getter.getAnnotation(SkipProperty.class) != null) {
			return false;
		}
		if(descPD.getWriteMethod() == null) {
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private static void copyCollection2Array(Object src, Object dest, String propName, Collection inValue) throws Exception {
		if(log.isDebugEnabled()) {
			log.debug(propName + "のcopyCollection2Arrayを試みます");
		}
		Method m = getSetterMethod(dest, propName);
		Class<?> type = m.getParameterTypes()[0].getComponentType();
		Object o = Array.newInstance(type, inValue.size());
		int i = 0;
		for(Object val : inValue) {
			Object destVal;
			if(type.isAssignableFrom(val.getClass())) {
				if(type.isPrimitive()) {
					destVal = val;
				} else if(val instanceof String) {
					//TODO ださい。。。
					destVal = val;
				} else if(val instanceof Date) {
					//TODO ださい。。。
					destVal = new Date(((Date)val).getTime());
				} else {
					destVal = val.getClass().newInstance();
					copyProperties(val, destVal);
				}
			} else {
				destVal = type.newInstance();
				copyProperties(val, destVal);
			}
			Array.set(o, i++, destVal);
		}
		m.invoke(dest, o);
	}

	@SuppressWarnings("unchecked")
	private static boolean doneCollectionCopy(Object src, Object dest, String propName, Collection inValue) throws Exception {
		if(log.isDebugEnabled()) {
			log.debug(propName + "のdoneCollectionCopyを試みます");
		}
		Method m = getSetterMethod(dest, propName);
		Type t = m.getGenericParameterTypes()[0];
		if(!(t instanceof ParameterizedType)) {
			if(log.isDebugEnabled()) {
				log.debug(propName + "では、コレクションのコピーは行えません");
			}
			return false;
		}
		ParameterizedType pt = (ParameterizedType)t;
		Class genericType = (Class)pt.getActualTypeArguments()[0];
		Collection destCol = (Collection)inValue.getClass().newInstance();
		for(Object val : inValue) {
			Object destVal;
			if(val instanceof String) {
				//TODO ださい。。。
				destVal = val;
			} else if(val instanceof Date) {
				//TODO ださい。。。
				destVal = new Date(((Date)val).getTime());
			} else {
				destVal = genericType.newInstance();
			}
			if(log.isDebugEnabled()) {
				log.debug(val.getClass().getName() + "から" + destVal.getClass().getName() + "へコピーします");
			}
			copyProperties(val, destVal);
			destCol.add(destVal);
		}
		m.invoke(dest, destCol);
		if(log.isDebugEnabled()) {
			log.debug(propName + "では、コレクションのコピーが完了しました。");
		}
		return true;
	}
	
	public static Method getGetterMethod(Object dest, String propertyName) {
		char[] chs = propertyName.toCharArray();
		chs[0] = Character.toUpperCase(chs[0]);
		String methodName = "get" + new String(chs);
		log.debug(methodName);
		Method[] ms = dest.getClass().getMethods();
		for(Method m : ms) {
			if(m.getName().equals(methodName) && m.getReturnType() != Void.TYPE && m.getParameterTypes().length == 0) {
				log.debug(m);
				return m;
			}
		}
		methodName = "is" + new String(chs);
		log.debug(methodName);
		ms = dest.getClass().getMethods();
		for(Method m : ms) {
			if(m.getName().equals(methodName) && m.getReturnType() != Void.TYPE && m.getParameterTypes().length == 0) {
				log.debug(m);
				return m;
			}
		}
		return null;
	}
	
	public static Object getProperty(Object dest, String propertyName) {
		String[] strArray = propertyName.split("\\.");
		if(strArray.length == 0) {
			throw new RuntimeException();
		}
		try {
			for(String methodName : strArray) {
				Method m  = getGetterMethod(dest, methodName);
				dest = m.invoke(dest);
			}
			return dest;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Method getSetterMethod(Object dest, String propertyName) {
		char[] chs = propertyName.toCharArray();
		chs[0] = Character.toUpperCase(chs[0]);
		String methodName = "set" + new String(chs);
		log.debug(methodName);
		Method[] ms = dest.getClass().getMethods();
		for(Method m : ms) {
			if(m.getName().equals(methodName) && m.getParameterTypes().length == 1) {
				return m;
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		copyProperties(new Date(), new Date());
	}
	
}
