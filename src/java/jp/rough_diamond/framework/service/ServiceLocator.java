/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.service;

/**
 * サービスを取得するラッパークラス
 * 一度取得できたクラスはキャッシュされる。
 */
@SuppressWarnings("unchecked")
public class ServiceLocator {
	public final static String SERVICE_LOCATOR_KEY = "serviceLocator"; 
	public final static String SERVICE_FINDER_KEY = "serviceFinder";

	/**
     * サービスを取得する
	 * @param <T>	返却するインスタンスのタイプ
     * @param cl    取得対象サービスクラス
     * @return      サービス 
     */
	public static <T extends Service> T getService(Class<T> cl) {
		return getService(cl, cl);
    }	
	
	/**
	 * サービスを取得する
	 * @param <T>				返却するインスタンスのタイプ
	 * @param cl				サービスの基点となるインスタンスのタイプ
	 * @param defaultClassName	DIコンテナに設定が無い場合に実体化するインスタンスのタイプ名	
     * @return      サービス 
	 */
	public static <T extends Service> T getService(Class<T> cl, String defaultClassName) {
		Class<? extends T> defaultClass;
		try {
			defaultClass = (Class<? extends T>)Class.forName(defaultClassName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return getService(cl, defaultClass);
	}
	
	/**
	 * サービスを取得する
	 * @param <T>				返却するインスタンスのタイプ
	 * @param cl				サービスの基点となるインスタンスのタイプ
	 * @param defaultClass		DIコンテナに設定が無い場合に実体化するインスタンスのタイプ	
     * @return      サービス 
	 */
	public static <T extends Service> T getService(Class<T> cl, Class<? extends T> defaultClass) {
    	return ServiceLocatorLogic.getServiceLocatorLogic().getService(cl, defaultClass);
	}
}
