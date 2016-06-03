/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.web.struts.multipart;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import jp.rough_diamond.commons.io.ReadCountInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MultipartProgressRequestWrapper extends HttpServletRequestWrapper {
	private final static Log log = LogFactory.getLog(MultipartProgressRequestWrapper.class);
	
	static Map<String, MultipartProgressRequestWrapper> REQUEST_MAP;
	
	static {
		REQUEST_MAP = new HashMap<String, MultipartProgressRequestWrapper>();
	}
	
	public static void putRequest(String id, MultipartProgressRequestWrapper request) {
		loggingId(id);
		REQUEST_MAP.put(id, request);
	}
	
	public static MultipartProgressRequestWrapper getRequest(String id) {
		loggingId(id);
		return REQUEST_MAP.get(id);
	}
	
	public static void removeRequest(String id) {
		loggingId(id);
		REQUEST_MAP.remove(id);
	}

	/**
	 * @param id
	 */
	private static void loggingId(String id) {
		if(log.isDebugEnabled()) {
			log.debug("id:" + id);
		}
	}
	
	public MultipartProgressRequestWrapper(HttpServletRequest arg0) {
		super(arg0);
	}

	private ServletInputStreamWrapper sis = null;

	@Override
	public ServletInputStreamWrapper getInputStream() throws IOException {
		if(sis == null) {
			sis = new ServletInputStreamWrapper(
					new ReadCountInputStream(super.getInputStream()));
		}
		return sis;
	}
	
	public long getReadSize() throws IOException {
		return getInputStream().getReadSize();
	}
	
	private static class ServletInputStreamWrapper extends ServletInputStream {
		private ReadCountInputStream is;

		ServletInputStreamWrapper(ReadCountInputStream is) {
			this.is = is;
		}

		@Override
		public int read() throws IOException {
			return is.read();
		}
		
		long getReadSize() {
			return is.getReadSize();
		}
	}
}
