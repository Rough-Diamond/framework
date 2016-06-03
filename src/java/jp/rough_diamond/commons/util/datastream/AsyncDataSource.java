package jp.rough_diamond.commons.util.datastream;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * �P�ȏ�̃f�[�^�\�[�X����̕ԋp�Ƃ��̃f�[�^�\�[�X�ւ̎擾��񓯊��ɍs�����߂̃f�[�^�\�[�X�iProducer-ConsumerPattern�j
 * ����AThreadPooling���̗��p���s���Ă��Ȃ��̂ŁA��ʂɃX���b�h������t�ɐ��\���򉻂���\��������̂ŁA
 * ��ʂ̃f�[�^�\�[�X��񓯊�������ۂɂ͒��ӂ��K�v 
 * @param <E>
 */
public class AsyncDataSource<E> implements DataSource<E> {
	private final static Log log = LogFactory.getLog(AsyncDataSource.class);
	
	final DataSource<E>[] dss;
	final int queueCapacity;

	/**
	 * �w�肳�ꂽ�f�[�^�\�[�X��Producer-Consumer Pattern������
	 * �f�[�^�̃L���b�V�������10,000���Ƃ���i�����I��DI�����悤���ȁB�B�B�j
	 * @param ds	Producer�Ƃ���f�[�^�\�[�X
	 */
	public AsyncDataSource(DataSource<E> ds) {
		this(10000, ds);
	}
	
	/**
	 * �w�肳�ꂽ�f�[�^�\�[�X��Producer-Consumer Pattern������
	 * �f�[�^�̃L���b�V�������10,000���Ƃ���i�����I��DI�����悤���ȁB�B�B�j
	 * @param dss Producer�Ƃ���f�[�^�\�[�X�Q
	 */
	public AsyncDataSource(DataSource<E>... dss) {
		this(10000, dss);
	}

	/**
	 * �w�肳�ꂽ�f�[�^�\�[�X��Producer-Consumer Pattern������
	 * @param queueCapacity	�L���b�V�����
	 * @param ds			Producer�Ƃ���f�[�^�\�[�X
	 */
	@SuppressWarnings("unchecked")
	public AsyncDataSource(int queueCapacity, DataSource<E> ds) {
		this(queueCapacity, new DataSource[]{ds});
	}
	
	/**
	 * �w�肳�ꂽ�f�[�^�\�[�X��Producer-Consumer Pattern������
	 * @param queueCapacity	�L���b�V�����
	 * @param dss			Producer�Ƃ���f�[�^�\�[�X�Q
	 */
	public AsyncDataSource(int queueCapacity, DataSource<E>... dss) {
		this.dss = dss;
		this.queueCapacity = queueCapacity;
	}

	@Override
	public Iterator<E> iterator() {
		return new IteratorExt<E>();
	}

	final static Object EOF = new Object();
	@SuppressWarnings("all")
	class IteratorExt<E> implements Iterator<E> {
		final BlockingQueue<Object> queue;
		boolean stop = false;
		E lastValue = null;
		int aliveCount;
		RuntimeException e;
		IteratorExt() {
			this.aliveCount = dss.length;
			this.queue = new LinkedBlockingQueue<Object>(queueCapacity);
			for(int i =  0 ; i < dss.length ; i++) {
				Iterator<E> iterator = (Iterator<E>)dss[i].iterator();
				Runnable run = new RunnableImpl(iterator);
				Thread t = new Thread(run);
				t.setName("ADS-" + System.currentTimeMillis());
				t.start();
			}
		}
		
		synchronized void notifyFinish() {
			try {
				aliveCount--;
				if(aliveCount == 0) {
					queue.put(EOF);
				}
			} catch(InterruptedException e) {
				log.warn(e);
				//XXX stop�͂��̃X���b�h�Őݒ肷��̂͊댯�Ȃ񂾂��A�A�A
				stop = true;
			}
		}
		
		class RunnableImpl implements Runnable {
			final Iterator<E> iterator;
			RunnableImpl(Iterator<E> iterator) {
				this.iterator = iterator;
			}
			@Override
			public void run() {
				try {
					while(iterator.hasNext()) {
						queue.put(iterator.next());
					}
				} catch(InterruptedException e) {
					log.warn(e);
					//stop�͂��̃X���b�h�Őݒ肷��̂͊댯�Ȃ񂾂��A�A�A
					stop = true;
					throw new RuntimeException(e);
				} catch(RuntimeException e) {
					IteratorExt.this.e = e;
				} finally {
					notifyFinish();
				}
			}
		}

		@Override
		public boolean hasNext() {
			if(stop) {
				return false;
			}
			if(lastValue == null) {
				try {
					Object o = queue.take();
					if(e != null) {
						throw e;
					}
					if(o == EOF) {
						stop = true;
					} else {
						lastValue = (E)o;
					}
					return hasNext();
				} catch (InterruptedException e) {
					log.warn(e);
					//stop�͂��̃X���b�h�Őݒ肷��̂͊댯�Ȃ񂾂��A�A�A
					stop = true;
					throw new RuntimeException(e);
				}
			}
			return true;
		}

		@Override
		public E next() {
			if(hasNext()) {
				E ret = lastValue;
				lastValue = null;
				return ret;
			}
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
