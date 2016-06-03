/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.rough_diamond.commons.di.DIContainerFactory;
import jp.rough_diamond.commons.extractor.Condition;
import jp.rough_diamond.commons.extractor.Extractor;
import jp.rough_diamond.commons.extractor.Property;
import jp.rough_diamond.commons.lang.ArrayUtils;
import jp.rough_diamond.commons.pager.AbstractNonCachePager;
import jp.rough_diamond.commons.pager.Pager;
import jp.rough_diamond.commons.resource.Message;
import jp.rough_diamond.commons.resource.Messages;
import jp.rough_diamond.commons.resource.MessagesIncludingException;
import jp.rough_diamond.framework.transaction.VersionUnmuchException;

import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * 外部テーブルの値のチェックを行うクラス
 */
abstract public class RelationalChecker {
    List<Parent> list;
    protected Map<String, List<Parent>> parentMap;
    Map<String, List<Child>> childrenMap;

    @SuppressWarnings("unchecked")
    protected ThreadLocal<Set> loadedEntitySet = new ThreadLocal<Set>();

    private static RelationalChecker checker;
    private static RelationalChecker nullObject;

    static {
    	nullObject = new RelationalChecker(){
			@Override
			public void allowPersist(Object o) {
			}
			@Override
			public void allowUpdate(Object o) {
			}
			@Override
			protected <T> Class<T> getTargetClassByEntityName(String entityName) {
				return null;
			}
			@Override
			public void allowRemove(Object o) {
			}
    	};
    }
    
    public static RelationalChecker getRerationalChecker() {
        if(checker == null) {
            checker = (RelationalChecker)DIContainerFactory.getDIContainer().getObject("rerationalChecker");
            if(checker == null) {
                checker = nullObject;
            }
        }
        return checker;
    }
    static void setRelationalChecker(RelationalChecker checker) {
        RelationalChecker.checker = checker;
    }

    abstract public void allowPersist(Object o) throws MessagesIncludingException;
    abstract public void allowUpdate(Object o) throws MessagesIncludingException, VersionUnmuchException;

    protected Messages allowUpdateFromChild(String entityName, Object target, Object old) {
        Messages ret = new Messages();
        List<Child> list = childrenMap.get(entityName);
        if(list == null || list.size() == 0) {
            return ret;
        }
        for(Child p : list) {
            ret.add(allowUpdateFromChild(target, old, p));
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    private Messages allowUpdateFromChild(Object target, Object old, Child child) {
        Messages ret = new Messages();
        Parent parent = child.getParent();
        if(loadedEntitySet.get().contains(parent)) {
            return ret;
        }
        Object[] newKeys = getKeys(target, child.getKeys());
        Object[] oldKeys;
        if(old == null) {
            oldKeys = new Object[0];
        } else {
            oldKeys = getKeys(old, child.getKeys());
        }
        if(Arrays.equals(newKeys, oldKeys)) {
            return ret;
        }
        if(ArrayUtils.isEmpty(newKeys)) {
            return ret;
        }
        BasicService service = BasicService.getService();
        loadedEntitySet.get().add(child);
        try {
            Extractor extractor = new Extractor(getTargetClassByEntityName(parent.getEntityName()));
            String[] parentKeys = parent.getKeys();
            for(int i = 0 ; i < newKeys.length ; i++) {
                if(newKeys[i] == null) {
                    extractor.add(Condition.isNull(new Property(parentKeys[i])));
                } else {
                    extractor.add(Condition.eq(new Property(parentKeys[i]), newKeys[i]));
                }
            }
            List list = service.findByExtractor(extractor, true);
            if(list.size() == 0) {
                ret.add("", new Message("errors.relationship.parent", getLastName(parent.getEntityName())));
            }
            return ret;
        } finally {
            loadedEntitySet.get().remove(child);
        }
    }

	abstract protected <T> Class<T> getTargetClassByEntityName(String entityName); 
    
    protected Messages allowUpdateFromParent(String entityName, Object target, Object old) throws VersionUnmuchException {
        Messages ret = new Messages();
        List<Parent> list = parentMap.get(entityName);
        if(list == null || list.size() == 0) {
            return ret;
        }
        for(Parent p : list) {
            ret.add(allowUpdateFromParent(target, old, p));
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    private Messages allowUpdateFromParent(Object target, Object old, Parent parent) throws VersionUnmuchException {
        Messages ret = new Messages();
        Object[] newKeys = getKeys(target, parent.getKeys());
        Object[] oldKeys = getKeys(old, parent.getKeys());
        if(Arrays.equals(newKeys, oldKeys)) {
            return ret;
        }
        BasicService service = BasicService.getService();
        loadedEntitySet.get().add(parent);
        try {
            for(Child c : parent.getChildren()) {
                if(loadedEntitySet.get().contains((c))) {
                    continue;
                }
                Extractor extractor = new Extractor(getTargetClassByEntityName(c.getEntityName()));
                String[] childKeys = c.getKeys();
                for(int i = 0 ; i < oldKeys.length ; i++) {
                    extractor.add(Condition.eq(new Property(childKeys[i]), oldKeys[i]));
                }
                Pager pager = new PagerExt(extractor);
                if(c.getCascadeType() == Child.CascadeType.RESTRICT) {
                    if(pager.getSize() > 0) {
                        ret.add("", new Message("errors.relationship.child", getLastName(c.getEntityName())));
                    }
                } else {
                    List tmp = pager.getCurrentPageCollection();
                    for(Object childObj : tmp) {
                        setValues(childObj, childKeys, newKeys);
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
        } finally {
            loadedEntitySet.get().remove(parent);
        }
    }

    abstract public void allowRemove(Object o) throws MessagesIncludingException, VersionUnmuchException;

    protected static String getLastName(String entityName) {
        int index = entityName.lastIndexOf(".");
        return (index == -1) ? entityName : entityName.substring(index + 1);
    }

    protected static void setValues(Object target, String[] keys, Object[] values) {
        try {
            for(int i = 0 ; i < keys.length ; i++) {
                PropertyUtils.setProperty(target, keys[i], values[i]);
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected static void setNull(Object target, String[] keys) {
        try {
            for(String key : keys) {
                PropertyUtils.setProperty(target, key, null);
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    protected static Object[] getKeys(Object target, String[] keys) {
        List ret = new ArrayList();
        for(String key : keys) {
            Object value = getValue(target, key);
            ret.add(value);
        }
        return ret.toArray(new Object[ret.size()]);
    }

    protected static Object getValue(Object target, String key) {
        try {
            if(key.startsWith("\"") &&
                    key.endsWith("\"")) {
                return key.split("\"")[1];
            } else {
                try {
                    return PropertyUtils.getNestedProperty(target, key);
                } catch(NestedNullException e) {
                    return null;
                }
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setParents(List<Parent> list) {
        this.list = list;
    }

    private boolean isInit = false;
    @SuppressWarnings("unchecked")
    protected synchronized boolean init() {
        if(!isInit) {
            init2();
        }
        if(loadedEntitySet.get() == null) {
            loadedEntitySet.set(new HashSet());
            return true;
        } else {
            return false;
        }
    }

    private void init2() {
        parentMap = new HashMap<String, List<Parent>>();
        childrenMap = new HashMap<String, List<Child>>();
        if(list == null) {
            return;
        }
        for(Parent parent : list) {
            String entityName = parent.getEntityName();
            List<Parent> list = parentMap.get(entityName);
            if(list == null) {
                list = new ArrayList<Parent>();
                parentMap.put(entityName, list);
            }
            list.add(parent);
            for(Child child : parent.getChildren()) {
                String childName = child.getEntityName();
                List<Child> children = childrenMap.get(childName);
                if(children == null) {
                    children = new ArrayList<Child>();
                    childrenMap.put(childName, children);
                }
                children.add(child);
            }
        }
    }

    protected final static class PagerExt<T> extends AbstractNonCachePager<T> {
		private static final long serialVersionUID = 1L;
		private FindResult<T>  result;
        private Extractor   extractor;
        public PagerExt(Extractor extractor) {
            this.extractor = extractor;
            this.extractor.setLimit(100);
            setSizePerPage(100);
        }

        @Override
        protected long getCount() {
            return result.count;
        }

        @Override
        protected List<T> getList() {
            return result.list;
        }

        @Override
        protected void refresh(int offset, int limit) {
            extractor.setOffset(offset);
            result = BasicService.getService().findByExtractorWithCount(extractor, true);
        }
    }
}
