/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.service.hibernate;

import java.io.Serializable;

import jp.rough_diamond.commons.entity.Unit;
import jp.rough_diamond.commons.service.NumberingService;
import jp.rough_diamond.commons.testdata.NumberingLoader;
import jp.rough_diamond.commons.testdata.UnitLoader;
import jp.rough_diamond.commons.testing.DataLoadingTestCase;
import jp.rough_diamond.commons.testing.Loader;
import jp.rough_diamond.framework.service.Service;
import jp.rough_diamond.framework.service.ServiceLocator;

public class HibernateNumberingServiceTest extends DataLoadingTestCase {

	protected void setUp() throws Exception {
		super.setUp();
		Loader.load(UnitLoader.class);
		Loader.load(NumberingLoader.class);
	}
	
	public void testGetNumberByString() throws Exception {
		NumberingService service = ServiceLocator.getService(HibernateNumberingService.class);
		assertEquals("�ԋp�l������Ă��܂��B", service.getNumber("yamane"), 1L);
		assertEquals("�ԋp�l������Ă��܂��B", service.getNumber("jp.rough_diamond.commons.entity.Unit"), 3L);
		assertEquals("�ԋp�l������Ă��܂��B", service.getNumber("hoge"), 11L);
	}
	
	public void testGetNumberByClass() throws Exception {
		TestGetNumberByClassService service = ServiceLocator.getService(TestGetNumberByClassService.class);

		//�����q�̈������`�F�b�N���邱�Ƃ��ړI�Ȃ̂ŁAServiceLocator���o�R�����ɐ�������
		//�悢�q�͂܂˂����Ⴞ�߂ł��B
		NumberingService nService = new HibernateNumberingService(true);
		assertEquals("�ԋp�l������Ă��܂��B", service.doIt(nService), 3L);
		
		//�����q�̈������`�F�b�N���邱�Ƃ��ړI�Ȃ̂ŁAServiceLocator���o�R�����ɐ�������
		//�悢�q�͂܂˂����Ⴞ�߂ł��B
		nService = new HibernateNumberingService();
		assertEquals("�ԋp�l������Ă��܂��B", service.doIt(nService), 6L);
	}
	
	public static class TestGetNumberByClassService implements Service {
		public Serializable doIt(NumberingService nService) {
			return nService.getNumber(Unit.class);
		}
	}
}
