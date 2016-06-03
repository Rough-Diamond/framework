/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.lang;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * クラス関連のユーティリティクラス
 * 本クラスは、jakarta commons langのClassUtilsの拡張APIです。
 *
 */
public class ClassUtils extends org.apache.commons.lang.ClassUtils {
	/**
	 * クラスオブジェクトのリソース名を返却する
	 * なお、先頭に「/」は付与していません。必要に応じて付与してください。
	 * @param 取得対象クラス
	 * @return クラスに対応するリソース名
	 */
	public static String translateResourceName(Class<?> cl) {
		String className = cl.getName();
		String resourceName = className.replaceAll("\\.", "/") + ".class";
		return resourceName;
	}
	
	/**
	 * クラスオブジェクトに対応するURLを返却する
	 * @param cl
	 * @return
	 */
	public static URL getClassURL(Class<?> cl) {
		return getClassURL(cl.getClassLoader(), translateResourceName(cl));
	}
	
	static URL getClassURL(ClassLoader cl, String resourceName) {
		return cl.getResource(resourceName);
	}
	
	/**
	 * クラスオブジェクトが所属するFileオブジェクトを返却する
	 * Fileオブジェクトがディレクトリの場合はディレクトリとして、ファイルの場合はjarファイル内に
	 * 指定されたクラスオブジェクトが配置されている判断できます。
	 * @param cl
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static File getClassPathRoot(Class<?> cl) {
		try {
			String resourceName = translateResourceName(cl);
			URL url = getClassURL(cl.getClassLoader(), resourceName);
			if(url.getProtocol().equals("file")) {
				String fileName = URLDecoder.decode(url.getPath());
				String rootDirName = fileName.replaceAll(resourceName + "$", "");
				return new File(rootDirName);
			} else {
				String jarFileName = url.getPath().replaceAll("\\!.*", "");
				return new File(URLDecoder.decode(new URL(jarFileName).getPath()));
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}
