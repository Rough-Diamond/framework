/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.di;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

/**
 * SpringFrameworkを利用するDIコンテナの実装です。<br />
 * @see <a href="http://www.springframework.org/" target="_blank">Spring Framework</a> 
 */
public class SpringFramework 
		extends AbstractDIContainer { 
	
	private final static Log log = LogFactory.getLog(SpringFramework.class);

	private BeanFactory factory;

	/**
	 * ClassPath上のbeans.xmlを用いてXMLBeanFactoryが生成したBeanFactoryをDIコンテナ実装とするオブジェクトを生成します。
	 */
	public SpringFramework() {
		this("beans.xml");
	}
	
	/**
	 * 指定されたリソースをClassPathから定義ファイルを読込んでXMLBeanFactoryが生成したBeanFactoryをDIコンテナ実装とするオブジェクトを生成します。
	 * @param configName	classPath上に存在するDI定義ファイル
	 */
	public SpringFramework(String configName) {
		this(new ClassPathResource(configName));
	}

	/**
	 * 指定されたファイルの定義ファイルを読込んでXMLBeanFactoryが生成したBeanFactoryをDIコンテナ実装とするオブジェクトを生成します。
	 * @param f	DI定義ファイルのファイルオブジェクト
	 */
	public SpringFramework(File f) throws FileNotFoundException {
		this(new FileSystemResource(f));
	}
	
	/**
	 * 指定されたURLの定義ファイルを読込んでXMLBeanFactoryが生成したBeanFactoryをDIコンテナ実装とするオブジェクトを生成します。
	 * @param url	DI定義ファイルのURLオブジェクト
	 */
	public SpringFramework(URL url) throws IOException {
		this(new UrlResource(url));
	}
	
	/**
	 * 指定された入力ストリームを読込んでXMLBeanFactoryが生成したBeanFactoryをDIコンテナ実装とするオブジェクトを生成します。
	 * @param is	DI定義情報の入力ストリーム
	 * @deprecated エラーが出ることがあるので使用しないことを推奨
	 */
	public SpringFramework(InputStream is) {
		this(new InputStreamResource(is, ""));
	}
	
	/**
	 * 指定されたリソースオブジェクトからXMLBeanFactoryを生成する。
	 * @param resource
	 * @since 1.3
	 */
	public SpringFramework(Resource resource) {
		factory = new XmlBeanFactory(resource);
	}

	@SuppressWarnings("unchecked")
	public <T> T getObject(Class<T> type, Object key) {
		if(log.isDebugEnabled()) {
			log.debug("getObject calling[" + key + "]");
		}
		try {
			return (T)factory.getBean((String)key);
		} catch(Exception ex) {
			if(log.isDebugEnabled()) {
				log.debug(key + " is not bean.", ex);
			}
			return null;
		} catch(Throwable t) {
			log.error(key + " is not bean.", t);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getSource(Class<T> type) {
		return (T)factory;
	}
}
