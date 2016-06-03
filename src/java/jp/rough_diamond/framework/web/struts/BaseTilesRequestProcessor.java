/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.web.struts;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.rough_diamond.commons.util.ClassLoaderIgnoreablePropertyUtilsBean;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.tiles.TilesRequestProcessor;

public class BaseTilesRequestProcessor extends TilesRequestProcessor {
	private final static Log log = LogFactory.getLog(BaseTilesRequestProcessor.class);

	ThreadLocal<ServletException> populateException = new ThreadLocal<ServletException>();
	ThreadLocal<Boolean> populated = new ThreadLocal<Boolean>();
	
	private final ClassLoaderIgnoreablePropertyUtilsBean propertyUtilsBean = new ClassLoaderIgnoreablePropertyUtilsBean(Boolean.TRUE);
	
	@Override
	public void init(ActionServlet servlet, ModuleConfig moduleConfig) throws ServletException {
		super.init(servlet, moduleConfig);
		boolean isClassloaderPopulate = Boolean.valueOf(servlet.getInitParameter("allowClassLoaderPopulate"));
		log.debug(isClassloaderPopulate);
		if(!isClassloaderPopulate) {
			BeanUtilsBean.setInstance(new BeanUtilsBean(new ConvertUtilsBean(), propertyUtilsBean));
		}
	}

	@Override
    public void process(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {
		populateException.remove();
		populated.set(Boolean.FALSE);
		super.process(request, response);
    }
	
	@Override
    protected void processPopulate(HttpServletRequest request,
            HttpServletResponse response,
            ActionForm form,
            ActionMapping mapping) throws ServletException {
		if(populated.get()) {
			if(populateException.get() != null) {
				throw populateException.get();
			}
		} else {
			Boolean org = propertyUtilsBean.setClassLoaderPopulate(Boolean.FALSE);
			try {
				super.processPopulate(request, response, form, mapping);
			} finally {
				propertyUtilsBean.setClassLoaderPopulate(org);
				populated.set(Boolean.TRUE);
			}
		}
    }

	@Override
	protected void doForward(String arg0, HttpServletRequest arg1, HttpServletResponse arg2) throws IOException, ServletException {
		log.debug(">>doForward");
		if(!VelocityViewer.doVelocity(arg0, getServletContext(), arg1, arg2)) {
			super.doForward(arg0, arg1, arg2);
		}
	}

	@Override
    protected boolean processRoles(HttpServletRequest request,
            HttpServletResponse response,
            ActionMapping mapping) throws IOException, ServletException {
        Action action = processActionCreate(request, response, mapping);
        if (action == null) {
            return super.processRoles(request, response, mapping);
        }
        if(!(action instanceof BaseAction)) {
            return super.processRoles(request, response, mapping);
        }
        ActionForm form = processActionForm(request, response, mapping);
        try {
        	processPopulate(request, response, form, mapping);
        } catch(ServletException e) {
        	populateException.set(e);
        }
        BaseAction baseAction = (BaseAction)action;
        ActionForward forward = baseAction.hasRole(form, request, response, mapping);
        if(forward != null) {
            processForwardConfig(request, response, forward);
            return false;
        }
		return super.processRoles(request, response, mapping);
	}	

	@Override
    protected ActionForward processActionPerform(
            HttpServletRequest request, 
            HttpServletResponse response, 
            Action action, 
            ActionForm form, 
            ActionMapping mapping) throws IOException, ServletException {

		if(log.isInfoEnabled()) {
			log.info("processActionPerform:[" + action.getClass() + "|" + request.getParameter("command") + "]");
		}
        request.setAttribute("currentForm", form);
        request.setAttribute("currentFormName", mapping.getName());
        request.setAttribute("currentPath", mapping.getPath());
        return super.processActionPerform(request, response, action, form, mapping);
    }

	@Override
	public void initDefinitionsMapping() throws ServletException {
		//アクセス記述子のみの変更
		super.initDefinitionsMapping();
	}
}
