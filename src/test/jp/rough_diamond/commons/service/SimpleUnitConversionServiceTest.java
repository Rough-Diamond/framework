/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.service;

import java.math.BigDecimal;

import jp.rough_diamond.commons.entity.Amount;
import jp.rough_diamond.commons.entity.ScalableNumber;
import jp.rough_diamond.commons.entity.Unit;
import jp.rough_diamond.commons.testdata.NumberingLoader;
import jp.rough_diamond.commons.testdata.UnitLoader;
import jp.rough_diamond.commons.testing.DataLoadingTestCase;
import jp.rough_diamond.commons.testing.Loader;
import jp.rough_diamond.framework.service.ServiceLocator;

@SuppressWarnings("deprecation")
public class SimpleUnitConversionServiceTest extends DataLoadingTestCase {
	SimpleUnitConversionService service = ServiceLocator.getService(SimpleUnitConversionService.class);
	BasicService bService = BasicService.getService();

	protected void setUp() throws Exception {
		super.setUp();
		Loader.load(UnitLoader.class);
		Loader.load(NumberingLoader.class);
	}

	public void test変換後の数値が整数のケースで正しく単位変換が行われること() throws Exception {
		Unit unit = bService.findByPK(Unit.class, 2L);
		Amount srcAmount = new Amount();
		srcAmount.setUnit(unit);
		srcAmount.setQuantity(new ScalableNumber("10"));
		Amount dest = service.convertUnit(srcAmount, bService.findByPK(Unit.class, 1L));
		assertEquals("数量が誤っています。", 10000, dest.getQuantity().intValue());
		assertEquals("単位が誤っています。", 1L, dest.getUnit().getId().longValue());
	}
	
	public void test変換後の数値が小数のケースで正しく単位変換が行われること() throws Exception {
		Unit unit = bService.findByPK(Unit.class, 1L);
		Amount srcAmount = new Amount();
		srcAmount.setUnit(unit);
		srcAmount.setQuantity(new ScalableNumber("100"));
		Amount dest = service.convertUnit(srcAmount, bService.findByPK(Unit.class, 2L));
		assertEquals("数量が誤っています。", 0.1D, dest.getQuantity().doubleValue());
		assertEquals("単位が誤っています。", 2L, dest.getUnit().getId().longValue());
	}
	
	public void test変換係数に小数が含まれるケースで正しく単位変換が行われること() throws Exception {
		Unit unit = bService.findByPK(Unit.class, 1L);
		Amount srcAmount = new Amount();
		srcAmount.setUnit(unit);
		srcAmount.setQuantity(new ScalableNumber("2000"));
		System.out.println(2000 / 1609.344);
		Amount dest = service.convertUnit(srcAmount, bService.findByPK(Unit.class, 3L));
		assertEquals("数量が誤っています。", 1.24D, dest.getQuantity().doubleValue());
		assertEquals("単位が誤っています。", 3L, dest.getUnit().getId().longValue());
		dest = service.convertUnit(srcAmount, bService.findByPK(Unit.class, 3L), BigDecimal.ROUND_UP);
		assertEquals("数量が誤っています。", 1.25D, dest.getQuantity().doubleValue());
		assertEquals("単位が誤っています。", 3L, dest.getUnit().getId().longValue());
	}

	public void testスケールが負数のケースで正しく単位変換が行われること() throws Exception {
		Unit unit = bService.findByPK(Unit.class, 1L);
		Amount srcAmount = new Amount();
		srcAmount.setUnit(unit);
		srcAmount.setQuantity(new ScalableNumber("1.5"));
		Amount dest = service.convertUnit(srcAmount, bService.findByPK(Unit.class, 4L));
		assertEquals("数量が誤っています。", 200, dest.getQuantity().intValue());
		assertEquals("単位が誤っています。", 4L, dest.getUnit().getId().longValue());
	}
	
	public void testベースユニットが違うものに変換しようとした場合は例外が送出されること() throws Exception {
		Unit unit = bService.findByPK(Unit.class, 1L);
		Amount srcAmount = new Amount();
		srcAmount.setUnit(unit);
		srcAmount.setQuantity(new ScalableNumber("100"));
		try {
			service.convertUnit(srcAmount, bService.findByPK(Unit.class, 5L));
			fail("例外が送出されていません");
		} catch(UnitConversionService.NotConversionException e) {
		}
	}
	
	public void testAmountに単位が指定されていない場合でかつ変換後単位がnullでない場合は例外が送出されること() throws Exception {
		Amount srcAmount = new Amount(new BigDecimal("100"), null);
		try {
			service.convertUnit(srcAmount, bService.findByPK(Unit.class, 1L));
			fail("例外が送出されていません");
		} catch(UnitConversionService.NotConversionException e) {
		}
	}
	
	public void testAmountに単位が指定されていない場合でかつ変換後の単位がnullの場合はAmountのクローンを返却すること() throws Exception {
		Amount srcAmount = new Amount(new BigDecimal("100"), null);
		Amount dest = service.convertUnit(srcAmount, null);
		assertEquals("数量が誤っています。", 100, dest.getQuantity().intValue());
		assertNull("単位が指定されています", dest.getUnit());
		assertFalse(srcAmount == dest);
	}
	
	public void test変換後の単位がnullでAmountに単位が指定されていた場合は単位無しのAmountを生成して返却すること() throws Exception {
		Unit unit = bService.findByPK(Unit.class, 1L);
		Amount srcAmount = new Amount();
		srcAmount.setUnit(unit);
		srcAmount.setQuantity(new ScalableNumber("100"));
		Amount dest = service.convertUnit(srcAmount, null);
		assertEquals("数量が誤っています。", 100, dest.getQuantity().intValue());
		assertNull("単位が指定されています", dest.getUnit());
	}
	
	public void testAmountの単位と変換後の単位が同値の場合はAmountのクローンを返却すること() throws Exception {
		Unit unit = bService.findByPK(Unit.class, 1L);
		Amount srcAmount = new Amount();
		srcAmount.setUnit(unit);
		srcAmount.setQuantity(new ScalableNumber("100"));
		Amount dest = service.convertUnit(srcAmount, bService.findByPK(Unit.class, 1L));
		assertEquals("数量が誤っています。", 100, dest.getQuantity().intValue());
		assertEquals("単位が誤っています。", unit, dest.getUnit());
		assertFalse("クローンではなくそのものを返却しています。", srcAmount == dest);
	}
	
	public void testGetRoundingMode() throws Exception {
		int mode = SimpleUnitConversionService.getRoundingMode("ROUND_HALF_UP");
		assertEquals("値が間違っています。", BigDecimal.ROUND_HALF_UP, mode);
		try {
			SimpleUnitConversionService.getRoundingMode("XXXXX");
			fail("例外が送出されていません。");
		} catch(IllegalArgumentException e) {
			
		}
	}
}
