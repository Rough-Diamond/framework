/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.service.hibernate;

import java.util.List;

import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;

import jp.rough_diamond.commons.extractor.Condition;
import jp.rough_diamond.commons.extractor.Extractor;
import jp.rough_diamond.commons.extractor.Property;
import jp.rough_diamond.commons.lang.ArrayUtils;
import jp.rough_diamond.commons.pager.Pager;
import jp.rough_diamond.commons.resource.Message;
import jp.rough_diamond.commons.resource.Messages;
import jp.rough_diamond.commons.resource.MessagesIncludingException;
import jp.rough_diamond.commons.service.BasicService;
import jp.rough_diamond.commons.service.Child;
import jp.rough_diamond.commons.service.Parent;
import jp.rough_diamond.commons.service.RelationalChecker;
import jp.rough_diamond.framework.transaction.VersionUnmuchException;
import jp.rough_diamond.framework.transaction.hibernate.HibernateUtils;

public class HibernateRelationalChecker extends RelationalChecker {
	@Override
	@SuppressWarnings("unchecked")
    public void allowPersist(Object o) throws MessagesIncludingException {
        boolean isFirst = init();
        try {
            Session session = HibernateUtils.getSession();
            Class cl = o.getClass();
            ClassMetadata cm = session.getSessionFactory().getClassMetadata(cl);
            while(cm == null && cl.getSuperclass() != null) {
            	cl = cl.getSuperclass();
            	cm = session.getSessionFactory().getClassMetadata(cl);
            }
            if(cm == null) {
            	return;		//多分このパスに入ることは無いが、Find Bugsが怒るのでとりあえず。。。
            }
            String entityName = cm.getEntityName();
            Messages msgs = new Messages();
            msgs.add(allowUpdateFromChild(entityName, o, (Object)null));
            if(msgs.hasError()) {
                throw new MessagesIncludingException(msgs);
            }
        } finally {
            if(isFirst) {
                loadedEntitySet.set(null);
            }
        }
    }

	@Override
    @SuppressWarnings("unchecked")
    public void allowUpdate(Object o) throws MessagesIncludingException, VersionUnmuchException {
        boolean isFirst = init();
        try {
            Session session = HibernateUtils.getSession();
            Class cl = o.getClass();
            ClassMetadata cm = session.getSessionFactory().getClassMetadata(cl);
            while(cm == null && cl != Object.class) {
            	cl = cl.getSuperclass();
            	cm = session.getSessionFactory().getClassMetadata(cl);
            }
            if(cm == null) {
            	return;		//多分このパスに入ることは無いが、Find Bugsが怒るのでとりあえず。。。
            }
            String entityName = cm.getEntityName();
            Object org = BasicService.getService().findByPK(cl, cm.getIdentifier(o, EntityMode.POJO), true);
            if(org == null) {
                return;
            }
            Messages msgs = new Messages();
            msgs.add(allowUpdateFromParent(entityName, o, org));
            msgs.add(allowUpdateFromChild(entityName, o, org));
            if(msgs.hasError()) {
                throw new MessagesIncludingException(msgs);
            }
        } finally {
            if(isFirst) {
                loadedEntitySet.set(null);
            }
        }
    }

	@Override
	@SuppressWarnings("unchecked")
			protected <T> Class<T> getTargetClassByEntityName(String entityName) {
        SessionFactory factory = HibernateUtils.getSession().getSessionFactory();
        ClassMetadata cm = factory.getClassMetadata(entityName);
        return cm.getMappedClass(EntityMode.POJO);
	}

	@Override
    @SuppressWarnings("unchecked")
    public void allowRemove(Object o) throws MessagesIncludingException, VersionUnmuchException {
        boolean isFirst = init();
        try {
            Session session = HibernateUtils.getSession();
            Class cl = o.getClass();
            ClassMetadata cm = session.getSessionFactory().getClassMetadata(cl);
            String entityName = cm.getEntityName();
            List<Parent> list = parentMap.get(entityName);
            if(list == null || list.size() == 0) {
                return;
            }
            Object org = BasicService.getService().findByPK(cl, cm.getIdentifier(o, EntityMode.POJO), true);
            if(org == null) {
                return;
            }
            Messages msgs = new Messages();
            for(Parent p : list) {
                msgs.add(allowRemove(o, p));
            }
            if(msgs.hasError()) {
                throw new MessagesIncludingException(msgs);
            }
        } finally {
            if(isFirst) {
                loadedEntitySet.set(null);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Messages allowRemove(Object o, Parent p) throws VersionUnmuchException{
        Messages ret = new Messages();
        Object[] keys = getKeys(o, p.getKeys());
        if(ArrayUtils.isEmpty(keys)) {
            return ret;
        }
        SessionFactory factory = HibernateUtils.getSession().getSessionFactory();
        BasicService service = BasicService.getService();
        for(Child c : p.getChildren()) {
            ClassMetadata cm = factory.getClassMetadata(c.getEntityName());
            Extractor extractor = new Extractor(cm.getMappedClass(EntityMode.POJO));
            String[] childKeys = c.getKeys();
            for(int i = 0 ; i < keys.length ; i++) {
                extractor.add(Condition.eq(new Property(childKeys[i]), keys[i]));
            }
            Pager pager = new PagerExt(extractor);
            if(c.getCascadeType() == Child.CascadeType.RESTRICT) {
                if(pager.getSize() > 0) {
                    ret.add("", new Message("errors.relationship.child", getLastName(c.getEntityName())));
                }
            } else {
                List tmp = pager.getCurrentPageCollection();
                for(Object childObj : tmp) {
                    setNull(childObj, childKeys);
                    try {
                        service.update(childObj);
                    } catch(MessagesIncludingException e) {
                        ret.add(e.getMessages());
                        return ret;
                    }
                }
            }
        }
        return ret;
    }
}
