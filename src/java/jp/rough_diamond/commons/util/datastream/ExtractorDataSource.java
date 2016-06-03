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
 * Extractor�ɂ���ē�����V�[�P���V�������[�h����f�[�^�W���B
 * �������@�ɂ���āA�Ώ�Extractor�ň�x�ɓ�����f�[�^�W���̂ݎ擾������@�ƁA
 * �����ɍ��v����f�[�^���J��Ԃ��擾������@�̂Q��ނ�p�ӂ��Ă���B
 * �O�҂ɂ��ẮABasicService.findByExtractor�̕ԋp�l��iterator��ԋp����̂Ɠ����ł���B
 * �܂��A�J��Ԃ��f�[�^�𓾂�ʂ̕��@�Ƃ���ExtractorPager��p������@�����邪�AExtractPager�́A
 * �S�̂̌������擾���鏈���������I�ɍs���܂��A�Ăяo�������Q�d���[�v����������K�v�����邽�߁A
 * ��ʃf�[�^���J��Ԃ���������̂ł���΁AExtractorDataSource�̕������ǂ��I�����Ƃ�����B
 * ������Ăяo���ꍇ�́A�X�̃f�[�^�擾�v���͓���̃g�����U�N�V�����Ŏ��s�����̂ŁA
 * �g�����U�N�V�����J�n/�I���̐��\�򉻂͂قڍl�����Ȃ��Ă��悢
 * @param <T>
 */
public class ExtractorDataSource<T> implements DataSource<T> {
	private final static Log log = LogFactory.getLog(ExtractorDataSource.class);
	
	final boolean isMultiple;
	final int sizePerEntry;
	final int baseLimit;
	final Extractor ex;
	
	/**
	 * �w�肳�ꂽExtractor�ɂ���āA�ԋp�����f�[�^���f�[�^�W���Ƃ݂Ȃ������q
	 * Extractor�Ŏ擾�����S�f�[�^����x�Ŏ擾����
	 * @param ex	�f�[�^�擾�Ώ�Extractor
	 */
	public ExtractorDataSource(Extractor ex) {
		this(ex, false, Integer.MAX_VALUE);
	}
	
	/**
	 * @param ex			�f�[�^�擾�Ώ�Extractor
	 * @param isMultiple	false�̏ꍇ�́AExtractor�݂̂��󂯂鐶���q�Ɠ����U�镑�����s��
	 * @param sizePerEntry	��x�̃f�[�^�擾�v�����ɓ���ő�f�[�^�������w�肷��B�����w�肷��΁A�擾�v���̔����񐔂͌������邪���̕��A
	 * 						���������ʏ����
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

	//E�ɂ�����x���o�Ȃ��Ȃ����B�Ӗ��s��
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
