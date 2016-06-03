package jp.rough_diamond.commons.util.datastream;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * １つ以上のデータソースからの返却とこのデータソースへの取得を非同期に行うためのデータソース（Producer-ConsumerPattern）
 * 現状、ThreadPooling等の利用を行っていないので、大量にスレッドを消費し逆に性能が劣化する可能性もあるので、
 * 大量のデータソースを非同期化する際には注意が必要 
 * @param <E>
 */
public class AsyncDataSource<E> implements DataSource<E> {
	private final static Log log = LogFactory.getLog(AsyncDataSource.class);
	
	final DataSource<E>[] dss;
	final int queueCapacity;

	/**
	 * 指定されたデータソースをProducer-Consumer Pattern化する
	 * データのキャッシュ上限は10,000件とする（将来的にDI化しようかな。。。）
	 * @param ds	Producerとするデータソース
	 */
	public AsyncDataSource(DataSource<E> ds) {
		this(10000, ds);
	}
	
	/**
	 * 指定されたデータソースをProducer-Consumer Pattern化する
	 * データのキャッシュ上限は10,000件とする（将来的にDI化しようかな。。。）
	 * @param dss Producerとするデータソース群
	 */
	public AsyncDataSource(DataSource<E>... dss) {
		this(10000, dss);
	}

	/**
	 * 指定されたデータソースをProducer-Consumer Pattern化する
	 * @param queueCapacity	キャッシュ上限
	 * @param ds			Producerとするデータソース
	 */
	@SuppressWarnings("unchecked")
	public AsyncDataSource(int queueCapacity, DataSource<E> ds) {
		this(queueCapacity, new DataSource[]{ds});
	}
	
	/**
	 * 指定されたデータソースをProducer-Consumer Pattern化する
	 * @param queueCapacity	キャッシュ上限
	 * @param dss			Producerとするデータソース群
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
				//XXX stopはこのスレッドで設定するのは危険なんだが、、、
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
					//stopはこのスレッドで設定するのは危険なんだが、、、
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
					//stopはこのスレッドで設定するのは危険なんだが、、、
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
