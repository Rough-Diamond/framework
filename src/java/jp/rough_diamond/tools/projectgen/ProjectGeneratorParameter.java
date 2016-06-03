/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.tools.projectgen;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * プロジェクト生成パラメータ
 */
public class ProjectGeneratorParameter {
	public ProjectGeneratorParameter() {
		frameworkRoot = getFrameworkRootDefault();
	}
	
	/**
	 * RDF Coreのルートディレクトリ
	 */
	private String frameworkRoot;
	/**
	 * プロジェクト名
	 */
	private String projectName = "";
	/**
	 * ソースエンコーディング
	 */
	private String sourceEncoding = Charset.defaultCharset().toString();
	/**
	 * プロジェクトルート
	 */
	private String projectRoot;
	/**
	 * アプリケーションタイプ
	 */
	private ApplicationType 	appType;
	/**
	 * アプリケーションオプション
	 */
	private ApplicationOption[]	options = new ApplicationOption[0];
	
	public boolean isContainOption(String options) {
		return getOptions().contains(ApplicationOption.valueOf(options));
	}
	public Set<ApplicationOption> getOptions() {
		return new LinkedHashSet<ApplicationOption>(Arrays.asList(options));
	}

	public void setOptions(ApplicationOption... options) {
		this.options = Arrays.copyOf(options, options.length);
	}

	public ApplicationType getAppType() {
		return appType;
	}

	public void setAppType(ApplicationType appType) {
		this.appType = appType;
	}

	public String getProjectRoot() {
		return projectRoot;
	}

	public void setProjectRoot(String projectRoot) {
		this.projectRoot = projectRoot.replaceAll("\\\\", "/");
	}

	public String getSourceEncoding() {
		return sourceEncoding;
	}

	public void setSourceEncoding(String sourceEncoding) {
		this.sourceEncoding = sourceEncoding;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public static String getFrameworkRootDefault() {
		return System.getProperty("user.dir").replaceAll("\\\\", "/");
	}
	
	public String getFrameworkRoot() {
		return frameworkRoot;
	}

	public void setFrameworkRoot(String frameworkRoot) {
		this.frameworkRoot = frameworkRoot.replaceAll("\\\\", "/");
	}
	
	public String getFrameworkPathRelative() throws IOException {
		File pr = getProjectRootPathDirect();
		if(pr.isAbsolute()) {
			return getFrameworkRoot();
		} else {
			pr = getProjectRootPath();
			String fr = getFrameworkRoot();
			StringBuilder sb = new StringBuilder();
			File tmp = pr;
			while(tmp != null) {
				if(fr.startsWith(tmp.getCanonicalPath().replaceAll("\\\\", "/"))) {
					break;
				}
				tmp = tmp.getParentFile();
				sb.append("../");
			}
			sb.append(new File(getFrameworkRoot()).getName());
			return sb.toString();
		}
	}
	
	public File getProjectRootPath() {
		File tmp = getProjectRootPathDirect();
		if(tmp.isAbsolute()) {
			return tmp;
		} else {
			return new File(new File(getFrameworkRoot()), "../" + getProjectRoot());
		}
	}
	
	File getProjectRootPathDirect() {
		return new File(getProjectRoot());
	}
	
	public static enum ApplicationType {
		/**
		 * 通常のJavaアプリケーション
		 */
		SIMPLE,
		/**
		 * Webアプリケーション（現在はWEBAPP_WITH_TOMCAT_PI）と同じ
		 */
		@Deprecated
		WEBAPP,
		/**
		 * Webアプリケーション(Tomcatプラグイン使用） 
		 */
		WEBAPP_WITH_TOMCAT_PI,
		;
	}
	
	public static enum ApplicationOption {
		/**
		 * makeBeanを使用する
		 */
		USING_MAKE_BEAN,
		/**
		 * データベースアクセスフレームワークを使用する
		 */
		USING_DATABASE,
		/**
		 * Mule ESBを使用する
		 */
		USING_ESB,
		/**
		 * RDF Web Frameworkを使用する
		 * (Strutsベース）
		 */
		USING_RDF_WEB_FR,
		;
	}
}
