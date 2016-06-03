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
	public void test�y�[�W���̌v�Z() throws Exception {
		PagerExt p = new PagerExt();
		//����؂��
		p.size = 100;
		p.sizePerPage = 10;
		assertEquals("�y�[�W��������Ă��܂��B", 10, p.getPageSize());

		//����؂�Ȃ�
		p.size = 100;
		p.sizePerPage = 9;
		assertEquals("�y�[�W��������Ă��܂��B", 12, p.getPageSize());
		
		//����
		p.size = 0;
		p.sizePerPage = 10;
		assertEquals("�y�[�W��������Ă��܂��B", 1, p.getPageSize());
		
		//���E
		p.size = 9;
		p.sizePerPage = 10;
		assertEquals("�y�[�W��������Ă��܂��B", 1, p.getPageSize());
		p.size = 10;
		assertEquals("�y�[�W��������Ă��܂��B", 1, p.getPageSize());
		p.size = 11;
		assertEquals("�y�[�W��������Ă��܂��B", 2, p.getPageSize());
	}
	
	public void test�y�[�W�w��() throws Exception {
		PagerExt p = new PagerExt();
		//100�y�[�W������
		p.size = 1000;
		p.sizePerPage = 10;
		assertEquals("�y�[�W�ԍ�������Ă��܂��B", 1, p.getCurrentPage());
		assertTrue(p.isFirst());
		assertFalse(p.isLast());
		try {
			p.previous();
			fail("�擪�y�[�W�Ȃ̂ɑO�y�[�W�Ɉړ��ł��܂��B");
		} catch(Exception e) {
		}
		assertEquals("�y�[�W�ԍ�������Ă��܂��B", 1, p.getCurrentPage());
		assertTrue(p.isFirst());
		assertFalse(p.isLast());

		p.gotoPage(100);
		assertEquals("�y�[�W�ԍ�������Ă��܂��B", 100, p.getCurrentPage());
		assertFalse(p.isFirst());
		assertTrue(p.isLast());
		try {
			p.next();
			fail("�ŏI�y�[�W�Ȃ̂ɑO�y�[�W�Ɉړ��ł��܂��B");
		} catch(Exception e) {
		}
		assertEquals("�y�[�W�ԍ�������Ă��܂��B", 100, p.getCurrentPage());
		assertFalse(p.isFirst());
		assertTrue(p.isLast());
		
		try {
			p.gotoPage(101);
			fail("�ő�y�[�W�𒴂����y�[�W�ԍ����w��ł��܂���");
		} catch(Exception e) {
			
		}

		try {
			p.gotoPage(0);
			fail("�ŏ��y�[�W�𒴂����y�[�W�ԍ����w��ł��܂���");
		} catch(Exception e) {
			
		}
		
		//�P�y�[�W�ł̃y�[�W�m�F
		p = new PagerExt();
		p.size = 0;
		p.sizePerPage = 10;
		assertEquals("�y�[�W�ԍ�������Ă��܂��B", 1, p.getCurrentPage());
		assertTrue(p.isFirst());
		assertTrue(p.isLast());
	}
	
	public void test�y�[�W�E�C���h�E�̃e�X�g() {
		PagerExt p = new PagerExt();
		//100�y�[�W������
		p.size = 1000;
		p.sizePerPage = 10;
		
		//�擪
		p.gotoPage(1);
		assertEquals("�E�C���h�E�J�n������Ă��܂��B", 1, p.getWindFirst());
		assertEquals("�E�C���h�E�J�n������Ă��܂��B", 10, p.getWindFinish());

		//����
		p.gotoPage(100);
		assertEquals("�E�C���h�E�J�n������Ă��܂��B", 91, p.getWindFirst());
		assertEquals("�E�C���h�E�J�n������Ă��܂��B", 100, p.getWindFinish());

		//�^��
		p.gotoPage(50);
		assertEquals("�E�C���h�E�J�n������Ă��܂��B", 45, p.getWindFirst());
		assertEquals("�E�C���h�E�J�n������Ă��܂��B", 54, p.getWindFinish());
	}
	
	public void test��Ηv�f�ʒu�̃e�X�g() {
		PagerExt p = new PagerExt();
		//100�y�[�W������
		p.size = 998;
		p.sizePerPage = 100;
		
		//�擪
		p.gotoPage(1);
		assertEquals("�v�f�J�n�ʒu������Ă��܂��B", 1, p.getIndexAtFirstElement());
		assertEquals("�E�C���h�E�J�n������Ă��܂��B", 100, p.getIndexAtLastElement());

		//����
		p.gotoPage(10);
		assertEquals("�v�f�J�n�ʒu������Ă��܂��B", 901, p.getIndexAtFirstElement());
		assertEquals("�E�C���h�E�J�n������Ă��܂��B", 998, p.getIndexAtLastElement());
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
