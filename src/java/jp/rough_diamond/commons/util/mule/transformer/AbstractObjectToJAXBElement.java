/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.util.mule.transformer;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import jp.rough_diamond.commons.util.PropertyUtils.SkipProperty;



import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;

/**
 * 通常のJavaオブジェクトからプロパティ名ベースでJAXBエレメントに変換させるトランスフォーマー
 * CXFのwsdl2javaによって出力されたスタブコードを元に変換処理を行う
 */
abstract public class AbstractObjectToJAXBElement extends AbstractTransformer {
	private final static Log log = LogFactory.getLog(AbstractObjectToJAXBElement.class);
	
	@Override
	protected Object doTransform(Object src, String encoding) throws TransformerException {
		Method method = getMethod();
		if(method == null) {
			log.warn("ラッパーメソッドが取得できないためタイプ変換は行いません。");
			return src;
		}
		Object factory = createObjectFactory(getPortType());
		if(factory == null) {
			log.warn("ObjectFactoryの取得に失敗したため変換は行いません。");
			return src;
		}
		int numberOfParams = method.getParameterTypes().length;
		if(numberOfParams == 1) {
			return transform(src, method.getParameterTypes()[0], factory);
		}
		Object[] params = (Object[])src;
		Object[] ret = new Object[params.length];
		for(int i = 0 ; i < ret.length ; i++) {
			ret[i] = transform(params[i], method.getParameterTypes()[i], factory);
		}
		return ret;
	}

	Object transform(Object src, Class<?> parameterType, Object factory) {
		if(log.isDebugEnabled()) {
			log.debug("srcType:" + src.getClass().getName());
			log.debug("destType:" + parameterType.getName());
		}
		if(parameterType.isAssignableFrom(src.getClass())) {
			log.debug("変換が不要なオブジェクトなので変換処理は行いません。");
			return src;
		}
		try {
			return createAndCopyJAXBElement(factory, src, parameterType);
		} catch(Exception e) {
			log.warn("プロパティのコピー中に例外が発生したため変換は行いません。", e);
			return src;
		}
	}

	@SuppressWarnings("unchecked")
	private Object createAndCopyJAXBElement(Object factory, Object srcVal, Class<?> cl) throws Exception {
		Object destVal = createObjectByType(factory, cl);
		if(srcVal.getClass().isArray()) {
			ListInfo listInfo = ListInfo.getListInfo(destVal);
			copyJAXElementOfArrayFromArray(factory, srcVal, listInfo);
		} else if(srcVal instanceof Collection) {
			ListInfo listInfo = ListInfo.getListInfo(destVal);
			copyJAXElementOfArrayFromCollection(factory, (Collection)srcVal, listInfo);
		} else if(srcVal instanceof Map) {
			copyJAXElementOfMapFromMap(factory, (Map)srcVal, destVal);
		} else {
			log.debug(destVal.getClass().getName());
			copyProperty(factory, srcVal, destVal);
		}
		return destVal;
	}

	@SuppressWarnings("unchecked")
	void copyJAXElementOfMapFromMap(Object factory, Map map, Object dest) throws Exception {
		MapInfo info = MapInfo.getMapInfo(dest);
		Iterator iterator = map.entrySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry srcEntry = (Map.Entry)iterator.next();
			Object destEntry = transform(srcEntry, info.entryType, factory);
			info.list.add(destEntry);
		}
	}

	void copyProperty(Object factory, Object src, Object dest) throws Exception {
		PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(dest);
		for(PropertyDescriptor pd : pds) {
			Class<?> pdType = pd.getPropertyType();
			PropertyDescriptor srcPD = PropertyUtils.getPropertyDescriptor(src, pd.getName());
			if(srcPD == null) {
				if(log.isDebugEnabled()) {
					log.warn(pd.getName() + "プロパティが変換前オブジェクトに存在しません。スキップします。");
				}
				continue;
			}
			if(srcPD.getName().equals("class")) {
				continue;
			}
			Method getter = srcPD.getReadMethod();
			if(getter == null) {
				if(log.isDebugEnabled()) {
					log.warn(pd.getName() + "プロパティのgetterメソッドが変換前オブジェクトに存在しません。スキップします。");
				}
				continue;
			}
			if(getter.getAnnotation(SkipProperty.class) != null) {
				if(log.isDebugEnabled()) {
					log.warn(pd.getName() + "プロパティは、スキップ対象プロパティです。スキップします。");
				}
				continue;
			}
			Object srcVal = PropertyUtils.getProperty(src, srcPD.getName());
			if(srcVal == null) {
				continue;
			}
			if(pdType.equals(XMLGregorianCalendar.class)) {
				copyDateObject(pd, srcVal, dest);
			} else if(pdType.equals(JAXBElement.class)) {
				copyJAXBElement(factory, pd, srcVal, dest);
			} else {
				PropertyUtils.setProperty(dest, pd.getName(), transform(srcVal, pd.getPropertyType(), factory));
			}
		}
	}

	void copyJAXBElement(Object factory, PropertyDescriptor pd, Object srcVal, Object dest) throws Exception {
		String copyMethodName = getCopyMethodName(pd, dest);
		log.debug(copyMethodName);
		Method m  = getCreateMethod(factory, copyMethodName);
		if(!m.getParameterTypes()[0].isAssignableFrom(srcVal.getClass())) {
			srcVal = createAndCopyJAXBElement(factory, srcVal, m.getParameterTypes()[0]);
		}
		Object destVal = m.invoke(factory, srcVal);
		PropertyUtils.setProperty(dest, pd.getName(), destVal);
	}

	@SuppressWarnings("unchecked")
	void copyJAXElementOfArrayFromCollection(Object factory,
			Collection srcVal, ListInfo listInfo) throws Exception {
		for(Object element : srcVal) {
			copyElement(factory, element, listInfo);
		}
	}

	void copyJAXElementOfArrayFromArray(Object factory, Object srcVal, ListInfo listInfo) throws Exception {
		for(int i = 0 ; i < Array.getLength(srcVal) ; i++) {
			Object element = Array.get(srcVal, i);
			copyElement(factory, element, listInfo);
		}
	}

	@SuppressWarnings("unchecked")
	void copyElement(Object factory, Object element, ListInfo listInfo) throws Exception {
		Class componentType;
		if(listInfo.listGenericsType instanceof Class) {
			componentType = (Class)listInfo.listGenericsType;
		} else {
			componentType = (Class)((ParameterizedType)listInfo.listGenericsType).getRawType();
		}
		if(componentType.isAssignableFrom(element.getClass())) {
			listInfo.list.add(element);
		} else {
			Object destElement = transform(element, componentType, factory);
			listInfo.list.add(destElement);
		}
	}

	private Method getCreateMethod(Object factory, String copyMethodName) {
		Method[] methods = factory.getClass().getMethods();
		for(Method m : methods) {
			if(m.getName().equals(copyMethodName) 
					&& JAXBElement.class.isAssignableFrom(m.getReturnType()) 
					&& m.getParameterTypes().length == 1) {
				return m;
			}
		}
		throw new RuntimeException();
	}

	private String getCopyMethodName(PropertyDescriptor pd, Object dest) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("create");
		sb.append(dest.getClass().getSimpleName());
		char[] tmp = pd.getName().toCharArray();
		tmp[0] = Character.toUpperCase(tmp[0]);
		sb.append(tmp);
		return sb.toString();
	}
	
	void copyDateObject(PropertyDescriptor pd, Object srcVal, Object dest) throws Exception {
		if(srcVal instanceof Date) {
			Date d = (Date)srcVal;
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			srcVal = cal;
		}
		if(srcVal instanceof Calendar && !(srcVal instanceof GregorianCalendar)) {
			//XXX 多分ここには入らん
			Calendar cal = (Calendar)srcVal;
			GregorianCalendar gCal = new GregorianCalendar(
					cal.get(Calendar.YEAR),
					cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH),
					cal.get(Calendar.HOUR_OF_DAY),
					cal.get(Calendar.MINUTE),
					cal.get(Calendar.SECOND));
			gCal.setTimeZone(cal.getTimeZone());
			srcVal = gCal;
		}
		if(srcVal instanceof GregorianCalendar) {
			GregorianCalendar gCal = (GregorianCalendar)srcVal;
			DatatypeFactory dtf = DatatypeFactory.newInstance();
			XMLGregorianCalendar xCal = dtf.newXMLGregorianCalendar(gCal);
			PropertyUtils.setProperty(dest, pd.getName(), xCal);
		} else if(log.isDebugEnabled()){
			log.debug(pd.getName() + "プロパティが変換前オブジェクトでは日付ではありません。");
		}
	}

	Object createObjectByType(Object factory, Class<?> parameterType) {
		try {
			Class<?> factoryType = factory.getClass();
			Method[] methods = factoryType.getMethods();
			for(Method m : methods) {
				if(m.getName().startsWith("create") && m.getParameterTypes().length == 0 && m.getReturnType().equals(parameterType)) {
					return m.invoke(factory, new Object[0]);
				}
			}
		} catch(Exception e) {
			log.debug(e.getMessage(), e);
		}
		return null;
	}
	
	Object createObjectFactory(Class<?> parameterType) {
		try {
			Package p = parameterType.getPackage();
			return Class.forName(p.getName() + "." + "ObjectFactory").newInstance();
		} catch (Exception e) {
			log.debug(parameterType);
			log.debug(e.getMessage(), e);
			return null;
		}
	}
	
	protected Method getMethod() {
		Class<?> portType = getPortType();
		String operation = getOperation();
		Method[] methods = portType.getMethods();
		for(Method m : methods) {
			//XXX 同名メソッドが複数ある場合はＮＧ
			if(m.getName().equals(operation)) {
				return m;
			}
		}
		return null;
	}
	
	/**
	 * WSDLのポートに対応するクラスを返却する
	 * @return
	 */
	abstract protected Class<?> getPortType();

	/**
	 * オペレーション名を返却する 
	 */
	abstract protected String getOperation();
}
