/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.pager;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;


/**
 *
 */
public class AbstractPagerTest extends TestCase {
	public void testページ数の計算() throws Exception {
		PagerExt p = new PagerExt();
		//割り切れる
		p.size = 100;
		p.sizePerPage = 10;
		assertEquals("ページ数が誤っています。", 10, p.getPageSize());

		//割り切れない
		p.size = 100;
		p.sizePerPage = 9;
		assertEquals("ページ数が誤っています。", 12, p.getPageSize());
		
		//未満
		p.size = 0;
		p.sizePerPage = 10;
		assertEquals("ページ数が誤っています。", 1, p.getPageSize());
		
		//境界
		p.size = 9;
		p.sizePerPage = 10;
		assertEquals("ページ数が誤っています。", 1, p.getPageSize());
		p.size = 10;
		assertEquals("ページ数が誤っています。", 1, p.getPageSize());
		p.size = 11;
		assertEquals("ページ数が誤っています。", 2, p.getPageSize());
	}
	
	public void testページ指定() throws Exception {
		PagerExt p = new PagerExt();
		//100ページある状態
		p.size = 1000;
		p.sizePerPage = 10;
		assertEquals("ページ番号が誤っています。", 1, p.getCurrentPage());
		assertTrue(p.isFirst());
		assertFalse(p.isLast());
		try {
			p.previous();
			fail("先頭ページなのに前ページに移動できます。");
		} catch(Exception e) {
		}
		assertEquals("ページ番号が誤っています。", 1, p.getCurrentPage());
		assertTrue(p.isFirst());
		assertFalse(p.isLast());

		p.gotoPage(100);
		assertEquals("ページ番号が誤っています。", 100, p.getCurrentPage());
		assertFalse(p.isFirst());
		assertTrue(p.isLast());
		try {
			p.next();
			fail("最終ページなのに前ページに移動できます。");
		} catch(Exception e) {
		}
		assertEquals("ページ番号が誤っています。", 100, p.getCurrentPage());
		assertFalse(p.isFirst());
		assertTrue(p.isLast());
		
		try {
			p.gotoPage(101);
			fail("最大ページを超えたページ番号を指定できました");
		} catch(Exception e) {
			
		}

		try {
			p.gotoPage(0);
			fail("最小ページを超えたページ番号を指定できました");
		} catch(Exception e) {
			
		}
		
		//単ページでのページ確認
		p = new PagerExt();
		p.size = 0;
		p.sizePerPage = 10;
		assertEquals("ページ番号が誤っています。", 1, p.getCurrentPage());
		assertTrue(p.isFirst());
		assertTrue(p.isLast());
	}
	
	public void testページウインドウのテスト() {
		PagerExt p = new PagerExt();
		//100ページある状態
		p.size = 1000;
		p.sizePerPage = 10;
		
		//先頭
		p.gotoPage(1);
		assertEquals("ウインドウ開始が誤っています。", 1, p.getWindFirst());
		assertEquals("ウインドウ開始が誤っています。", 10, p.getWindFinish());

		//末尾
		p.gotoPage(100);
		assertEquals("ウインドウ開始が誤っています。", 91, p.getWindFirst());
		assertEquals("ウインドウ開始が誤っています。", 100, p.getWindFinish());

		//真ん中
		p.gotoPage(50);
		assertEquals("ウインドウ開始が誤っています。", 45, p.getWindFirst());
		assertEquals("ウインドウ開始が誤っています。", 54, p.getWindFinish());
	}
	
	public void test絶対要素位置のテスト() {
		PagerExt p = new PagerExt();
		//100ページある状態
		p.size = 998;
		p.sizePerPage = 100;
		
		//先頭
		p.gotoPage(1);
		assertEquals("要素開始位置が誤っています。", 1, p.getIndexAtFirstElement());
		assertEquals("ウインドウ開始が誤っています。", 100, p.getIndexAtLastElement());

		//末尾
		p.gotoPage(10);
		assertEquals("要素開始位置が誤っています。", 901, p.getIndexAtFirstElement());
		assertEquals("ウインドウ開始が誤っています。", 998, p.getIndexAtLastElement());
	}
	
	static class PagerExt extends AbstractPager<String> {
		private static final long serialVersionUID = 1L;
		@Override
		public List<String> getCurrentPageCollection() {
			return new ArrayList<String>();
		}

		long size = 100;
		@Override
		public long getSize() {
			return size;
		}

		int sizePerPage;
		@Override
		public int getSizePerPage() {
			return sizePerPage;
		}
	}
}
