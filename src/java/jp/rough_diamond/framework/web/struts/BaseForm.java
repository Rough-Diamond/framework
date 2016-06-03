/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.web.struts;

import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.struts.action.ActionForm;
import javax.servlet.http.HttpServletRequest;

import jp.rough_diamond.commons.resource.Messages;

import org.apache.struts.action.ActionMapping;

/**
 * ‰½‚©—L‚Á‚½Žž—p
 */
abstract public class BaseForm extends ActionForm {
	private static final long serialVersionUID = 1L;
	transient ThreadLocal<Messages> mgs = new ThreadLocalMessages();
	transient ThreadLocal<Messages> errs = new ThreadLocalMessages();
    private static class ThreadLocalMessages extends ThreadLocal<Messages> {
    	@Override
    	protected Messages initialValue() {
    		return new Messages();
    	}
    }
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    	in.defaultReadObject();
    	mgs = new ThreadLocalMessages();
    	errs = new ThreadLocalMessages();
    }
    
    public Messages getMessage(){
        return this.mgs.get();
    }

    protected void setMessage(Messages mgs) {
        this.mgs.set(mgs);
    }

    public Messages getErrors(){
        return this.errs.get();
    }

    protected void setErrors(Messages mgs) {
        this.errs.set(mgs);
    }

    @Override
    public void reset(ActionMapping map, HttpServletRequest req){
        setMessage(new Messages());
        setErrors(new Messages());
    }
}
