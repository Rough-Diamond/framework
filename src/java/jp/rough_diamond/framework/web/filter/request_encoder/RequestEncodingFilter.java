/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.web.filter.request_encoder;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * HTTP���N�G�X�g�f�[�^�̃G���R�[�h���s��Filter<br />
 * �܂��A�G���R�[�h��ʂ�ύX����ꍇ�́AWeb�A�v���P�[�V�����̍ċN�����K�v�ł���B
 * @precondition web.xml�ɁAencoding��񂪎w�肳��Ă��邱�ƁB<br />
 * ��F<blockquote><pre>
 *  <filter>
 *    <filter-name>JapaneaseEncoder</filter-name>
 *    <filter-class>
 *      com.nec.jp.g1.util.j2ee.filter.RequestEncodingFilter
 *    </filter-class>
 *    <init-param>
 *      <param-name>encoding</param-name>
 *      <param-value>Shift_JIS</param-value>
 *    </init-param>
 *  </filter></pre></blockquote>
**/
public class RequestEncodingFilter implements Filter {
    public final static Log log = LogFactory.getLog(RequestEncodingFilter.class); 

    /**
     * �t�B���^�[�j��
     * @see javax.servlet.Filter#destroy()
    **/
    public void destroy() {
        encoding = null;
    }
    
    /**
     * �t�B���^���s
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
    **/
    public void doFilter(ServletRequest request, 
                ServletResponse response, FilterChain chain) 
                                    throws IOException, ServletException {
        request.setCharacterEncoding(encoding);
        chain.doFilter(request, response);
    }
    
    /**
     * �t�B���^������
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
    **/
    public void init(FilterConfig config) throws ServletException {
        encoding = config.getInitParameter("encoding");
        if(encoding == null) {
            throw new ServletException("don't specfic 'encoding'.");
        }
        if(log.isInfoEnabled()) {
        	log.info("encoding:" + encoding);
        }
    }
    
    private String      encoding;
}