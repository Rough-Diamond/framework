/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.web.struts;

import org.apache.struts.util.MessageResources;
import org.apache.struts.util.MessageResourcesFactory;

public class ResourceManagerMessageResourceFactory extends
		MessageResourcesFactory {
	private static final long serialVersionUID = 1L;

	@Override
	public MessageResources createResources(String arg0) {
		return new ResourceManagerMessageResource(this, arg0);
	}
}
