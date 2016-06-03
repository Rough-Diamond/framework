/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.ant.taskdefs;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import junit.framework.TestCase;

public class WSDL2JavaExtTest extends TestCase {
	public void testGetSubPackageName() throws Exception {
		assertEquals("hello", WSDL2JavaExt.getSubPackageName("Hello.wsdl"));
	}
	
	public void testGetPackageName() throws Exception {
		WSDL2JavaExt task = new WSDL2JavaExt();
		File f = new File("Hello.wsdl");
		task.setRootPackage("jp.rough_diamond.edi.stub");
		assertEquals("jp.rough_diamond.edi.stub.hello", task.getPackage(f));
	}
	
	public void testGetOperations() throws Exception {
		Document wsdlDoc = getDocument(getFile("MultiOperationService.wsdl"));
		WSDL2JavaExt ext = new WSDL2JavaExt();
		ext.setVersion("2.1");
		List<String> list = ext.getOperations(wsdlDoc);
		assertEquals("返却数が誤っています。", 2, list.size());
		assertEquals("operation名が誤っています。", "foo", list.get(0));
		assertEquals("operation名が誤っています。", "bar", list.get(1));
	}
	
	public void testGetServieName() throws Exception {
		Document wsdlDoc = getDocument(getFile("MultiOperationService.wsdl"));
		WSDL2JavaExt ext = new WSDL2JavaExt();
		ext.setVersion("2.1");
		assertEquals("返却値が誤っています。", "MultiOperationServiceConnector", ext.getServiceName(wsdlDoc, "zzz", true));
		assertEquals("返却値が誤っています。", "MultiOperationServiceConnector_zzz", ext.getServiceName(wsdlDoc, "zzz", false));
	}
	
	public void testResetServiceNames() throws Exception {
		File f = getFile("mule-client-mono-config.xml");
		WSDL2JavaExt ext = new WSDL2JavaExt();
		ext.setVersion("2.1");
		Document muleConfig = getDocument(f);
		ext.doc = muleConfig;
		String xpathStr = "/mule:mule/mule:model";
		XPathExpression exp = ext.getXPathExpression(xpathStr);
		Element model = (Element)exp.evaluate(muleConfig, XPathConstants.NODE);
		ext.resetServiceNames(model);
		assertEquals("個数が誤っています。", 1, ext.serviceNames.size());
	}
	
	public void testAddClientConnectorConfig_まっさら状態からマルチオペレーション() throws Exception {
		WSDL2JavaExt ext = new WSDL2JavaExt();
		ext.setVersion("2.1");
		ext.setRootPackage("hoge");
		File wsdl = getFile("MultiOperationService.wsdl");
		Document wsdlDoc = getDocument(wsdl);
		Document muleConfig = getDocument(getFile("mule-client-null-config.xml"));
		ext.doc = muleConfig;
		ext.addClientConnectorConfig(wsdl, wsdlDoc);
		assertTrue("変更なしと言ってます。", ext.isMuleConfigEdit);
		String xpathStr = "/mule:mule/mule:model";
		XPathExpression exp = ext.getXPathExpression(xpathStr);
		NodeList nodeList = (NodeList)exp.evaluate(muleConfig, XPathConstants.NODESET);
		assertEquals("Modelの個数が誤っています。", 1, nodeList.getLength());
		Element e = (Element)nodeList.item(0);
		assertEquals("モデル名が誤っています。", "MultiOperationServiceConnectorModel", e.getAttribute("name"));
		xpathStr = "mule:service";
		exp = ext.getXPathExpression(xpathStr);
		nodeList = (NodeList)exp.evaluate(e, XPathConstants.NODESET);
		assertEquals("Serviceの個数が誤っています。", 2, nodeList.getLength());
		e = (Element)nodeList.item(0);
		assertEquals("Service名が誤っています。", "MultiOperationServiceConnector_foo", e.getAttribute("name"));
		xpathStr = "mule:inbound/vm:inbound-endpoint";
		exp = ext.getXPathExpression(xpathStr);
		Element iep = (Element)exp.evaluate(e, XPathConstants.NODE);
		assertEquals("pathが誤っています。", "MultiOperationServiceConnector_fooIn", iep.getAttribute("path"));

		xpathStr = "mule:outbound/mule:custom-outbound-router/cxf:outbound-endpoint";
		exp = ext.getXPathExpression(xpathStr);
		Element oep = (Element)exp.evaluate(e, XPathConstants.NODE);
		assertEquals("クライアントクラスが誤っています。", "hoge.multioperationservice.MultiOperationService", oep.getAttribute("clientClass"));
		assertEquals("オペレーションが誤っています。", "foo", oep.getAttribute("operation"));
		assertEquals("WSDLPortが誤っています。", "MultiOperationServicePort", oep.getAttribute("wsdlPort"));
		xpathStr = "mule:custom-transformer";
		exp = ext.getXPathExpression(xpathStr);
		Element t = (Element)exp.evaluate(oep, XPathConstants.NODE);
		assertEquals("変換クラス名が誤っています。", "hoge.multioperationservice.ObjectToJAXBElement_Foo", t.getAttribute("class"));
		assertEquals("変換名が誤っています。", "MultiOperationServiceConnector_fooInTransformer", t.getAttribute("name"));
		
		e = (Element)nodeList.item(1);
		assertEquals("Service名が誤っています。", "MultiOperationServiceConnector_bar", e.getAttribute("name"));
		xpathStr = "mule:inbound/vm:inbound-endpoint";
		exp = ext.getXPathExpression(xpathStr);
		iep = (Element)exp.evaluate(e, XPathConstants.NODE);
		assertEquals("pathが誤っています。", "MultiOperationServiceConnector_barIn", iep.getAttribute("path"));
		xpathStr = "mule:outbound/mule:custom-outbound-router/cxf:outbound-endpoint";
		exp = ext.getXPathExpression(xpathStr);
		oep = (Element)exp.evaluate(e, XPathConstants.NODE);
		assertEquals("クライアントクラスが誤っています。", "hoge.multioperationservice.MultiOperationService", oep.getAttribute("clientClass"));
		assertEquals("オペレーションが誤っています。", "bar", oep.getAttribute("operation"));
		assertEquals("WSDLPortが誤っています。", "MultiOperationServicePort", oep.getAttribute("wsdlPort"));

		xpathStr = "mule:custom-transformer";
		exp = ext.getXPathExpression(xpathStr);
		t = (Element)exp.evaluate(oep, XPathConstants.NODE);
		assertEquals("変換クラス名が誤っています。", "hoge.multioperationservice.ObjectToJAXBElement_Bar", t.getAttribute("class"));
		assertEquals("変換名が誤っています。", "MultiOperationServiceConnector_barInTransformer", t.getAttribute("name"));
	}
	
	public void testAddClientConnectorConfig_まっさら状態からモノオペレーション() throws Exception {
		WSDL2JavaExt ext = new WSDL2JavaExt();
		ext.setVersion("2.1");
		ext.setRootPackage("hoge");
		File wsdl = getFile("MonoOperationService.wsdl");
		Document wsdlDoc = getDocument(wsdl);
		Document muleConfig = getDocument(getFile("mule-client-null-config.xml"));
		ext.doc = muleConfig;
		ext.addClientConnectorConfig(wsdl, wsdlDoc);
		assertTrue("変更なしと言ってます。", ext.isMuleConfigEdit);
		String xpathStr = "/mule:mule/mule:model";
		XPathExpression exp = ext.getXPathExpression(xpathStr);
		NodeList nodeList = (NodeList)exp.evaluate(muleConfig, XPathConstants.NODESET);
		assertEquals("Modelの個数が誤っています。", 1, nodeList.getLength());
		Element e = (Element)nodeList.item(0);
		assertEquals("モデル名が誤っています。", "MonoOperationServiceConnectorModel", e.getAttribute("name"));
		xpathStr = "mule:service";
		exp = ext.getXPathExpression(xpathStr);
		nodeList = (NodeList)exp.evaluate(e, XPathConstants.NODESET);
		assertEquals("Serviceの個数が誤っています。", 1, nodeList.getLength());
		e = (Element)nodeList.item(0);
		assertEquals("Service名が誤っています。", "MonoOperationServiceConnector", e.getAttribute("name"));
		xpathStr = "mule:inbound/vm:inbound-endpoint";
		exp = ext.getXPathExpression(xpathStr);
		Element iep = (Element)exp.evaluate(e, XPathConstants.NODE);
		assertEquals("pathが誤っています。", "MonoOperationServiceConnectorIn", iep.getAttribute("path"));

		xpathStr = "mule:outbound/mule:custom-outbound-router/cxf:outbound-endpoint";
		exp = ext.getXPathExpression(xpathStr);
		Element oep = (Element)exp.evaluate(e, XPathConstants.NODE);
		assertEquals("クライアントクラスが誤っています。", "hoge.monooperationservice.MonoOperationService", oep.getAttribute("clientClass"));
		assertEquals("オペレーションが誤っています。", "foo", oep.getAttribute("operation"));
		assertEquals("WSDLPortが誤っています。", "MonoOperationServicePort", oep.getAttribute("wsdlPort"));

		xpathStr = "mule:custom-transformer";
		exp = ext.getXPathExpression(xpathStr);
		Element t = (Element)exp.evaluate(oep, XPathConstants.NODE);
		assertEquals("変換クラス名が誤っています。", "hoge.monooperationservice.ObjectToJAXBElement", t.getAttribute("class"));
		assertEquals("変換名が誤っています。", "MonoOperationServiceConnectorInTransformer", t.getAttribute("name"));
	}

	public void testAddClientConnectorConfig_モノ状態からマルチオペレーション() throws Exception {
		WSDL2JavaExt ext = new WSDL2JavaExt();
		ext.setVersion("2.1");
		ext.setRootPackage("hoge");
		File wsdl = getFile("MultiOperationService.wsdl");
		Document wsdlDoc = getDocument(wsdl);
		Document muleConfig = getDocument(getFile("mule-client-mono-config.xml"));
		ext.doc = muleConfig;
		ext.addClientConnectorConfig(wsdl, wsdlDoc);
		assertTrue("変更なしと言ってます。", ext.isMuleConfigEdit);
		String xpathStr = "/mule:mule/mule:model";
		XPathExpression exp = ext.getXPathExpression(xpathStr);
		NodeList nodeList = (NodeList)exp.evaluate(muleConfig, XPathConstants.NODESET);
		assertEquals("Modelの個数が誤っています。", 1, nodeList.getLength());
		Element e = (Element)nodeList.item(0);
		assertEquals("モデル名が誤っています。", "MultiOperationServiceConnectorModel", e.getAttribute("name"));
		xpathStr = "mule:service";
		exp = ext.getXPathExpression(xpathStr);
		nodeList = (NodeList)exp.evaluate(e, XPathConstants.NODESET);
		assertEquals("Serviceの個数が誤っています。", 2, nodeList.getLength());
		e = (Element)nodeList.item(0);
		assertEquals("Service名が誤っています。", "MultiOperationServiceConnector_foo", e.getAttribute("name"));
		xpathStr = "mule:inbound/vm:inbound-endpoint";
		exp = ext.getXPathExpression(xpathStr);
		Element iep = (Element)exp.evaluate(e, XPathConstants.NODE);
		assertEquals("pathが誤っています。", "MultiOperationServiceConnector_fooIn", iep.getAttribute("path"));

		xpathStr = "mule:outbound/mule:custom-outbound-router/cxf:outbound-endpoint";
		exp = ext.getXPathExpression(xpathStr);
		Element oep = (Element)exp.evaluate(e, XPathConstants.NODE);
		assertEquals("クライアントクラスが誤っています。", "hoge.multioperationservice.MultiOperationService", oep.getAttribute("clientClass"));
		assertEquals("オペレーションが誤っています。", "foo", oep.getAttribute("operation"));
		assertEquals("WSDLPortが誤っています。", "MultiOperationServicePort", oep.getAttribute("wsdlPort"));

		xpathStr = "mule:custom-transformer";
		exp = ext.getXPathExpression(xpathStr);
		Element t = (Element)exp.evaluate(oep, XPathConstants.NODE);
		assertEquals("変換クラス名が誤っています。", "hoge.multioperationservice.ObjectToJAXBElement_Foo", t.getAttribute("class"));
		assertEquals("変換名が誤っています。", "MultiOperationServiceConnector_fooInTransformer", t.getAttribute("name"));
		
		e = (Element)nodeList.item(1);
		assertEquals("Service名が誤っています。", "MultiOperationServiceConnector_bar", e.getAttribute("name"));
		xpathStr = "mule:inbound/vm:inbound-endpoint";
		exp = ext.getXPathExpression(xpathStr);
		iep = (Element)exp.evaluate(e, XPathConstants.NODE);
		assertEquals("pathが誤っています。", "MultiOperationServiceConnector_barIn", iep.getAttribute("path"));

		xpathStr = "mule:outbound/mule:custom-outbound-router/cxf:outbound-endpoint";
		exp = ext.getXPathExpression(xpathStr);
		oep = (Element)exp.evaluate(e, XPathConstants.NODE);
		assertEquals("クライアントクラスが誤っています。", "hoge.multioperationservice.MultiOperationService", oep.getAttribute("clientClass"));
		assertEquals("オペレーションが誤っています。", "bar", oep.getAttribute("operation"));
		assertEquals("WSDLPortが誤っています。", "MultiOperationServicePort", oep.getAttribute("wsdlPort"));

		xpathStr = "mule:custom-transformer";
		exp = ext.getXPathExpression(xpathStr);
		t = (Element)exp.evaluate(oep, XPathConstants.NODE);
		assertEquals("変換クラス名が誤っています。", "hoge.multioperationservice.ObjectToJAXBElement_Bar", t.getAttribute("class"));
		assertEquals("変換名が誤っています。", "MultiOperationServiceConnector_barInTransformer", t.getAttribute("name"));
	}
	
	public void testAddClientConnectorConfig_マルチ状態からモノオペレーション() throws Exception {
		WSDL2JavaExt ext = new WSDL2JavaExt();
		ext.setVersion("2.1");
		ext.setRootPackage("hoge");
		File wsdl = getFile("MonoOperationService.wsdl");
		Document wsdlDoc = getDocument(wsdl);
		Document muleConfig = getDocument(getFile("mule-client-multi-config.xml"));
		ext.doc = muleConfig;
		ext.addClientConnectorConfig(wsdl, wsdlDoc);
		assertTrue("変更なしと言ってます。", ext.isMuleConfigEdit);
		String xpathStr = "/mule:mule/mule:model";
		XPathExpression exp = ext.getXPathExpression(xpathStr);
		NodeList nodeList = (NodeList)exp.evaluate(muleConfig, XPathConstants.NODESET);
		assertEquals("Modelの個数が誤っています。", 1, nodeList.getLength());
		Element e = (Element)nodeList.item(0);
		assertEquals("モデル名が誤っています。", "MonoOperationServiceConnectorModel", e.getAttribute("name"));
		xpathStr = "mule:service";
		exp = ext.getXPathExpression(xpathStr);
		nodeList = (NodeList)exp.evaluate(e, XPathConstants.NODESET);
		assertEquals("Serviceの個数が誤っています。", 1, nodeList.getLength());
		e = (Element)nodeList.item(0);
		assertEquals("Service名が誤っています。", "MonoOperationServiceConnector", e.getAttribute("name"));
		xpathStr = "mule:inbound/vm:inbound-endpoint";
		exp = ext.getXPathExpression(xpathStr);
		Element iep = (Element)exp.evaluate(e, XPathConstants.NODE);
		assertEquals("pathが誤っています。", "MonoOperationServiceConnectorIn", iep.getAttribute("path"));

		xpathStr = "mule:outbound/mule:custom-outbound-router/cxf:outbound-endpoint";
		exp = ext.getXPathExpression(xpathStr);
		Element oep = (Element)exp.evaluate(e, XPathConstants.NODE);
		assertEquals("クライアントクラスが誤っています。", "hoge.monooperationservice.MonoOperationService", oep.getAttribute("clientClass"));
		assertEquals("オペレーションが誤っています。", "foo", oep.getAttribute("operation"));
		assertEquals("WSDLPortが誤っています。", "MonoOperationServicePort", oep.getAttribute("wsdlPort"));

		xpathStr = "mule:custom-transformer";
		exp = ext.getXPathExpression(xpathStr);
		Element t = (Element)exp.evaluate(oep, XPathConstants.NODE);
		assertEquals("変換クラス名が誤っています。", "hoge.monooperationservice.ObjectToJAXBElement", t.getAttribute("class"));
		assertEquals("変換名が誤っています。", "MonoOperationServiceConnectorInTransformer", t.getAttribute("name"));
	}

	@SuppressWarnings("deprecation")
	File getFile(String wsdlName) throws Exception {
		URL url = this.getClass().getClassLoader().getResource("jp/rough_diamond/ant/taskdefs/" + wsdlName);
		System.out.println(url);
		return new File(URLDecoder.decode(url.getPath()));
	}
	
	Document getDocument(File f) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(f);
		return doc;
	}
}
