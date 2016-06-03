/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.di;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Map‚ð—p‚¢‚½DIContainer
 */
public class MapDIContainer extends AbstractDIContainer {
    private Map<Object, Object> map;

    public MapDIContainer(Map<Object, Object> map) {
        this.map = new HashMap<Object, Object>(map);
    }

    @SuppressWarnings("unchecked")
	public <T> T getObject(Class<T> type, Object key) {
        return (T)map.get(key);
    }

    @SuppressWarnings("unchecked")
	public <T> T getSource(Class<T> type) {
        return (T)Collections.unmodifiableMap(map);
    }
}
