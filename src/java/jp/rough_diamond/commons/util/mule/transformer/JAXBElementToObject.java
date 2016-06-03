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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import jp.rough_diamond.commons.util.PropertyUtils;



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 通常のJAXBエレメントからプロパティ名ベースでJavaオブジェクトに変換させるトランスフォーマー
 * CXFのwsdl2javaによって出力されたスタブコードを元に変換処理を行う
 */
@SuppressWarnings("unchecked")
public class JAXBElementToObject {
	private final static Log log = LogFactory.getLog(JAXBElementToObject.class);
	
	public Object transform(Object src, Type returnType) {
		if(src == null) {
			return null;
		}
		log.debug(src);
		if(JAXBElement.class.isAssignableFrom(src.getClass())) {
			return transform(((JAXBElement)src).getValue(), returnType);
		}
		if(!(returnType instanceof Class)) {
			//XXX ParameterizedType以外の場合はどーなるんだろう。。。
			return transform(src, (ParameterizedType)returnType);
		}
		Class returnClassType = (Class)returnType;
		if(returnClassType.isAssignableFrom(src.getClass())) {
			log.debug("変換が不要なオブジェクトなので変換処理は行いません。");
			return src;
		}
		if(returnClassType.isPrimitive()) {
			log.debug("変換が不要なオブジェクト（プリミティブ）なので変換処理は行いません。");
			return src;
		}
		if(returnClassType.isArray()) {
			Class componentType  = returnClassType.getComponentType();
			List list = makeList(src, componentType);
			Object ret = Array.newInstance(componentType, list.size());
			return list.toArray((Object[])ret);
		}
		Object dest = createObjectByType(returnClassType);
		if(dest == null) {
			log.warn("返却オブジェクトの生成に失敗したため変換は行いません");
			return src;
		}
		try {
			copyProperty(src, dest);
			return dest;
		} catch(Exception e) {
			log.warn("プロパティのコピー中に例外が発生したため変換は行いません。", e);
			return src;
		}
	}
	
	Object transform(Object src, ParameterizedType pt) {
		Class cl = (Class)pt.getRawType();
		log.debug(cl.getName());
		if(List.class.isAssignableFrom(cl)) {
			return makeList(src, pt.getActualTypeArguments()[0]);
		} else if(Map.class.isAssignableFrom(cl)) {
			return makeMap(src, pt.getActualTypeArguments());
		}
		return null;
	}
	
	Object makeMap(Object src, Type[] actualTypeArguments) {
		Map ret = new HashMap();
		try {
			MapInfo info = MapInfo.getMapInfo(src);
			for(Object o : info.list) {
				Object key = transform(MapInfo.getKey(o), actualTypeArguments[0]);
				Object value = transform(MapInfo.getValue(o), actualTypeArguments[1]);
				ret.put(key, value);
			}
			return ret;
		} catch(Exception e) {
			log.warn("プロパティのコピー中に例外が発生したため変換は行いません。", e);
			return ret;
		}
	}

	List makeList(Object src, Type type) {
		List ret = new ArrayList();
		try {
			ListInfo info = ListInfo.getListInfo(src);
			for(Object o : info.list) {
				ret.add(transform(o, type));
			}
			return ret;
		} catch(Exception e) {
			log.warn("プロパティのコピー中に例外が発生したため変換は行いません。", e);
			return ret;
		}
	}

	void copyProperty(Object src, Object dest) throws Exception {
		PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(src);
		for(PropertyDescriptor pd : pds) {
			PropertyDescriptor destPD = PropertyUtils.getPropertyDescriptor(dest, pd.getName());
			if(destPD == null) {
				log.warn(pd.getName() + "プロパティが変換後オブジェクトに存在しません。スキップします。");
				continue;
			} else if(destPD.getWriteMethod() == null) {
				if(log.isDebugEnabled()) {
					log.debug(pd.getName() + "プロパティのsetterが存在しません。スキップします。");
				}
				continue;
			}
			Method m = PropertyUtils.getGetterMethod(src, pd.getName()); 
			Object srcVal = m.invoke(src);
			if(srcVal == null) {
				if(log.isDebugEnabled()) {
					log.debug(pd.getName() + "プロパティはnullです。強制的にnullをセットします。");
				}
				destPD.getWriteMethod().invoke(dest, (Object)null);
				continue;
			}
			if(srcVal instanceof JAXBElement) {
				srcVal = ((JAXBElement)srcVal).getValue();
			}
			copyElement(destPD, srcVal, dest);
		}
	}

	void copyElement(PropertyDescriptor destPD, Object srcVal, Object dest) throws Exception {
		if(srcVal instanceof XMLGregorianCalendar) {
			copyDateObject(destPD, (XMLGregorianCalendar)srcVal, dest);
		} else {
			Object destVal = transform(srcVal, destPD.getWriteMethod().getGenericParameterTypes()[0]);
			PropertyUtils.setProperty(dest, destPD.getName(), destVal);
		}
	}
	
	void copyDateObject(PropertyDescriptor pd, XMLGregorianCalendar srcVal, Object dest) throws Exception {
		Calendar cal = xmlCalendarToCalendar(srcVal);
		Class paramType = pd.getWriteMethod().getParameterTypes()[0];
		if(Date.class.isAssignableFrom(paramType)) {
			pd.getWriteMethod().invoke(dest, cal.getTime());
		} else {
			pd.getWriteMethod().invoke(dest, cal);
		}
	}

	Calendar xmlCalendarToCalendar(XMLGregorianCalendar srcVal) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, zeroWhenFieldUndefined(srcVal.getYear()));
		cal.set(Calendar.MONTH, zeroWhenFieldUndefined(srcVal.getMonth(), 1) - 1);
		cal.set(Calendar.DAY_OF_MONTH, zeroWhenFieldUndefined(srcVal.getDay()));
		cal.set(Calendar.HOUR_OF_DAY, zeroWhenFieldUndefined(srcVal.getHour()));
		cal.set(Calendar.MINUTE, zeroWhenFieldUndefined(srcVal.getMinute()));
		cal.set(Calendar.SECOND, zeroWhenFieldUndefined(srcVal.getSecond()));
		cal.set(Calendar.MILLISECOND, zeroWhenFieldUndefined(srcVal.getMillisecond()));
		cal.setTimeZone(srcVal.getTimeZone(zeroWhenFieldUndefined(srcVal.getTimezone())));
		return cal;
	}

	int zeroWhenFieldUndefined(int val) {
		return zeroWhenFieldUndefined(val, 0);
	}
	
	int zeroWhenFieldUndefined(int val, int defaultVal) {
		return (val == DatatypeConstants.FIELD_UNDEFINED) ? defaultVal : val;
	}
	
	Object createObjectByType(Class<?> parameterType) {
		try {
			return parameterType.newInstance();
		} catch(Exception e) {
			log.debug(e.getMessage(), e);
		}
		return null;
	}
}
