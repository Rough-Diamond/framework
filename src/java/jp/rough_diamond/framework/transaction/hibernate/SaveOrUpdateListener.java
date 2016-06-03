/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.framework.transaction.hibernate;

import java.io.Serializable;

import jp.rough_diamond.commons.service.annotation.Temporary;
import jp.rough_diamond.framework.transaction.TransactionManager;

import org.hibernate.event.SaveOrUpdateEvent;
import org.hibernate.event.SaveOrUpdateEventListener;
import org.hibernate.event.def.DefaultSaveEventListener;
import org.hibernate.event.def.DefaultSaveOrUpdateEventListener;
import org.hibernate.event.def.DefaultUpdateEventListener;

/**
 *
 */
public class SaveOrUpdateListener {
	private final static ThreadLocal<Boolean> isSaveingOrUpdating = new ThreadLocal<Boolean>();
	
	public static boolean isSaveingOrUpdating() {
		return (Boolean.TRUE.equals(isSaveingOrUpdating.get()));
	}

	static void onSaveOrUpdate(SaveOrUpdateEventListener listener, SaveOrUpdateEvent event) {
		isSaveingOrUpdating.set(Boolean.TRUE);
		try {
			listener.onSaveOrUpdate(event);
			Object o = event.getObject();
			//TODO commonÇéQè∆ÇµÇƒÇ¢ÇÈëoï˚å¸éQè∆ÇæÇ©ÇÁÇ»ÇÒÇ∆Ç©ÇµÇΩÇ¢
			if(o instanceof Temporary) {
				TransactionManager.addModifiedTemporaryType(o.getClass());
			}
		} finally {
			isSaveingOrUpdating.remove();
		}
	}
	
	public static class SaveOrUpdateListenerInner implements SaveOrUpdateEventListener {
		private static final long serialVersionUID = 1L;
		SaveOrUpdateEventListener baseListener; 
		public SaveOrUpdateListenerInner() {
			this(new DefaultSaveOrUpdateEventListener());
		}
		public SaveOrUpdateListenerInner(SaveOrUpdateEventListener listener) {
			baseListener = listener;
		}
		@Override
		public void onSaveOrUpdate(SaveOrUpdateEvent event) {
			if(new Dummy().isUpdate(event)) {
				FlushListener.notifyUpdateObjects(event.getObject());
			}
			SaveOrUpdateListener.onSaveOrUpdate(baseListener, event);
		}
		
		static class Dummy extends DefaultSaveOrUpdateEventListener {
			private static final long serialVersionUID = 1L;
			Integer entityState;
			protected boolean isUpdate(SaveOrUpdateEvent event) {
				SaveOrUpdateEvent tmp = new SaveOrUpdateEvent(event.getEntityName(), event.getObject(), event.getSession());
				onSaveOrUpdate(tmp);
				return (Integer.valueOf(PERSISTENT).equals(entityState));
			}
			@Override
			protected Serializable performSaveOrUpdate(SaveOrUpdateEvent event) {
				entityState = getEntityState(
						event.getEntity(),
						event.getEntityName(),
						event.getEntry(),
						event.getSession()
				);
				return null;
			}
		}
	}
	
	public static class SaveListenerInner implements SaveOrUpdateEventListener {
		private static final long serialVersionUID = 1L;
		SaveOrUpdateEventListener baseListener; 
		public SaveListenerInner() {
			this(new DefaultSaveEventListener());
		}
		public SaveListenerInner(SaveOrUpdateEventListener listener) {
			baseListener = listener;
		}
		@Override
		public void onSaveOrUpdate(SaveOrUpdateEvent event) {
			SaveOrUpdateListener.onSaveOrUpdate(baseListener, event);
		}
	}

	public static class UpdateListenerInner implements SaveOrUpdateEventListener {
		private static final long serialVersionUID = 1L;
		SaveOrUpdateEventListener baseListener;
		public UpdateListenerInner() {
			this(new DefaultUpdateEventListener());
		}
		public UpdateListenerInner(SaveOrUpdateEventListener listener) {
			baseListener = listener;
		}
		@Override
		public void onSaveOrUpdate(SaveOrUpdateEvent event) {
			FlushListener.notifyUpdateObjects(event.getObject());
			SaveOrUpdateListener.onSaveOrUpdate(baseListener, event);
		}
	}
}
