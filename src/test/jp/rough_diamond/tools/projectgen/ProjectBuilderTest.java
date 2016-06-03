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
			assertTrue("���[�g�f�B���N�g�������݂��܂���B", rootDir.exists());
			assertTrue("�\�[�X�f�B���N�g�������݂��܂���B", new File(rootDir, "src/java").isDirectory());
			assertTrue("�e�X�g�R�[�h�f�B���N�g�������݂��܂���B", new File(rootDir, "src/test").isDirectory());
			assertTrue("��Ɨp���C�u�����i�[�f�B���N�g�������݂��܂���B", new File(rootDir, "etc/otherlib").isDirectory());
			assertTrue("����`�t�@�C���i�[�f�B���N�g�������݂��܂���B", new File(rootDir, "conf/template").isDirectory());
			assertTrue("jar�i�[�t�H���_���o�^����Ă��܂���B", new File(rootDir, "lib").isDirectory());
			assertTrue("makeBean��`�t�@�C���i�[�t�H���_���o�^����Ă��܂���B", new File(rootDir, "etc/beanDef").isDirectory());
			assertTrue("�X�L�[�}��`�t�@�C���i�[�t�H���_���o�^����Ă��܂���B", new File(rootDir, "etc/schema").isDirectory());
			assertTrue("�X�L�[�}��`�v���p�e�B�i�[�t�H���_���o�^����Ă��܂���B", new File(rootDir, "src/resource").isDirectory());
			assertTrue("�G���^�[�v���C�Y�T�[�r�X��`�t�@�C���i�[�t�H���_���o�^����Ă��܂���B", new File(rootDir, "etc/serviceDef").isDirectory());
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
			assertTrue("���[�g�f�B���N�g�������݂��܂���B", rootDir.exists());
			assertTrue("�\�[�X�f�B���N�g�������݂��܂���B", new File(rootDir, "src/java").isDirectory());
			assertTrue("�e�X�g�R�[�h�f�B���N�g�������݂��܂���B", new File(rootDir, "src/test").isDirectory());
			assertTrue("��Ɨp���C�u�����i�[�f�B���N�g�������݂��܂���B", new File(rootDir, "etc/otherlib").isDirectory());
			assertTrue("����`�t�@�C���i�[�f�B���N�g�������݂��܂���B", new File(rootDir, "conf/template").isDirectory());
			assertTrue("makeBean��`�t�@�C���i�[�t�H���_���o�^����Ă��܂���B", new File(rootDir, "etc/beanDef").isDirectory());
			assertTrue("WEB-INF�t�H���_���o�^����Ă��܂���B", new File(rootDir, "webapp/WEB-INF").isDirectory());
			assertTrue("jar�i�[�t�H���_���o�^����Ă��܂���B", new File(rootDir, "webapp/WEB-INF/lib").isDirectory());
			assertTrue("�X�L�[�}��`�t�@�C���i�[�t�H���_���o�^����Ă��܂���B", new File(rootDir, "etc/schema").isDirectory());
			assertTrue("�X�L�[�}��`�t�@�C���i�[�t�H���_���o�^����Ă��܂���B", new File(rootDir, "etc/schema").isDirectory());
			assertTrue("�X�L�[�}��`�v���p�e�B�i�[�t�H���_���o�^����Ă��܂���B", new File(rootDir, "src/resource").isDirectory());
			assertTrue("�G���^�[�v���C�Y�T�[�r�X��`�t�@�C���i�[�t�H���_���o�^����Ă��܂���B", new File(rootDir, "etc/serviceDef").isDirectory());
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
	        assertEquals("�����R�[�h������Ă��܂��B", "MS932", doc.getXmlEncoding());
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			XPathExpression exp = xpath.compile("/project/property[@name='src.encoding']/@value");
			assertEquals("�����R�[�h������Ă��܂��B", "MS932", exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/project/property[@name='lib.dir']/@value");
			assertEquals("���C�u�����i�[�f�B���N�g��������Ă��܂��B", "./webapp/WEB-INF/lib", exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/project/import/@file");
			assertEquals("�t���[�����[�N���[�g�f�B���N�g��������Ă��܂��B", "../framework/build-common.xml", exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/project/property[@name='hibernate.fileset.dirs']/@value");
			assertEquals("XDoclet�����Ώۃ\�[�X�t�H���_�̎w�肪����Ă��܂��B", "${src.dir};../framework/src/java", exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/project/property[@name='do.not.use.makeSchemaAccessor']");
			Node node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("makeSchemaAccessor�X�L�b�v�t���O�����݂��Ă��܂��B", node);
			exp = xpath.compile("/project/property[@name='bean.def.xml']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("beanDef��`�t�@�C���v���p�e�B�����݂��܂���B", node);
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
	        assertEquals("�����R�[�h������Ă��܂��B", "MS932", doc.getXmlEncoding());
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			XPathExpression exp = xpath.compile("/project/property[@name='src.encoding']/@value");
			assertEquals("�����R�[�h������Ă��܂��B", "MS932", exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/project/property[@name='lib.dir']/@value");
			assertEquals("���C�u�����i�[�f�B���N�g��������Ă��܂��B", "./lib", exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/project/import/@file");
			assertEquals("�t���[�����[�N���[�g�f�B���N�g��������Ă��܂��B", "../framework/build-common.xml", exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/project/property[@name='hibernate.fileset.dirs']");
			Node node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("XDoclet�����Ώۃ\�[�X�t�H���_�̎w�肪����Ă��܂��B", node);
			exp = xpath.compile("/project/property[@name='do.not.use.makeSchemaAccessor']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("makeSchemaAccessor�X�L�b�v�t���O�����݂��Ă��܂��B", node);
			exp = xpath.compile("/project/property[@name='bean.def.xml']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("beanDef��`�t�@�C���v���p�e�B�����݂��܂��B", node);
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
	        assertEquals("�����R�[�h������Ă��܂��B", "MS932", doc.getXmlEncoding());
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
			//���W�b�N���Ȃ��̂ŏo�͊m�F����̂�
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
			//���W�b�N���Ȃ��̂ŏo�͊m�F����̂�
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
			//���W�b�N���Ȃ��̂ŏo�͊m�F����̂�
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
			//���W�b�N���Ȃ��̂ŏo�͊m�F����̂�
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
	        assertEquals("�����R�[�h������Ă��܂��B", "MS932", doc.getXmlEncoding());
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
	        assertEquals("�����R�[�h������Ă��܂��B", "MS932", doc.getXmlEncoding());
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
			//���W�b�N���Ȃ��̂ŏo�͊m�F����̂�
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
			//���W�b�N���Ȃ��̂ŏo�͊m�F����̂�
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
	        assertEquals("�����R�[�h������Ă��܂��B", "MS932", doc.getXmlEncoding());
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
	        assertEquals("�����R�[�h������Ă��܂��B", "MS932", doc.getXmlEncoding());
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			xpath.setNamespaceContext(new NameSpaceContextImpl());
			XPathExpression exp = xpath.compile("/beans/bean[@id='databaseCharset']");
			Node node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("�f�[�^�x�[�X�̕����R�[�h�̎w�肪����܂���B", node);
			exp = xpath.compile("/beans/bean[@id='velocityContext']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("VelocityContext�̎w�肪����܂���B", node);
			exp = xpath.compile("/beans/bean[@id='velocityProperties']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("velocityProperties�̎w�肪����܂���B", node);
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
	        assertEquals("�����R�[�h������Ă��܂��B", "MS932", doc.getXmlEncoding());
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			xpath.setNamespaceContext(new NameSpaceContextImpl());
			XPathExpression exp = xpath.compile("/beans/bean[@id='databaseCharset']");
			Node node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("�f�[�^�x�[�X�̕����R�[�h�̎w�肪����܂��B", node);
			exp = xpath.compile("/beans/bean[@id='velocityContext']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("VelocityContext�̎w�肪����܂���B", node);
			exp = xpath.compile("/beans/bean[@id='velocityProperties']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("velocityProperties�̎w�肪����܂���B", node);
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
	        assertEquals("�����R�[�h������Ă��܂��B", "UTF-8", doc.getXmlEncoding());
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			xpath.setNamespaceContext(new NameSpaceContextImpl());
			XPathExpression exp = xpath.compile("/beans/bean[@id='databaseCharset']");
			Node node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("�f�[�^�x�[�X�̕����R�[�h�̎w�肪����܂��B", node);
			exp = xpath.compile("/beans/bean[@id='velocityContext']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("VelocityContext�̎w�肪����܂��B", node);
			exp = xpath.compile("/beans/bean[@id='velocityProperties']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("velocityProperties�̎w�肪����܂��B", node);
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
	        assertEquals("�����R�[�h������Ă��܂��B", "UTF-8", doc.getXmlEncoding());
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			xpath.setNamespaceContext(new NameSpaceContextImpl());
			XPathExpression exp = xpath.compile("/beans/bean[@id='databaseCharset']");
			Node node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("�f�[�^�x�[�X�̕����R�[�h�̎w�肪����܂��B", node);
			exp = xpath.compile("/beans/bean[@id='velocityContext']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("VelocityContext�̎w�肪����܂��B", node);
			exp = xpath.compile("/beans/bean[@id='velocityProperties']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("velocityProperties�̎w�肪����܂��B", node);
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
	        assertEquals("�����R�[�h������Ă��܂��B", "MS932", doc.getXmlEncoding());
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			xpath.setNamespaceContext(new NameSpaceContextImpl());
			XPathExpression exp = xpath.compile("/beans/bean[@id='connectionManager']");
			Node node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("�R�l�N�V�����}�l�[�W���̎w�肪����܂���B", node);
			exp = xpath.compile("/beans/bean[@id='serviceBus']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("�T�[�r�X�o�X�̎w�肪����܂���B", node);
			exp = xpath.compile("/beans/bean[@id='serviceFinder']/constructor-arg/list/bean");
			assertEquals("ServiceFinder�̎w�萔������Ă��܂��B", 3, 
					((NodeList)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODESET)).getLength());
			exp = xpath.compile("/beans/bean[@id='serviceFinder']/constructor-arg/list/bean[1]/@class");
			assertEquals("ServiceFinder�̃N���X��������Ă��܂��B", "jp.rough_diamond.framework.es.ServiceFinder", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='serviceFinder']/constructor-arg/list/bean[2]/@class");
			assertEquals("ServiceFinder�̃N���X��������Ă��܂��B", "jp.rough_diamond.framework.transaction.ServiceFinder", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='serviceFinder']/constructor-arg/list/bean[3]/@class");
			assertEquals("ServiceFinder�̃N���X��������Ă��܂��B", "jp.rough_diamond.framework.service.SimpleServiceFinder", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='transactionInterceptor']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("TransactionInterceptor�̎w�肪����܂���B", node);
			exp = xpath.compile("/beans/bean[@id='localeController']/@class");
			assertEquals("localeController�̃N���X��������Ă��܂��B", "jp.rough_diamond.commons.resource.LocaleControllerByThreadLocal", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='resourceName']/constructor-arg/value");
			assertFalse("resourceName�ɃX�L�[�}���\�[�X���܂܂�Ă��܂���B",  
					((String)exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING)).indexOf("schemaResources") == -1);
			exp = xpath.compile("/beans/bean[@id='userController']/@class");
			assertEquals("userController�̃N���X��������Ă��܂��B", "jp.rough_diamond.framework.user.UserControllerByThreadLocal", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='velocityWrapper']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("Velocity�̎w�肪����܂���B", node);
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
	        assertEquals("�����R�[�h������Ă��܂��B", "UTF-8", doc.getXmlEncoding());
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			xpath.setNamespaceContext(new NameSpaceContextImpl());
			XPathExpression exp = xpath.compile("/beans/bean[@id='connectionManager']");
			Node node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("�R�l�N�V�����}�l�[�W�����w�肳��Ă��܂��B", node);
			exp = xpath.compile("/beans/bean[@id='serviceBus']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("�T�[�r�X�o�X�̎w�肪����܂��B", node);
			exp = xpath.compile("/beans/bean[@id='serviceFinder']/constructor-arg/list/bean");
			assertEquals("ServiceFinder�̎w�萔������Ă��܂��B", 1, 
					((NodeList)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODESET)).getLength());
			exp = xpath.compile("/beans/bean[@id='serviceFinder']/constructor-arg/list/bean[1]/@class");
			assertEquals("ServiceFinder�̃N���X��������Ă��܂��B", "jp.rough_diamond.framework.service.SimpleServiceFinder", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='transactionInterceptor']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("TransactionInterceptor�̎w�肪����܂��B", node);
			exp = xpath.compile("/beans/bean[@id='localeController']/@class");
			assertEquals("localeController�̃N���X��������Ă��܂��B", "jp.rough_diamond.commons.resource.SimpleLocaleController", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='resourceName']/constructor-arg/value");
			assertTrue("resourceName�ɃX�L�[�}���\�[�X���܂܂�Ă��܂��B",  
					((String)exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING)).indexOf("schemaResources") == -1);
			exp = xpath.compile("/beans/bean[@id='userController']/@class");
			assertEquals("userController�̃N���X��������Ă��܂��B", "jp.rough_diamond.framework.user.SimpleUserController", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='velocityWrapper']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("Velocity�̎w�肪����܂����B", node);
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
	        assertEquals("�����R�[�h������Ă��܂��B", "MS932", doc.getXmlEncoding());
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			xpath.setNamespaceContext(new NameSpaceContextImpl());
			XPathExpression exp = xpath.compile("/beans/bean[@id='connectionManager']");
			Node node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("�R�l�N�V�����}�l�[�W�����w�肳��Ă��܂��B", node);
			exp = xpath.compile("/beans/bean[@id='serviceBus']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("�T�[�r�X�o�X�̎w�肪����܂��B", node);
			exp = xpath.compile("/beans/bean[@id='serviceFinder']/constructor-arg/list/bean");
			assertEquals("ServiceFinder�̎w�萔������Ă��܂��B", 1, 
					((NodeList)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODESET)).getLength());
			exp = xpath.compile("/beans/bean[@id='serviceFinder']/constructor-arg/list/bean[1]/@class");
			assertEquals("ServiceFinder�̃N���X��������Ă��܂��B", "jp.rough_diamond.framework.service.SimpleServiceFinder", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='transactionInterceptor']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("TransactionInterceptor�̎w�肪����܂��B", node);
			exp = xpath.compile("/beans/bean[@id='localeController']/@class");
			assertEquals("localeController�̃N���X��������Ă��܂��B", "jp.rough_diamond.commons.resource.LocaleControllerByThreadLocal", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='resourceName']/constructor-arg/value");
			assertTrue("resourceName�ɃX�L�[�}���\�[�X���܂܂�Ă��܂��B",  
					((String)exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING)).indexOf("schemaResources") == -1);
			exp = xpath.compile("/beans/bean[@id='userController']/@class");
			assertEquals("userController�̃N���X��������Ă��܂��B", "jp.rough_diamond.framework.user.UserControllerByThreadLocal", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='velocityWrapper']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("Velocity�̎w�肪����܂���B", node);
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
	        assertEquals("�����R�[�h������Ă��܂��B", "MS932", doc.getXmlEncoding());
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			xpath.setNamespaceContext(new NameSpaceContextImpl());
			XPathExpression exp = xpath.compile("/beans/bean[@id='connectionManager']");
			Node node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("�R�l�N�V�����}�l�[�W�����w�肳��Ă��܂���B", node);
			exp = xpath.compile("/beans/bean[@id='serviceBus']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("�T�[�r�X�o�X�̎w�肪����܂��B", node);
			exp = xpath.compile("/beans/bean[@id='serviceFinder']/constructor-arg/list/bean");
			assertEquals("ServiceFinder�̎w�萔������Ă��܂��B", 2, 
					((NodeList)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODESET)).getLength());
			exp = xpath.compile("/beans/bean[@id='serviceFinder']/constructor-arg/list/bean[1]/@class");
			assertEquals("ServiceFinder�̃N���X��������Ă��܂��B", "jp.rough_diamond.framework.transaction.ServiceFinder", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='serviceFinder']/constructor-arg/list/bean[2]/@class");
			assertEquals("ServiceFinder�̃N���X��������Ă��܂��B", "jp.rough_diamond.framework.service.SimpleServiceFinder", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='transactionInterceptor']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("TransactionInterceptor�̎w�肪����܂���B", node);
			exp = xpath.compile("/beans/bean[@id='localeController']/@class");
			assertEquals("localeController�̃N���X��������Ă��܂��B", "jp.rough_diamond.commons.resource.SimpleLocaleController", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='resourceName']/constructor-arg/value");
			assertFalse("resourceName�ɃX�L�[�}���\�[�X���܂܂�Ă��܂���B",  
					((String)exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING)).indexOf("schemaResources") == -1);
			exp = xpath.compile("/beans/bean[@id='userController']/@class");
			assertEquals("userController�̃N���X��������Ă��܂��B", "jp.rough_diamond.framework.user.SimpleUserController", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='velocityWrapper']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("Velocity�̎w�肪����܂��B", node);
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
	        assertEquals("�����R�[�h������Ă��܂��B", "UTF-8", doc.getXmlEncoding());
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			xpath.setNamespaceContext(new NameSpaceContextImpl());
			XPathExpression exp = xpath.compile("/beans/bean[@id='connectionManager']");
			Node node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("�R�l�N�V�����}�l�[�W�����w�肳��Ă��܂��B", node);
			exp = xpath.compile("/beans/bean[@id='serviceBus']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNotNull("�T�[�r�X�o�X�̎w�肪����܂���B", node);
			exp = xpath.compile("/beans/bean[@id='serviceFinder']/constructor-arg/list/bean");
			assertEquals("ServiceFinder�̎w�萔������Ă��܂��B", 2, 
					((NodeList)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODESET)).getLength());
			exp = xpath.compile("/beans/bean[@id='serviceFinder']/constructor-arg/list/bean[1]/@class");
			assertEquals("ServiceFinder�̃N���X��������Ă��܂��B", "jp.rough_diamond.framework.es.ServiceFinder", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='serviceFinder']/constructor-arg/list/bean[2]/@class");
			assertEquals("ServiceFinder�̃N���X��������Ă��܂��B", "jp.rough_diamond.framework.service.SimpleServiceFinder", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='transactionInterceptor']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("TransactionInterceptor�̎w�肪����܂��B", node);
			exp = xpath.compile("/beans/bean[@id='localeController']/@class");
			assertEquals("localeController�̃N���X��������Ă��܂��B", "jp.rough_diamond.commons.resource.SimpleLocaleController", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='resourceName']/constructor-arg/value");
			assertTrue("resourceName�ɃX�L�[�}���\�[�X���܂܂�Ă��܂��B",  
					((String)exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING)).indexOf("schemaResources") == -1);
			exp = xpath.compile("/beans/bean[@id='userController']/@class");
			assertEquals("userController�̃N���X��������Ă��܂��B", "jp.rough_diamond.framework.user.SimpleUserController", 
					exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/beans/bean[@id='velocityWrapper']");
			node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("Velocity�̎w�肪����܂��B", node);
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
	        assertEquals("�����R�[�h������Ă��܂��B", "MS932", doc.getXmlEncoding());
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
	        assertEquals("�����R�[�h������Ă��܂��B", "MS932", doc.getXmlEncoding());
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
	        assertEquals("�����R�[�h������Ă��܂��B", "MS932", doc.getXmlEncoding());
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
	        assertEquals("�����R�[�h������Ă��܂��B", "MS932", doc.getXmlEncoding());
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			xpath.setNamespaceContext(new NameSpaceContextImpl());
			XPathExpression exp = xpath.compile("/web-app/display-name");
			assertEquals("�f�B�X�v���C��������Ă��܂��B", "hoge", exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/web-app/listener[1]/listener-class");
			assertEquals("ESB�N�����X�i���w�肳��Ă��܂���B", "jp.rough_diamond.framework.es.MuleListener", exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
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
	        assertEquals("�����R�[�h������Ă��܂��B", "MS932", doc.getXmlEncoding());
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			xpath.setNamespaceContext(new NameSpaceContextImpl());
			XPathExpression exp = xpath.compile("/web-app/display-name");
			assertEquals("�f�B�X�v���C��������Ă��܂��B", "hoge", exp.evaluate(doc.getDocumentElement(), XPathConstants.STRING));
			exp = xpath.compile("/web-app/listener");
			Node node = (Node)exp.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			assertNull("ESB�N�����X�i���w�肳��Ă��܂��B", node);
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
			assertTrue("���݂��܂���B", prop.containsKey("log4j.appender"));
			assertTrue("���݂��܂���B", prop.containsKey("log4j.file.path"));
			assertTrue("���݂��܂���B", prop.containsKey("log4j.encoding"));
			assertTrue("���݂��܂���B", prop.containsKey("local.host"));
			assertTrue("���݂��܂���B", prop.containsKey("local.port"));
			assertTrue("���݂��܂���B", prop.containsKey("server.host"));
			assertTrue("���݂��܂���B", prop.containsKey("server.port"));
			assertTrue("���݂��܂���B", prop.containsKey("wsdl.dir"));
			assertTrue("���݂��܂���B", prop.containsKey("torque.database.type"));
			assertTrue("���݂��܂���B", prop.containsKey("torque.output.src.encoding"));
			assertTrue("���݂��܂���B", prop.containsKey("torque.output.sql.encoding"));
			assertTrue("���݂��܂���B", prop.containsKey("hibernate.dialect"));
			assertTrue("���݂��܂���B", prop.containsKey("hibernate.jdbc.driver"));
			assertTrue("���݂��܂���B", prop.containsKey("hibernate.jdbc.url"));
			assertTrue("���݂��܂���B", prop.containsKey("hibernate.jdbc.user"));
			assertTrue("���݂��܂���B", prop.containsKey("hibernate.jdbc.pass"));
			assertTrue("���݂��܂���B", prop.containsKey("hibernate.datasource.url"));
			assertTrue("���݂��܂���B", prop.containsKey("hibernate.show.sql"));
			assertTrue("���݂��܂���B", prop.containsKey("hibernate.schema"));
			assertTrue("���݂��܂���B", prop.containsKey("hibernate.connection.provider"));
			assertTrue("���݂��܂���B", prop.containsKey("hibernate.pool.min"));
			assertTrue("���݂��܂���B", prop.containsKey("hibernate.pool.max"));
			assertTrue("���݂��܂���B", prop.containsKey("hibernate.max.statement"));
			assertTrue("���݂��܂���B", prop.containsKey("hibernate.timeout"));
			assertTrue("���݂��܂���B", prop.containsKey("velocity.input.encoding"));
			assertTrue("���݂��܂���B", prop.containsKey("velocity.output.encoding"));
			assertTrue("���݂��܂���B", prop.containsKey("velocity.resource.loader"));
			assertTrue("���݂��܂���B", prop.containsKey("velocity.resource.root"));
			assertTrue("���݂��܂���B", prop.containsKey("velocity.file.check.interval"));
			assertTrue("���݂��܂���B", prop.containsKey("velocity.macro"));
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
			assertTrue("���݂��܂���B", prop.containsKey("log4j.appender"));
			assertTrue("���݂��܂���B", prop.containsKey("log4j.file.path"));
			assertTrue("���݂��܂���B", prop.containsKey("log4j.encoding"));
			assertFalse("���݂��܂��B", prop.containsKey("local.host"));
			assertFalse("���݂��܂��B", prop.containsKey("local.port"));
			assertFalse("���݂��܂��B", prop.containsKey("server.host"));
			assertFalse("���݂��܂��B", prop.containsKey("server.port"));
			assertFalse("���݂��܂��B", prop.containsKey("wsdl.dir"));
			assertFalse("���݂��܂��B", prop.containsKey("torque.database.type"));
			assertFalse("���݂��܂��B", prop.containsKey("torque.output.src.encoding"));
			assertFalse("���݂��܂��B", prop.containsKey("torque.output.sql.encoding"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.dialect"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.jdbc.driver"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.jdbc.url"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.jdbc.user"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.jdbc.pass"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.datasource.url"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.show.sql"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.schema"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.connection.provider"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.pool.min"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.pool.max"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.max.statement"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.timeout"));
			assertTrue("���݂��܂���B", prop.containsKey("velocity.input.encoding"));
			assertTrue("���݂��܂���B", prop.containsKey("velocity.output.encoding"));
			assertTrue("���݂��܂���B", prop.containsKey("velocity.resource.loader"));
			assertTrue("���݂��܂���B", prop.containsKey("velocity.resource.root"));
			assertTrue("���݂��܂���B", prop.containsKey("velocity.file.check.interval"));
			assertTrue("���݂��܂���B", prop.containsKey("velocity.macro"));
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
			assertTrue("���݂��܂���B", prop.containsKey("log4j.appender"));
			assertTrue("���݂��܂���B", prop.containsKey("log4j.file.path"));
			assertTrue("���݂��܂���B", prop.containsKey("log4j.encoding"));
			assertFalse("���݂��܂��B", prop.containsKey("local.host"));
			assertFalse("���݂��܂��B", prop.containsKey("local.port"));
			assertFalse("���݂��܂��B", prop.containsKey("server.host"));
			assertFalse("���݂��܂��B", prop.containsKey("server.port"));
			assertFalse("���݂��܂��B", prop.containsKey("wsdl.dir"));
			assertTrue("���݂��܂���B", prop.containsKey("torque.database.type"));
			assertTrue("���݂��܂���B", prop.containsKey("torque.output.src.encoding"));
			assertTrue("���݂��܂���B", prop.containsKey("torque.output.sql.encoding"));
			assertTrue("���݂��܂���B", prop.containsKey("hibernate.dialect"));
			assertTrue("���݂��܂���B", prop.containsKey("hibernate.jdbc.driver"));
			assertTrue("���݂��܂���B", prop.containsKey("hibernate.jdbc.url"));
			assertTrue("���݂��܂���B", prop.containsKey("hibernate.jdbc.user"));
			assertTrue("���݂��܂���B", prop.containsKey("hibernate.jdbc.pass"));
			assertTrue("���݂��܂���B", prop.containsKey("hibernate.datasource.url"));
			assertTrue("���݂��܂���B", prop.containsKey("hibernate.show.sql"));
			assertTrue("���݂��܂���B", prop.containsKey("hibernate.schema"));
			assertTrue("���݂��܂���B", prop.containsKey("hibernate.connection.provider"));
			assertTrue("���݂��܂���B", prop.containsKey("hibernate.pool.min"));
			assertTrue("���݂��܂���B", prop.containsKey("hibernate.pool.max"));
			assertTrue("���݂��܂���B", prop.containsKey("hibernate.max.statement"));
			assertTrue("���݂��܂���B", prop.containsKey("hibernate.timeout"));
			assertFalse("���݂��܂��B", prop.containsKey("velocity.input.encoding"));
			assertFalse("���݂��܂��B", prop.containsKey("velocity.output.encoding"));
			assertFalse("���݂��܂��B", prop.containsKey("velocity.resource.loader"));
			assertFalse("���݂��܂��B", prop.containsKey("velocity.resource.root"));
			assertFalse("���݂��܂��B", prop.containsKey("velocity.file.check.interval"));
			assertFalse("���݂��܂��B", prop.containsKey("velocity.macro"));
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
			assertTrue("���݂��܂���B", prop.containsKey("log4j.appender"));
			assertTrue("���݂��܂���B", prop.containsKey("log4j.file.path"));
			assertTrue("���݂��܂���B", prop.containsKey("log4j.encoding"));
			assertTrue("���݂��܂���B", prop.containsKey("local.host"));
			assertTrue("���݂��܂���B", prop.containsKey("local.port"));
			assertTrue("���݂��܂���B", prop.containsKey("server.host"));
			assertTrue("���݂��܂���B", prop.containsKey("server.port"));
			assertTrue("���݂��܂���B", prop.containsKey("wsdl.dir"));
			assertFalse("���݂��܂��B", prop.containsKey("torque.database.type"));
			assertFalse("���݂��܂��B", prop.containsKey("torque.output.src.encoding"));
			assertFalse("���݂��܂��B", prop.containsKey("torque.output.sql.encoding"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.dialect"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.jdbc.driver"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.jdbc.url"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.jdbc.user"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.jdbc.pass"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.datasource.url"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.show.sql"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.schema"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.connection.provider"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.pool.min"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.pool.max"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.max.statement"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.timeout"));
			assertFalse("���݂��܂��B", prop.containsKey("velocity.input.encoding"));
			assertFalse("���݂��܂��B", prop.containsKey("velocity.output.encoding"));
			assertFalse("���݂��܂��B", prop.containsKey("velocity.resource.loader"));
			assertFalse("���݂��܂��B", prop.containsKey("velocity.resource.root"));
			assertFalse("���݂��܂��B", prop.containsKey("velocity.file.check.interval"));
			assertFalse("���݂��܂��B", prop.containsKey("velocity.macro"));
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
			assertTrue("���݂��܂���B", prop.containsKey("log4j.appender"));
			assertTrue("���݂��܂���B", prop.containsKey("log4j.file.path"));
			assertTrue("���݂��܂���B", prop.containsKey("log4j.encoding"));
			assertFalse("���݂��܂��B", prop.containsKey("local.host"));
			assertFalse("���݂��܂��B", prop.containsKey("local.port"));
			assertFalse("���݂��܂��B", prop.containsKey("server.host"));
			assertFalse("���݂��܂��B", prop.containsKey("server.port"));
			assertFalse("���݂��܂��B", prop.containsKey("wsdl.dir"));
			assertFalse("���݂��܂��B", prop.containsKey("torque.database.type"));
			assertFalse("���݂��܂��B", prop.containsKey("torque.output.src.encoding"));
			assertFalse("���݂��܂��B", prop.containsKey("torque.output.sql.encoding"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.dialect"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.jdbc.driver"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.jdbc.url"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.jdbc.user"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.jdbc.pass"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.datasource.url"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.show.sql"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.schema"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.connection.provider"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.pool.min"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.pool.max"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.max.statement"));
			assertFalse("���݂��܂��B", prop.containsKey("hibernate.timeout"));
			assertFalse("���݂��܂��B", prop.containsKey("velocity.input.encoding"));
			assertFalse("���݂��܂��B", prop.containsKey("velocity.output.encoding"));
			assertFalse("���݂��܂��B", prop.containsKey("velocity.resource.loader"));
			assertFalse("���݂��܂��B", prop.containsKey("velocity.resource.root"));
			assertFalse("���݂��܂��B", prop.containsKey("velocity.file.check.interval"));
			assertFalse("���݂��܂��B", prop.containsKey("velocity.macro"));
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
			assertEquals("�����t�@�C����������Ă��܂��B", 21, set.size());
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
			assertTrue("�t�@�C�����쐬����Ă��܂���B", f.exists());
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
			assertEquals("�t�@�C����������Ă��܂��B", "etc/schema/hoge-schema.xml", fileName);
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
					fail(info.makerMethodName + "�̌Ăяo���Ɏ��s���܂���");
				}
			}
			assertEquals("�K�v�ȏ�Ƀ��\�b�h�����d�����Ă��܂��B", 18, setStr.size());
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
			assertEquals("�t�@�C�����R�s�[����Ă��܂���B", 1, f.list().length);
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
			assertEquals("�ԋp��������Ă��܂��B", 1, ret.length);
			System.out.println(ret[0]);
			assertTrue("�t�@�C����������Ă��܂��B", ret[0].replaceAll("\\\\", "/").startsWith("libs/common/rd-ant-ext"));
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
			assertEquals("�ԋp��������Ă��܂��B", 25, ret.size());
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
			//TODO �͐s�����e�X�g�����ĂȂ��E�E�E
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
			//TODO �͐s�����e�X�g�����ĂȂ��E�E�E
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
			//TODO �͐s�����e�X�g�����ĂȂ��E�E�E
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
