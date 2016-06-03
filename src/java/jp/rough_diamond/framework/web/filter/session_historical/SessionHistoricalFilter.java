/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.web.filter.session_historical;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jp.rough_diamond.commons.io.IOUtils;
import jp.rough_diamond.commons.util.AutoRemoveHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * �u���E�U�Ńo�b�N�������ꂽ�ꍇ�ɁA�Z�b�V����������ނ����邽�߂�Filter<br />
 * �{�t�B���^�́ASessionHistricalId�^�O�ƕ����ė��p���邱��
 * �Z�b�V�������̉i�����́Ainit�p�����[�^�́ustorageDir�v�Ɏw�肳�ꂽ�f�B���N�g���z����
 * �i�[����B
**/
public class SessionHistoricalFilter implements Filter {
	private final static Log log = LogFactory.getLog(SessionHistoricalFilter.class);
	
    /**
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
    **/
    public void init(FilterConfig config) {
        this.config = config;
        init2(config);
    }

    @SuppressWarnings("unchecked")
    static void init2(FilterConfig config) {
        String fileName = config.getInitParameter("storageDir");
        if(fileName != null) {
            config.getServletContext().log("sessionStorageDir:" + fileName);
            sessionStorageDir = new File(fileName);
        }
        ran = new Random();
        unserializedSessionMap = new AutoRemoveHashMap(24L * 60L * 60L * 1000L);
    }

    /**
     * @see javax.servlet.Filter#destroy()
    **/
    public void destroy() { }

    /**
     * �Z�b�V��������t�B���^�[<br />
     * �uhistricalId�v���p�����^�Ɏw�肳��Ă���΁A
     * �uhistricalId�v�ɑΉ�����Z�b�V�������i���X�g���[�W���畜�����A
     * ���݂̃Z�b�V�������ƒu��������B<br />
     * �܂��A�uhistrialId�v���Ō�ɃA�N�Z�X���ꂽ�ۂ̒l�ƈ�v���Ă���ꍇ�́A
     * �Z�b�V��������V���ɉi��������B
    **/
    public void doFilter(ServletRequest request,
                ServletResponse response, FilterChain chain)
                        throws java.io.IOException, ServletException {
        if(request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest)request;
            String id = "" + System.currentTimeMillis() + "" + ran.nextInt();
            HttpSession session = httpRequest.getSession(false);
            if(session != null) {
                rebuildSession(httpRequest);
                session.setAttribute("lastHistricalSessionId", id);
            }
            chain.doFilter(request, response);
            session = httpRequest.getSession(false);
            if(session != null) {
                storeSession(session, id);
//            } else {
//                session.removeAttribute("lastHistricalSessionId");
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @SuppressWarnings("unchecked")
    void rebuildSession(HttpServletRequest request) throws IOException {
        HttpSession session = request.getSession(false);
        synchronized(session) {
            String lastHistrialId = (String)session.getAttribute("lastHistricalSessionId");
            if(lastHistrialId == null) {
                lastHistrialId = "";
            }
            String currentHistrialId = request.getParameter("histricalId");
            if(currentHistrialId == null) {
                currentHistrialId = "";
            }
            if("".equals(currentHistrialId) ||
               lastHistrialId.equals(currentHistrialId)) {
/* �������݃��W�b�N
                session.removeAttribute("lastHistricalSessionId");
                ColdSession coldSession = ColdSession.freezeSession(
                                    session, config.getServletContext());
                lastHistrialId = storeSession(session, coldSession);
                session.setAttribute("lastHistricalSessionId", lastHistrialId);
*/
            } else {
                ColdSession coldSession = loadSession(session, currentHistrialId);
                if(coldSession == null) {
                    return;
                }
                String coldSessionId = coldSession.getId();
                String sessionId = session.getId();
                if(!coldSessionId.equals(sessionId)) {
                    //�^�C���A�E�g�������̓X�k�[�s���O
                    session.invalidate();
                    return;
                }
                List list = new ArrayList();
                Enumeration en = session.getAttributeNames();
                while(en.hasMoreElements()) {
                    list.add(en.nextElement());
                }
                Iterator iterator = list.iterator();
                while(iterator.hasNext()) {
                    String name = (String)iterator.next();
                    session.removeAttribute(name);
                }
                iterator = coldSession.getSessionMap().entrySet().iterator();
                while(iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry)iterator.next();
                    session.setAttribute(
                            (String)entry.getKey(), entry.getValue());
                }
            }
        }
    }

    private void storeSession(HttpSession session,
                        String id) throws IOException {
        session.removeAttribute("lastHistricalSessionId");
        ColdSession coldSession = ColdSession.freezeSession(
                            session, config.getServletContext());
        storeSession(session, coldSession, id);
        session.setAttribute("lastHistricalSessionId", id);
    }

    File getSessionStorage(HttpSession session, String id)
                                                throws IOException {
        File dir = getSessionStorageDir(session);
        File file = new File(dir, id + ".ser");
        file.deleteOnExit();
        return file;
    }

    static void removeTimeoutSessionHistorical(HttpSession session) throws IOException {
        File dir = getSessionStorageDir(session);
        IOUtils.deleteDir(dir);
    }

    static File getSessionStorageDir(HttpSession session) throws IOException {
        String sessionId = session.getId();
        File dir = (sessionStorageDir == null)
                ? new File(sessionId)
                : new File(sessionStorageDir, sessionId);
        dir.deleteOnExit();
        if(dir.exists()) {
            if(dir.isFile()) {
                throw new IOException(dir.getCanonicalPath() + "is file.");
            }
        } else {
            boolean b = dir.mkdirs();
			if(b){}						//Find Bugs���߂�l���̂Ă�
        }
        return dir;
    }

    @SuppressWarnings("unchecked")
    private void storeSession(HttpSession session,
                ColdSession coldSession, String id) throws IOException {
        File file = getSessionStorage(session, id);
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        try {
            oos.writeObject(coldSession);
        } catch(NotSerializableException nse) {
            log.debug("Serialize�s�\�ȃI�u�W�F�N�g�����݂��܂��B��������Ɋm�ۂ��܂��B�����͎c���܂���B");
            String sessionId = session.getId();
            Map map = (Map)unserializedSessionMap.get(sessionId);
            if(map == null) {
                map = new AutoRemoveHashMap(session.getMaxInactiveInterval() * 1000L);
                unserializedSessionMap.put(sessionId, map);
            }
            map.put(id, coldSession);
        } finally {
            oos.close();
        }
    }

    @SuppressWarnings("unchecked")
    private ColdSession loadSession(HttpSession session,
            String sessionHistrialId) throws IOException {
        ColdSession ret = null;
        try {
            String sessionId = session.getId();
            Map map = (Map)unserializedSessionMap.get(sessionId);
            if(map != null) {
                ret = (ColdSession)map.get(sessionHistrialId);
                if(ret != null) {
                    return ret;
                }
            }
            File file = getSessionStorage(session, sessionHistrialId);
            if(file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis);
                ObjectInputStream ois = new ObjectInputStream(bis);
                ret = (ColdSession)ois.readObject();
                ois.close();
                return ret;
            } else {
                return null;
            }
        } catch(ClassNotFoundException cnfe) {
            config.getServletContext().log("session loading fail.", cnfe);
            cnfe.printStackTrace();
            throw new IOException(cnfe.getMessage());
        }
    }

    private FilterConfig    config;

    private static Random   ran;
    private static File     sessionStorageDir;
    @SuppressWarnings("unchecked")
    private static Map      unserializedSessionMap;

}