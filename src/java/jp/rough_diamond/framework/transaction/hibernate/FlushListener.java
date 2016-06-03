/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.framework.transaction.hibernate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import jp.rough_diamond.framework.transaction.TransactionManager;

import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.engine.EntityEntry;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.event.AutoFlushEvent;
import org.hibernate.event.AutoFlushEventListener;
import org.hibernate.event.FlushEntityEvent;
import org.hibernate.event.FlushEntityEventListener;
import org.hibernate.event.def.DefaultAutoFlushEventListener;
import org.hibernate.event.def.DefaultFlushEntityEventListener;
import org.hibernate.persister.entity.EntityPersister;

public class FlushListener {
	private final static ThreadLocal<Stack<Boolean>> isFlushing = new ThreadLocal<Stack<Boolean>>(){
		@Override
		protected Stack<Boolean> initialValue() {
			return new Stack<Boolean>();
		}
	};
	
	private final static String UpdateObjectsKey = FlushListener.class + "#updateObjects";
	
	static void notifyUpdateObjects(Object o) {
		isFlushing.get().push(Boolean.TRUE);
		try {
			Map<Object, Object[]> updateObjects = getUpdateObjects();
			SessionImplementor si = (SessionImplementor)HibernateUtils.getSession();
			EntityPersister ep = si.getEntityPersister(null, o);
			updateObjects.put(o, ep.getPropertyValues(o, HibernateUtils.getSession().getEntityMode()));
		} finally {
			isFlushing.get().pop();
		}
	}
	
	@SuppressWarnings("unchecked")
	static Map<Object, Object[]> getUpdateObjects() {
		Map<Object, Object[]> updateObjects = (Map<Object, Object[]>)TransactionManager.getTransactionContext().get(UpdateObjectsKey);
		if(updateObjects == null) {
			updateObjects = new HashMap<Object, Object[]>();
			TransactionManager.getTransactionContext().put(UpdateObjectsKey, updateObjects);
		}
		return updateObjects;
	}
	
	public static boolean isFlushing() {
		return (isFlushing.get().size() != 0);
	}
	
	public static class FlushListenerInner implements FlushEntityEventListener {
		private static final long serialVersionUID = 1L;
		FlushEntityEventListener baseListener;
		public FlushListenerInner() {
			this(new DefaultFlushEntityEventListener());
		}
		public FlushListenerInner(FlushEntityEventListener listener) {
			baseListener = listener;
		}
		@Override
		public void onFlushEntity(FlushEntityEvent event) throws HibernateException {
			isFlushing.get().push(Boolean.TRUE);
			try {
				if(isSkip(event)) {
					return;
				}
				final EntityEntry ee = event.getEntityEntry();
				final Object entity = event.getEntity();
				//FIXME íºê⁄ EntityMode.POJOÇ∆èëÇ¢ÇƒÇ¢ÇÈÇ∆Ç±ÇÎÇâ∫ãLÇÃÇÊÇ§Ç…Ç∑ÇÈ
				final EntityMode mode = event.getSession().getEntityMode();
				final EntityPersister ep = ee.getPersister();
				final Object[] valuesWhenUpdate = getUpdateObjects().get(entity);
				Object[] valuesNow = ep.getPropertyValues(entity, mode);
				boolean isChange = !Arrays.equals(valuesNow, valuesWhenUpdate);
				if(isChange) {
					ep.setPropertyValues(entity, valuesWhenUpdate, mode);
				}
				try {
					baseListener.onFlushEntity(event);
				} finally {
					if(isChange) {
						ep.setPropertyValues(entity, valuesNow, mode);
					}
				}
			} finally {
				isFlushing.get().pop();
			}
		}

		boolean isSkip(FlushEntityEvent event) {
			final Object entity = event.getEntity();
			Map<Object, Object[]> updateObjects = getUpdateObjects();
			return (!updateObjects.containsKey(entity));
		}
	}

	public static class AutoFlushListenerInner implements AutoFlushEventListener {
		private static final long serialVersionUID = 1L;
		AutoFlushEventListener baseListener;
		public AutoFlushListenerInner() {
			this(new DefaultAutoFlushEventListener());
		}
		public AutoFlushListenerInner(AutoFlushEventListener listener) {
			baseListener = listener;
		}
		@Override
		public void onAutoFlush(AutoFlushEvent event) throws HibernateException {
			isFlushing.get().push(Boolean.TRUE);
			try {
				baseListener.onAutoFlush(event);
			} finally {
				isFlushing.get().pop();
			}
		}
	}
}
