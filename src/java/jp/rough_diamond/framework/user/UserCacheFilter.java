/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.user;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * セッションにセットされているユーザーオブジェクトをユーザー管理オブジェクトにセットする。
 * 併せて、ユーザー情報がセッション内の情報と一致しない場合にセッションをクリアする
 * web.xmlには以下のように、ユーザーオブジェクトのセッション名（必須）、
 * クリアするオブジェクトのプレフィックス（任意）を指定することが可能である。
 * クリアするオブジェクトのプレフィックスを省略した場合はセッションのクリーンアップは行わない
 *   <init-param>
 *     <param-name>sessionName</param-name>
 *     <param-value>user</param-value>
 *   </init-param>
 *   <init-param>
 *     <param-name>clearObjectPrefix</param-name>
 *     <param-value>jp.rough_diamond</param-value>
 *   </init-param>
 */
public class UserCacheFilter implements Filter {
    private final static Log log = LogFactory.getLog(UserCacheFilter.class);

    private String userSessionName;
    private String clearObjectPrefix;
    
    private AbstractUserChangeListener listener;
    private FilterConfig config;
    
    public void init(FilterConfig arg0) throws ServletException {
        this.config = arg0;
        
        String sessionFixationSheild = this.config.getInitParameter("sessionFixationSheild");
		listener = (sessionFixationSheild == null) ? new NormalUserChangeListener() : new SessionFixationSheildListener();

		UserController controller = UserController.getController();
        controller.addListener(listener);
        
        userSessionName = config.getInitParameter("sessionName");
        if(userSessionName == null) {
        	throw new ServletException("sessionName isn't specification.");
        }
        clearObjectPrefix = config.getInitParameter("clearObjectPrefix");
    }

    public void doFilter(ServletRequest req, ServletResponse res,
            FilterChain chain) throws IOException, ServletException {
        log.debug("リクエストを受けました。");
        HttpServletRequest request = (HttpServletRequest)req;
        listener.tl.set(request);
        HttpSession session = request.getSession(false);
        User user = null;
        if(session != null) {
            user = (User)session.getAttribute(userSessionName);
            if(user != null) {
            	initUser(user, request);
            }
        }
        UserController.getController().setUser(user);
        chain.doFilter(req, res);
    }

    public void destroy() {
        UserController controller = UserController.getController();
        controller.removeListener(listener);
    }

    /**
     * ユーザーオブジェクトに何らかの処理を施す場合には本メソッドをオーバーライドすること
     * @param user
     * @param request
     */
    protected void initUser(Object user, HttpServletRequest request) {
    	
    }
    
    abstract private class AbstractUserChangeListener implements UserChangeListener {
        protected final ThreadLocal<HttpServletRequest> tl = new ThreadLocal<HttpServletRequest>();
		@Override
		public void notify(Object oldUser, Object newUser) {
            log.debug("ユーザー変更要求です。");
            HttpServletRequest request = tl.get();
            if(request == null) {
                log.debug("リクエストが渡ってないのでセットしません");
                return;
            }
            HttpSession session = request.getSession(false);
            if(newUser == null && session != null) {
                log.debug("セッションをクリアします。");
                clearSession();
                return;
            } else if(newUser != null) {
            	session = request.getSession();
                Object user = session.getAttribute(userSessionName);
                if(user == null || !user.equals(newUser)) {
                    clearSession();
                	session = request.getSession();
                    log.debug("セッションをセットします。");
                    session.setAttribute(userSessionName, newUser);
                } else {
                    log.debug("セッション的に変化は無いのでセッションのクリアは行いません");
                    session.setAttribute(userSessionName, newUser);
                }
            }
		}
		
		abstract protected void clearSession();
    }
    
    private final class NormalUserChangeListener extends AbstractUserChangeListener {
		@Override
		protected void clearSession() {
			HttpSession session = tl.get().getSession();
			UserCacheFilter.this.clearSession(session);
		}
    }
    
    private final class SessionFixationSheildListener extends AbstractUserChangeListener {
		@Override
		protected void clearSession() {
			HttpSession session = tl.get().getSession();
			session.invalidate();
		}
    }
    
/*
    	private ThreadLocal<HttpServletRequest> tl = new ThreadLocal<HttpServletRequest>();

        public void notify(Object oldUser, Object newUser) {
            log.debug("ユーザー変更要求です。");
            HttpServletRequest request = tl.get();
            if(request == null) {
                log.debug("リクエストが渡ってないのでセットしません");
                return;
            }
            HttpSession session = request.getSession(false);
            if(newUser == null && session != null) {
                log.debug("セッションをクリアします。");
                clearSession(session);
            } else if(newUser != null) {
                session = (session == null) ? request.getSession() : session;
                Object user = session.getAttribute(userSessionName);
                if(user == null || !user.equals(newUser)) {
                    clearSession(session);
                    log.debug("セッションをセットします。");
                    session.setAttribute(userSessionName, newUser);
                } else {
                    log.debug("セッション的に変化は無いのでセッションのクリアは行いません");
                    session.setAttribute(userSessionName, newUser);
                }
            }
        }
    }
*/
    
    @SuppressWarnings("unchecked")
    private void clearSession(HttpSession session) {
    	if(clearObjectPrefix == null) {
    		return;
    	}
        Enumeration en = session.getAttributeNames();
        while(en.hasMoreElements()) {
            String name = (String)en.nextElement();
            Object o = session.getAttribute(name);
            if(o.getClass().getName().startsWith(clearObjectPrefix)) {
                session.removeAttribute(name);
            }
        }
    }
}
