/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.ant.taskdefs;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import jp.rough_diamond.tools.servicegen.NameSpaceContextImpl;

import org.apache.commons.io.FileUtils;
import org.apache.cxf.tools.wsdlto.WSDLToJava;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Path;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class WSDL2JavaExt extends Task {
	@Override
	public void execute() throws BuildException {
		try {
			StringBuilder sb = new StringBuilder(wsdlDir.getCanonicalPath());
			sb.append("/");
			sb.append("*.wsdl");
			DirectoryScanner ds = FileScanUtil.getDirectoryScanner(sb.toString());
			String[] names = ds.getIncludedFiles();
			File rootDir = ds.getBasedir();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder muleDB = dbf.newDocumentBuilder();
			this.doc = muleDB.parse(muleConfigFile);
			isMuleConfigEdit = false;
	        for(String name : names) {
				System.out.println(name);
				File wsdl = new File(rootDir, name);
				Document wsdlDoc = muleDB.parse(wsdl);
				generate(wsdl, wsdlDoc);
				addClientConnectorConfig(wsdl, wsdlDoc);
			}
	        if(isMuleConfigEdit) {
	        	output();
	        }
	        deletebackupFile();
		} catch(Exception e) {
			e.printStackTrace();
			throw new BuildException(e);
		}
	}

	void deletebackupFile() {
		Delete task = new Delete();
		task.setProject(getProject());
		task.setDir(muleConfigFile.getParentFile());
		task.setIncludes(muleConfigFile.getName() + ".*");
		task.execute();
	}

	private void output() throws Exception {
		System.out.println("出力します。");
		TransformerFactory tfactory = TransformerFactory.newInstance(); 
		Transformer transformer = tfactory.newTransformer();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
		File bkup = new File(muleConfigFile.getParentFile(), muleConfigFile.getName() + "." + sdf.format(new Date()));
		System.out.println(bkup.getCanonicalPath());
		FileUtils.copyFile(muleConfigFile, bkup);
        transformer.transform(new DOMSource(doc), new StreamResult(muleConfigFile));
//      	StringWriter sw = new StringWriter();
//      	transformer.transform(new DOMSource(doc), new StreamResult(sw));
//      	System.out.println(sw.toString());
	}

	Document doc;
	boolean isMuleConfigEdit = false;
	
	void addClientConnectorConfig(File wsdl, Document wsdlDoc) throws Exception {
		String modelName = getConnectorName(wsdlDoc) + "Model";
		System.out.println(modelName);
		Element model = getModelElement(modelName);
		addServices(model, wsdl, wsdlDoc);
	}

	void addServices(Element model, File wsdl, Document wsdlDoc) throws Exception {
		resetServiceNames(model);
		List<String> operations = getOperations(wsdlDoc);
		if(operations.size() == 1) {
			addService(model, wsdl, wsdlDoc, operations.get(0), true);
		} else {
			for(String operation : operations) {
				addService(model, wsdl, wsdlDoc, operation, false);
			}
		}
		removeServices(model);
	}

	Set<String> serviceNames;
	void removeServices(Element model) throws Exception {
		for(String serviceName : serviceNames) {
			String xpathStr = String.format("mule:service[@name='%s']", serviceName);
			XPathExpression exp = getXPathExpression(xpathStr);
			Element service = (Element)exp.evaluate(model, XPathConstants.NODE);
			model.removeChild(service);
			System.out.println(serviceName + "を削除しました。");
		}
	}
	
	void resetServiceNames(Element model) throws Exception {
		serviceNames = new HashSet<String>();
		String xpathStr = "mule:service";
		XPathExpression exp = getXPathExpression(xpathStr);
		NodeList nodeList = (NodeList)exp.evaluate(model, XPathConstants.NODESET);
		for(int i = 0 ; i < nodeList.getLength() ; i++) {
			Element e = (Element)nodeList.item(i);
			serviceNames.add(e.getAttribute("name"));
		}
	}
	
	String getServiceName(Document wsdlDoc, String operation, boolean monoOperation) throws Exception {
		String connectorName = getConnectorName(wsdlDoc);
		connectorName = (monoOperation) ? connectorName : connectorName + "_" + operation;
		return connectorName;
	}
	
	void addService(Element model, File wsdl, Document wsdlDoc, String operation, boolean monoOperation) throws Exception {
		String connectorName = getServiceName(wsdlDoc, operation, monoOperation);
		serviceNames.remove(connectorName);
		if(hasServiceElement(model, connectorName)) {
			System.out.println("処理をスキップします。");
			return;
		}
		isMuleConfigEdit = true;
		System.out.println(connectorName + "を追加します。");

		String packageName = getPackage(wsdl);
		String muleNameSpace = NameSpaceContextImpl.MULE_NAME_SPACE + getVersion();
		Element serviceEL = doc.createElementNS(muleNameSpace, "service");
		serviceEL.setAttribute("name", connectorName);
		Text t = doc.createTextNode("\n    ");
		model.appendChild(t);
		model.appendChild(serviceEL);

		Element inboundEL = doc.createElementNS(muleNameSpace, "inbound");
		t = doc.createTextNode("\n      ");
		serviceEL.appendChild(t);
		serviceEL.appendChild(inboundEL);

		Element inboundEndpointEL = doc.createElementNS(NameSpaceContextImpl.VM_NAME_SPACE + getVersion(), "vm:inbound-endpoint");
		t = doc.createTextNode("\n        ");
		inboundEL.appendChild(t);
		inboundEL.appendChild(inboundEndpointEL);
		t = doc.createTextNode("\n      ");
		inboundEL.appendChild(t);
		inboundEndpointEL.setAttribute("path", connectorName + "In");

		Element bridgeComponentEL = doc.createElementNS(muleNameSpace, "bridge-component");
		t = doc.createTextNode("\n      ");
		serviceEL.appendChild(t);
		serviceEL.appendChild(bridgeComponentEL);

		Element outboundEL = doc.createElementNS(muleNameSpace, "outbound");
		t = doc.createTextNode("\n      ");
		serviceEL.appendChild(t);
		serviceEL.appendChild(outboundEL);

		Element outboundRouterEL = doc.createElementNS(muleNameSpace, "custom-outbound-router");
		outboundRouterEL.setAttribute("class", "jp.rough_diamond.framework.es.DynamicEndPointRouter");
		t = doc.createTextNode("\n        ");
		outboundEL.appendChild(t);
		outboundEL.appendChild(outboundRouterEL);
		t = doc.createTextNode("\n      ");
		outboundEL.appendChild(t);
		
		Element outboundEndpointEL = doc.createElementNS(NameSpaceContextImpl.CXF_NAME_SPACE + getVersion(), "cxf:outbound-endpoint");
		t = doc.createTextNode("\n          ");
		outboundRouterEL.appendChild(t);
		outboundRouterEL.appendChild(outboundEndpointEL);
		t = doc.createTextNode("\n        ");
		outboundRouterEL.appendChild(t);
		String serviceName = getServiceName(wsdlDoc);
		outboundEndpointEL.setAttribute("address", "http://${" + serverEndpointPrefix + ".host}:${" + serverEndpointPrefix + ".port}/services/" + serviceName);
		outboundEndpointEL.setAttribute("clientClass", packageName + "." + serviceName);
		outboundEndpointEL.setAttribute("wsdlLocation", "file:///${wsdl.dir}/" + wsdl.getName());
		outboundEndpointEL.setAttribute("mtomEnabled", "true");
		outboundEndpointEL.setAttribute("operation", operation);
		outboundEndpointEL.setAttribute("wsdlPort", getWsdlPort(wsdlDoc));
		
		t = doc.createTextNode("\n            ");
		outboundEndpointEL.appendChild(t);
		Element customTransformerEL = doc.createElementNS(muleNameSpace, "custom-transformer");
		outboundEndpointEL.appendChild(customTransformerEL);
		t = doc.createTextNode("\n          ");
		outboundEndpointEL.appendChild(t);
		customTransformerEL.setAttribute("name", connectorName + "InTransformer");
		String transformerClassName = (monoOperation) ? TRANSFORMER_CLASS_NAME : getTransformaerClassName(operation);
		customTransformerEL.setAttribute("class", packageName + "." + transformerClassName);
		
		t = doc.createTextNode("\n    ");
		serviceEL.appendChild(t);

		t = doc.createTextNode("\n  ");
		model.appendChild(t);
	}

	boolean hasServiceElement(Element model, String connectorName) throws Exception {
		String xpathStr = "mule:service";
		XPathExpression exp = getXPathExpression(xpathStr);
		NodeList nodeList = (NodeList)exp.evaluate(model, XPathConstants.NODESET);
		System.out.println(nodeList.getLength());
		for(int i = 0 ; i < nodeList.getLength() ; i++) {
			Element el = (Element)nodeList.item(i);
			if(connectorName.equals(el.getAttribute("name"))) {
				System.out.println("見つかりました。");
				return true;
			}
		}
		System.out.println(connectorName + "が見つかりませんでした。");
		return false;
	}

	private Element getModelElement(String modelName) throws Exception {
		String xpathStr = "/mule:mule/mule:model";
		XPathExpression exp = getXPathExpression(xpathStr);
		NodeList nodeList = (NodeList)exp.evaluate(doc, XPathConstants.NODESET);
		System.out.println(nodeList.getLength());
		for(int i = 0 ; i < nodeList.getLength() ; i++) {
			Element el = (Element)nodeList.item(i);
			if(modelName.equals(el.getAttribute("name"))) {
				System.out.println(modelName + "が見つかりました。");
				return el;
			}
		}
		isMuleConfigEdit = true;
		System.out.println(modelName + "が見つかりませんでした。作成します。");
		Element ret = doc.createElementNS(NameSpaceContextImpl.MULE_NAME_SPACE + getVersion(), "model");
		Text t = doc.createTextNode("\n  ");
		doc.getDocumentElement().appendChild(t);
		doc.getDocumentElement().appendChild(ret);
		t = doc.createTextNode("\n");
		doc.getDocumentElement().appendChild(t);
		ret.setAttribute("name", modelName);
		
		return ret;
	}

	private String getConnectorName(Document wsdlDoc) throws Exception {
		return getServiceName(wsdlDoc) + "Connector";
	}
	
	private String getWsdlPort(Document wsdlDoc) throws Exception {
		String xpathStr = "//wsdl:port";
		XPathExpression exp = getXPathExpression(xpathStr);
		Element el = (Element)exp.evaluate(wsdlDoc, XPathConstants.NODE);
		String ret = el.getAttribute("name");
		System.out.println(ret);
		return ret;
	}

//	private String getOperation(Document wsdlDoc) throws Exception {
//		XPathFactory xpf = XPathFactory.newInstance();
//		XPath xpath = xpf.newXPath();
//		xpath.setNamespaceContext(new NameSpaceContextImpl(getVersion()));
//		String xpathStr = "//wsdl:operation";
//		System.out.println(xpathStr);
//		XPathExpression exp = xpath.compile(xpathStr);
//		Element el = (Element)exp.evaluate(wsdlDoc, XPathConstants.NODE);
//		String ret = el.getAttribute("name");
//		System.out.println(ret);
//		return ret;
//	}
//
	private String getPortType(Document wsdlDoc) throws Exception {
		String xpathStr = "//wsdl:portType";
		XPathExpression exp = getXPathExpression(xpathStr);
		Element el = (Element)exp.evaluate(wsdlDoc, XPathConstants.NODE);
		String ret = el.getAttribute("name");
		System.out.println(ret);
		return ret;
	}

	private String getServiceName(Document wsdlDoc) throws Exception {
		String xpathStr = "/wsdl:definitions/wsdl:service";
		XPathExpression exp = getXPathExpression(xpathStr);
		Element el = (Element)exp.evaluate(wsdlDoc, XPathConstants.NODE);
		String ret = el.getAttribute("name");
		System.out.println(ret);
		return ret;
	}

	List<String> getOperations(Document wsdlDoc) throws Exception {
		String xpathStr = "/wsdl:definitions/wsdl:portType/wsdl:operation";
		XPathExpression exp = getXPathExpression(xpathStr);
		NodeList nodeList = (NodeList)exp.evaluate(wsdlDoc, XPathConstants.NODESET);
		System.out.println(nodeList.getLength());
		List<String> ret = new ArrayList<String>();
		for(int i = 0 ; i < nodeList.getLength() ; i++) {
			Element el = (Element)nodeList.item(i);
			ret.add(el.getAttribute("name"));
		}
		return ret;
	}

	private void generate(File wsdl, Document wsdlDoc) throws Exception {
		String packageName = getPackage(wsdl);
		File packageDir = new File(getSrcdir(), packageName.replace('.', '/'));
		boolean mkdirRet = packageDir.mkdirs();
		mkdirRet = !mkdirRet;//FindBug警告回避
		File[] files = packageDir.listFiles();
		for(File f : files) {
			if(f.isFile()) {
				boolean ret = f.delete();
				ret = !ret;
			}
		}

		Java java = new Java();
		java.setProject(getProject());
		java.setClassname(WSDLToJava.class.getName());
		java.setFork(true);
		java.createArg().setValue("-client");
		java.createArg().setValue("-p");
		java.createArg().setValue(packageName);
		java.createArg().setValue("-d");
		java.createArg().setFile(getSrcdir());
		java.createArg().setValue("-verbose");
		java.createArg().setFile(wsdl);
		java.setClasspath((Path)getProject().getReference(getClassPathRef()));
		java.execute();
		
		List<String> operations = getOperations(wsdlDoc);
		String portTypeClassName = getPortType(wsdlDoc);
		if(operations.size() == 1) {
			String transformClassBody = String.format(TRANSFORMER_CLASS_TEMPLATE, packageName, TRANSFORMER_CLASS_NAME, 
					TRANSFORMER_CLASS_NAME, TRANSFORMER_CLASS_NAME, operations.get(0), portTypeClassName);
			System.out.println(transformClassBody);
			FileUtils.writeStringToFile(new File(packageDir, TRANSFORMER_CLASS_NAME + ".java"), transformClassBody, "UTF-8");
		} else {
			for(String operation : operations) {
				String className = getTransformaerClassName(operation);
				String transformClassBody = String.format(TRANSFORMER_CLASS_TEMPLATE, packageName, className, 
						className, className, operation, portTypeClassName);
				System.out.println(transformClassBody);
				FileUtils.writeStringToFile(new File(packageDir, className + ".java"), transformClassBody, "UTF-8");
			}
		}
	}
	
	XPathExpression getXPathExpression(String xpathStr) throws Exception {
		XPathFactory xpf = XPathFactory.newInstance();
		XPath xpath = xpf.newXPath();
		xpath.setNamespaceContext(new NameSpaceContextImpl(getVersion()));
		System.out.println(xpathStr);
		XPathExpression exp = xpath.compile(xpathStr);
		return exp;
	}
	
	String getTransformaerClassName(String operation) {
		char[] array = operation.toCharArray();
		array[0] = Character.toUpperCase(array[0]);
		return TRANSFORMER_CLASS_NAME + "_" + new String(array);
	}
	
	String getPackage(File wsdl) {
		StringBuilder sb = new StringBuilder();
		sb.append(getRootPackage());
		if(!getRootPackage().endsWith(".")) {
			sb.append(".");
		}
		sb.append(getSubPackageName(wsdl.getName()));
		return sb.toString();
	}
	
	static String getSubPackageName(String wsdlName) {
		return wsdlName.replaceAll("\\.wsdl$", "").toLowerCase();
	}
	
	private File wsdlDir;
	private File srcdir;
	private String rootPackage;
	private String classPathRef;
	private File muleConfigFile;
	private String serverEndpointPrefix;
	private String muleVersion;
	
	public String getVersion() {
		return muleVersion;
	}

	public void setVersion(String version) {
		this.muleVersion = version;
	}

	public File getMuleConfigFile() {
		return muleConfigFile;
	}

	public void setMuleConfigFile(File muleConfigFile) {
		this.muleConfigFile = muleConfigFile;
	}

	public String getClassPathRef() {
		return classPathRef;
	}

	public void setClassPathRef(String classPathRef) {
		this.classPathRef = classPathRef;
	}

	public String getRootPackage() {
		return rootPackage;
	}
	public void setRootPackage(String rootPackage) {
		this.rootPackage = rootPackage;
	}

	public File getWsdlDir() {
		return wsdlDir;
	}

	public void setWsdlDir(File wsdlDir) {
		this.wsdlDir = wsdlDir;
	}

	public File getSrcdir() {
		return srcdir;
	}

	public void setSrcdir(File srcdir) {
		this.srcdir = srcdir;
	}
	
	public String getServerEndpointPrefix() {
		return serverEndpointPrefix;
	}

	public void setServerEndpointPrefix(String serverEndpointPrefix) {
		this.serverEndpointPrefix = serverEndpointPrefix;
	}

	static final String TRANSFORMER_CLASS_NAME = "ObjectToJAXBElement";
	static final String TRANSFORMER_CLASS_TEMPLATE = 
		"package %s;\n" +
		"\n" +
		"import jp.rough_diamond.commons.util.mule.transformer.AbstractObjectToJAXBElement;\n" +
		"\n" +
		"public class %s extends AbstractObjectToJAXBElement {\n" +
		"    public final static %s INSTANCE = new %s();\n" +
		"\n" +
		"    @Override\n" +
		"    protected String getOperation() {\n" +
		"        return \"%s\";\n" +
		"    }\n" +
		"\n" +
		"    @Override\n" +
		"    protected Class<?> getPortType() {\n" +
		"        return %s.class;\n" +
		"    }\n" +
		"}\n";
}
