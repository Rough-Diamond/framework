/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.web.struts.multipart;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * アップロード時の進捗を知らせるためのフィルター
 */
public class MultipartProgressFilter implements Filter {

	public void init(FilterConfig config) throws ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		String uploadId = request.getParameter("uploadId");
		request = getWrappedRequest(uploadId, request);
		try {
			chain.doFilter(request, response);
		} finally {
			MultipartProgressRequestWrapper.removeRequest(uploadId);
		}
	}

	private ServletRequest getWrappedRequest(String uploadId, ServletRequest request) {
		if(uploadId == null) {
			return request;
		}
		if(!(request instanceof HttpServletRequest)) {
			return request;
		}
		HttpServletRequest hrequest = (HttpServletRequest)request;
        if (!"POST".equalsIgnoreCase(hrequest.getMethod())) {
            return (request);
        }
        String contentType = request.getContentType();
        if ((contentType != null) &&
            contentType.startsWith("multipart/form-data")) {
    		MultipartProgressRequestWrapper mprw = new MultipartProgressRequestWrapper(hrequest);
    		MultipartProgressRequestWrapper.putRequest(uploadId, mprw);
    		return mprw;
        } else {
            return (request);
        }
	}

	public void destroy() {
	}

}
