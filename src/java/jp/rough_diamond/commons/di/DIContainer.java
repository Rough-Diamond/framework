/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.di;

/**
 * DIコンテナの実装を隠蔽するインタフェースです。<br />
 */
public interface DIContainer {
	/**
	 * DIされるオブジェクトを取得します。
	 * @param key	キー
	 * @return DIオブジェクト
	 */
	public Object getObject(Object key);
	
	/**
	 * このDIContainerがラップしている実体を返却します。
	 * @return 実際のDIContainer
	 */
	public Object getSource();

	/**
	 * DIされるオブジェクトを取得する
	 * @param type	DIされるオブジェクトのタイプ
	 * @param key	キー
	 * @return		DIされるオブジェクト
	 */
	public <T> T getObject(Class<T> type, Object key);
	
	/**
	 * このDIContainerがラップしている実体を返却する
	 * @param type 実体のタイプ
	 * @return DIコンテナ実体
	 */
	public <T> T getSource(Class<T> type);
}
