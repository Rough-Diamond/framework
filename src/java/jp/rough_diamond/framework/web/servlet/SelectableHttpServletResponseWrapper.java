/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.web.servlet;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * キャッシュ有無を選択できるHttpServletResponseWrapper
 */
public class SelectableHttpServletResponseWrapper extends 	HttpServletResponseWrapper {
	private Map<Boolean, ServletOutputStream> strategy;
	
	public SelectableHttpServletResponseWrapper(HttpServletResponse response) throws IOException {
		super(response);
		strategy = new HashMap<Boolean, ServletOutputStream>();
		strategy.put(Boolean.TRUE, new ServletOutputStreamWrapper(response.getOutputStream()));
		strategy.put(Boolean.FALSE, response.getOutputStream());
		cookies = new ArrayList<Cookie>();
	}

	private boolean 		isResponseCache = false;
	
	/**
	 * キャッシュの必要可否を指定
	 * @param isResponseCache
	 */
	public void setResponseCache(boolean isResponseCache) {
		if(isWriterUsed || isStreamUsed) {
			throw new RuntimeException("既に出力を開始しています。");
		}
		this.isResponseCache = isResponseCache;
	}

	/**
	 * キャッシュファイルのストリームをクローズする
	 */
	public void closeCacheStream() throws IOException {
		if(isResponseCache) {
			ServletOutputStreamWrapper wrapper = 
				(ServletOutputStreamWrapper)strategy.get(Boolean.TRUE);
			wrapper.flush();
			wrapper.os.close();
		}
	}

	public String getCachingFileName() {
		try {
			if(isResponseCache) {
				return ((ServletOutputStreamWrapper)strategy.get(Boolean.TRUE)).f.getCanonicalPath();
			} else {
				throw new RuntimeException("キャッシュしていません。");
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private boolean isWriterUsed;
	
	private PrintWriter pw = null;
	@Override
	public PrintWriter getWriter() throws IOException {
		if(pw == null) {
			if(isStreamUsed) {
				throw new IllegalStateException("already OutputStream used.");
			}
			pw = new PrintWriter(new OutputStreamWriter(getOutputStream(), getCharacterEncoding()));
		}
		isWriterUsed = true;
		return pw;
	}

	private boolean isStreamUsed = false;
	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if(isWriterUsed) {
			throw new IllegalStateException("already Writer used.");
		}
		isStreamUsed = true;
		return strategy.get(isResponseCache);
	}

	private List<Cookie>	cookies;
	@Override
	public void addCookie(Cookie cookie) {
		super.addCookie(cookie);
		cookies.add(cookie);
	}
	
	public List<Cookie> getCookies() {
		return cookies;
	}
	
	private static class ServletOutputStreamWrapper extends ServletOutputStream {
		private ServletOutputStream 	sos;
		private Initializer				initializer;
		private File 					f;
		private OutputStream			os;

		public ServletOutputStreamWrapper(ServletOutputStream outputStream) throws IOException {
			this.sos = outputStream;
			initializer = new DefaultInitializer();
		}

		@Override
		public void write(int b) throws IOException {
			initializer.init();
			sos.write(b);
			os.write(b);
		}

		@Override
		public void close() throws IOException {
			initializer.init();
			sos.close();
			os.close();
		}

		@Override
		public void flush() throws IOException {
			initializer.init();
			sos.flush();
			os.flush();
		}
		
		private interface Initializer {
			public void init() throws IOException;
		}
		
		private static class NonInitializer implements Initializer {
			public void init() throws IOException {
				//空実装
			}
		}
		
		private class DefaultInitializer implements Initializer {
			public void init() throws IOException {
				if(initializer == this) {
					init2();
				}
			}
			
			private synchronized void init2() throws IOException {
				if(initializer == this) {
					f = File.createTempFile("cacheResponse-", ".dat");
					f.deleteOnExit();
					os = new BufferedOutputStream(new FileOutputStream(f));
					initializer = new NonInitializer();
				}
			}
		}
	}
}
