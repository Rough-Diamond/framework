/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.service;

/**
 * サービスを取得するためのインタフェース
 */
public interface ServiceFinder {
	/**
	 * サービスを取得する
	 * @param <T>				返却するインスタンスのタイプ
	 * @param cl				サービスの基点となるインスタンスのタイプ
	 * @param defaultClass		DIコンテナに設定が無い場合に実体化するインスタンスのタイプ	
     * @return      サービス 
	 */
    public <T extends Service> T getService(Class<T> cl, Class<? extends T> defaultClass);
}
