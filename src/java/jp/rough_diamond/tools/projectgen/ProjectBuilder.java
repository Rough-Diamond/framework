/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.tools.projectgen;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import jp.rough_diamond.commons.io.IOUtils;
import jp.rough_diamond.commons.velocity.VelocityUtils;
import jp.rough_diamond.tools.projectgen.ProjectGeneratorParameter.ApplicationOption;
import jp.rough_diamond.tools.projectgen.ProjectGeneratorParameter.ApplicationType;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

/**
 * RDF Core使用プロジェクトビルダー
 */
@SuppressWarnings({"deprecation" })
public class ProjectBuilder {
	final static VelocityEngine ve;
	static {
		try {
	        Properties props = new Properties();
	        props.setProperty("input.encoding",                     "Shift_JIS");
	        props.setProperty("output.encoding",                    "Shift_JIS");
	        props.setProperty("resource.loader",                    "class");
	        props.setProperty("class.resource.loader.description",  "Velocity File Resource Loader");
	        props.setProperty("class.resource.loader.class",        "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			ve = new VelocityEngine(props);
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}
	private ProjectGeneratorParameter param;
	ProjectBuilder(ProjectGeneratorParameter param) {
		this.param = param;
	}
	
	String makeBuildScript() throws Exception {
		Template t = ve.getTemplate("jp/rough_diamond/tools/projectgen/template/build.xml.vm");
		return makeText(t);
	}
	
	String makeDefaultPropertiesFile() throws Exception {
		Template t = ve.getTemplate("jp/rough_diamond/tools/projectgen/template/filtering.default.properties.vm");
		return makeText(t);
	}
	
	String makeWebXml() throws Exception {
		Template t = ve.getTemplate("jp/rough_diamond/tools/projectgen/template/web.xml.vm");
		return makeText(t);
	}
	
	String makeStrutsConfigXml() throws Exception {
		Template t = ve.getTemplate("jp/rough_diamond/tools/projectgen/template/struts-config.xml.vm");
		return makeText(t);
	}
	
	String makeTilesDefsXml() throws Exception {
		Template t = ve.getTemplate("jp/rough_diamond/tools/projectgen/template/tiles-defs.xml.vm");
		return makeText(t);
	}
	
	String makeToolBoxXml() throws Exception {
		Template t = ve.getTemplate("jp/rough_diamond/tools/projectgen/template/toolbox.xml.vm");
		return makeText(t);
	}
	
	String makeBeansXml() throws Exception {
		Template t = ve.getTemplate("jp/rough_diamond/tools/projectgen/template/beans.xml.vm");
		return makeText(t);
	}
	
	String makeBeansDomainXml() throws Exception {
		Template t = ve.getTemplate("jp/rough_diamond/tools/projectgen/template/beans-domain.xml.vm");
		return makeText(t);
	}
	
	String makeCXFRoutingProperties() throws Exception {
		Template t = ve.getTemplate("jp/rough_diamond/tools/projectgen/template/cxfrouting.properties.vm");
		return makeText(t);
	}
	
	String makeLog4jProperties() throws Exception {
		Template t = ve.getTemplate("jp/rough_diamond/tools/projectgen/template/log4j.properties.vm");
		return makeText(t);
	}
	
	String makeMuleConfigReadme() throws Exception {
		Template t = ve.getTemplate("jp/rough_diamond/tools/projectgen/template/mule-config-README.vm");
		return makeText(t);
	}
	
	String makeMuleCommonConfig() throws Exception {
		Template t = ve.getTemplate("jp/rough_diamond/tools/projectgen/template/mule-common-config.xml.vm");
		return makeText(t);
	}
	
	String makeMuleConfig() throws Exception {
		Template t = ve.getTemplate("jp/rough_diamond/tools/projectgen/template/mule-config.xml.vm");
		return makeText(t);
	}
	
	String makeMessageResourceProperties() throws Exception {
		Template t = ve.getTemplate("jp/rough_diamond/tools/projectgen/template/messageResource.properties.vm");
		return makeText(t);
	}
	
	String makeErrorListVM() throws Exception {
		Template t = ve.getTemplate("jp/rough_diamond/tools/projectgen/template/errorList.vm");
		return makeText(t);
	}
	
	String makeBeanDefXml() throws Exception {
		Template t = ve.getTemplate("jp/rough_diamond/tools/projectgen/template/beanDef.xml.vm");
		return makeText(t);
	}
	
	String makeServicesXml() throws Exception {
		Template t = ve.getTemplate("jp/rough_diamond/tools/projectgen/template/services.xml.vm");
		return makeText(t);
	}
	
	String makeSchemaXml() throws Exception {
		Template t = ve.getTemplate("jp/rough_diamond/tools/projectgen/template/schema.xml.vm");
		return makeText(t);
	}
	
	String makeClasspath() throws Exception {
		Template t = ve.getTemplate("jp/rough_diamond/tools/projectgen/template/classpath.vm");
		return makeText(t);
	}
	
	String makeProjectFile() throws Exception {
		Template t = ve.getTemplate("jp/rough_diamond/tools/projectgen/template/project.vm");
		return makeText(t);
	}
	
	String makeTomcatConfig() throws Exception {
		Template t = ve.getTemplate("jp/rough_diamond/tools/projectgen/template/tomcatplugin.vm");
		return makeText(t);
	}
	
	String makeText(Template t) throws Exception {
		StringWriter sw = new StringWriter();
		Context c = new VelocityContext();
		c.put("param", param);
		c.put("builder", this);
		c.put("util", new VelocityUtils());
		c.put("srp", "#");
		c.put("doll", "$");
		t.merge(c, sw);
		return sw.toString();
	}

	public static void generate(ProjectGeneratorParameter param) throws Exception {
		ProjectBuilder pb = new ProjectBuilder(param);
		pb.generate();
	}
	
	void generate() throws Exception {
		mkdirs();
		generateFiles(getGenerateFiles());
		copyJars(getLibCopyInfos());
		generateClasspath();
		generateProjectFile();
		if(param.getAppType() == ApplicationType.WEBAPP_WITH_TOMCAT_PI) {
			generateTomcatConfig();
		}
	}

	void generateTomcatConfig() throws Exception {
		String text = makeTomcatConfig();
		byte[] data = text.getBytes("UTF-8");
		File f = new File(param.getProjectRootPath(), ".tomcatplugin");
		FileOutputStream fos = new FileOutputStream(f);
		try {
			IOUtils.copy(new ByteArrayInputStream(data), fos);
		} finally {
			try {
				fos.flush();
			} finally {
				fos.close();
			}
		}
	}

	void generateProjectFile() throws Exception {
		String text = makeProjectFile();
		byte[] data = text.getBytes("UTF-8");
		File f = new File(param.getProjectRootPath(), ".project");
		FileOutputStream fos = new FileOutputStream(f);
		try {
			IOUtils.copy(new ByteArrayInputStream(data), fos);
		} finally {
			try {
				fos.flush();
			} finally {
				fos.close();
			}
		}
	}

	void generateClasspath() throws Exception {
		String text = makeClasspath();
		byte[] data = text.getBytes("UTF-8");
		File f = new File(param.getProjectRootPath(), ".classpath");
		FileOutputStream fos = new FileOutputStream(f);
		try {
			IOUtils.copy(new ByteArrayInputStream(data), fos);
		} finally {
			try {
				fos.flush();
			} finally {
				fos.close();
			}
		}
	}

	void generateFiles(Set<FileGeneratorInfo> set) throws Exception {
		for(FileGeneratorInfo info : set) {
			String text = makeText(info);
			String fileName = getFileName(info);
			byte[] data = text.getBytes(param.getSourceEncoding());
			File f = new File(param.getProjectRootPath(), fileName);
			FileOutputStream fos = new FileOutputStream(f);
			try {
				IOUtils.copy(new ByteArrayInputStream(data), fos);
			} finally {
				try {
					fos.flush();
				} finally {
					fos.close();
				}
			}
		}
	}
	
	String getFileName(FileGeneratorInfo info) {
		return info.name.replace("${projectName}", param.getProjectName());
	}

	Set<FileGeneratorInfo> getGenerateFiles() {
		Set<FileGeneratorInfo> ret = new HashSet<FileGeneratorInfo>(FILE_SET_FOR_COMMON);
		ret.addAll(FILE_MAP_FOR_TYPE.get(param.getAppType()));
		for(ApplicationOption ao : param.getOptions()) {
			ret.addAll(FILE_MAP_FOR_OPTION.get(ao));
		}
		return ret;
	}
	
	String makeText(FileGeneratorInfo info) throws Exception {
		Method m = ProjectBuilder.class.getDeclaredMethod(info.makerMethodName);
		return (String)m.invoke(this);
	}
	
	void copyJars(List<LibCopyInfo> libCopyInfos) throws Exception {
		for(LibCopyInfo info : libCopyInfos) {
			copyJars(info);
		}
	}

	List<LibCopyInfo> getLibCopyInfos() throws Exception {
		List<LibCopyInfo> ret = new ArrayList<LibCopyInfo>(LIB_LIST_FOR_COMMON);
		ret.addAll(LIB_MAP_FOR_TYPE.get(param.getAppType()));
		for(ApplicationOption ao : param.getOptions()) {
			ret.addAll(LIB_MAP_FOR_OPTION.get(ao));
		}
		return ret;
	}
	
	public Set<String> getLibNameSet() {
		return this.libNameSet;
	}
	
	Set<String> libNameSet = new TreeSet<String>();
	void copyJars(LibCopyInfo info) throws Exception {
		String[] libNames = getJars(info);
		File frRoot = new File(param.getFrameworkRoot());
		for(String libName : libNames) {
			File in = new File(frRoot, libName);
			String name = info.destDir.replace("${libDir}", LIB_DIR_MAP.get(param.getAppType())) + "/" + in.getName();
			File out = new File(param.getProjectRootPath(), name);
			FileUtils.copyFile(in, out);
			libNameSet.add(name);
		}
	}
	
	String[] getJars(LibCopyInfo info) throws Exception {
		DirectoryScanner ds = new DirectoryScanner();
		ds.setBasedir(param.getFrameworkRoot());
		ds.setIncludes(new String[]{info.libNamePattern});
		ds.scan();
		return ds.getIncludedFiles();
	}
	
	void mkdirs() {
		File root = param.getProjectRootPath();
		boolean dummy = root.mkdirs();
		for(String dir : DIR_MAP_COMMON) {
			dummy = new File(root, dir).mkdirs();
		}
		for(String dir : DIR_MAP_FOR_TYPE.get(param.getAppType())) {
			dummy = new File(root, dir).mkdirs();
		}
		for(ApplicationOption option : param.getOptions()) {
			for(String dir : DIR_MAP_FOR_OPTION.get(option)) {
				dummy = new File(root, dir).mkdirs();
			}
		}
		//FindBugs様対応
		dummy = !dummy;
	}

	final static List<String> DIR_MAP_COMMON;
	final static Map<ApplicationType, List<String>> DIR_MAP_FOR_TYPE;
	final static Map<ApplicationOption, List<String>> DIR_MAP_FOR_OPTION;
	static {
		Map<ApplicationType, List<String>> tmpForType = 
			new HashMap<ApplicationType, List<String>>();
		DIR_MAP_COMMON = Collections.unmodifiableList(new ArrayList<String>(
				Arrays.asList("src/java", "src/test", "etc/otherlib", "conf/template", "src/resource")));
		tmpForType.put(ApplicationType.SIMPLE, 
				Collections.unmodifiableList(new ArrayList<String>(Arrays.asList("lib"))));
		tmpForType.put(ApplicationType.WEBAPP, 
				Collections.unmodifiableList(new ArrayList<String>(Arrays.asList(
						"webapp/WEB-INF", "webapp/WEB-INF/lib"))));
		tmpForType.put(ApplicationType.WEBAPP_WITH_TOMCAT_PI, tmpForType.get(ApplicationType.WEBAPP)); 
		DIR_MAP_FOR_TYPE = Collections.unmodifiableMap(tmpForType);
		Map<ApplicationOption, List<String>> tmpForOption= 
			new HashMap<ApplicationOption, List<String>>();
		tmpForOption.put(ApplicationOption.USING_MAKE_BEAN, 
				Collections.unmodifiableList(new ArrayList<String>(Arrays.asList(
						"etc/beanDef"))));
		tmpForOption.put(ApplicationOption.USING_DATABASE, 
				Collections.unmodifiableList(new ArrayList<String>(Arrays.asList(
						"etc/schema"))));
		tmpForOption.put(ApplicationOption.USING_ESB, 
				Collections.unmodifiableList(new ArrayList<String>(Arrays.asList(
						"etc/wsdl", "etc/serviceDef", "src/java/mule/org"))));
		tmpForOption.put(ApplicationOption.USING_RDF_WEB_FR, 
				Collections.unmodifiableList(new ArrayList<String>(Arrays.asList(
						"webapp/WEB-INF/velocity"))));
		DIR_MAP_FOR_OPTION = Collections.unmodifiableMap(tmpForOption);
	}
	
	final static Set<FileGeneratorInfo> FILE_SET_FOR_COMMON;
	final static Map<ApplicationType, Set<FileGeneratorInfo>> FILE_MAP_FOR_TYPE;
	final static Map<ApplicationOption, Set<FileGeneratorInfo>> FILE_MAP_FOR_OPTION;
	static {
		Set<FileGeneratorInfo> tmpSet = new HashSet<FileGeneratorInfo>(Arrays.asList(
			new FileGeneratorInfo("conf/template/beans-domain.xml", 	"makeBeansDomainXml"),
			new FileGeneratorInfo("conf/template/beans.xml", 			"makeBeansXml"),
			new FileGeneratorInfo("build.xml", 							"makeBuildScript"),
			new FileGeneratorInfo("conf/filtering.default.properties", 	"makeDefaultPropertiesFile"),
			new FileGeneratorInfo("conf/template/log4j.properties", 	"makeLog4jProperties")));
		FILE_SET_FOR_COMMON = Collections.unmodifiableSet(tmpSet);
		Map<ApplicationType, Set<FileGeneratorInfo>> tmpMapForType = new HashMap<ApplicationType, Set<FileGeneratorInfo>>();
		tmpMapForType.put(ApplicationType.SIMPLE, Collections.unmodifiableSet(new HashSet<FileGeneratorInfo>()));
		tmpMapForType.put(ApplicationType.WEBAPP, Collections.unmodifiableSet(new HashSet<FileGeneratorInfo>(Arrays.asList(
				new FileGeneratorInfo("webapp/WEB-INF/web.xml", "makeWebXml")
		))));
		tmpMapForType.put(ApplicationType.WEBAPP_WITH_TOMCAT_PI, tmpMapForType.get(ApplicationType.WEBAPP));
		FILE_MAP_FOR_TYPE = Collections.unmodifiableMap(tmpMapForType);
		Map<ApplicationOption, Set<FileGeneratorInfo>> tmpMapForOption = new HashMap<ApplicationOption, Set<FileGeneratorInfo>>();
		tmpMapForOption.put(ApplicationOption.USING_DATABASE, Collections.unmodifiableSet(new HashSet<FileGeneratorInfo>(Arrays.asList(
				new FileGeneratorInfo("src/resource/messageResource.properties", 	"makeMessageResourceProperties"),
				new FileGeneratorInfo("etc/schema/${projectName}-schema.xml", 		"makeSchemaXml")
		))));
		tmpMapForOption.put(ApplicationOption.USING_ESB, Collections.unmodifiableSet(new HashSet<FileGeneratorInfo>(Arrays.asList(
				new FileGeneratorInfo("conf/template/cxfrouting.properties", 		"makeCXFRoutingProperties"),
				new FileGeneratorInfo("src/java/mule/mule-common-config.xml",		"makeMuleCommonConfig"),
				new FileGeneratorInfo("src/java/mule/mule-server-config.xml",		"makeMuleConfig"),
				new FileGeneratorInfo("src/java/mule/mule-client-config.xml",		"makeMuleConfig"),
				new FileGeneratorInfo("src/java/mule/org/mule-server-config.xml",	"makeMuleConfig"),
				new FileGeneratorInfo("src/java/mule/org/mule-client-config.xml",	"makeMuleConfig"),
				new FileGeneratorInfo("src/java/mule/org/README",					"makeMuleConfigReadme"),
				new FileGeneratorInfo("etc/serviceDef/services.xml",				"makeServicesXml")
		))));
		tmpMapForOption.put(ApplicationOption.USING_MAKE_BEAN, Collections.unmodifiableSet(new HashSet<FileGeneratorInfo>(Arrays.asList(
				new FileGeneratorInfo("etc/beanDef/beanDef.xml", 					"makeBeanDefXml")
		))));
		tmpMapForOption.put(ApplicationOption.USING_RDF_WEB_FR, Collections.unmodifiableSet(new HashSet<FileGeneratorInfo>(Arrays.asList(
				new FileGeneratorInfo("webapp/WEB-INF/web.xml", 				"makeWebXml"),
				new FileGeneratorInfo("webapp/WEB-INF/velocity/errorList.vm",	"makeErrorListVM"),
				new FileGeneratorInfo("webapp/WEB-INF/struts-config.xml",		"makeStrutsConfigXml"),
				new FileGeneratorInfo("webapp/WEB-INF/toolbox.xml",				"makeToolBoxXml"),
				new FileGeneratorInfo("webapp/WEB-INF/tiles-defs.xml", 			"makeTilesDefsXml")
		))));
		FILE_MAP_FOR_OPTION = Collections.unmodifiableMap(tmpMapForOption);
	}

	final static List<LibCopyInfo> LIB_LIST_FOR_COMMON;
	final static Map<ApplicationType, List<LibCopyInfo>> LIB_MAP_FOR_TYPE;
	static Map<ApplicationOption, List<LibCopyInfo>> LIB_MAP_FOR_OPTION;
	static {
		LIB_LIST_FOR_COMMON = Collections.unmodifiableList(Arrays.asList(
				new LibCopyInfo("${libDir}", "libs/antlr/*.jar"),
				new LibCopyInfo("${libDir}", "libs/aopalliance/*.jar"),
				new LibCopyInfo("${libDir}", "libs/asm/*.jar"),
				new LibCopyInfo("${libDir}", "libs/cglib/*.jar"),
				new LibCopyInfo("${libDir}", "libs/common/commons-*.jar"),
				new LibCopyInfo("${libDir}", "libs/common/jakarta-*.jar"),
				new LibCopyInfo("${libDir}", "libs/hudson/*.jar"),
				new LibCopyInfo("${libDir}", "libs/log4j/*.jar"),
				new LibCopyInfo("${libDir}", "libs/spring/*.jar"),
				new LibCopyInfo("${libDir}", "target/rd-framework.jar")
		));
		Map<ApplicationType, List<LibCopyInfo>> mapForType = new HashMap<ApplicationType, List<LibCopyInfo>>();
		mapForType.put(ApplicationType.SIMPLE, Collections.unmodifiableList(new ArrayList<LibCopyInfo>()));
		mapForType.put(ApplicationType.WEBAPP, Collections.unmodifiableList(Arrays.asList(
				new LibCopyInfo("etc/otherlib", "lib/servletapi/*.jar")
		)));
		mapForType.put(ApplicationType.WEBAPP_WITH_TOMCAT_PI, Collections.unmodifiableList(new ArrayList<LibCopyInfo>()));
		LIB_MAP_FOR_TYPE = Collections.unmodifiableMap(mapForType);
		Map<ApplicationOption, List<LibCopyInfo>> mapForOption = new HashMap<ApplicationOption, List<LibCopyInfo>>();
		mapForOption.put(ApplicationOption.USING_DATABASE, Collections.unmodifiableList(Arrays.asList(
				new LibCopyInfo("${libDir}", "libs/hibernate/*.jar"),
				new LibCopyInfo("etc/otherlib", "libs/dbUnit/*.jar"),
				new LibCopyInfo("etc/otherlib", "libs/POI/*.jar"),
				new LibCopyInfo("etc/otherlib", "libs/H2/*.jar")
		)));
		mapForOption.put(ApplicationOption.USING_ESB, Collections.unmodifiableList(Arrays.asList(
				new LibCopyInfo("${libDir}", "libs/cxf/*.jar"),
				new LibCopyInfo("${libDir}", "libs/geronimo/*.jar"),
				new LibCopyInfo("${libDir}", "libs/httpclient/*.jar"),
				new LibCopyInfo("${libDir}", "libs/javamail/*.jar"),
				new LibCopyInfo("${libDir}", "libs/jaxen/*.jar"),
				new LibCopyInfo("${libDir}", "libs/jdom/*.jar"),
				new LibCopyInfo("${libDir}", "libs/mule/*.jar"),
				new LibCopyInfo("${libDir}", "libs/wsdl2java/*.jar"),
				new LibCopyInfo("etc/otherlib", "lib/servletapi/*.jar")
		)));
		mapForOption.put(ApplicationOption.USING_MAKE_BEAN, Collections.unmodifiableList(new ArrayList<LibCopyInfo>()));
		mapForOption.put(ApplicationOption.USING_RDF_WEB_FR, Collections.unmodifiableList(Arrays.asList(
				new LibCopyInfo("${libDir}", "libs/struts/*.jar"),
				new LibCopyInfo("${libDir}", "libs/velocity/*.jar")
		)));
		LIB_MAP_FOR_OPTION = Collections.unmodifiableMap(mapForOption);
	}
	
	final static Map<ApplicationType, String> LIB_DIR_MAP;
	static {
		Map<ApplicationType, String> map = new HashMap<ApplicationType, String>();
		map.put(ApplicationType.SIMPLE, 				"lib");
		map.put(ApplicationType.WEBAPP, 				"webapp/WEB-INF/lib");
		map.put(ApplicationType.WEBAPP_WITH_TOMCAT_PI, 	"webapp/WEB-INF/lib");
		LIB_DIR_MAP = Collections.unmodifiableMap(map);
	}
	
	static class FileGeneratorInfo {
		final String name;
		final String makerMethodName;
		FileGeneratorInfo(String name, String makerMethodName) {
			this.name = name;
			this.makerMethodName = makerMethodName;
		}
		@Override
		public boolean equals(Object o) {
			if(o instanceof FileGeneratorInfo) {
				return name.equals(((FileGeneratorInfo)o).name);
			} else {
				return false;
			}
		}
		@Override
		public int hashCode() {
			return name.hashCode();
		}
	}
	
	static class LibCopyInfo {
		final String destDir;
		final String libNamePattern;
		LibCopyInfo(String destDir, String libNamePattern) {
			this.destDir = destDir;
			this.libNamePattern = libNamePattern;
		}
	}
}
