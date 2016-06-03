/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.pager;

import java.util.List;

import jp.rough_diamond.commons.lang.Range;

public abstract class AbstractNonCachePager<E> extends AbstractPager<E> {
	private static final long serialVersionUID = 1L;
	public long getSize() {
		if(isNeedRefresh()) {
			refresh();
		}
		return getCount();
	}

	public List<E> getCurrentPageCollection() {
		if(isNeedRefresh()) {
			refresh();
		}
		return getList();
	}

	public void forceRefresh() {
		setNeedRefresh(true);
	}
	
	private int lastSizePerPage;
	protected void refresh() {
		if(isNeedRefresh()) {
			int offset = (getCurrentPage() - 1) * getSizePerPage();
			refresh(offset, getSizePerPage());
			setNeedRefresh(false);
			if(getCurrentPageCollection().size() == 0 && getCurrentPage() != 1) {
				if(log.isDebugEnabled()) {
					log.debug("ページあたりの表示数等の変更によって現在ページ数＞最大ページ数になりました");
					log.debug("最大ページ数：" + getPageSize());
					log.debug("現在のページ番号:" + getCurrentPage());
				}
				int pageNo = recalculatePageNo();
				log.debug("再計算後のページ番号：" + pageNo);
				gotoPage(pageNo);
				setNeedRefresh(true);
				refresh();
			}
			lastSizePerPage = getSizePerPage();
		}
	}

	private int recalculatePageNo() {
		int lastFirstIndex = lastSizePerPage * getCurrentPage();
		int currentSizePerPage = getSizePerPage();
		int maxPageSize = getPageSize();
		for(int i = maxPageSize ; i != 1 ; i--) {
			int min = (i - 1) * currentSizePerPage + 1;
			int max = (i) * currentSizePerPage;
			Range<Integer> range = new Range<Integer>(min, max);
			if(range.isIncludion(lastFirstIndex)) {
				return i;
			}
		}
		return 1;
	}
	
	private boolean 	isNeedRefresh = true;

	public boolean isNeedRefresh() {
		return isNeedRefresh;
	}

	public void setNeedRefresh(boolean isNeedRefresh) {
		this.isNeedRefresh = isNeedRefresh;
	}

	abstract protected long getCount();
	abstract protected List<E> getList();
	abstract protected void refresh(int offset, int limit);

	public int getSelectingPage() {
		return getCurrentPage();
	}
	
	public void setSelectingPage(int pageNo) {
		if(pageNo > getPageSize()) {
			gotoPage(getPageSize());
		} else {
			gotoPage(pageNo);
		}
	}

	private int lastPageNo;
	@Override
	protected void preGotoPage(int page) {
		this.lastPageNo = getCurrentPage();
	}
	
	@Override
	protected void postGotoPage(int page) {
		super.postGotoPage(page);
		if(this.lastPageNo != page) {
			setNeedRefresh(true);
		}
	}
    
        
    private int sizePerPage;
    
    public int getSizePerPage() {
        return sizePerPage;
    }
    
    public void setSizePerPage(int sizePerPage) {
    	if(this.sizePerPage != sizePerPage) {
            this.sizePerPage = sizePerPage;
            setNeedRefresh(true);
    	}
    }
}
