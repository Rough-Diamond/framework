/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.resource;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * リクエストのローケルをローケルコントローラーにセットするフィルター
 */
public class LocaleFilter implements Filter {
    public void init(FilterConfig arg0) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
    	LocaleController controller = LocaleController.getController();
        controller.setLocale(request.getLocale());


        try {
            chain.doFilter(request, response);
        } finally {
            controller.setLocale(Locale.getDefault());
        }
    }

    public void destroy() {
    }
}
