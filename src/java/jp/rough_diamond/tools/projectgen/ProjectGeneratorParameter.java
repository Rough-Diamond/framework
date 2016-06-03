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
 * �v���W�F�N�g�����p�����[�^
 */
public class ProjectGeneratorParameter {
	public ProjectGeneratorParameter() {
		frameworkRoot = getFrameworkRootDefault();
	}
	
	/**
	 * RDF Core�̃��[�g�f�B���N�g��
	 */
	private String frameworkRoot;
	/**
	 * �v���W�F�N�g��
	 */
	private String projectName = "";
	/**
	 * �\�[�X�G���R�[�f�B���O
	 */
	private String sourceEncoding = Charset.defaultCharset().toString();
	/**
	 * �v���W�F�N�g���[�g
	 */
	private String projectRoot;
	/**
	 * �A�v���P�[�V�����^�C�v
	 */
	private ApplicationType 	appType;
	/**
	 * �A�v���P�[�V�����I�v�V����
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
		 * �ʏ��Java�A�v���P�[�V����
		 */
		SIMPLE,
		/**
		 * Web�A�v���P�[�V�����i���݂�WEBAPP_WITH_TOMCAT_PI�j�Ɠ���
		 */
		@Deprecated
		WEBAPP,
		/**
		 * Web�A�v���P�[�V����(Tomcat�v���O�C���g�p�j 
		 */
		WEBAPP_WITH_TOMCAT_PI,
		;
	}
	
	public static enum ApplicationOption {
		/**
		 * makeBean���g�p����
		 */
		USING_MAKE_BEAN,
		/**
		 * �f�[�^�x�[�X�A�N�Z�X�t���[�����[�N���g�p����
		 */
		USING_DATABASE,
		/**
		 * Mule ESB���g�p����
		 */
		USING_ESB,
		/**
		 * RDF Web Framework���g�p����
		 * (Struts�x�[�X�j
		 */
		USING_RDF_WEB_FR,
		;
	}
}
