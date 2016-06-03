/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.util.mule.transformer;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

@SuppressWarnings("unchecked")
class ListInfo {
	final List list;
	final Type listGenericsType;
	ListInfo(List list, Type listGenericsType) {
		this.list = list;
		this.listGenericsType = listGenericsType;
	}

	static ListInfo getListInfo(Object jaxbArrayObj) throws Exception {
		Method m = getGetListMethod(jaxbArrayObj);
		List<?> list = (List<?>)m.invoke(jaxbArrayObj, new Object[0]);
		Type t = m.getGenericReturnType();
		if(!(t instanceof ParameterizedType)) {
			throw new RuntimeException("GenericsÇÃíËã`Ç™Ç†ÇËÇ‹ÇπÇÒÅB");
		}
		ParameterizedType pt = (ParameterizedType)t;
		Class<?> listGenericsType = (Class<?>)pt.getActualTypeArguments()[0];
		return new ListInfo(list, listGenericsType);
	}

	static Method getGetListMethod(Object destVal) {
		Method[] methods = destVal.getClass().getMethods();
		for(Method m : methods) {
			if(List.class.isAssignableFrom(m.getReturnType()) && m.getParameterTypes().length == 0) {
				return m;
			}
		}
		throw new RuntimeException("ListÇÃï‘ãpÇ™Ç≈Ç´Ç‹ÇπÇÒÅB");
	}
}
