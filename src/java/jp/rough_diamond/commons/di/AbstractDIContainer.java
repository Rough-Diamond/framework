/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.di;

/**
 * DIContainerインタフェースを実装した抽象クラスです。
 */
public abstract class AbstractDIContainer implements DIContainer {
    public Object getObject(Object key) {
        return getObject(Object.class, key);
    }
    
    public Object getSource() {
        return getSource(Object.class);
    }
}
