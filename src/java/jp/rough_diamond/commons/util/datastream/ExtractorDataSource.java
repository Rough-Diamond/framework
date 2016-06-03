package jp.rough_diamond.commons.util.datastream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import jp.rough_diamond.commons.extractor.Extractor;
import jp.rough_diamond.commons.service.BasicService;
import jp.rough_diamond.framework.service.Service;
import jp.rough_diamond.framework.service.ServiceLocator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.BaseObjectPool;

/**
 * Extractorによって得られるシーケンシャルリードするデータ集合。
 * 生成方法によって、対象Extractorで一度に得られるデータ集合のみ取得する方法と、
 * 条件に合致するデータを繰り返し取得する方法の２種類を用意している。
 * 前者については、BasicService.findByExtractorの返却値のiteratorを返却するのと同等である。
 * また、繰り返しデータを得る別の方法としてExtractorPagerを用いる方法があるが、ExtractPagerは、
 * 全体の件数を取得する処理も内部的に行いまた、呼び出し側も２重ループを処理する必要があるため、
 * 大量データを繰り返し処理するのであれば、ExtractorDataSourceの方がより良い選択肢といえる。
 * 複数回呼び出す場合は、個々のデータ取得要求は同一のトランザクションで実行されるので、
 * トランザクション開始/終了の性能劣化はほぼ考慮しなくてもよい
 * @param <T>
 */
public class ExtractorDataSource<T> implements DataSource<T> {
	private final static Log log = LogFactory.getLog(ExtractorDataSource.class);
	
	final boolean isMultiple;
	final int sizePerEntry;
	final int baseLimit;
	final Extractor ex;
	
	/**
	 * 指定されたExtractorによって、返却されるデータをデータ集合とみなす生成子
	 * Extractorで取得される全データを一度で取得する
	 * @param ex	データ取得対象Extractor
	 */
	public ExtractorDataSource(Extractor ex) {
		this(ex, false, Integer.MAX_VALUE);
	}
	
	/**
	 * @param ex			データ取得対象Extractor
	 * @param isMultiple	falseの場合は、Extractorのみを受ける生成子と同じ振る舞いを行う
	 * @param sizePerEntry	一度のデータ取得要求時に得る最大データ件数を指定する。多く指定すれば、取得要求の発生回数は減少するがその分、
	 * 						メモリを大量消費する
	 */
	public ExtractorDataSource(Extractor ex, boolean isMultiple, int sizePerEntry) {
		this.ex = ex;
		this.isMultiple = isMultiple;
		this.sizePerEntry = sizePerEntry;
		this.baseLimit = ex.getLimit();
		ex.setLimit(this.sizePerEntry);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Iterator<T> iterator() {
		if(isMultiple) {
			return new IteratorImpl<T>();
		} else {
			return ((List<T>)BasicService.getService().findByExtractor(ex)).iterator();
		}
	}

	static class RunnableImpl implements Runnable {
		final private GetterStrategy<?> strategy;
		RunnableImpl(GetterStrategy<?> strategy) {
			this.strategy = strategy;
		}
		
		@Override
		public void run() {
			ServiceLocator.getService(ServiceImpl.class).doIt(strategy);
		}
	}
	
	public static class ServiceImpl implements Service {
		public void doIt(GetterStrategy<?> strategy) {
			strategy.start();
		}
	}
	
	public static class GetterStrategy<E> {
		final Extractor ex;
		final int limit;
		int pageNo = 0;
		final int baseOffset;
		RuntimeException exception;
		LinkedBlockingQueue<List<E>> queue = new LinkedBlockingQueue<List<E>>(1);
		
		GetterStrategy(Extractor ex, int limit) {
			this.ex = ex;
			this.limit = limit;
			this.baseOffset = ex.getOffset();
			this.ex.setLimit(limit);
		}
		
		public void start() {
			try {
				while(true) {
					ex.setOffset(pageNo++ * limit + baseOffset);
					List<E> list = BasicService.getService().findByExtractor(ex);
					queue.put(list);
					if(list.size() < limit) {
						break;
					}
				}
			} catch (InterruptedException e) {
				log.warn(e.getMessage(), e);
			} catch(RuntimeException e) {
				this.exception = e;
				try {
					queue.put(new ArrayList<E>());
				} catch (InterruptedException e1) {
					log.warn(e.getMessage(), e);
				}
			}
		}
		List<E> getList() {
			try {
				List<E> ret = queue.take();
				if(exception != null) {
					throw exception;
				}
				return ret;
			} catch (InterruptedException e) {
				log.warn(e);
			}
			return new ArrayList<E>();
		}
	}
	
	static Map<GetterStrategy<?>, Thread> map = new WeakHashMap<GetterStrategy<?>, Thread>();
	
	static <E> List<E> getList(GetterStrategy<E> strategy) {
		Thread t = map.get(strategy);
		if(t == null) {
			t = new Thread(new RunnableImpl(strategy));
			t.setName("EDS-" + System.currentTimeMillis());
			map.put(strategy, t);
			t.start();
		}
		List<E> ret = strategy.getList();
		return ret;
	}

	static <E> void terminateThread(GetterStrategy<E> strategy) {
		Thread t = map.get(strategy);
		if(t != null) {
			map.remove(strategy);
			t.interrupt();
		}
	}

	//Eにしたら警告出なくなった。意味不明
	//@see http://d.hatena.ne.jp/kidotaka/20091122/1258922844
	class IteratorImpl<E> implements Iterator<E> {
		final List<E> EMPTY = new ArrayList<E>();
		List<E> list = EMPTY;
		int index = 0;
		int getterCount = 0;
		final GetterStrategy<E> strategy;
		
		IteratorImpl() {
			strategy = new GetterStrategy<E>(ex, sizePerEntry);
		}
		
		@Override
		public boolean hasNext() {
			if(baseLimit != -1 && getterCount >= baseLimit) {
				return false;
			}
			if(list.size() <= index) {
				if(list.size() < sizePerEntry && list != EMPTY) {
					return false;
				}
				list = getList(strategy);
				if(list.size() == 0) {
					return false;
				}
				index = 0;
			}
			return true;
		}

		@Override
		protected void finalize() {
			terminateThread(strategy);
		}
		
		@Override
		public E next() {
			if(!hasNext()) {
				throw new NoSuchElementException();
			}
			getterCount++;
			return list.get(index++);
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
