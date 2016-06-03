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
    
    public void test����L�[�ŕ�����mutex���擾�ł��Ȃ�����() throws Exception {
        Mutex mutex = Mutex.getMutex("hogehoge", false);
        try {
            Mutex mutex2 = Mutex.getMutex("hogehoge", false);
            try {
            	fail("��O���������Ă��܂���B");
            } finally {
            	mutex2.releaseMutex();
            }
        } catch(Mutex.BusyException e) {
            
        } finally {
            mutex.releaseMutex();
        }
    }
    
    public void test����L�[�ł�Mutex���J�������Ύ擾�ł��邱��() throws Exception {
        Mutex mutex = Mutex.getMutex("hogehoge");
        mutex.releaseMutex();
        Mutex mutex2 = Mutex.getMutex("hogehoge");
        mutex2.releaseMutex();
    }
    
    public void test����L�[�ł����G���g�����g�ł���΍ŏ��Ɏ擾����Mutex���擾�ł��邱��() throws Exception {
        Mutex mutex = Mutex.getMutex("hogehoge", true);
        try {
            Mutex mutex2 = Mutex.getMutex("hogehoge", true);
            try {
                assertEquals("����mutex�ł͂���܂���B", mutex, mutex2);
            } finally {
                mutex2.releaseMutex();
            }
        } finally {
            mutex.releaseMutex();
        }
    }
    
    public void test�w�肵�����ԓ���Mutex���J�������Γ���L�[��Mutex���擾�ł��邱��() throws Exception {
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
    
    public void test�w�肵�����ԓ���Mutex���J������Ȃ���Γ���L�[��Mutex�͎擾�ł��Ȃ�����() throws Exception {
        for(int i = 0 ; i < 10 ; i++) {
            Mutex mutex = Mutex.getMutex("hogehoge", false);
            try {
                RunnableImpl impl = new RunnableImpl(mutex);
                Thread t = new Thread(impl);
                t.start();
                try {
                    Mutex mutex2 = Mutex.getMutex("hogehoge", false, 10L);
                    mutex2.releaseMutex();
                    fail("�~���[�e�b�N�X���擾�ł��܂����B");
                } catch(Mutex.BusyException e) {
                }
                t.join();
            } finally {
                mutex.releaseMutex();
            }
        }
    }
    
    public void test�ē��\�ł��ʃX���b�h�ł̎擾�͍s���Ȃ�����() throws Exception {
        Mutex mutex = Mutex.getMutex("hogehoge", true);
        try {
            Runnable2 runnable = new Runnable2();
            Thread t = new Thread(runnable);
            t.start();
            t.join();
            assertFalse("Mutex���擾�ł��܂����B", runnable.isGettingMutex);
        } finally {
            mutex.releaseMutex();
        }
    }
    
    public void testMutex�̃L�[�ɑ΂���mutex�O��notify���Ă�mutex���̃��b�N�͈ێ��������邱��() throws Exception {
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
	    	assertFalse("BusyException���������Ă��܂��B", runnable.isException);
	    	assertFalse("Mutex���擾�ł��Ă��܂��B", runnable.isGettingMutex);
	    	mutex.releaseMutex();
	    	Thread.sleep(500);
	    	System.out.println(runnable.isGettingMutex);
	    	assertFalse("BusyException���������Ă��܂��B", runnable.isException);
	    	assertTrue("Mutex���擾�ł��Ă��܂���B", runnable.isGettingMutex);
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
            log.debug("�X���b�h���J�n���܂��B");
            try {
                log.debug("�Q�܂��B");
                Thread.sleep(100);
                log.debug("�N���܂����B");
                mutex.releaseMutex();
            } catch (InterruptedException e) {
            }
        }
    }
}
