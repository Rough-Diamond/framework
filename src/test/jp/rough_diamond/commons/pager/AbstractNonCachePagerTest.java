/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.pager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

/**
 *
 */
public class AbstractNonCachePagerTest extends TestCase {
	public void testSetSizePerPageで現在値と同じ値を指定した場合はrefreshが走らないこと() throws Exception {
		Pager p = new Pager();
		p.setSizePerPage(10);
		assertFalse("リフレッシュが呼び出されています。", p.isRefresh);
		p.getCurrentPageCollection();
		assertTrue("リフレッシュが呼び出されていません。", p.isRefresh);
		p.isRefresh = false;
		p.setSizePerPage(10);
		p.getCurrentPageCollection();
		assertFalse("リフレッシュが呼び出されています。", p.isRefresh);
	}
	
	public void testGotoPageで現在値と同じ値を指定した場合はrefreshが走らないこと() throws Exception {
		Pager p = new Pager();
		p.setSizePerPage(10);

		p.isRefresh = false;
		p.gotoPage(10);
		assertTrue("リフレッシュが呼び出されていません。", p.isRefresh);

		p.isRefresh = false;
		p.getCurrentPageCollection();
		assertTrue("リフレッシュが呼び出されていません。", p.isRefresh);

		p.isRefresh = false;
		p.gotoPage(10);
		p.getCurrentPageCollection();
		assertFalse("リフレッシュが呼び出されています。", p.isRefresh);

		p.isRefresh = false;
		p.gotoPage(11);
		p.getCurrentPageCollection();
		assertTrue("リフレッシュが呼び出されていません。", p.isRefresh);
	}
	
	public void testSetSelectingPageで現在値と同じ値を指定した場合もgotoPage同様であること() throws Exception {
		Pager p = new Pager();
		p.setSizePerPage(10);

		p.isRefresh = false;
		p.setSelectingPage(10);
		assertTrue("リフレッシュが呼び出されていません。", p.isRefresh);

		p.isRefresh = false;
		p.getCurrentPageCollection();
		assertTrue("リフレッシュが呼び出されていません。", p.isRefresh);

		p.isRefresh = false;
		p.setSelectingPage(10);
		p.getCurrentPageCollection();
		assertFalse("リフレッシュが呼び出されています。", p.isRefresh);

		p.isRefresh = false;
		p.setSelectingPage(11);
		p.getCurrentPageCollection();
		assertTrue("リフレッシュが呼び出されていません。", p.isRefresh);
	}

	public void testページ再計算の処理が正しく動いていること() throws Exception {
		Pager p = new Pager();
		p.count = 100;
		
		p.setSizePerPage(2);
		p.gotoPage(2);
		p.getCurrentPageCollection();
		assertEquals("ページ番号が誤っています。", 2, p.getCurrentPage());
		assertEquals("offsetが誤っています。", 2, p.offset);
		assertEquals("limitが誤っています。", 2, p.limit);
		
		p.setSizePerPage(10);
		p.getCurrentPageCollection();
		assertEquals("ページ番号が誤っています。", 2, p.getCurrentPage());
		assertEquals("offsetが誤っています。", 10, p.offset);
		assertEquals("limitが誤っています。", 10, p.limit);
		
		p.setSizePerPage(1000);
		p.getCurrentPageCollection();
		assertEquals("ページ番号が誤っています。", 1, p.getCurrentPage());
		assertEquals("offsetが誤っています。", 0, p.offset);
		assertEquals("limitが誤っています。", 1000, p.limit);
	}
	
	static class Pager extends AbstractNonCachePager<String> {
		private static final long serialVersionUID = 1L;
		boolean isRefresh = false;
		int offset;
		int limit;
		int count = Integer.MAX_VALUE;
		@Override
		protected long getCount() {
			return count;
		}

		@Override
		protected List<String> getList() {
			if(this.offset > this.count) {
				return new ArrayList<String>();
			} else {
				return Arrays.asList("abc", "def", "ghi", "jkl", "mno", "pqr", "stu", "vwx", "yz");
			}
		}

		@Override
		protected void refresh(int offset, int limit) {
			isRefresh = true;
			this.offset = offset;
			this.limit = limit;
		}
	}
}
