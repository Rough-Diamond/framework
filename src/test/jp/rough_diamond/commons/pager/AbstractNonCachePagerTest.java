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
	public void testSetSizePerPage�Ō��ݒl�Ɠ����l���w�肵���ꍇ��refresh������Ȃ�����() throws Exception {
		Pager p = new Pager();
		p.setSizePerPage(10);
		assertFalse("���t���b�V�����Ăяo����Ă��܂��B", p.isRefresh);
		p.getCurrentPageCollection();
		assertTrue("���t���b�V�����Ăяo����Ă��܂���B", p.isRefresh);
		p.isRefresh = false;
		p.setSizePerPage(10);
		p.getCurrentPageCollection();
		assertFalse("���t���b�V�����Ăяo����Ă��܂��B", p.isRefresh);
	}
	
	public void testGotoPage�Ō��ݒl�Ɠ����l���w�肵���ꍇ��refresh������Ȃ�����() throws Exception {
		Pager p = new Pager();
		p.setSizePerPage(10);

		p.isRefresh = false;
		p.gotoPage(10);
		assertTrue("���t���b�V�����Ăяo����Ă��܂���B", p.isRefresh);

		p.isRefresh = false;
		p.getCurrentPageCollection();
		assertTrue("���t���b�V�����Ăяo����Ă��܂���B", p.isRefresh);

		p.isRefresh = false;
		p.gotoPage(10);
		p.getCurrentPageCollection();
		assertFalse("���t���b�V�����Ăяo����Ă��܂��B", p.isRefresh);

		p.isRefresh = false;
		p.gotoPage(11);
		p.getCurrentPageCollection();
		assertTrue("���t���b�V�����Ăяo����Ă��܂���B", p.isRefresh);
	}
	
	public void testSetSelectingPage�Ō��ݒl�Ɠ����l���w�肵���ꍇ��gotoPage���l�ł��邱��() throws Exception {
		Pager p = new Pager();
		p.setSizePerPage(10);

		p.isRefresh = false;
		p.setSelectingPage(10);
		assertTrue("���t���b�V�����Ăяo����Ă��܂���B", p.isRefresh);

		p.isRefresh = false;
		p.getCurrentPageCollection();
		assertTrue("���t���b�V�����Ăяo����Ă��܂���B", p.isRefresh);

		p.isRefresh = false;
		p.setSelectingPage(10);
		p.getCurrentPageCollection();
		assertFalse("���t���b�V�����Ăяo����Ă��܂��B", p.isRefresh);

		p.isRefresh = false;
		p.setSelectingPage(11);
		p.getCurrentPageCollection();
		assertTrue("���t���b�V�����Ăяo����Ă��܂���B", p.isRefresh);
	}

	public void test�y�[�W�Čv�Z�̏����������������Ă��邱��() throws Exception {
		Pager p = new Pager();
		p.count = 100;
		
		p.setSizePerPage(2);
		p.gotoPage(2);
		p.getCurrentPageCollection();
		assertEquals("�y�[�W�ԍ�������Ă��܂��B", 2, p.getCurrentPage());
		assertEquals("offset������Ă��܂��B", 2, p.offset);
		assertEquals("limit������Ă��܂��B", 2, p.limit);
		
		p.setSizePerPage(10);
		p.getCurrentPageCollection();
		assertEquals("�y�[�W�ԍ�������Ă��܂��B", 2, p.getCurrentPage());
		assertEquals("offset������Ă��܂��B", 10, p.offset);
		assertEquals("limit������Ă��܂��B", 10, p.limit);
		
		p.setSizePerPage(1000);
		p.getCurrentPageCollection();
		assertEquals("�y�[�W�ԍ�������Ă��܂��B", 1, p.getCurrentPage());
		assertEquals("offset������Ă��܂��B", 0, p.offset);
		assertEquals("limit������Ă��܂��B", 1000, p.limit);
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
