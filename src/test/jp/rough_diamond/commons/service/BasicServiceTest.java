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
		assertEquals("全体件数が誤ってます。", 5, fr.count);
		assertEquals("取得件数が誤っています。", 1, fr.list.size());
	}
	
	public void testJoinを用いた検索結果でコレクションサイズと件数が一致しない不具合解消のテスト() throws Exception {
		Extractor ex = new Extractor(Unit.class);
		ex.addInnerJoin(new InnerJoin(new Property(Unit.class, null, null),
				new Property(Unit.class, "child", Unit.BASE)));
		ex.add(Condition.eq(new Property(Unit.class, "child", Unit.BASE +"." + Unit.ID), 1L));
		FindResult<Unit> result = BasicService.getService().findByExtractorWithCount(ex);
		assertEquals("件数が誤っています。", 4, result.list.size());
		assertEquals("総件数が誤っています。", 4, result.count);
		
		ex.setDistinct(true);
		result = BasicService.getService().findByExtractorWithCount(ex);
		assertEquals("件数が誤っています。", 1, result.list.size());
		assertEquals("総件数が誤っています。", 1, result.count);

		//ExtractValue付きの場合はPreparedStatementを使用するのでこっちも確認
		ex.addExtractValue(new ExtractValue("id", new Property(Unit.ID)));
		ex.setDistinct(false);
		result = BasicService.getService().findByExtractorWithCount(ex);
		assertEquals("件数が誤っています。", 4, result.list.size());
		assertEquals("総件数が誤っています。", 4, result.count);

		ex.setDistinct(true);
		result = BasicService.getService().findByExtractorWithCount(ex);
		assertEquals("件数が誤っています。", 1, result.list.size());
		assertEquals("総件数が誤っています。", 1, result.count);
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
			assertEquals("返却数が誤っています。", 5, listeners.size());
			
			
			Unit u = BasicService.getService().findByPK(Unit.class, 1L);
			Messages msgs = u.validateObject();
			assertFalse("エラーが発生しています。", msgs.hasError());

			u.setName("verify");
			msgs = u.validateObject();
			System.out.println(msgs);
			assertFalse("エラーが発生しています。", msgs.hasError());

			u.setName("verify1");
			msgs = u.validateObject();
			System.out.println(msgs);
			assertTrue("エラーが発生していません。", msgs.hasError());

			u.setName("verify2");
			msgs = u.validateObject();
			System.out.println(msgs);
			assertTrue("エラーが発生していません。", msgs.hasError());

			u.setName("verify3");
			msgs = u.validateObject();
			System.out.println(msgs);
			assertTrue("エラーが発生していません。", msgs.hasError());

			u.setName("verify4");
			msgs = u.validateObject();
			System.out.println(msgs);
			assertFalse("エラーが発生しています。", msgs.hasError());

			msgs = BasicService.getService().validate(u, WhenVerifier.INSERT);
			System.out.println(msgs);
			assertTrue("エラーが発生していません。", msgs.hasError());
			
			u.setName("verify5");
			msgs = u.validateObject();
			System.out.println(msgs);
			assertFalse("エラーが発生していませす。", msgs.hasError());
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
			assertEquals("返却値が数が誤っています。", 1, list.size());
			assertEquals("コールバックメソッド呼び出し回数が誤っています。", 6, list.get(0).list.size());
			int index = 0;
			assertEquals("コールバックメソッド呼び出し順序が誤っています.", "CallbackListener#postLoad5", 	list.get(0).list.get(index++));
			assertEquals("コールバックメソッド呼び出し順序が誤っています.", "postLoad3", 					list.get(0).list.get(index++));
			assertEquals("コールバックメソッド呼び出し順序が誤っています.", "CallbackListener#apostLoad3", 	list.get(0).list.get(index++));
			assertEquals("コールバックメソッド呼び出し順序が誤っています.", "CallbackListener#postLoad3", 	list.get(0).list.get(index++));
			assertEquals("コールバックメソッド呼び出し順序が誤っています.", "postLoad1", 					list.get(0).list.get(index++));
			assertEquals("コールバックメソッド呼び出し順序が誤っています.", "CallbackListener#postLoad1", 	list.get(0).list.get(index++));
		} finally {
			DIContainerFactory.setDIContainer(real);
		}
	}
	
	public void test更新メソッドを使わずにデータが更新されないこと() throws Exception {
		ServiceLocator.getService(更新メソッドを使わずにデータが更新されないことを確認するService.class).test1();
		Unit u = BasicService.getService().findByPK(Unit.class, 1L);
		assertEquals("ユニット名が誤っています。", "m", u.getName());
	}
	
	public void test更新メソッドを叩いた後のデータが更新されないこと() throws Exception {
		Unit u1 = ServiceLocator.getService(更新メソッドを使わずにデータが更新されないことを確認するService.class).test2();
		Unit u2 = BasicService.getService().findByPK(Unit.class, 1L);
		assertEquals("ユニット名が誤っています。", "xyz", u2.getName());
		assertEquals("ユニット名が誤っています。", "abc", u1.getName());
	}
	
	public void test更新を２回叩いた場合は後のデータで更新されていること() throws Exception {
		ServiceLocator.getService(更新メソッドを使わずにデータが更新されないことを確認するService.class).test3();
		Unit u = BasicService.getService().findByPK(Unit.class, 1L);
		assertEquals("ユニット名が誤っています。", "abc", u.getName());
	}
	
	public void test新規作成したオブジェクトを挿入後値を書き換えても反映されないこと() throws Exception {
		ServiceLocator.getService(更新メソッドを使わずにデータが更新されないことを確認するService.class).test4();
		Extractor ex = new Extractor(Unit.class);
		ex.add(Condition.eq(new Property(Unit.NAME), "適当データ"));
		Unit u = (Unit)BasicService.getService().findByExtractor(ex).get(0);
		assertNull("備考が誤っています。", u.getDescription());
	}
	
	public void test新規作成したオブジェクトを挿入後更新したら更新後の値が反映されること() throws Exception {
		ServiceLocator.getService(更新メソッドを使わずにデータが更新されないことを確認するService.class).test5();
		Extractor ex = new Extractor(Unit.class);
		ex.add(Condition.eq(new Property(Unit.NAME), "適当データ"));
		Unit u = (Unit)BasicService.getService().findByExtractor(ex).get(0);
		assertEquals("備考が誤っています。", "xyz", u.getDescription());
	}
	
	public void test削除に影響を与えていないこと() throws Exception {
		BasicService.getService().deleteByPK(Unit.class, 4L);
		assertNull("削除に失敗しています。", BasicService.getService().findByPK(Unit.class, 4L));
	}
	
	public void testBasicServiceを介さずにsaveOrUpdateを叩いても同様の動作をすること() throws Exception {
		Unit u1 = ServiceLocator.getService(更新メソッドを使わずにデータが更新されないことを確認するService.class).test6();
		Unit u2 = BasicService.getService().findByPK(Unit.class, 1L);
		assertEquals("ユニット名が誤っています。", "xyz", u2.getName());
		assertEquals("ユニット名が誤っています。", "abc", u1.getName());
	}
	
	public void testIsSkipUniqueCheckType() throws Exception {
		BasicService bs = BasicService.getService();
		List<String> list = new ArrayList<String>();
		list.add(Unit.class.getName());
		assertFalse("Unitクラスのユニークチェックをします。", bs.isSkipUniqueCheckType2(Unit.class, null));
		assertTrue("Unitクラスのユニークチェックをしません。", bs.isSkipUniqueCheckType2(Unit.class, list));
		assertFalse("Numberingクラスのユニークチェックをします。", bs.isSkipUniqueCheckType2(Numbering.class, list));
		list.add(Object.class.getName());
		assertTrue("Unitクラスのユニークチェックをしません。", bs.isSkipUniqueCheckType2(Unit.class, list));
		assertTrue("Numberingクラスのユニークチェックをしません。", bs.isSkipUniqueCheckType2(Numbering.class, list));
	}
	
	public static class 更新メソッドを使わずにデータが更新されないことを確認するService implements Service {
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
			u.setName("適当データ");
			u.setBase(u);
			u.setRate(new ScalableNumber(1L, 0));
			u.setScale(1);
			u.save();
			u.setDescription("xyz");
		}
		
		public void test5() throws Exception {
			Unit u = new Unit();
			u.setName("適当データ");
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
		//自己参照イベントより後に来ること
		@PostLoad(priority = 3)
		public void postLoad3(CallbackTestType object) {
			object.list.add("CallbackListener#postLoad3");
		}
		//postLoad3と優先順位が一緒の場合は文字コードの昇順に実行されること
		@PostLoad(priority = 3)
		public void aPostLoad3(CallbackTestType object) {
			object.list.add("CallbackListener#apostLoad3");
		}
		//引数なしの場合は呼び出されないこと
		@PostLoad(priority = 3)
		public void postLoad3() { 
			Assert.fail("呼ばれてはいけないメソッドが呼び出されています");
		}
		//違うクラスの場合は呼び出されないこと
		@PostLoad(priority = 3)
		public void postLoad3(String str) { 
			Assert.fail("呼ばれてはいけないメソッドが呼び出されています");
		}
		//親クラスの場合は受け入れ可能であること
		@PostLoad(priority=1)
		public void postLoad1(Object o) {
			((CallbackTestType)o).list.add("CallbackListener#postLoad1");
		}
		//EventTypeを受ける時も呼び出されること
		@PostLoad(priority=5)
		public void postLoad5(Object o, CallbackEventType type) {
			((CallbackTestType)o).list.add("CallbackListener#postLoad5");
		}
		//引数３つ以上は呼び出されないこと
		@PostLoad(priority=5)
		public void postLoad5(Object o, CallbackEventType type, String hoge) {
			Assert.fail("呼ばれてはいけないメソッドが呼び出されています");
		}
		//コールバックアノテーションがないので呼び出されないこと
		public void foo(Object o) {
			Assert.fail("呼ばれてはいけないメソッドが呼び出されています");
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
