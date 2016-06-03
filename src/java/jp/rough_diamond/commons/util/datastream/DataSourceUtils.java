/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.util.datastream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;


/**
 * データソースを効率的に使用するためのユーティリティクラス群
 */
public class DataSourceUtils {
	/**
	 * データソースのデータをリストに変換する
	 * @param <T>
	 * @param ds
	 * @return
	 */
	public static <T>  List<T> toList(DataSource<T> ds) {
		List<T> ret = new ArrayList<T>();
		for(T t : ds) {
			ret.add(t);
		}
		return ret;
	}

	public static <T> DataSource<T> makeIterator(final IterableLogic<T> logic)  {
		return new SimpleDataSource<T>(new Iterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return new IteratorExt<T>(logic);
			}
		});
	}

	static class IteratorExt<T> implements Iterator<T> {
		final IterableLogic<T> logic;
		Queue<T> queue = new LinkedList<T>();
		
		IteratorExt(IterableLogic<T> logic) {
			this.logic = logic;
		}
		
		@Override
		public boolean hasNext() {
			if(queue == null) {
				return false;
			} else if(queue.size() == 0) {
				queue = logic.getNextQueue();
				return hasNext();
			}
			return true;
		}

		@Override
		public T next() {
			if(queue == null) {
				throw new NoSuchElementException();
			} else if(queue.size() == 0) {
				queue = logic.getNextQueue();
				return next();
			}
			return queue.poll();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
