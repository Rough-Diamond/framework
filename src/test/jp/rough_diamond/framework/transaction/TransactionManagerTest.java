/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.transaction;

import java.util.List;

import jp.rough_diamond.commons.entity.ScalableNumber;
import jp.rough_diamond.commons.entity.Unit;
import jp.rough_diamond.commons.extractor.Condition;
import jp.rough_diamond.commons.extractor.Extractor;
import jp.rough_diamond.commons.extractor.Property;
import jp.rough_diamond.commons.service.BasicService;
import jp.rough_diamond.commons.testdata.NumberingLoader;
import jp.rough_diamond.commons.testdata.UnitLoader;
import jp.rough_diamond.commons.testing.DataLoadingTestCase;
import jp.rough_diamond.commons.testing.Loader;
import jp.rough_diamond.framework.service.Service;
import jp.rough_diamond.framework.service.ServiceLocator;

public class TransactionManagerTest extends DataLoadingTestCase {
	protected void setUp() throws Exception {
		super.setUp();
		Loader.load(NumberingLoader.class);
		Loader.load(UnitLoader.class);
	}
	
	public void testIsInTransaction() {
		assertFalse("�g�����U�N�V�����O�Ȃ̂�true���ԋp����Ă��܂��B", TransactionManager.isInTransaction());
		ServiceLocator.getService(ServiceExt.class).doIt();
	}

	public void testSetRollbackOnly() throws Exception {
		ServiceLocator.getService(RollbackTestService1.class).doIt();
		Extractor ex = new Extractor(Unit.class);
		ex.add(Condition.eq(new Property(Unit.NAME), "�ق��ق�"));
		List<Unit> list = BasicService.getService().findByExtractor(ex);
		assertEquals("�R�~�b�g����Ă��܂���B", 1, list.size());
		
		ex = new Extractor(Unit.class);
		ex.add(Condition.eq(new Property(Unit.NAME), "�ۂ��ۂ�"));
		list = BasicService.getService().findByExtractor(ex);
		assertEquals("�R�~�b�g����Ă��܂��B", 0, list.size());

		ex = new Extractor(Unit.class);
		ex.add(Condition.eq(new Property(Unit.NAME), "�ڂ��ڂ�"));
		list = BasicService.getService().findByExtractor(ex);
		assertEquals("�R�~�b�g����Ă��܂���B", 1, list.size());
	}
	
	public static class RollbackTestService1 implements Service {
		@TransactionAttribute(TransactionAttributeType.REQUIRED_NEW)
		public void doIt() throws Exception {
			Unit u = new Unit();
			u.setName("�ق��ق�");
			u.setBase(u);
			u.setRate(new ScalableNumber(1L, 0));
			u.setScale(1);
			BasicService.getService().insert(u);
			ServiceLocator.getService(RollbackTestService2.class).doIt();
		}
	}
	
	public static class RollbackTestService2 implements Service {
		@TransactionAttribute(TransactionAttributeType.REQUIRED_NEW)
		public void doIt() throws Exception {
			Unit u = new Unit();
			u.setName("�ۂ��ۂ�");
			u.setBase(u);
			u.setRate(new ScalableNumber(1L, 0));
			u.setScale(1);
			BasicService.getService().insert(u);
			TransactionManager.setRollBackOnly();
			ServiceLocator.getService(RollbackTestService3.class).doIt();
		}
	}
	
	public static class RollbackTestService3 implements Service {
		@TransactionAttribute(TransactionAttributeType.REQUIRED_NEW)
		public void doIt() throws Exception {
			Unit u = new Unit();
			u.setName("�ڂ��ڂ�");
			u.setBase(u);
			u.setRate(new ScalableNumber(1L, 0));
			u.setScale(1);
			BasicService.getService().insert(u);
		}
	}
	
	public static class ServiceExt implements Service {
		public void doIt() {
			assertTrue("�g�����U�N�V�������Ȃ̂�true���ԋp����Ă��܂�", TransactionManager.isInTransaction());
		}
	}
}
