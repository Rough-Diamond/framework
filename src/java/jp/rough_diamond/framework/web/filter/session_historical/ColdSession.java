/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.web.filter.session_historical;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

@SuppressWarnings("unchecked")
class ColdSession implements Serializable {
	private static final long serialVersionUID = 1L;
	public ColdSession() {
    }

    static ColdSession freezeSession(
            HttpSession session, ServletContext context) {
        ColdSession ret = new ColdSession();
        ret.id = session.getId();
        Map map = new HashMap();
        Enumeration en = session.getAttributeNames();
        while(en.hasMoreElements()) {
            String name = (String)en.nextElement();
            Object o = session.getAttribute(name);
            if(o instanceof Serializable) {
                map.put(name, o);
            } else {
                context.log(name + " is not implements Serializable. skip value");
            }
        }
        ret.map = map;
        return ret;
    }

    public String getId() {
        return id;
    }

    public Map getSessionMap() {
        return map;
    }

    private String  id;
    private Map     map;
}