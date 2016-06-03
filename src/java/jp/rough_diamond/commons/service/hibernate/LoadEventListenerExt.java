/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.service.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.engine.EntityKey;
import org.hibernate.engine.PersistenceContext;
import org.hibernate.event.LoadEvent;
import org.hibernate.event.def.DefaultLoadEventListener;
import org.hibernate.persister.entity.EntityPersister;

public class LoadEventListenerExt extends DefaultLoadEventListener {
	private static final long serialVersionUID = 1L;

	@Override
	protected Object proxyOrLoad(LoadEvent event, EntityPersister persister,
			EntityKey keyToLoad, LoadType options) throws HibernateException {

		final PersistenceContext persistenceContext = event.getSession().getPersistenceContext();
		persistenceContext.removeProxy(keyToLoad);
		return super.proxyOrLoad(event, persister, keyToLoad, options);
	}
}
