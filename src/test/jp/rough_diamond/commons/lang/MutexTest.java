/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.lang;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jp.rough_diamond.commons.lang.Mutex;
import junit.framework.TestCase;

public class MutexTest extends TestCase {
    private final static Log log = LogFactory.getLog(MutexTest.class);
    
    public void test同一キーで複数のmutexが取得できないこと() throws Exception {
        Mutex mutex = Mutex.getMutex("hogehoge", false);
        try {
            Mutex mutex2 = Mutex.getMutex("hogehoge", false);
            try {
            	fail("例外が発生していません。");
            } finally {
            	mutex2.releaseMutex();
            }
        } catch(Mutex.BusyException e) {
            
        } finally {
            mutex.releaseMutex();
        }
    }
    
    public void test同一キーでもMutexが開放されれば取得できること() throws Exception {
        Mutex mutex = Mutex.getMutex("hogehoge");
        mutex.releaseMutex();
        Mutex mutex2 = Mutex.getMutex("hogehoge");
        mutex2.releaseMutex();
    }
    
    public void test同一キーでもリエントラントであれば最初に取得したMutexが取得できること() throws Exception {
        Mutex mutex = Mutex.getMutex("hogehoge", true);
        try {
            Mutex mutex2 = Mutex.getMutex("hogehoge", true);
            try {
                assertEquals("同一mutexではありません。", mutex, mutex2);
            } finally {
                mutex2.releaseMutex();
            }
        } finally {
            mutex.releaseMutex();
        }
    }
    
    public void test指定した時間内にMutexが開放されれば同一キーのMutexが取得できること() throws Exception {
        Mutex mutex = Mutex.getMutex("hogehoge", false);
        try {
            RunnableImpl impl = new RunnableImpl(mutex);
            Thread t = new Thread(impl);
            t.start();
            Mutex mutex2 = Mutex.getMutex("hogehoge", false, 500L);
            mutex2.releaseMutex();
        } finally {
            mutex.releaseMutex();
        }
    }
    
    public void test指定した時間内にMutexが開放されなければ同一キーのMutexは取得できないこと() throws Exception {
        for(int i = 0 ; i < 10 ; i++) {
            Mutex mutex = Mutex.getMutex("hogehoge", false);
            try {
                RunnableImpl impl = new RunnableImpl(mutex);
                Thread t = new Thread(impl);
                t.start();
                try {
                    Mutex mutex2 = Mutex.getMutex("hogehoge", false, 10L);
                    mutex2.releaseMutex();
                    fail("ミューテックスが取得できました。");
                } catch(Mutex.BusyException e) {
                }
                t.join();
            } finally {
                mutex.releaseMutex();
            }
        }
    }
    
    public void test再入可能でも別スレッドでの取得は行えないこと() throws Exception {
        Mutex mutex = Mutex.getMutex("hogehoge", true);
        try {
            Runnable2 runnable = new Runnable2();
            Thread t = new Thread(runnable);
            t.start();
            t.join();
            assertFalse("Mutexが取得できました。", runnable.isGettingMutex);
        } finally {
            mutex.releaseMutex();
        }
    }
    
    public void testMutexのキーに対してmutex外でnotifyしてもmutex内のロックは維持し続けること() throws Exception {
    	String key = "hogehoge";
    	Mutex mutex = Mutex.getMutex(key, 0);
    	try {
	    	Runnable3 runnable = new Runnable3(key);
	    	Thread t = new Thread(runnable);
	    	t.start();
	    	Thread.sleep(500);
	    	synchronized(key.intern()) {
	    		key.intern().notifyAll();
	    	}
	    	Thread.sleep(500);
	    	assertFalse("BusyExceptionが発生しています。", runnable.isException);
	    	assertFalse("Mutexが取得できています。", runnable.isGettingMutex);
	    	mutex.releaseMutex();
	    	Thread.sleep(500);
	    	System.out.println(runnable.isGettingMutex);
	    	assertFalse("BusyExceptionが発生しています。", runnable.isException);
	    	assertTrue("Mutexが取得できていません。", runnable.isGettingMutex);
    	} finally {
    		mutex.releaseMutex();
    	}
    }
    
    private static class Runnable3 implements Runnable {
    	private volatile boolean isGettingMutex = false;
    	private volatile boolean isException = false;
    	private Object key;
    	public Runnable3(Object mutexKey) {
    		this.key = mutexKey;
    	}
    	
    	public void run() {
    		try {
    			Mutex mutex = Mutex.getMutex(key, 0);
    			try {
    				isGettingMutex = true;
    			} finally {
    				mutex.releaseMutex();
    			}
    		} catch(Mutex.BusyException e) {
    			e.printStackTrace();
    			isException = true;
    		}
    	}
    }
    
    private static class Runnable2 implements Runnable {
        boolean isGettingMutex = false;
        public void run() {
            try {
                Mutex mutex = Mutex.getMutex("hogehoge", true);
                isGettingMutex = true;
                mutex.releaseMutex();
            } catch(Mutex.BusyException e) {
                isGettingMutex = false;
            }
        }
    }
    
    private static class RunnableImpl implements Runnable {
        private Mutex mutex;
        RunnableImpl(Mutex mutex) {
            this.mutex = mutex;
        }
        
        public void run() {
            log.debug("スレッドを開始します。");
            try {
                log.debug("寝ます。");
                Thread.sleep(100);
                log.debug("起きました。");
                mutex.releaseMutex();
            } catch (InterruptedException e) {
            }
        }
    }
}
