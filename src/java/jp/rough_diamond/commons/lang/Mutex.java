/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.lang;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.map.IdentityMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ミューテックス
 * ミューテックスは、synchronizedよりも柔軟な排他機構を実現します。
 * synchronizedの場合は、synchronizedブロックから抜けると排他ロックを強制的に解除しますが、
 * ミューテックスを用いる場合は、ミューテックスオブジェクトをリリースしない限り排他ロックを行い続けます。
 * （但し、JavaVM機構的に排他を行っているわけではなく、本ミューテックス上の振る舞いとしてです）
 * ミューテックスは、java.util.concurrent.locks.Lockインタフェースに近い振る舞いをします。
 * ミューテックスは、大きく再入可能ミューテックスと、再入不可ミューテックスに分かれます。
 * 再入可能ミューテックスは、ミューテックス取得スレッドで再度取得した場合は、ミューテックスの再取得を行うことができますが、
 * 別スレッドから同一キーのミューテックスを取得することは出来ません。
 * 再入不可ミューテックスは、再取得は常に行うことが出来ません。
 * また、ミューテックスは取得するまでのタイムアウト時間を指定することもできます。
 * 同一キーのミューテックスをいずれかのスレッドが取得している場合、ミューテックスはタイムアウト時間までは、取得可能な状態になるまで
 * 待ち続けます。
 */
@SuppressWarnings("unchecked")
public class Mutex {
    private static Map<Object, WeakReference<Mutex>> mutexMap; 
    private static Map mutexKeyMap;
    static {
        Map<Object, WeakReference<Mutex>> tmp = new HashMap<Object, WeakReference<Mutex>>();
        mutexMap = Collections.synchronizedMap(tmp);
        Map tmp2 = new IdentityMap();
        mutexKeyMap = Collections.synchronizedMap(tmp2);
    }
    
    private final static Log log = LogFactory.getLog(Mutex.class);
    
    private Object      key;
    private boolean    isReentrant;
    private Thread      t;
    private boolean 	 released;

    protected Mutex(Object key, boolean isReentrant) {
        log.debug("Mutexを生成します。");
        this.key = key;
        this.isReentrant = isReentrant;
        this.t = Thread.currentThread();
        mutexMap.put(key, new WeakReference<Mutex>(this));
        released = false;
    }
    
    @Override
    protected void finalize() throws Throwable {
        releaseMutex();
        super.finalize();
    }

    /**
     * 該当Mutexを開放します。
     */
    public void releaseMutex() {
    	if(!released) {
    		if(!mutexKeyMap.containsKey(key)) {
    			return;
    		}
    		Object realKey = mutexKeyMap.get(key);
	        synchronized (realKey) {
	            if(!mutexMap.containsKey(key)) {
	            	return;
	            }
                log.debug("Mutexを開放します。");
                mutexMap.remove(key);
                mutexKeyMap.remove(key);
                released = true;
                realKey.notifyAll();
	        }
    	}
    }
    
    /**
     * 再入可能のMutexを取得します。
     * 既に同一キーで任意のスレッド（実行スレッド含む）がMutexを活性化している場合は、
     * BusyExceptionがスローされます。
     * @param mutexKey
     * @return Mutex
     * @throws BusyException
     */
    public static Mutex getMutex(Object mutexKey) throws BusyException {
        return getMutex(mutexKey, true);
    }
    
    /**
     * Mutexを取得する
     * 再入可能なMutexを取得する場合でかつ、再入不可のMutexが活性化している場合はBusyExceptionがスローされます。
     * 再入可能なMutextを取得する場合でかつ、自スレッドでミューテックスを取得している場合は、同一ミューテックスが返却されます。
     * @param mutexKey
     * @param isReentrant 再入可能なMutexを取得する場合はtrueを指定してください。
     * @return Mutex
     * @throws BusyException
     */
    public static Mutex getMutex(Object mutexKey, boolean isReentrant) throws BusyException {
        return getMutex(mutexKey, isReentrant, -1L);
    }

    /**
     * 再入可能なMutexを一定期間待ち続けて取得する
     * @param mutexKey
     * @param timeout 最大取得待ち時間。負数なら即時復帰。0なら無限に待ち続ける
     */
    public static Mutex getMutex(Object mutexKey, long timeout) throws BusyException {
        return getMutex(mutexKey, true, timeout);
    }
    
    /**
     * Mutexを取得する
     * 再入可能なMutexを取得する場合でかつ、再入不可のMutexが活性化している場合はBusyExceptionがスローされます。
     * 再入可能なMutextを取得する場合でかつ、自スレッドでミューテックスを取得している場合は、同一ミューテックスが返却されます。
     * @param mutexKey
     * @param isReentrant 再入可否。trueであってもMutex取得時に再入可でなければ取得できない
     * @param timeout 最大取得待ち時間。負数なら即時復帰。0なら無限に待ち続ける
     * @return Mutex
     */
    public static Mutex getMutex(Object mutexKey, boolean isReentrant, long timeout) throws BusyException {
    	if(log.isDebugEnabled()) {
    		log.debug("Mutex取得要求：" + mutexKey);
    	}
        if(mutexKey instanceof String) {
            mutexKey = ((String)mutexKey).intern();
        }
        Object realKey;
        synchronized(mutexKey) {
	        realKey = mutexKeyMap.get(mutexKey);
	        if(realKey == null) {
	        	realKey = new Object();
	        	mutexKeyMap.put(mutexKey, realKey);
	        }
        }
        synchronized(realKey) {
        	dumpMutexes();
            WeakReference<Mutex> reference = mutexMap.get(mutexKey);
            if(reference != null && reference.get() != null) {
                Mutex mutex = reference.get();
                if(isReentrant && mutex.isReentrant && mutex.t == Thread.currentThread()) {
                    return mutex;
                }
                if(timeout >= 0L) {
                    try {
                        log.debug("寝ます。");
                        long before = System.currentTimeMillis();
                        realKey.wait(timeout);
                        long after = System.currentTimeMillis();
                        log.debug("起きました。");
                        long sleepTime = after - before;
                        if(timeout == 0L) {
                            return getMutex(mutexKey, isReentrant, 0L);
                        } else if(sleepTime == timeout){
                            return getMutex(mutexKey, isReentrant, -1L);
                        } else {
                        	timeout -= sleepTime;
                        	return getMutex(mutexKey, isReentrant, timeout);
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.debug("Mutexの取得に失敗しました。");
                throw new BusyException();
            } else {
                return new Mutex(mutexKey, isReentrant);
            }
        }
    }

    private static void dumpMutexes() {
    	if(log.isDebugEnabled()) {
	    	log.debug("mutexのダンプを出力します。");
	    	for(Map.Entry<Object, WeakReference<Mutex>> entry : mutexMap.entrySet()) {
	    		log.debug("key:" + entry.getKey());
	    		log.debug("value:" + entry.getValue().get());
	    	}
    	}
    }
    
    /**
     * Mutexを一定期間内に取得できなかった発生する例外です。 
     */
    public static final class BusyException extends Exception {
        private static final long serialVersionUID = 1L; 
    }
}
