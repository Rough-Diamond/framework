/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.tools.projectgen;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import jp.rough_diamond.commons.io.IOUtils;
import jp.rough_diamond.tools.projectgen.ProjectBuilder.FileGeneratorInfo;
import jp.rough_diamond.tools.projectgen.ProjectBuilder.LibCopyInfo;
import jp.rough_diamond.tools.projectgen.ProjectGeneratorParameter.ApplicationOption;
import junit.framework.TestCase;

public class ProjectBuilderTest extends TestCase {
	public void testMakeDirsSimpleProject() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.SIMPLE);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_ESB,
					ApplicationOption.USING_DATABASE);
			param.setProjectRoot(projectRoot);
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			assertTrue("ルートディレクトリが存在しません。", rootDir.exists());
			assertTrue("ソースディレクトリが存在しません。", new File(rootDir, "src/java").isDirectory());
			assertTrue("テストコードディレクトリが存在しません。", new File(rootDir, "src/test").isDirectory());
			assertTrue("作業用ライブラリ格納ディレクトリが存在しません。", new File(rootDir, "etc/otherlib").isDirectory());
			assertTrue("環境定義ファイル格納ディレクトリが存在しません。", new File(rootDir, "conf/template").isDirectory());
			assertTrue("jar格納フォルダが登録されていません。", new File(rootDir, "lib").isDirectory());
			assertTrue("makeBean定義ファイル格納フォルダが登録されていません。", new File(rootDir, "etc/beanDef").isDirectory());
			assertTrue("スキーマ定義ファイル格納フォルダが登録されていません。", new File(rootDir, "etc/schema").isDirectory());
			assertTrue("スキーマ定義プロパティ格納フォルダが登録されていません。", new File(rootDir, "src/resource").isDirectory());
			assertTrue("エンタープライズサービス定義ファイル格納フォルダが登録されていません。", new File(rootDir, "etc/serviceDef").isDirectory());
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeDirsTomcatPIProject() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_ESB,
					ApplicationOption.USING_DATABASE);
			param.setProjectRoot(projectRoot);
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			assertTrue("ルートディレクトリが存在しません。", rootDir.exists());
			assertTrue("ソースディレクトリが存在しません。", new File(rootDir, "src/java").isDirectory());
			assertTrue("テストコードディレクトリが存在しません。", new File(rootDir, "src/test").isDirectory());
			assertTrue("作業用ライブラリ格納ディレクトリが存在しません。", new File(rootDir, "etc/otherlib").isDirectory());
			assertTrue("環境定義ファイル格納ディレクトリが存在しません。", new File(rootDir, "conf/template").isDirectory());
			assertTrue("makeBean定義ファイル格納フォルダが登録されていません。", new File(rootDir, "etc/beanDef").isDirectory());
			assertTrue("WEB-INFフォルダが登録されていません。", new File(rootDir, "webapp/WEB-INF").isDirectory());
			assertTrue("jar格納フォルダが登録されていません。", new File(rootDir, "webapp/WEB-INF/lib").isDirectory());
			assertTrue("スキーマ定義ファイル格納フォルダが登録されていません。", new File(rootDir, "etc/schema").isDirectory());
			assertTrue("スキーマ定義ファイル格納フォルダが登録されていません。", new File(rootDir, "etc/schema").isDirectory());
			assertTrue("スキーマ定義プロパティ格納フォルダが登録されていません。", new File(rootDir, "src/resource").isDirectory());
			assertTrue("エンタープライズサービス定義ファイル格納フォルダが登録されていません。", new File(rootDir, "etc/serviceDef").isDirectory());
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeBuildScriptTypeIsTomcatAndOptionAll() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_ESB,
					ApplicationOption.USING_DATABASE);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String xml = pb.makeBuildScript();
			System.out.println(xml);
	        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document doc = db.parse(new InputSource(new StringReader(xml)));
	        assertEquals("文字コードが誤っています。", "MS932", doc.getXmlEncoding());
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			XPathExpression exp = xpath.compile("/project/property[@name='src.encoding']/@value");
			assertEquals("文字コードが誤っています。", "MS932", exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/project/property[@name='lib.dir']/@value");
			assertEquals("ライブラリ格納ディレクトリが誤っています。", "./webapp/WEB-INF/lib", exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/project/import/@file");
			assertEquals("フレームワークルートディレクトリが誤っています。", "../framework/build-common.xml", exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/project/property[@name='hibernate.fileset.dirs']/@value");
			assertEquals("XDoclet生成対象ソースフォルダの指定が誤っています。", "${src.dir};../framework/src/java", exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/project/property[@name='do.not.use.makeSchemaAccessor']");
			Node node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("makeSchemaAccessorスキップフラグが存在しています。", node);
			exp = xpath.compile("/project/property[@name='bean.def.xml']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("beanDef定義ファイルプロパティが存在しません。", node);
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeBuildScriptTypeIsSimpleWithDB() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.SIMPLE);
			param.setOptions(
					ApplicationOption.USING_ESB);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String xml = pb.makeBuildScript();
			System.out.println(xml);
	        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document doc = db.parse(new InputSource(new StringReader(xml)));
	        assertEquals("文字コードが誤っています。", "MS932", doc.getXmlEncoding());
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			XPathExpression exp = xpath.compile("/project/property[@name='src.encoding']/@value");
			assertEquals("文字コードが誤っています。", "MS932", exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/project/property[@name='lib.dir']/@value");
			assertEquals("ライブラリ格納ディレクトリが誤っています。", "./lib", exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/project/import/@file");
			assertEquals("フレームワークルートディレクトリが誤っています。", "../framework/build-common.xml", exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/project/property[@name='hibernate.fileset.dirs']");
			Node node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("XDoclet生成対象ソースフォルダの指定がされています。", node);
			exp = xpath.compile("/project/property[@name='do.not.use.makeSchemaAccessor']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("makeSchemaAccessorスキップフラグが存在しています。", node);
			exp = xpath.compile("/project/property[@name='bean.def.xml']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("beanDef定義ファイルプロパティが存在します。", node);
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeStrutsConfigXml() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_ESB,
					ApplicationOption.USING_DATABASE,
					ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String xml = pb.makeStrutsConfigXml();
			System.out.println(xml);
	        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document doc = db.parse(new InputSource(new StringReader(xml)));
	        assertEquals("文字コードが誤っています。", "MS932", doc.getXmlEncoding());
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeToolBoxXml() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_ESB,
					ApplicationOption.USING_DATABASE,
					ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String xml = pb.makeToolBoxXml();
			System.out.println(xml);
			//ロジックもないので出力確認するのみ
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeCXFRoutingProperties() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_ESB,
					ApplicationOption.USING_DATABASE,
					ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String xml = pb.makeCXFRoutingProperties();
			System.out.println(xml);
			//ロジックもないので出力確認するのみ
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeLog4jProperties() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_ESB,
					ApplicationOption.USING_DATABASE,
					ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String xml = pb.makeLog4jProperties();
			System.out.println(xml);
			//ロジックもないので出力確認するのみ
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeMuleConfigReadMe() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_ESB,
					ApplicationOption.USING_DATABASE,
					ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String xml = pb.makeMuleConfigReadme();
			System.out.println(xml);
			//ロジックもないので出力確認するのみ
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeMuleCommonConfigXml() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_ESB,
					ApplicationOption.USING_DATABASE,
					ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String xml = pb.makeMuleCommonConfig();
			System.out.println(xml);
	        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document doc = db.parse(new InputSource(new StringReader(xml)));
	        assertEquals("文字コードが誤っています。", "MS932", doc.getXmlEncoding());
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeMuleConfigXml() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_ESB,
					ApplicationOption.USING_DATABASE,
					ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String xml = pb.makeMuleConfig();
			System.out.println(xml);
	        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document doc = db.parse(new InputSource(new StringReader(xml)));
	        assertEquals("文字コードが誤っています。", "MS932", doc.getXmlEncoding());
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeMessageResourceProperties() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_ESB,
					ApplicationOption.USING_DATABASE,
					ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String xml = pb.makeMessageResourceProperties();
			System.out.println(xml);
			//ロジックもないので出力確認するのみ
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeErrorListVM() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_ESB,
					ApplicationOption.USING_DATABASE,
					ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String xml = pb.makeErrorListVM();
			System.out.println(xml);
			//ロジックもないので出力確認するのみ
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeSchemaXml() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_ESB,
					ApplicationOption.USING_DATABASE,
					ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String xml = pb.makeSchemaXml();
			System.out.println(xml);
	        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document doc = db.parse(new InputSource(new StringReader(xml)));
	        assertEquals("文字コードが誤っています。", "MS932", doc.getXmlEncoding());
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeBeansDomainXmlIsTomcatAndOptionAll() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_ESB,
					ApplicationOption.USING_DATABASE,
					ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String xml = pb.makeBeansDomainXml();
			System.out.println(xml);
	        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document doc = db.parse(new InputSource(new StringReader(xml)));
	        assertEquals("文字コードが誤っています。", "MS932", doc.getXmlEncoding());
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			xpath.setNamespaceContext(new NameSpaceContextImpl());
			XPathExpression exp = xpath.compile("/beans/bean[@id='databaseCharset']");
			Node node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("データベースの文字コードの指定がありません。", node);
			exp = xpath.compile("/beans/bean[@id='velocityContext']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("VelocityContextの指定がありません。", node);
			exp = xpath.compile("/beans/bean[@id='velocityProperties']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("velocityPropertiesの指定がありません。", node);
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeBeansDomainXmlIsTomcatAndRDFWebFramework() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String xml = pb.makeBeansDomainXml();
			System.out.println(xml);
	        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document doc = db.parse(new InputSource(new StringReader(xml)));
	        assertEquals("文字コードが誤っています。", "MS932", doc.getXmlEncoding());
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			xpath.setNamespaceContext(new NameSpaceContextImpl());
			XPathExpression exp = xpath.compile("/beans/bean[@id='databaseCharset']");
			Node node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("データベースの文字コードの指定があります。", node);
			exp = xpath.compile("/beans/bean[@id='velocityContext']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("VelocityContextの指定がありません。", node);
			exp = xpath.compile("/beans/bean[@id='velocityProperties']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("velocityPropertiesの指定がありません。", node);
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeBeansDomainXmlIsTomcatAndDatabase() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(ApplicationOption.USING_DATABASE);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("UTF-8");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String xml = pb.makeBeansDomainXml();
			System.out.println(xml);
	        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document doc = db.parse(new InputSource(new StringReader(xml)));
	        assertEquals("文字コードが誤っています。", "UTF-8", doc.getXmlEncoding());
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			xpath.setNamespaceContext(new NameSpaceContextImpl());
			XPathExpression exp = xpath.compile("/beans/bean[@id='databaseCharset']");
			Node node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("データベースの文字コードの指定があります。", node);
			exp = xpath.compile("/beans/bean[@id='velocityContext']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("VelocityContextの指定があります。", node);
			exp = xpath.compile("/beans/bean[@id='velocityProperties']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("velocityPropertiesの指定があります。", node);
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeBeansDomainXmlIsTomcatAndNoOption() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions();
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("UTF-8");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String xml = pb.makeBeansDomainXml();
			System.out.println(xml);
	        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document doc = db.parse(new InputSource(new StringReader(xml)));
	        assertEquals("文字コードが誤っています。", "UTF-8", doc.getXmlEncoding());
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			xpath.setNamespaceContext(new NameSpaceContextImpl());
			XPathExpression exp = xpath.compile("/beans/bean[@id='databaseCharset']");
			Node node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("データベースの文字コードの指定があります。", node);
			exp = xpath.compile("/beans/bean[@id='velocityContext']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("VelocityContextの指定があります。", node);
			exp = xpath.compile("/beans/bean[@id='velocityProperties']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("velocityPropertiesの指定があります。", node);
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeBeansXmlIsTomcatAndOptionAll() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_ESB,
					ApplicationOption.USING_DATABASE,
					ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String xml = pb.makeBeansXml();
			System.out.println(xml);
	        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document doc = db.parse(new InputSource(new StringReader(xml)));
	        assertEquals("文字コードが誤っています。", "MS932", doc.getXmlEncoding());
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			xpath.setNamespaceContext(new NameSpaceContextImpl());
			XPathExpression exp = xpath.compile("/beans/bean[@id='connectionManager']");
			Node node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("コネクションマネージャの指定がありません。", node);
			exp = xpath.compile("/beans/bean[@id='serviceBus']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("サービスバスの指定がありません。", node);
			exp = xpath.compile("/beans/bean[@id='serviceFinder']/constructor-arg/list/bean");
			assertEquals("ServiceFinderの指定数が誤っています。", 3, 
					((NodeList)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODESET)).getLength());
			exp = xpath.compile("/beans/bean[@id='serviceFinder']/constructor-arg/list/bean[1]/@class");
			assertEquals("ServiceFinderのクラス名が誤っています。", "jp.rough_diamond.framework.es.ServiceFinder", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='serviceFinder']/constructor-arg/list/bean[2]/@class");
			assertEquals("ServiceFinderのクラス名が誤っています。", "jp.rough_diamond.framework.transaction.ServiceFinder", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='serviceFinder']/constructor-arg/list/bean[3]/@class");
			assertEquals("ServiceFinderのクラス名が誤っています。", "jp.rough_diamond.framework.service.SimpleServiceFinder", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='transactionInterceptor']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("TransactionInterceptorの指定がありません。", node);
			exp = xpath.compile("/beans/bean[@id='localeController']/@class");
			assertEquals("localeControllerのクラス名が誤っています。", "jp.rough_diamond.commons.resource.LocaleControllerByThreadLocal", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='resourceName']/constructor-arg/value");
			assertFalse("resourceNameにスキーマリソースが含まれていません。",  
					((String)exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING)).indexOf("schemaResources") == -1);
			exp = xpath.compile("/beans/bean[@id='userController']/@class");
			assertEquals("userControllerのクラス名が誤っています。", "jp.rough_diamond.framework.user.UserControllerByThreadLocal", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='velocityWrapper']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("Velocityの指定がありません。", node);
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeBeansXmlIsTomcatAndNoOption() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions();
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("UTF-8");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String xml = pb.makeBeansXml();
			System.out.println(xml);
	        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document doc = db.parse(new InputSource(new StringReader(xml)));
	        assertEquals("文字コードが誤っています。", "UTF-8", doc.getXmlEncoding());
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			xpath.setNamespaceContext(new NameSpaceContextImpl());
			XPathExpression exp = xpath.compile("/beans/bean[@id='connectionManager']");
			Node node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("コネクションマネージャが指定されています。", node);
			exp = xpath.compile("/beans/bean[@id='serviceBus']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("サービスバスの指定があります。", node);
			exp = xpath.compile("/beans/bean[@id='serviceFinder']/constructor-arg/list/bean");
			assertEquals("ServiceFinderの指定数が誤っています。", 1, 
					((NodeList)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODESET)).getLength());
			exp = xpath.compile("/beans/bean[@id='serviceFinder']/constructor-arg/list/bean[1]/@class");
			assertEquals("ServiceFinderのクラス名が誤っています。", "jp.rough_diamond.framework.service.SimpleServiceFinder", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='transactionInterceptor']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("TransactionInterceptorの指定があります。", node);
			exp = xpath.compile("/beans/bean[@id='localeController']/@class");
			assertEquals("localeControllerのクラス名が誤っています。", "jp.rough_diamond.commons.resource.SimpleLocaleController", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='resourceName']/constructor-arg/value");
			assertTrue("resourceNameにスキーマリソースが含まれています。",  
					((String)exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING)).indexOf("schemaResources") == -1);
			exp = xpath.compile("/beans/bean[@id='userController']/@class");
			assertEquals("userControllerのクラス名が誤っています。", "jp.rough_diamond.framework.user.SimpleUserController", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='velocityWrapper']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("Velocityの指定がありませす。", node);
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeBeansXmlIsTomcatAndRDFWebFramework() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String xml = pb.makeBeansXml();
			System.out.println(xml);
	        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document doc = db.parse(new InputSource(new StringReader(xml)));
	        assertEquals("文字コードが誤っています。", "MS932", doc.getXmlEncoding());
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			xpath.setNamespaceContext(new NameSpaceContextImpl());
			XPathExpression exp = xpath.compile("/beans/bean[@id='connectionManager']");
			Node node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("コネクションマネージャが指定されています。", node);
			exp = xpath.compile("/beans/bean[@id='serviceBus']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("サービスバスの指定があります。", node);
			exp = xpath.compile("/beans/bean[@id='serviceFinder']/constructor-arg/list/bean");
			assertEquals("ServiceFinderの指定数が誤っています。", 1, 
					((NodeList)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODESET)).getLength());
			exp = xpath.compile("/beans/bean[@id='serviceFinder']/constructor-arg/list/bean[1]/@class");
			assertEquals("ServiceFinderのクラス名が誤っています。", "jp.rough_diamond.framework.service.SimpleServiceFinder", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='transactionInterceptor']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("TransactionInterceptorの指定があります。", node);
			exp = xpath.compile("/beans/bean[@id='localeController']/@class");
			assertEquals("localeControllerのクラス名が誤っています。", "jp.rough_diamond.commons.resource.LocaleControllerByThreadLocal", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='resourceName']/constructor-arg/value");
			assertTrue("resourceNameにスキーマリソースが含まれています。",  
					((String)exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING)).indexOf("schemaResources") == -1);
			exp = xpath.compile("/beans/bean[@id='userController']/@class");
			assertEquals("userControllerのクラス名が誤っています。", "jp.rough_diamond.framework.user.UserControllerByThreadLocal", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='velocityWrapper']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("Velocityの指定がありません。", node);
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeBeansXmlIsTomcatAndDatabase() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(ApplicationOption.USING_DATABASE);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String xml = pb.makeBeansXml();
			System.out.println(xml);
	        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document doc = db.parse(new InputSource(new StringReader(xml)));
	        assertEquals("文字コードが誤っています。", "MS932", doc.getXmlEncoding());
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			xpath.setNamespaceContext(new NameSpaceContextImpl());
			XPathExpression exp = xpath.compile("/beans/bean[@id='connectionManager']");
			Node node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("コネクションマネージャが指定されていません。", node);
			exp = xpath.compile("/beans/bean[@id='serviceBus']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("サービスバスの指定があります。", node);
			exp = xpath.compile("/beans/bean[@id='serviceFinder']/constructor-arg/list/bean");
			assertEquals("ServiceFinderの指定数が誤っています。", 2, 
					((NodeList)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODESET)).getLength());
			exp = xpath.compile("/beans/bean[@id='serviceFinder']/constructor-arg/list/bean[1]/@class");
			assertEquals("ServiceFinderのクラス名が誤っています。", "jp.rough_diamond.framework.transaction.ServiceFinder", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='serviceFinder']/constructor-arg/list/bean[2]/@class");
			assertEquals("ServiceFinderのクラス名が誤っています。", "jp.rough_diamond.framework.service.SimpleServiceFinder", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='transactionInterceptor']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("TransactionInterceptorの指定がありません。", node);
			exp = xpath.compile("/beans/bean[@id='localeController']/@class");
			assertEquals("localeControllerのクラス名が誤っています。", "jp.rough_diamond.commons.resource.SimpleLocaleController", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='resourceName']/constructor-arg/value");
			assertFalse("resourceNameにスキーマリソースが含まれていません。",  
					((String)exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING)).indexOf("schemaResources") == -1);
			exp = xpath.compile("/beans/bean[@id='userController']/@class");
			assertEquals("userControllerのクラス名が誤っています。", "jp.rough_diamond.framework.user.SimpleUserController", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='velocityWrapper']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("Velocityの指定があります。", node);
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeBeansXmlIsTomcatAndESB() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(ApplicationOption.USING_ESB);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("UTF-8");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String xml = pb.makeBeansXml();
			System.out.println(xml);
	        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document doc = db.parse(new InputSource(new StringReader(xml)));
	        assertEquals("文字コードが誤っています。", "UTF-8", doc.getXmlEncoding());
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			xpath.setNamespaceContext(new NameSpaceContextImpl());
			XPathExpression exp = xpath.compile("/beans/bean[@id='connectionManager']");
			Node node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("コネクションマネージャが指定されています。", node);
			exp = xpath.compile("/beans/bean[@id='serviceBus']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("サービスバスの指定がありません。", node);
			exp = xpath.compile("/beans/bean[@id='serviceFinder']/constructor-arg/list/bean");
			assertEquals("ServiceFinderの指定数が誤っています。", 2, 
					((NodeList)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODESET)).getLength());
			exp = xpath.compile("/beans/bean[@id='serviceFinder']/constructor-arg/list/bean[1]/@class");
			assertEquals("ServiceFinderのクラス名が誤っています。", "jp.rough_diamond.framework.es.ServiceFinder", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='serviceFinder']/constructor-arg/list/bean[2]/@class");
			assertEquals("ServiceFinderのクラス名が誤っています。", "jp.rough_diamond.framework.service.SimpleServiceFinder", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='transactionInterceptor']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("TransactionInterceptorの指定があります。", node);
			exp = xpath.compile("/beans/bean[@id='localeController']/@class");
			assertEquals("localeControllerのクラス名が誤っています。", "jp.rough_diamond.commons.resource.SimpleLocaleController", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='resourceName']/constructor-arg/value");
			assertTrue("resourceNameにスキーマリソースが含まれています。",  
					((String)exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING)).indexOf("schemaResources") == -1);
			exp = xpath.compile("/beans/bean[@id='userController']/@class");
			assertEquals("userControllerのクラス名が誤っています。", "jp.rough_diamond.framework.user.SimpleUserController", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='velocityWrapper']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("Velocityの指定があります。", node);
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeTilesDefsXml() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_ESB,
					ApplicationOption.USING_DATABASE,
					ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String xml = pb.makeTilesDefsXml();
			System.out.println(xml);
	        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document doc = db.parse(new InputSource(new StringReader(xml)));
	        assertEquals("文字コードが誤っています。", "MS932", doc.getXmlEncoding());
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeBeanDefXml() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_ESB,
					ApplicationOption.USING_DATABASE,
					ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String xml = pb.makeBeanDefXml();
			System.out.println(xml);
	        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document doc = db.parse(new InputSource(new StringReader(xml)));
	        assertEquals("文字コードが誤っています。", "MS932", doc.getXmlEncoding());
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeServicesXml() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_ESB,
					ApplicationOption.USING_DATABASE,
					ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String xml = pb.makeServicesXml();
			System.out.println(xml);
	        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document doc = db.parse(new InputSource(new StringReader(xml)));
	        assertEquals("文字コードが誤っています。", "MS932", doc.getXmlEncoding());
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeWebXmlIsTomcatAndOptionAll() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_ESB,
					ApplicationOption.USING_DATABASE,
					ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String xml = pb.makeWebXml();
			System.out.println(xml);
	        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document doc = db.parse(new InputSource(new StringReader(xml)));
	        assertEquals("文字コードが誤っています。", "MS932", doc.getXmlEncoding());
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			xpath.setNamespaceContext(new NameSpaceContextImpl());
			XPathExpression exp = xpath.compile("/web-app/display-name");
			assertEquals("ディスプレイ名が誤っています。", "hoge", exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/web-app/listener[1]/listener-class");
			assertEquals("ESB起動リスナが指定されていません。", "jp.rough_diamond.framework.es.MuleListener", exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeWebXmlIsTomcatAndWithoutESB() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_DATABASE,
					ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String xml = pb.makeWebXml();
			System.out.println(xml);
	        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document doc = db.parse(new InputSource(new StringReader(xml)));
	        assertEquals("文字コードが誤っています。", "MS932", doc.getXmlEncoding());
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			xpath.setNamespaceContext(new NameSpaceContextImpl());
			XPathExpression exp = xpath.compile("/web-app/display-name");
			assertEquals("ディスプレイ名が誤っています。", "hoge", exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/web-app/listener");
			Node node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("ESB起動リスナが指定されています。", node);
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeDefaultPropertiesFileIsTomcatAndOptionAll() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_ESB,
					ApplicationOption.USING_DATABASE,
					ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String props = pb.makeDefaultPropertiesFile();
			Properties prop = new Properties();
			prop.load(new ByteArrayInputStream(props.getBytes("MS932")));
			assertTrue("存在しません。", prop.containsKey("log4j.appender"));
			assertTrue("存在しません。", prop.containsKey("log4j.file.path"));
			assertTrue("存在しません。", prop.containsKey("log4j.encoding"));
			assertTrue("存在しません。", prop.containsKey("local.host"));
			assertTrue("存在しません。", prop.containsKey("local.port"));
			assertTrue("存在しません。", prop.containsKey("server.host"));
			assertTrue("存在しません。", prop.containsKey("server.port"));
			assertTrue("存在しません。", prop.containsKey("wsdl.dir"));
			assertTrue("存在しません。", prop.containsKey("torque.database.type"));
			assertTrue("存在しません。", prop.containsKey("torque.output.src.encoding"));
			assertTrue("存在しません。", prop.containsKey("torque.output.sql.encoding"));
			assertTrue("存在しません。", prop.containsKey("hibernate.dialect"));
			assertTrue("存在しません。", prop.containsKey("hibernate.jdbc.driver"));
			assertTrue("存在しません。", prop.containsKey("hibernate.jdbc.url"));
			assertTrue("存在しません。", prop.containsKey("hibernate.jdbc.user"));
			assertTrue("存在しません。", prop.containsKey("hibernate.jdbc.pass"));
			assertTrue("存在しません。", prop.containsKey("hibernate.datasource.url"));
			assertTrue("存在しません。", prop.containsKey("hibernate.show.sql"));
			assertTrue("存在しません。", prop.containsKey("hibernate.schema"));
			assertTrue("存在しません。", prop.containsKey("hibernate.connection.provider"));
			assertTrue("存在しません。", prop.containsKey("hibernate.pool.min"));
			assertTrue("存在しません。", prop.containsKey("hibernate.pool.max"));
			assertTrue("存在しません。", prop.containsKey("hibernate.max.statement"));
			assertTrue("存在しません。", prop.containsKey("hibernate.timeout"));
			assertTrue("存在しません。", prop.containsKey("velocity.input.encoding"));
			assertTrue("存在しません。", prop.containsKey("velocity.output.encoding"));
			assertTrue("存在しません。", prop.containsKey("velocity.resource.loader"));
			assertTrue("存在しません。", prop.containsKey("velocity.resource.root"));
			assertTrue("存在しません。", prop.containsKey("velocity.file.check.interval"));
			assertTrue("存在しません。", prop.containsKey("velocity.macro"));
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeDefaultPropertiesFileIsTomcatAndRDFWebFramework() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String props = pb.makeDefaultPropertiesFile();
			Properties prop = new Properties();
			prop.load(new ByteArrayInputStream(props.getBytes("MS932")));
			assertTrue("存在しません。", prop.containsKey("log4j.appender"));
			assertTrue("存在しません。", prop.containsKey("log4j.file.path"));
			assertTrue("存在しません。", prop.containsKey("log4j.encoding"));
			assertFalse("存在します。", prop.containsKey("local.host"));
			assertFalse("存在します。", prop.containsKey("local.port"));
			assertFalse("存在します。", prop.containsKey("server.host"));
			assertFalse("存在します。", prop.containsKey("server.port"));
			assertFalse("存在します。", prop.containsKey("wsdl.dir"));
			assertFalse("存在します。", prop.containsKey("torque.database.type"));
			assertFalse("存在します。", prop.containsKey("torque.output.src.encoding"));
			assertFalse("存在します。", prop.containsKey("torque.output.sql.encoding"));
			assertFalse("存在します。", prop.containsKey("hibernate.dialect"));
			assertFalse("存在します。", prop.containsKey("hibernate.jdbc.driver"));
			assertFalse("存在します。", prop.containsKey("hibernate.jdbc.url"));
			assertFalse("存在します。", prop.containsKey("hibernate.jdbc.user"));
			assertFalse("存在します。", prop.containsKey("hibernate.jdbc.pass"));
			assertFalse("存在します。", prop.containsKey("hibernate.datasource.url"));
			assertFalse("存在します。", prop.containsKey("hibernate.show.sql"));
			assertFalse("存在します。", prop.containsKey("hibernate.schema"));
			assertFalse("存在します。", prop.containsKey("hibernate.connection.provider"));
			assertFalse("存在します。", prop.containsKey("hibernate.pool.min"));
			assertFalse("存在します。", prop.containsKey("hibernate.pool.max"));
			assertFalse("存在します。", prop.containsKey("hibernate.max.statement"));
			assertFalse("存在します。", prop.containsKey("hibernate.timeout"));
			assertTrue("存在しません。", prop.containsKey("velocity.input.encoding"));
			assertTrue("存在しません。", prop.containsKey("velocity.output.encoding"));
			assertTrue("存在しません。", prop.containsKey("velocity.resource.loader"));
			assertTrue("存在しません。", prop.containsKey("velocity.resource.root"));
			assertTrue("存在しません。", prop.containsKey("velocity.file.check.interval"));
			assertTrue("存在しません。", prop.containsKey("velocity.macro"));
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeDefaultPropertiesFileIsTomcatAndDatabase() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_DATABASE);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String props = pb.makeDefaultPropertiesFile();
			Properties prop = new Properties();
			prop.load(new ByteArrayInputStream(props.getBytes("MS932")));
			assertTrue("存在しません。", prop.containsKey("log4j.appender"));
			assertTrue("存在しません。", prop.containsKey("log4j.file.path"));
			assertTrue("存在しません。", prop.containsKey("log4j.encoding"));
			assertFalse("存在します。", prop.containsKey("local.host"));
			assertFalse("存在します。", prop.containsKey("local.port"));
			assertFalse("存在します。", prop.containsKey("server.host"));
			assertFalse("存在します。", prop.containsKey("server.port"));
			assertFalse("存在します。", prop.containsKey("wsdl.dir"));
			assertTrue("存在しません。", prop.containsKey("torque.database.type"));
			assertTrue("存在しません。", prop.containsKey("torque.output.src.encoding"));
			assertTrue("存在しません。", prop.containsKey("torque.output.sql.encoding"));
			assertTrue("存在しません。", prop.containsKey("hibernate.dialect"));
			assertTrue("存在しません。", prop.containsKey("hibernate.jdbc.driver"));
			assertTrue("存在しません。", prop.containsKey("hibernate.jdbc.url"));
			assertTrue("存在しません。", prop.containsKey("hibernate.jdbc.user"));
			assertTrue("存在しません。", prop.containsKey("hibernate.jdbc.pass"));
			assertTrue("存在しません。", prop.containsKey("hibernate.datasource.url"));
			assertTrue("存在しません。", prop.containsKey("hibernate.show.sql"));
			assertTrue("存在しません。", prop.containsKey("hibernate.schema"));
			assertTrue("存在しません。", prop.containsKey("hibernate.connection.provider"));
			assertTrue("存在しません。", prop.containsKey("hibernate.pool.min"));
			assertTrue("存在しません。", prop.containsKey("hibernate.pool.max"));
			assertTrue("存在しません。", prop.containsKey("hibernate.max.statement"));
			assertTrue("存在しません。", prop.containsKey("hibernate.timeout"));
			assertFalse("存在します。", prop.containsKey("velocity.input.encoding"));
			assertFalse("存在します。", prop.containsKey("velocity.output.encoding"));
			assertFalse("存在します。", prop.containsKey("velocity.resource.loader"));
			assertFalse("存在します。", prop.containsKey("velocity.resource.root"));
			assertFalse("存在します。", prop.containsKey("velocity.file.check.interval"));
			assertFalse("存在します。", prop.containsKey("velocity.macro"));
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeDefaultPropertiesFileIsTomcatAndESB() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_ESB);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String props = pb.makeDefaultPropertiesFile();
			Properties prop = new Properties();
			prop.load(new ByteArrayInputStream(props.getBytes("MS932")));
			assertTrue("存在しません。", prop.containsKey("log4j.appender"));
			assertTrue("存在しません。", prop.containsKey("log4j.file.path"));
			assertTrue("存在しません。", prop.containsKey("log4j.encoding"));
			assertTrue("存在しません。", prop.containsKey("local.host"));
			assertTrue("存在しません。", prop.containsKey("local.port"));
			assertTrue("存在しません。", prop.containsKey("server.host"));
			assertTrue("存在しません。", prop.containsKey("server.port"));
			assertTrue("存在しません。", prop.containsKey("wsdl.dir"));
			assertFalse("存在します。", prop.containsKey("torque.database.type"));
			assertFalse("存在します。", prop.containsKey("torque.output.src.encoding"));
			assertFalse("存在します。", prop.containsKey("torque.output.sql.encoding"));
			assertFalse("存在します。", prop.containsKey("hibernate.dialect"));
			assertFalse("存在します。", prop.containsKey("hibernate.jdbc.driver"));
			assertFalse("存在します。", prop.containsKey("hibernate.jdbc.url"));
			assertFalse("存在します。", prop.containsKey("hibernate.jdbc.user"));
			assertFalse("存在します。", prop.containsKey("hibernate.jdbc.pass"));
			assertFalse("存在します。", prop.containsKey("hibernate.datasource.url"));
			assertFalse("存在します。", prop.containsKey("hibernate.show.sql"));
			assertFalse("存在します。", prop.containsKey("hibernate.schema"));
			assertFalse("存在します。", prop.containsKey("hibernate.connection.provider"));
			assertFalse("存在します。", prop.containsKey("hibernate.pool.min"));
			assertFalse("存在します。", prop.containsKey("hibernate.pool.max"));
			assertFalse("存在します。", prop.containsKey("hibernate.max.statement"));
			assertFalse("存在します。", prop.containsKey("hibernate.timeout"));
			assertFalse("存在します。", prop.containsKey("velocity.input.encoding"));
			assertFalse("存在します。", prop.containsKey("velocity.output.encoding"));
			assertFalse("存在します。", prop.containsKey("velocity.resource.loader"));
			assertFalse("存在します。", prop.containsKey("velocity.resource.root"));
			assertFalse("存在します。", prop.containsKey("velocity.file.check.interval"));
			assertFalse("存在します。", prop.containsKey("velocity.macro"));
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeDefaultPropertiesFileIsTomcatAndMakeBean() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			String props = pb.makeDefaultPropertiesFile();
			Properties prop = new Properties();
			prop.load(new ByteArrayInputStream(props.getBytes("MS932")));
			assertTrue("存在しません。", prop.containsKey("log4j.appender"));
			assertTrue("存在しません。", prop.containsKey("log4j.file.path"));
			assertTrue("存在しません。", prop.containsKey("log4j.encoding"));
			assertFalse("存在します。", prop.containsKey("local.host"));
			assertFalse("存在します。", prop.containsKey("local.port"));
			assertFalse("存在します。", prop.containsKey("server.host"));
			assertFalse("存在します。", prop.containsKey("server.port"));
			assertFalse("存在します。", prop.containsKey("wsdl.dir"));
			assertFalse("存在します。", prop.containsKey("torque.database.type"));
			assertFalse("存在します。", prop.containsKey("torque.output.src.encoding"));
			assertFalse("存在します。", prop.containsKey("torque.output.sql.encoding"));
			assertFalse("存在します。", prop.containsKey("hibernate.dialect"));
			assertFalse("存在します。", prop.containsKey("hibernate.jdbc.driver"));
			assertFalse("存在します。", prop.containsKey("hibernate.jdbc.url"));
			assertFalse("存在します。", prop.containsKey("hibernate.jdbc.user"));
			assertFalse("存在します。", prop.containsKey("hibernate.jdbc.pass"));
			assertFalse("存在します。", prop.containsKey("hibernate.datasource.url"));
			assertFalse("存在します。", prop.containsKey("hibernate.show.sql"));
			assertFalse("存在します。", prop.containsKey("hibernate.schema"));
			assertFalse("存在します。", prop.containsKey("hibernate.connection.provider"));
			assertFalse("存在します。", prop.containsKey("hibernate.pool.min"));
			assertFalse("存在します。", prop.containsKey("hibernate.pool.max"));
			assertFalse("存在します。", prop.containsKey("hibernate.max.statement"));
			assertFalse("存在します。", prop.containsKey("hibernate.timeout"));
			assertFalse("存在します。", prop.containsKey("velocity.input.encoding"));
			assertFalse("存在します。", prop.containsKey("velocity.output.encoding"));
			assertFalse("存在します。", prop.containsKey("velocity.resource.loader"));
			assertFalse("存在します。", prop.containsKey("velocity.resource.root"));
			assertFalse("存在します。", prop.containsKey("velocity.file.check.interval"));
			assertFalse("存在します。", prop.containsKey("velocity.macro"));
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}
	
	public void testGetGenerateFilesIsTomcatAndOptionAll() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_ESB,
					ApplicationOption.USING_DATABASE,
					ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			Set<ProjectBuilder.FileGeneratorInfo> set = pb.getGenerateFiles();
			assertEquals("生成ファイル数が誤っています。", 21, set.size());
		} finally {
			IOUtils.deleteDir(rootDir);
		}
		
	}
	
	public void testGenerateFiles() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_ESB,
					ApplicationOption.USING_DATABASE,
					ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			Set<ProjectBuilder.FileGeneratorInfo> set = new HashSet<ProjectBuilder.FileGeneratorInfo>(Arrays.asList(
					new ProjectBuilder.FileGeneratorInfo("etc/schema/${projectName}-schema.xml", 		"makeSchemaXml")
			));
			pb.generateFiles(set);
			File f = new File(rootDir, "etc/schema/hoge-schema.xml");
			assertTrue("ファイルが作成されていません。", f.exists());
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}
	
	public void testGetFileName() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_ESB,
					ApplicationOption.USING_DATABASE,
					ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			ProjectBuilder.FileGeneratorInfo info = new FileGeneratorInfo("etc/schema/${projectName}-schema.xml", 		"makeSchemaXml");
			String fileName = pb.getFileName(info);
			assertEquals("ファイル名が誤っています。", "etc/schema/hoge-schema.xml", fileName);
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}

	public void testMakeText() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_ESB,
					ApplicationOption.USING_DATABASE,
					ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			Set<ProjectBuilder.FileGeneratorInfo> set = pb.getGenerateFiles();
			Set<String> setStr = new HashSet<String>();
			for(ProjectBuilder.FileGeneratorInfo info : set) {
				try {
					System.out.println(info.makerMethodName);
					setStr.add(info.makerMethodName);
					pb.makeText(info);
				} catch(Exception e) {
					e.printStackTrace();
					fail(info.makerMethodName + "の呼び出しに失敗しました");
				}
			}
			assertEquals("必要以上にメソッド名が重複しています。", 18, setStr.size());
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}
	
	public void testCopyJars() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_ESB,
					ApplicationOption.USING_DATABASE,
					ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			ProjectBuilder.LibCopyInfo info = new ProjectBuilder.LibCopyInfo("${libDir}", "libs/common/rd*.jar");
			pb.copyJars(Arrays.asList(info));
			File f = new File(rootDir, "webapp/WEB-INF/lib");
			assertEquals("ファイルがコピーされていません。", 1, f.list().length);
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}
	
	public void testGetJars() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_ESB,
					ApplicationOption.USING_DATABASE,
					ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			ProjectBuilder.LibCopyInfo info = new ProjectBuilder.LibCopyInfo("${libDir}", "libs/common/rd*.jar");
			String[] ret = pb.getJars(info);
			assertEquals("返却数が誤っています。", 1, ret.length);
			System.out.println(ret[0]);
			assertTrue("ファイル名が誤っています。", ret[0].replaceAll("\\\\", "/").startsWith("libs/common/rd-ant-ext"));
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}
	
	public void testGetLibCopyInfos() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_ESB,
					ApplicationOption.USING_DATABASE,
					ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			List<LibCopyInfo> ret = pb.getLibCopyInfos();
			assertEquals("返却数が誤っています。", 25, ret.size());
		} finally {
			IOUtils.deleteDir(rootDir);
		}
		
	}
	
	public void testGenerateClasspath() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_ESB,
					ApplicationOption.USING_DATABASE,
					ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			ProjectBuilder.LibCopyInfo info = new ProjectBuilder.LibCopyInfo("${libDir}", "libs/common/rd*.jar");
			pb.copyJars(Arrays.asList(info));
			pb.generateClasspath();
			File f = new File(rootDir, ".classpath");
			String text = FileUtils.readFileToString(f, "UTF-8");
			System.out.println(text);
			//TODO 力尽きたテスト書いてない・・・
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}
	
	public void testGenerateProjectFile() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_ESB,
					ApplicationOption.USING_DATABASE,
					ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			pb.generateProjectFile();
			File f = new File(rootDir, ".project");
			String text = FileUtils.readFileToString(f, "UTF-8");
			System.out.println(text);
			//TODO 力尽きたテスト書いてない・・・
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}
	
	public void testGenerateTomcatConfig() throws Exception {
		String projectRoot = "" + System.currentTimeMillis();
		File rootDir = new File("../" + projectRoot);
		try {
			ProjectGeneratorParameter param = new ProjectGeneratorParameter();
			param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
			param.setOptions(
					ApplicationOption.USING_MAKE_BEAN,
					ApplicationOption.USING_ESB,
					ApplicationOption.USING_DATABASE,
					ApplicationOption.USING_RDF_WEB_FR);
			param.setProjectRoot(projectRoot);
			param.setSourceEncoding("MS932");
			param.setProjectName("hoge");
			ProjectBuilder pb = new ProjectBuilder(param);
			pb.mkdirs();
			pb.generateTomcatConfig();
			File f = new File(rootDir, ".tomcatplugin");
			String text = FileUtils.readFileToString(f, "UTF-8");
			System.out.println(text);
			//TODO 力尽きたテスト書いてない・・・
		} finally {
			IOUtils.deleteDir(rootDir);
		}
	}
	
	static class NameSpaceContextImpl implements NamespaceContext {
		@Override
		public String getNamespaceURI(String prefix) {
			return "http://java.sun.com/xml/ns/j2ee";
		}

		@Override
		public String getPrefix(String namespaceURI) {
			throw new UnsupportedOperationException();
		}

		@SuppressWarnings("unchecked")
		@Override
		public Iterator getPrefixes(String namespaceURI) {
			throw new UnsupportedOperationException();
		}
	}
	
	public static void main(String[] args) throws Exception {
		String projectRoot = "hoge";
		ProjectGeneratorParameter param = new ProjectGeneratorParameter();
		param.setAppType(ProjectGeneratorParameter.ApplicationType.WEBAPP_WITH_TOMCAT_PI);
		param.setOptions(
				ApplicationOption.USING_MAKE_BEAN,
				ApplicationOption.USING_ESB,
				ApplicationOption.USING_DATABASE,
				ApplicationOption.USING_RDF_WEB_FR);
		param.setProjectRoot(projectRoot);
		param.setSourceEncoding("MS932");
		param.setProjectName("hoge");
		ProjectBuilder pb = new ProjectBuilder(param);
		pb.generate();
	}
}
