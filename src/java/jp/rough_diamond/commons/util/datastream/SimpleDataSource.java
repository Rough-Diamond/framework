package jp.rough_diamond.commons.util.datastream;

import java.util.Iterator;

/**
 * データソース化されてないものをデータソース化するためのラッパークラス
 * @param <T>
 */
public class SimpleDataSource<T> implements DataSource<T> {
	final Iterable<T> iterable;
	public SimpleDataSource(Iterable<T> iterable) {
		this.iterable = iterable;
	}
	
	@Override
	public Iterator<T> iterator() {
		return iterable.iterator();
	}
}
