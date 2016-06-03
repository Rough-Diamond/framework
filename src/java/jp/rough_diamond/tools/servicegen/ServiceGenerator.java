/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.tools.servicegen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

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

import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class ServiceGenerator {
	private File input;
	private File srcDir;
	private File muleConfigFile;
	private String localhostEndpointPrefix;
	private String muleVersion;
	
	public ServiceGenerator(File input, File srcDir, File muleConfigFile, String localhostEndpointPrefix, String muleVersion) {
		this.input = input;
		this.srcDir = srcDir;
		this.muleConfigFile = muleConfigFile;
		this.localhostEndpointPrefix = localhostEndpointPrefix;
		this.muleVersion = muleVersion;
	}
	
	public void doIt() throws Exception {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(input);

        ServiceInfo[] infos = generateServiceInfos(doc.getDocumentElement());
		
		Properties props = new Properties();
        props.setProperty("input.encoding",                     "Shift_JIS");
        props.setProperty("output.encoding",                    "Shift_JIS");
        props.setProperty("resource.loader",                    "class");
        props.setProperty("class.resource.loader.description",  "Velocity File Resource Loader");
        props.setProperty("class.resource.loader.class",        "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(props);

		if(!isInterfaceOnly()) {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder muleDB = dbf.newDocumentBuilder();
			this.doc = muleDB.parse(muleConfigFile);
		}
		
		isMuleConfigEdit = false;
		for(ServiceInfo service : infos) {
        	makeServiceInterface(service);
    		if(!isInterfaceOnly()) {
	        	makeServiceDummyImplimentation(service);
	        	addServiceConnector(service);
    		}
        }
		if(isMuleConfigEdit) {
			output();
		}
	}
	
	boolean isInterfaceOnly() {
		return (muleConfigFile == null);
	}
	
	private Document doc;
	private boolean isMuleConfigEdit = false;
	private void addServiceConnector(ServiceInfo service) throws Exception {
		Element model = getModelElement(service);
		if(hasServiceElement(model, service)) {
			System.out.println("処理をスキップします。");
			return;
		}
		addService(model, service);
	}

	void addService(Element model, ServiceInfo service) {
		isMuleConfigEdit = true;
		String serviceName = service.className;
		System.out.println(serviceName + "を追加します。");
		
		String muleNameSpace = NameSpaceContextImpl.MULE_NAME_SPACE + muleVersion;
		String cxfNameSpace = NameSpaceContextImpl.CXF_NAME_SPACE + muleVersion;
		
		Element serviceEL = doc.createElementNS(muleNameSpace, "service");
		serviceEL.setAttribute("name", serviceName);
		Text t = doc.createTextNode("\n    ");
		model.appendChild(t);
		model.appendChild(serviceEL);

		Element inboundEL = doc.createElementNS(muleNameSpace, "inbound");
		t = doc.createTextNode("\n      ");
		serviceEL.appendChild(t);
		serviceEL.appendChild(inboundEL);

		Element inboundEndpointEL = doc.createElementNS(cxfNameSpace, "cxf:inbound-endpoint");
		t = doc.createTextNode("\n        ");
		inboundEL.appendChild(t);
		inboundEL.appendChild(inboundEndpointEL);
		t = doc.createTextNode("\n      ");
		inboundEL.appendChild(t);
		inboundEndpointEL.setAttribute("mtomEnabled", "true");
		inboundEndpointEL.setAttribute("address", "http://${" + localhostEndpointPrefix + ".host}:${" + localhostEndpointPrefix + ".port}/services/" + serviceName);
		inboundEndpointEL.setAttribute("serviceClass", service.packageName + "." + service.className);
		
		Element componentEL = doc.createElementNS(muleNameSpace, "component");
		t = doc.createTextNode("\n      ");
		serviceEL.appendChild(t);
		serviceEL.appendChild(componentEL);
		componentEL.setAttribute("class", service.packageName + ".impl." + serviceName + "Impl");

		t = doc.createTextNode("\n    ");
		serviceEL.appendChild(t);

		t = doc.createTextNode("\n  ");
		model.appendChild(t);
	}

	boolean hasServiceElement(Element model, ServiceInfo service) throws Exception {
		XPathFactory xpf = XPathFactory.newInstance();
		XPath xpath = xpf.newXPath();
		xpath.setNamespaceContext(new NameSpaceContextImpl(muleVersion));
		String serviceName = service.className;
//		serviceName = "CFXSampleServer2";
		String xpathStr = "mule:service";
		System.out.println(xpathStr);
		XPathExpression exp = xpath.compile(xpathStr);
		NodeList nodeList = (NodeList)exp.evaluate(model, XPathConstants.NODESET);
		System.out.println(nodeList.getLength());
		for(int i = 0 ; i < nodeList.getLength() ; i++) {
			Element el = (Element)nodeList.item(i);
			if(serviceName.equals(el.getAttribute("name"))) {
				System.out.println("見つかりました。");
				return true;
			}
		}
		System.out.println(serviceName + "が見つかりませんでした。");
		return false;
	}

	public Element getModelElement(ServiceInfo service) throws Exception {
		XPathFactory xpf = XPathFactory.newInstance();
		XPath xpath = xpf.newXPath();
		xpath.setNamespaceContext(new NameSpaceContextImpl(muleVersion));
		String modelName = service.className + "Model";
//		modelName = "SampleServer2";
		String xpathStr = "/mule:mule/mule:model";
		System.out.println(xpathStr);
		XPathExpression exp = xpath.compile(xpathStr);
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
		Element ret = doc.createElementNS(NameSpaceContextImpl.MULE_NAME_SPACE + muleVersion, "model");
		Text t = doc.createTextNode("\n  ");
		doc.getDocumentElement().appendChild(t);
		doc.getDocumentElement().appendChild(ret);
		t = doc.createTextNode("\n");
		doc.getDocumentElement().appendChild(t);
		ret.setAttribute("name", modelName);
		
		return ret;
	}
	
	private void output() throws Exception {
		System.out.println("出力します。");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
		File bkup = new File(muleConfigFile.getParentFile(), muleConfigFile.getName() + "." + sdf.format(new Date()));
		System.out.println(bkup.getCanonicalPath());
		FileUtils.copyFile(muleConfigFile, bkup);
        TransformerFactory tfactory = TransformerFactory.newInstance(); 
        Transformer transformer = tfactory.newTransformer();
        transformer.transform(new DOMSource(doc), new StreamResult(muleConfigFile));
	}
	
	private void makeServiceDummyImplimentation(ServiceInfo service) throws Exception {
        File dir;
        if(service.packageName.equals("")) {
            dir = srcDir;
        } else {
            dir = new File(srcDir, service.packageName.replace('.', '/'));
        }
        dir = new File(dir, "impl");
        boolean ret = dir.mkdirs();
        //FindBugs対応
        if(!ret) {
        	ret = !ret;
        }
        VelocityContext context = new VelocityContext();
        context.put("service", service);
        Template template = Velocity.getTemplate("jp/rough_diamond/tools/servicegen/serviceImplTemplate.vm");
        File f = new File(dir, service.className + "Impl.java");
        ret = f.delete();
        //FindBugs対応
        if(!ret) {
        	ret = !ret;
        }
        FileOutputStream fos = new FileOutputStream(f);
        PrintWriter pw = new PrintWriter(fos);
        template.merge(context, pw);
        pw.flush();
        fos.close();
	}

	private void makeServiceInterface(ServiceInfo service) throws Exception {
        File dir;
        if(service.packageName.equals("")) {
            dir = srcDir;
        } else {
            dir = new File(srcDir, service.packageName.replace('.', '/'));
        }
        boolean ret = dir.mkdirs();
        //FindBugs対応
        if(!ret) {
        	ret = !ret;
        }
        VelocityContext context = new VelocityContext();
        context.put("service", service);
        Template template = Velocity.getTemplate("jp/rough_diamond/tools/servicegen/serviceInterfaceTemplate.vm");
        File f = new File(dir, service.className + ".java");
        ret = f.delete();
        //FindBugs対応
        if(!ret) {
        	ret = !ret;
        }
        FileOutputStream fos = new FileOutputStream(f);
        PrintWriter pw = new PrintWriter(fos);
        template.merge(context, pw);
        pw.flush();
        fos.close();
	}

	private ServiceInfo[] generateServiceInfos(Element root) {
        NodeList list = root.getElementsByTagName("service");
        List<ServiceInfo> ret = new LinkedList<ServiceInfo>();
        for(int i = 0 ; i < list.getLength() ; i++) {
            ret.add(generateServiceInfo((Element)list.item(i)));
        }
		return ret.toArray(new ServiceInfo[ret.size()]);
	}

	private ServiceInfo generateServiceInfo(Element node) {
        ServiceInfo ret = new ServiceInfo();
        NamedNodeMap map = node.getAttributes();
        String fqcn = map.getNamedItem("class").getNodeValue();
        System.out.println(fqcn);
        int index = fqcn.lastIndexOf('.');
        char[] className;
        if(index == -1) {
            ret.packageName = "";
            className = fqcn.toCharArray();
        } else {
            ret.packageName = fqcn.substring(0, index);
            className = fqcn.substring(index + 1).toCharArray();
        }
        System.out.println(ret.packageName);
        className[0] = Character.toUpperCase(className[0]);
        ret.className = new String(className);
        System.out.println(ret.className);

        generateOperations(node.getElementsByTagName("operation"), ret);
        return ret;
	}

	private void generateOperations(NodeList list, ServiceInfo service) {
        Set<String> imports = new TreeSet<String>();
        for(int i = 0 ; i < list.getLength() ; i++) {
            Operation op = new Operation();
            Element el = (Element)list.item(i);
            NamedNodeMap map = el.getAttributes();
            char[] nameArray = map.getNamedItem("name").getNodeValue().toCharArray();
            nameArray[0] = Character.toLowerCase(nameArray[0]);
            op.name = new String(nameArray);
            System.out.println(op.name);
            String type = map.getNamedItem("returnType").getNodeValue();
            op.returnType = type;
            System.out.println(op.returnType);
            service.operations.add(op);
            generateArgs(el.getElementsByTagName("arg"), service, op, imports);
        }
        imports.add("java.util.*");
        System.out.println(imports);
        service.imports = (String[])imports.toArray(new String[imports.size()]);
	}

	private void generateArgs(NodeList list, ServiceInfo service, Operation op, Set<String> imports) {
        for(int i = 0 ; i < list.getLength() ; i++) {
            Arg arg = new Arg();
            Element el = (Element)list.item(i);
            arg.name = el.getAttribute("name");
            arg.type = el.getAttribute("type");
            op.args.add(arg);
        }
	}

	public static class ServiceInfo {
        public String       	packageName;
        public String getPackageName() {
			return packageName;
		}
		public void setPackageName(String packageName) {
			this.packageName = packageName;
		}
		public String getClassName() {
			return className;
		}
		public void setClassName(String className) {
			this.className = className;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String[] getImports() {
			return imports;
		}
		public void setImports(String[] imports) {
			this.imports = imports;
		}
		public List<Operation> getOperations() {
			return operations;
		}
		public void setOperations(List<Operation> operations) {
			this.operations = operations;
		}
		public String       	className;
        public String       	description;
        public String[]     	imports;
        public List<Operation>	operations = new LinkedList<Operation>();
	}
	
	public static class Operation {
		public String				name;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getReturnType() {
			return returnType;
		}
		public void setReturnType(String returnType) {
			this.returnType = returnType;
		}
		public List<Arg> getArgs() {
			return args;
		}
		public void setArgs(List<Arg> args) {
			this.args = args;
		}
		public List<ExceptionType> getExceptionTypes() {
			return exceptionTypes;
		}
		public void setExceptionTypes(List<ExceptionType> exceptionTypes) {
			this.exceptionTypes = exceptionTypes;
		}
		public String				returnType;
		public List<Arg>			args = new LinkedList<Arg>();
		public List<ExceptionType>	exceptionTypes = new LinkedList<ExceptionType>();
	}
	
	public static class Arg {
		public String				type;
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String				name;
	}
	
	public static class ExceptionType {
		
	}

	public static void main(String[] args) throws Exception {
		ServiceGenerator t = new ServiceGenerator(
				new File("etc/serviceDef/services.xml"),
				new File("src/other"),
				new File("src/other/mule-sample-config.xml"), "local", "2.1");
		t.doIt();
	}
}
