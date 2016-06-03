/*
 * ====================================================================
 * 
 *  Copyright 2007 Eiji Yamane(yamane@super-gs.jp)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 */
package jp.rough_diamond.framework.web.struts;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.rough_diamond.commons.di.DIContainerFactory;
import jp.rough_diamond.commons.resource.Messages;
import jp.rough_diamond.framework.user.RoleJudge;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

/**
 * 何か有った時用
 * @author e-yamane
 */
abstract public class BaseAction extends DispatchAction {
    private final static Log log = LogFactory.getLog(BaseAction.class);

    @Override
    public ActionForward execute(ActionMapping arg0, ActionForm arg1, HttpServletRequest arg2, HttpServletResponse arg3) throws Exception {
        try {
            return super.execute(arg0, arg1, arg2, arg3);
        } catch(Error e) {
            //Errorはstruts例外機構に組み込まれないので。。。
            throw new RuntimeException(e);
        }
    }

    protected ActionForward hasRole(ActionForm form, HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) throws ServletException {
    	//この時点で渡されるformはまだpopulateされていない可能性があるので注意すること
    	try {
	        String parameter = mapping.getParameter();
	        if (parameter == null) {
	            String message = messages.getMessage("dispatch.handler", mapping.getPath());
	            log.error(message);
	            throw new ServletException(message);
	        }
	        String name = getMethodName(mapping, form, request, response, parameter);
	        Method method = getMethod(name);
	        return hasRole(method) ? null : mapping.findForward("forbiddenAccess");
    	} catch(Exception e) {
    		throw new ServletException(e);
    	}
    }
    
    @Override
    protected String getMethodName(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response,
            String parameter) throws Exception {
    	String ret = super.getMethodName(mapping, form, request, response, parameter);
    	return (ret == null) ? "unspecified" : ret;
    }
    
    //Struts 1.2.8のメソッドコピー
    @Override
    protected ActionForward dispatchMethod(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response,
            String name) throws Exception {
        Method method = null;
        try {
            method = getMethod(name);
            NoCache noCache = method.getAnnotation(NoCache.class);
            if(noCache != null) {
                log.debug("キャッシュ無効リクエストです。");
                response.addHeader("Pragma", "no-cache");
                response.addHeader("Cache-Control", "no-cache");
                response.addHeader("Expires", "-1");
            }
            ContentType ct = method.getAnnotation(ContentType.class);
            if(ct != null) {
            	if(log.isDebugEnabled()) {
            		log.debug("ContentType:" + ct.value());
            	}
            	response.setContentType(ct.value());
            }
            //↑追加
        } catch(NoSuchMethodException e) {
            String message =
                    messages.getMessage("dispatch.method", mapping.getPath(), name);
            log.error(message, e);
            throw e;
        }

        ActionForward forward = null;
        try {
            Object args[] = {mapping, form, request, response};
            forward = (ActionForward) method.invoke(this, args);
            if(form instanceof BaseForm) {
            	BaseForm baseForm = (BaseForm)form;
            	Messages msg;
            	msg = baseForm.getMessage();
                if(msg.hasError()) {
                    saveMessages(request, MessagesTranslator.translate(msg));
                }
            	msg = baseForm.getErrors();
                if(msg.hasError()) {
                    saveErrors(request, MessagesTranslator.translate(msg));
                }
            }
        } catch(ClassCastException e) {
            String message =
                    messages.getMessage("dispatch.return", mapping.getPath(), name);
            log.error(message, e);
            throw e;

        } catch(IllegalAccessException e) {
            String message =
                    messages.getMessage("dispatch.error", mapping.getPath(), name);
            log.error(message, e);
            throw e;

        } catch(InvocationTargetException e) {
            // Rethrow the target exception if possible so that the
            // exception handling machinery can deal with it
            Throwable t = e.getTargetException();
            if (t instanceof Exception) {
                throw ((Exception) t);
            } else {
                String message =
                        messages.getMessage("dispatch.error", mapping.getPath(), name);
                log.error(message, e);
                throw new ServletException(t);
            }
        }

        // Return the returned ActionForward instance
        return (forward);
    }

	private boolean hasRole(Method method) {
		AllowRole classRole = this.getClass().getAnnotation(AllowRole.class);
		AllowRole methodRole = method.getAnnotation(AllowRole.class);
		if(methodRole != null) {
			return hasRole(methodRole);
		} else {
			return hasRole(classRole);
		}
	}

	private boolean hasRole(AllowRole allowRole) {
		if(allowRole == null) {
			log.warn("AllowRole NOT SPECIFIED!!");
			return false;
		}
		if(allowRole.isAllAccess()) {
			return true;
		} else {
			RoleJudge judge = (RoleJudge)DIContainerFactory.getDIContainer().getObject("roleJudge");
			return judge.hasRole(allowRole.allowedRoles());
		}
	}
}
