/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.service;

import java.util.List;

/**
 * ServiceFinderÇÃChain of Responsibility
 * (é¿ëïÇÕChain of ResponsibilityÉpÉ^Å[ÉìÇ≈ÇÕÇ†ÇËÇ‹ÇπÇÒ)
 */
public class ServiceFinderChain implements ServiceFinder {
	private List<ServiceFinder> serviceFinderChain;
	public ServiceFinderChain(List<ServiceFinder> serviceFinderChain) {
		this.serviceFinderChain = serviceFinderChain;
	}
	
	public <T extends Service> T getService(Class<T> cl, Class<? extends T> defaultClass) {
		for(ServiceFinder serviceFinder : serviceFinderChain) {
			T ret = serviceFinder.getService(cl, defaultClass);
			if(ret != null) {
				return ret;
			}
		}
		return null;
	}
}
