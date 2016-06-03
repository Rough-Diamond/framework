/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.pager;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import jp.rough_diamond.commons.extractor.Extractor;
import jp.rough_diamond.commons.service.BasicService;
import jp.rough_diamond.commons.service.FindResult;

public class ExtractorPager<E> extends AbstractNonCachePager<E> {
	private static final long serialVersionUID = 1L;
	private Extractor extractor;
	private FindResult<E> result;
	public ExtractorPager(Extractor extractor) {
		this.extractor = extractor;
	}
	@Override
	protected long getCount() {
		return result.count;
	}

	@Override
	protected List<E> getList() {
		return result.list;
	}

	@Override
	protected void refresh(int offset, int limit) {
		extractor.setOffset(offset);
		extractor.setLimit(limit);
		result = BasicService.getService().findByExtractorWithCount(extractor);
	}

	public Iterator<List<E>> iterator() {
		return new ExtractorPagerIterator<E>(extractor, getSizePerPage());
	}
	
	public static <T> Iterator<List<T>> makePageIterator(Extractor e, int limitSize) {
		return new ExtractorPagerIterator<T>(e, limitSize);
	}
	
	private static class ExtractorPagerIterator<E> implements Iterator<List<E>> {
		private ExtractorPager<E> pager;
		private boolean isLast;
		private List<E> next;
		ExtractorPagerIterator(Extractor e, int limitSize) {
			pager = new ExtractorPager<E>(e);
			pager.setSizePerPage(limitSize);
			pager.gotoPage(1);
			next = pager.getCurrentPageCollection();
			isLast = (next.size() == 0);
		}
		
		public boolean hasNext() {
			return !isLast;
		}

		public List<E> next() {
			if(isLast) {
				throw new NoSuchElementException();
			}
			List<E> ret = next;
			isLast = pager.isLast();
			if(!isLast) {
				pager.next();
				next = pager.getCurrentPageCollection();
			}
			return ret;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
