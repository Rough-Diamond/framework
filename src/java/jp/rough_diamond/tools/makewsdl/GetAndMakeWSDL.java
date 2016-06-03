/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.tools.makewsdl;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.mule.api.MuleContext;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.routing.InboundRouterCollection;
import org.mule.api.service.Service;

import jp.rough_diamond.commons.di.DIContainer;
import jp.rough_diamond.commons.di.DIContainerFactory;
import jp.rough_diamond.commons.di.SpringFramework;
import jp.rough_diamond.framework.es.ServiceBus;


public class GetAndMakeWSDL {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		System.out.println(args.length);
		String wsdlStorageDir = (args.length == 0) ? "etc/wsdl" : args[0];
		Class cl = (args.length < 2) ? SpringFramework.class : Class.forName(args[1]);
		String config = (args.length < 3) ? null : args[2];
		DIContainer di;
		if(config == null) {
			di = (DIContainer)cl.newInstance();
		} else {
			di = (DIContainer)cl.getConstructor(String.class).newInstance(config);
		}
		DIContainerFactory.setDIContainer(di);
		GetAndMakeWSDL t = new GetAndMakeWSDL(new File(wsdlStorageDir));
		t.execute();
	}
	
	private File wsdlStorageDir;

	public GetAndMakeWSDL(File wsdlStorageDir) {
		this.wsdlStorageDir = wsdlStorageDir;
	}
	
	public void execute() throws Exception {
		Set<String> urlSet = getUrlSet();
		HttpClient client = new HttpClient();
		for(String url : urlSet) {
			GetMethod method = new GetMethod(url + "?wsdl");
			String wsdlName = getWsdlName(url);
			int status = client.executeMethod(method);
			if(status == HttpStatus.SC_OK) {
                saveWSDL(wsdlName, method.getResponseBody());
			}
		}
	}

	private void saveWSDL(String wsdlName, byte[] responseBody) throws Exception {
		File f = new File(wsdlStorageDir, wsdlName);
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
		IOException ioe = null;
		try {
			bos.write(responseBody);
		} catch(IOException e) {
			ioe = e;
		} finally {
			try {
				bos.close();
			} catch(IOException e) {
				if(ioe == null) {
					throw e;
				} else {
					e.printStackTrace();
					throw ioe;
				}
			}
		}
	}

	static String getWsdlName(String url) {
		String[] split = url.split("/");
		return split[split.length - 1] + ".wsdl";
	}

	@SuppressWarnings("unchecked")
	Set<String> getUrlSet() {
		MuleContext context = ServiceBus.getInstance().getContext();
		Collection<Service> col = context.getRegistry().lookupServices();
		Set<String> urlSet = new HashSet<String>();
		for(Service service : col) {
			System.out.println(service.getName());
			InboundRouterCollection irc = service.getInboundRouter();
			List<InboundEndpoint> ies = irc.getEndpoints();
			for(InboundEndpoint ie : ies) {
				if("cxf".equals(ie.getProtocol())) {
					urlSet.add(ie.getEndpointURI().toString());
				}
			}
		}
		System.out.println(urlSet);
		return urlSet;
	}
}
