/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 登録されたコンポーネントで一定期間アクセスが無い場合は、
 * 自動的に消去するHashMap
**/
public class AutoRemoveHashMap<K,V> extends HashMap<K,V> {
	private static final long serialVersionUID = 1L;

	public AutoRemoveHashMap() {
        this(Long.MAX_VALUE);
    }

    public AutoRemoveHashMap(long interval) {
        super();
        this.interval = interval;
        schedulerMap = new HashMap<Object,TimeBomb>();
    }

    public AutoRemoveHashMap(int initialCapacity, long interval) {
        super(initialCapacity);
        this.interval = interval;
        schedulerMap = new HashMap<Object,TimeBomb>(initialCapacity);
    }

    public AutoRemoveHashMap(int initialCapacity, float loadFactor, long interval) {
        super(initialCapacity, loadFactor);
        this.interval = interval;
        schedulerMap = new HashMap<Object,TimeBomb>(initialCapacity, loadFactor);
    }

    public AutoRemoveHashMap(Map<K,V> m, long interval) {
        super(m);
        this.interval = interval;
        schedulerMap = new HashMap<Object,TimeBomb>();
        schedulerMap.clear();
    }

    public void clear() {
        Iterator<K> iterator = keySet().iterator();
        while(iterator.hasNext()) {
            Object key = iterator.next();
            TimeBomb bomb = (TimeBomb)schedulerMap.get(key);
            bomb.cancel();
        }
        super.clear();
        schedulerMap.clear();
    }

    public V remove(Object key) {
        TimeBomb bomb = (TimeBomb)schedulerMap.get(key);
        bomb.cancel();
        schedulerMap.remove(key);
        return super.remove(key);
    }

    public V get(Object key) {
        V o = super.get(key);
        if(o != null) {
            reset(key);
        }
        return o;
    }

    public V put(K key, V value) {
        V o = super.put(key, value);
        reset(key);
        return o;
    }

    private void reset(Object key) {
        TimeBomb bomb = (TimeBomb)schedulerMap.get(key);
        if(bomb != null) {
            bomb.cancel();
        }
        bomb = new TimeBomb(key);
        SCHEDULER.schedule(bomb, interval);
        schedulerMap.put(key, bomb);
    }

    private final Map<Object,TimeBomb>   schedulerMap;
    private final long  interval;

    private final static Timer SCHEDULER = new Timer(true);

    private class TimeBomb extends TimerTask {
        public TimeBomb(Object key) {
            this.key = key;
        }

        public void run() {
            AutoRemoveHashMap.super.remove(key);
            AutoRemoveHashMap.this.schedulerMap.remove(key);
        }

        private Object key;
    }

}