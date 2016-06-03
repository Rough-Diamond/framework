package jp.rough_diamond.commons.util.datastream;

import java.util.Iterator;
import java.util.Queue;

/**
 * DataSourceを簡易に作成するための抽象クラス
 * １つのソースオブジェクトから複数のディスティネーションオブジェクトを返却する必要があるもしくは、その逆のケースの場合は、
 * Transformerより簡易に作成できる。
 * @param <D>
 * @param <S>
 */
abstract public class SimpleIterableLogic<D, S> implements IterableLogic<D> {
	final Iterator<S> iterator;
	
	public SimpleIterableLogic(DataSource<S> ds) {
		this.iterator = ds.iterator();
	}
	
	@Override
	public Queue<D> getNextQueue() {
		return getNextQueue(iterator);
	}

	/**
	/**
	 * 必要なデータを１つ以上キューに詰めて返却する
	 * @param iterator	もととなるデータソース
	 * @return　データがこれ以上存在しない場合にはnullを返却する事
	 */
	abstract protected Queue<D> getNextQueue(Iterator<S> iterator);
}
