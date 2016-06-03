/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.di;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * DIコンテナの実装を隠蔽するDIContainerを管理するクラスです。
 * <p>
 *   デフォルト実装では、SpringFrameworkをラップするDIContainerを保持します。
 * </p>
 * <p>
 *   なお、SpringFrameworkをラップするDIContainerはリフレクション経由で生成を行うため、
 *   SpringFramrworkとの静的な関連はありません。<br />
 *   SpringFramework以外のDIContainerをラップするDIContainerを使用する場合は、
 *   getDIContainer()を呼び出す前にsetDIContainer()を呼び出してDIContainerを事前にセットすることで、
 *   SpringFrameworkとの動的な関連も持たなくなり、SpringFramework関連のjarをclasspathに加える必要はなくなります。
 * </p>
 */
public class DIContainerFactory {
	private final static Log log = LogFactory.getLog(DIContainerFactory.class);
	
	private static DIContainer instance;
	private final static String DEFAULT_DI_CONTAINER = "jp.rough_diamond.commons.di.SpringFramework";
	
	/**
	 * DIContainerを取得する
	 * @return DIContainer
	 */
	synchronized public static DIContainer getDIContainer() {
		if(instance == null) {
			try {
				instance = (DIContainer)Class.forName(DEFAULT_DI_CONTAINER).newInstance();
			} catch (Exception e) {
				log.warn(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}
		return instance;
	}
	
	/**
	 * DIContainerを設定する
	 * 以降、getDIContainer()で返却されるDIContainerは設定されたDIContainerとなる。
	 * nullをセットした場合はデフォルトのDIContainerであるSpringFrameworkが返却される。
	 * @param container DIContainer
	 */
	public synchronized static void setDIContainer(DIContainer container) {
		instance = container;
	}
}
