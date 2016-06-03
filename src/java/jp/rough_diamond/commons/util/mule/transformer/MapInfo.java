/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.util.mule.transformer;

import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

@SuppressWarnings("unchecked")
class MapInfo {
	final List list;
	final Class<?> entryType;
	MapInfo(List list, Class<?> entryType) {
		this.list = list;
		this.entryType = entryType;
	}
	static MapInfo getMapInfo(Object jaxbMapObj) throws Exception {
		List list = (List<?>)PropertyUtils.getProperty(jaxbMapObj, "entry");
		Class entryType = Class.forName(jaxbMapObj.getClass().getName() + "$Entry");
		return new MapInfo(list, entryType);
	}
	static Object getKey(Object entry) throws Exception {
		return PropertyUtils.getProperty(entry, "key");
	}
	
	static Object getValue(Object entry) throws Exception {
		return PropertyUtils.getProperty(entry, "value");
	}
}
