package jp.rough_diamond.commons.util.datastream;

import java.util.Iterator;

/**
 * あるデータソースから違う型へ変換もしくは同じ型でも内容を加工するためのデータソース
 *　基本的に１つのソースオブジェクトから１つのディスティネーションオブジェクトを作るのに用いる。
 * 非対称の場合はSimpleIterableLogicを継承したクラスを用いて作成した方が良い
 * @param <S>
 * @param <D>
 */
abstract public class Transformer<S, D> implements DataSource<D> {
	final DataSource<S> srcDS;
	/**
	 * @param srcDS	ソースオブジェクトのデータソース
	 */
	public Transformer(DataSource<S> srcDS) {
		this.srcDS = srcDS;
	}

	@Override
	public Iterator<D> iterator() {
		Iterator<S> srcIterator = srcDS.iterator();
		return new IteratorImpl<D>(srcIterator);
	}
	
	/**
	 * iterator()で返却されるIteratorのhasNextの返却処理
	 * @param iterator
	 * @return see {@link Iterator#hasNext()}
	 */
	protected boolean hasNext(Iterator<S> iterator) {
		return iterator.hasNext();
	}
	
	/**
	 * iterator()で返却されるIteratorのnextの返却処理
	 * @param srcIterator
	 * @return see {@link Iterator#next()}
	 */
	protected D next(Iterator<S> srcIterator) {
		S src = srcIterator.next();
		return transform(src);
	}

	/**
	 * オブジェクトの変換を行う抽象メソッド
	 * @param src
	 * @return
	 */
	abstract protected D transform(S src);
	
	class IteratorImpl<E> implements Iterator<E> {
		final Iterator<S> srcIterator;
		IteratorImpl(Iterator<S> iterator) {
			this.srcIterator = iterator;
		}
		
		@Override
		public boolean hasNext() {
			return Transformer.this.hasNext(srcIterator);
		}

		@Override
		@SuppressWarnings("unchecked")
		public E next() {
			return (E)Transformer.this.next(srcIterator);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
