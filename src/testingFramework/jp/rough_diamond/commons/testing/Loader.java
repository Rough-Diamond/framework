/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.testing;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jp.rough_diamond.framework.service.ServiceLocator;

/**
 * このクラスはラッパーである
 * 実処理はDBInitializerにある
 */
public class Loader {
	private final static Log log = LogFactory.getLog(Loader.class);
	
	private static Set<Class<? extends DBInitializer>> execResetter = new HashSet<Class<? extends DBInitializer>>();
	public static void reset(Class<? extends DBInitializer> resetterType) throws Exception {
		if(!execResetter.contains(resetterType)) {
			DBInitializer resetter = ServiceLocator.getService(resetterType);
			resetter.delete();
			execResetter.add(resetterType);
		}
	}
	
	public static void load(Class<? extends DBInitializer> loaderType) throws Exception {
		ResetterType rt = loaderType.getAnnotation(ResetterType.class);
		if(rt == null) {
			log.warn("ResetterTypeが指定されていないため、指定されたローダーでリセットします。");
			reset(loaderType);
		} else {
			reset(rt.type());
		}
		DBInitializer loader = ServiceLocator.getService(loaderType);
		loader.load();
	}
}
