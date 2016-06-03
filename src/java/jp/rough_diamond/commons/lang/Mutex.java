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
 * �~���[�e�b�N�X
 * �~���[�e�b�N�X�́Asynchronized�����_��Ȕr���@�\���������܂��B
 * synchronized�̏ꍇ�́Asynchronized�u���b�N���甲����Ɣr�����b�N�������I�ɉ������܂����A
 * �~���[�e�b�N�X��p����ꍇ�́A�~���[�e�b�N�X�I�u�W�F�N�g�������[�X���Ȃ�����r�����b�N���s�������܂��B
 * �i�A���AJavaVM�@�\�I�ɔr�����s���Ă���킯�ł͂Ȃ��A�{�~���[�e�b�N�X��̐U�镑���Ƃ��Ăł��j
 * �~���[�e�b�N�X�́Ajava.util.concurrent.locks.Lock�C���^�t�F�[�X�ɋ߂��U�镑�������܂��B
 * �~���[�e�b�N�X�́A�傫���ē��\�~���[�e�b�N�X�ƁA�ē��s�~���[�e�b�N�X�ɕ�����܂��B
 * �ē��\�~���[�e�b�N�X�́A�~���[�e�b�N�X�擾�X���b�h�ōēx�擾�����ꍇ�́A�~���[�e�b�N�X�̍Ď擾���s�����Ƃ��ł��܂����A
 * �ʃX���b�h���瓯��L�[�̃~���[�e�b�N�X���擾���邱�Ƃ͏o���܂���B
 * �ē��s�~���[�e�b�N�X�́A�Ď擾�͏�ɍs�����Ƃ��o���܂���B
 * �܂��A�~���[�e�b�N�X�͎擾����܂ł̃^�C���A�E�g���Ԃ��w�肷�邱�Ƃ��ł��܂��B
 * ����L�[�̃~���[�e�b�N�X�������ꂩ�̃X���b�h���擾���Ă���ꍇ�A�~���[�e�b�N�X�̓^�C���A�E�g���Ԃ܂ł́A�擾�\�ȏ�ԂɂȂ�܂�
 * �҂������܂��B
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
        log.debug("Mutex�𐶐����܂��B");
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
     * �Y��Mutex���J�����܂��B
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
                log.debug("Mutex���J�����܂��B");
                mutexMap.remove(key);
                mutexKeyMap.remove(key);
                released = true;
                realKey.notifyAll();
	        }
    	}
    }
    
    /**
     * �ē��\��Mutex���擾���܂��B
     * ���ɓ���L�[�ŔC�ӂ̃X���b�h�i���s�X���b�h�܂ށj��Mutex�����������Ă���ꍇ�́A
     * BusyException���X���[����܂��B
     * @param mutexKey
     * @return Mutex
     * @throws BusyException
     */
    public static Mutex getMutex(Object mutexKey) throws BusyException {
        return getMutex(mutexKey, true);
    }
    
    /**
     * Mutex���擾����
     * �ē��\��Mutex���擾����ꍇ�ł��A�ē��s��Mutex�����������Ă���ꍇ��BusyException���X���[����܂��B
     * �ē��\��Mutext���擾����ꍇ�ł��A���X���b�h�Ń~���[�e�b�N�X���擾���Ă���ꍇ�́A����~���[�e�b�N�X���ԋp����܂��B
     * @param mutexKey
     * @param isReentrant �ē��\��Mutex���擾����ꍇ��true���w�肵�Ă��������B
     * @return Mutex
     * @throws BusyException
     */
    public static Mutex getMutex(Object mutexKey, boolean isReentrant) throws BusyException {
        return getMutex(mutexKey, isReentrant, -1L);
    }

    /**
     * �ē��\��Mutex�������ԑ҂������Ď擾����
     * @param mutexKey
     * @param timeout �ő�擾�҂����ԁB�����Ȃ瑦�����A�B0�Ȃ疳���ɑ҂�������
     */
    public static Mutex getMutex(Object mutexKey, long timeout) throws BusyException {
        return getMutex(mutexKey, true, timeout);
    }
    
    /**
     * Mutex���擾����
     * �ē��\��Mutex���擾����ꍇ�ł��A�ē��s��Mutex�����������Ă���ꍇ��BusyException���X���[����܂��B
     * �ē��\��Mutext���擾����ꍇ�ł��A���X���b�h�Ń~���[�e�b�N�X���擾���Ă���ꍇ�́A����~���[�e�b�N�X���ԋp����܂��B
     * @param mutexKey
     * @param isReentrant �ē��ہBtrue�ł����Ă�Mutex�擾���ɍē��łȂ���Ύ擾�ł��Ȃ�
     * @param timeout �ő�擾�҂����ԁB�����Ȃ瑦�����A�B0�Ȃ疳���ɑ҂�������
     * @return Mutex
     */
    public static Mutex getMutex(Object mutexKey, boolean isReentrant, long timeout) throws BusyException {
    	if(log.isDebugEnabled()) {
    		log.debug("Mutex�擾�v���F" + mutexKey);
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
                        log.debug("�Q�܂��B");
                        long before = System.currentTimeMillis();
                        realKey.wait(timeout);
                        long after = System.currentTimeMillis();
                        log.debug("�N���܂����B");
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
                log.debug("Mutex�̎擾�Ɏ��s���܂����B");
                throw new BusyException();
            } else {
                return new Mutex(mutexKey, isReentrant);
            }
        }
    }

    private static void dumpMutexes() {
    	if(log.isDebugEnabled()) {
	    	log.debug("mutex�̃_���v���o�͂��܂��B");
	    	for(Map.Entry<Object, WeakReference<Mutex>> entry : mutexMap.entrySet()) {
	    		log.debug("key:" + entry.getKey());
	    		log.debug("value:" + entry.getValue().get());
	    	}
    	}
    }
    
    /**
     * Mutex�������ԓ��Ɏ擾�ł��Ȃ��������������O�ł��B 
     */
    public static final class BusyException extends Exception {
        private static final long serialVersionUID = 1L; 
    }
}
