/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.web.struts;

import java.util.Locale;
import java.util.ResourceBundle;

import jp.rough_diamond.commons.resource.ResourceManager;

import org.apache.struts.util.MessageResources;
import org.apache.struts.util.MessageResourcesFactory;

public class ResourceManagerMessageResource extends MessageResources {
	private static final long serialVersionUID = 1L;

	public ResourceManagerMessageResource(MessageResourcesFactory arg0, String arg1) {
		super(arg0, arg1);
	}

	@Override
	public String getMessage(Locale arg0, String arg1) {
		ResourceBundle rb = ResourceManager.getResource(arg0);
		return rb.getString(arg1);
	}
}
