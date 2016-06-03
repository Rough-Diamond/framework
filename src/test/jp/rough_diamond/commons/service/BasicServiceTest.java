/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import jp.rough_diamond.commons.di.CompositeDIContainer;
import jp.rough_diamond.commons.di.DIContainer;
import jp.rough_diamond.commons.di.DIContainerFactory;
import jp.rough_diamond.commons.di.MapDIContainer;
import jp.rough_diamond.commons.entity.Numbering;
import jp.rough_diamond.commons.entity.ScalableNumber;
import jp.rough_diamond.commons.entity.Unit;
import jp.rough_diamond.commons.extractor.Condition;
import jp.rough_diamond.commons.extractor.ExtractValue;
import jp.rough_diamond.commons.extractor.Extractor;
import jp.rough_diamond.commons.extractor.InnerJoin;
import jp.rough_diamond.commons.extractor.Property;
import jp.rough_diamond.commons.resource.Message;
import jp.rough_diamond.commons.resource.Messages;
import jp.rough_diamond.commons.service.annotation.PostLoad;
import jp.rough_diamond.commons.service.annotation.Verifier;
import jp.rough_diamond.commons.testdata.NumberingLoader;
import jp.rough_diamond.commons.testdata.UnitLoader;
import jp.rough_diamond.commons.testing.DataLoadingTestCase;
import jp.rough_diamond.commons.testing.Loader;
import jp.rough_diamond.framework.service.Service;
import jp.rough_diamond.framework.service.ServiceLocator;
import jp.rough_diamond.framework.transaction.hibernate.HibernateUtils;
import junit.framework.Assert;

/**
 *
 */
public class BasicServiceTest extends DataLoadingTestCase {
	protected void setUp() throws Exception {
		super.setUp();
		Loader.load(UnitLoader.class);
		Loader.load(NumberingLoader.class);
	}

	public void testFindByExtractorWithCount() throws Exception {
		Extractor e = new Extractor(Unit.class);
		e.setLimit(1);
		FindResult<Unit> fr = BasicService.getService().findByExtractorWithCount(e);
		assertEquals("�S�̌���������Ă܂��B", 5, fr.count);
		assertEquals("�擾����������Ă��܂��B", 1, fr.list.size());
	}
	
	public void testJoin��p�����������ʂŃR���N�V�����T�C�Y�ƌ�������v���Ȃ��s������̃e�X�g() throws Exception {
		Extractor ex = new Extractor(Unit.class);
		ex.addInnerJoin(new InnerJoin(new Property(Unit.class, null, null),
				new Property(Unit.class, "child", Unit.BASE)));
		ex.add(Condition.eq(new Property(Unit.class, "child", Unit.BASE +"." + Unit.ID), 1L));
		FindResult<Unit> result = BasicService.getService().findByExtractorWithCount(ex);
		assertEquals("����������Ă��܂��B", 4, result.list.size());
		assertEquals("������������Ă��܂��B", 4, result.count);
		
		ex.setDistinct(true);
		result = BasicService.getService().findByExtractorWithCount(ex);
		assertEquals("����������Ă��܂��B", 1, result.list.size());
		assertEquals("������������Ă��܂��B", 1, result.count);

		//ExtractValue�t���̏ꍇ��PreparedStatement���g�p����̂ł��������m�F
		ex.addExtractValue(new ExtractValue("id", new Property(Unit.ID)));
		ex.setDistinct(false);
		result = BasicService.getService().findByExtractorWithCount(ex);
		assertEquals("����������Ă��܂��B", 4, result.list.size());
		assertEquals("������������Ă��܂��B", 4, result.count);

		ex.setDistinct(true);
		result = BasicService.getService().findByExtractorWithCount(ex);
		assertEquals("����������Ă��܂��B", 1, result.list.size());
		assertEquals("������������Ă��܂��B", 1, result.count);
	}
	
	public void testVerifierCallback() throws Exception {
		List<Object> list2 = new ArrayList<Object>();
		list2.add(new VerifierCallbackListener());
		DIContainer real = DIContainerFactory.getDIContainer();
		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put(BasicService.PERSISTENCE_EVENT_LISTENERS, list2);
		MapDIContainer wrapper = new MapDIContainer(map);
		DIContainerFactory.setDIContainer(new CompositeDIContainer(
				Arrays.asList(new DIContainer[]{wrapper, real})));
		System.out.println(DIContainerFactory.getDIContainer().getClass().getName());
		try {
			SortedSet<CallbackEventListener> listeners = BasicService.getService().getEventListener(Unit.class, CallbackEventType.VERIFIER);
			assertEquals("�ԋp��������Ă��܂��B", 5, listeners.size());
			
			
			Unit u = BasicService.getService().findByPK(Unit.class, 1L);
			Messages msgs = u.validateObject();
			assertFalse("�G���[���������Ă��܂��B", msgs.hasError());

			u.setName("verify");
			msgs = u.validateObject();
			System.out.println(msgs);
			assertFalse("�G���[���������Ă��܂��B", msgs.hasError());

			u.setName("verify1");
			msgs = u.validateObject();
			System.out.println(msgs);
			assertTrue("�G���[���������Ă��܂���B", msgs.hasError());

			u.setName("verify2");
			msgs = u.validateObject();
			System.out.println(msgs);
			assertTrue("�G���[���������Ă��܂���B", msgs.hasError());

			u.setName("verify3");
			msgs = u.validateObject();
			System.out.println(msgs);
			assertTrue("�G���[���������Ă��܂���B", msgs.hasError());

			u.setName("verify4");
			msgs = u.validateObject();
			System.out.println(msgs);
			assertFalse("�G���[���������Ă��܂��B", msgs.hasError());

			msgs = BasicService.getService().validate(u, WhenVerifier.INSERT);
			System.out.println(msgs);
			assertTrue("�G���[���������Ă��܂���B", msgs.hasError());
			
			u.setName("verify5");
			msgs = u.validateObject();
			System.out.println(msgs);
			assertFalse("�G���[���������Ă��܂����B", msgs.hasError());
		} finally {
			DIContainerFactory.setDIContainer(real);
		}
	}
	
	public void testEventCallback() throws Exception {
		List<Object> list2 = new ArrayList<Object>();
		list2.add(new CallbackListener());
		DIContainer real = DIContainerFactory.getDIContainer();
		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put(BasicService.PERSISTENCE_EVENT_LISTENERS, list2);
		MapDIContainer wrapper = new MapDIContainer(map);
		DIContainerFactory.setDIContainer(new CompositeDIContainer(
				Arrays.asList(new DIContainer[]{wrapper, real})));
		System.out.println(DIContainerFactory.getDIContainer().getClass().getName());
		try {
			Extractor e = new Extractor(Unit.class);
			e.addExtractValue(new ExtractValue("id", new Property(Unit.ID)));
			e.add(Condition.eq(new Property(Unit.ID), 1));
			e.setReturnType(CallbackTestType.class);
			System.out.println(DIContainerFactory.getDIContainer().getClass().getName());
			List<CallbackTestType> list = BasicService.getService().findByExtractor(e);
			System.out.println(DIContainerFactory.getDIContainer().getClass().getName());
			assertEquals("�ԋp�l����������Ă��܂��B", 1, list.size());
			assertEquals("�R�[���o�b�N���\�b�h�Ăяo���񐔂�����Ă��܂��B", 6, list.get(0).list.size());
			int index = 0;
			assertEquals("�R�[���o�b�N���\�b�h�Ăяo������������Ă��܂�.", "CallbackListener#postLoad5", 	list.get(0).list.get(index++));
			assertEquals("�R�[���o�b�N���\�b�h�Ăяo������������Ă��܂�.", "postLoad3", 					list.get(0).list.get(index++));
			assertEquals("�R�[���o�b�N���\�b�h�Ăяo������������Ă��܂�.", "CallbackListener#apostLoad3", 	list.get(0).list.get(index++));
			assertEquals("�R�[���o�b�N���\�b�h�Ăяo������������Ă��܂�.", "CallbackListener#postLoad3", 	list.get(0).list.get(index++));
			assertEquals("�R�[���o�b�N���\�b�h�Ăяo������������Ă��܂�.", "postLoad1", 					list.get(0).list.get(index++));
			assertEquals("�R�[���o�b�N���\�b�h�Ăяo������������Ă��܂�.", "CallbackListener#postLoad1", 	list.get(0).list.get(index++));
		} finally {
			DIContainerFactory.setDIContainer(real);
		}
	}
	
	public void test�X�V���\�b�h���g�킸�Ƀf�[�^���X�V����Ȃ�����() throws Exception {
		ServiceLocator.getService(�X�V���\�b�h���g�킸�Ƀf�[�^���X�V����Ȃ����Ƃ��m�F����Service.class).test1();
		Unit u = BasicService.getService().findByPK(Unit.class, 1L);
		assertEquals("���j�b�g��������Ă��܂��B", "m", u.getName());
	}
	
	public void test�X�V���\�b�h��@������̃f�[�^���X�V����Ȃ�����() throws Exception {
		Unit u1 = ServiceLocator.getService(�X�V���\�b�h���g�킸�Ƀf�[�^���X�V����Ȃ����Ƃ��m�F����Service.class).test2();
		Unit u2 = BasicService.getService().findByPK(Unit.class, 1L);
		assertEquals("���j�b�g��������Ă��܂��B", "xyz", u2.getName());
		assertEquals("���j�b�g��������Ă��܂��B", "abc", u1.getName());
	}
	
	public void test�X�V���Q��@�����ꍇ�͌�̃f�[�^�ōX�V����Ă��邱��() throws Exception {
		ServiceLocator.getService(�X�V���\�b�h���g�킸�Ƀf�[�^���X�V����Ȃ����Ƃ��m�F����Service.class).test3();
		Unit u = BasicService.getService().findByPK(Unit.class, 1L);
		assertEquals("���j�b�g��������Ă��܂��B", "abc", u.getName());
	}
	
	public void test�V�K�쐬�����I�u�W�F�N�g��}����l�����������Ă����f����Ȃ�����() throws Exception {
		ServiceLocator.getService(�X�V���\�b�h���g�킸�Ƀf�[�^���X�V����Ȃ����Ƃ��m�F����Service.class).test4();
		Extractor ex = new Extractor(Unit.class);
		ex.add(Condition.eq(new Property(Unit.NAME), "�K���f�[�^"));
		Unit u = (Unit)BasicService.getService().findByExtractor(ex).get(0);
		assertNull("���l������Ă��܂��B", u.getDescription());
	}
	
	public void test�V�K�쐬�����I�u�W�F�N�g��}����X�V������X�V��̒l�����f����邱��() throws Exception {
		ServiceLocator.getService(�X�V���\�b�h���g�킸�Ƀf�[�^���X�V����Ȃ����Ƃ��m�F����Service.class).test5();
		Extractor ex = new Extractor(Unit.class);
		ex.add(Condition.eq(new Property(Unit.NAME), "�K���f�[�^"));
		Unit u = (Unit)BasicService.getService().findByExtractor(ex).get(0);
		assertEquals("���l������Ă��܂��B", "xyz", u.getDescription());
	}
	
	public void test�폜�ɉe����^���Ă��Ȃ�����() throws Exception {
		BasicService.getService().deleteByPK(Unit.class, 4L);
		assertNull("�폜�Ɏ��s���Ă��܂��B", BasicService.getService().findByPK(Unit.class, 4L));
	}
	
	public void testBasicService�������saveOrUpdate��@���Ă����l�̓�������邱��() throws Exception {
		Unit u1 = ServiceLocator.getService(�X�V���\�b�h���g�킸�Ƀf�[�^���X�V����Ȃ����Ƃ��m�F����Service.class).test6();
		Unit u2 = BasicService.getService().findByPK(Unit.class, 1L);
		assertEquals("���j�b�g��������Ă��܂��B", "xyz", u2.getName());
		assertEquals("���j�b�g��������Ă��܂��B", "abc", u1.getName());
	}
	
	public void testIsSkipUniqueCheckType() throws Exception {
		BasicService bs = BasicService.getService();
		List<String> list = new ArrayList<String>();
		list.add(Unit.class.getName());
		assertFalse("Unit�N���X�̃��j�[�N�`�F�b�N�����܂��B", bs.isSkipUniqueCheckType2(Unit.class, null));
		assertTrue("Unit�N���X�̃��j�[�N�`�F�b�N�����܂���B", bs.isSkipUniqueCheckType2(Unit.class, list));
		assertFalse("Numbering�N���X�̃��j�[�N�`�F�b�N�����܂��B", bs.isSkipUniqueCheckType2(Numbering.class, list));
		list.add(Object.class.getName());
		assertTrue("Unit�N���X�̃��j�[�N�`�F�b�N�����܂���B", bs.isSkipUniqueCheckType2(Unit.class, list));
		assertTrue("Numbering�N���X�̃��j�[�N�`�F�b�N�����܂���B", bs.isSkipUniqueCheckType2(Numbering.class, list));
	}
	
	public static class �X�V���\�b�h���g�킸�Ƀf�[�^���X�V����Ȃ����Ƃ��m�F����Service implements Service {
		public void test1() {
			Unit u = BasicService.getService().findByPK(Unit.class, 1L);
			u.setName("xyz");
		}
		
		public Unit test2() throws Exception {
			Unit u = BasicService.getService().findByPK(Unit.class, 1L);
			u.setName("xyz");
			u.save();
			u.setName("abc");
			return u;
		}

		public void test3() throws Exception {
			Unit u = BasicService.getService().findByPK(Unit.class, 1L);
			u.setName("xyz");
			u.save();
			u.setName("abc");
			u.save();
		}

		public void test4() throws Exception {
			Unit u = new Unit();
			u.setName("�K���f�[�^");
			u.setBase(u);
			u.setRate(new ScalableNumber(1L, 0));
			u.setScale(1);
			u.save();
			u.setDescription("xyz");
		}
		
		public void test5() throws Exception {
			Unit u = new Unit();
			u.setName("�K���f�[�^");
			u.setBase(u);
			u.setRate(new ScalableNumber(1L, 0));
			u.setScale(1);
			u.save();
			u.setDescription("xyz");
			u.save();
		}

		public Unit test6() throws Exception {
			Unit u = BasicService.getService().findByPK(Unit.class, 1L);
			u.setName("xyz");
			HibernateUtils.getSession().saveOrUpdate(u);
			u.setName("abc");
			return u;
		}
	}
	
	public static class VerifierCallbackListener {
		@Verifier
		public Messages verifyUnit(Unit unit) {
			Messages ret = new Messages();
			if(unit.getName().equals("verify1")) {
				ret.add("unit.name", new Message("errors.duplicate", "1"));
			}
			return ret;
		}
		
		@Verifier
		public Messages verifyUnit(Unit unit, CallbackEventType eventType) {
			Messages ret = new Messages();
			if(unit.getName().equals("verify2")) {
				ret.add("unit.name", new Message("errors.duplicate", "2"));
			}
			return ret;
		}

		@Verifier
		public Messages verifyUnit(Unit unit, WhenVerifier when) {
			Messages ret = new Messages();
			if(unit.getName().equals("verify3")) {
				ret.add("unit.name", new Message("errors.duplicate", "3"));
			}
			return ret;
		}

		@Verifier(when = {WhenVerifier.INSERT})
		public Messages verifyUnit(Unit unit, CallbackEventType eventType, WhenVerifier when) {
			Messages ret = new Messages();
			if(unit.getName().equals("verify4")) {
				ret.add("unit.name", new Message("errors.duplicate", "4"));
			}
			return ret;
		}

		@Verifier
		public Messages verifyObject(Object unit) {
			Messages ret = new Messages();
			return ret;
		}

		@Verifier
		public Messages verifyUnitExt(UnitExt unit) {
			Messages ret = new Messages();
			return ret;
		}
	}
	
	public static class UnitExt extends Unit {}
	
	public static class CallbackListener {
		//���ȎQ�ƃC�x���g����ɗ��邱��
		@PostLoad(priority = 3)
		public void postLoad3(CallbackTestType object) {
			object.list.add("CallbackListener#postLoad3");
		}
		//postLoad3�ƗD�揇�ʂ��ꏏ�̏ꍇ�͕����R�[�h�̏����Ɏ��s����邱��
		@PostLoad(priority = 3)
		public void aPostLoad3(CallbackTestType object) {
			object.list.add("CallbackListener#apostLoad3");
		}
		//�����Ȃ��̏ꍇ�͌Ăяo����Ȃ�����
		@PostLoad(priority = 3)
		public void postLoad3() { 
			Assert.fail("�Ă΂�Ă͂����Ȃ����\�b�h���Ăяo����Ă��܂�");
		}
		//�Ⴄ�N���X�̏ꍇ�͌Ăяo����Ȃ�����
		@PostLoad(priority = 3)
		public void postLoad3(String str) { 
			Assert.fail("�Ă΂�Ă͂����Ȃ����\�b�h���Ăяo����Ă��܂�");
		}
		//�e�N���X�̏ꍇ�͎󂯓���\�ł��邱��
		@PostLoad(priority=1)
		public void postLoad1(Object o) {
			((CallbackTestType)o).list.add("CallbackListener#postLoad1");
		}
		//EventType���󂯂鎞���Ăяo����邱��
		@PostLoad(priority=5)
		public void postLoad5(Object o, CallbackEventType type) {
			((CallbackTestType)o).list.add("CallbackListener#postLoad5");
		}
		//�����R�ȏ�͌Ăяo����Ȃ�����
		@PostLoad(priority=5)
		public void postLoad5(Object o, CallbackEventType type, String hoge) {
			Assert.fail("�Ă΂�Ă͂����Ȃ����\�b�h���Ăяo����Ă��܂�");
		}
		//�R�[���o�b�N�A�m�e�[�V�������Ȃ��̂ŌĂяo����Ȃ�����
		public void foo(Object o) {
			Assert.fail("�Ă΂�Ă͂����Ȃ����\�b�h���Ăяo����Ă��܂�");
		}
	}
	
	public static class CallbackTestType {
		private List<String> list = new ArrayList<String>();
		public CallbackTestType(Long id) { }
		
		@PostLoad(priority = 3)
		public void postLoad3() {
			list.add("postLoad3");
		}

		@PostLoad(priority = 1)
		public void postLoad1() {
			list.add("postLoad1");
		}
	}
}
