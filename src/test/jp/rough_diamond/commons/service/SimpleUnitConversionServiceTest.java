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

	public void test�ϊ���̐��l�������̃P�[�X�Ő������P�ʕϊ����s���邱��() throws Exception {
		Unit unit = bService.findByPK(Unit.class, 2L);
		Amount srcAmount = new Amount();
		srcAmount.setUnit(unit);
		srcAmount.setQuantity(new ScalableNumber("10"));
		Amount dest = service.convertUnit(srcAmount, bService.findByPK(Unit.class, 1L));
		assertEquals("���ʂ�����Ă��܂��B", 10000, dest.getQuantity().intValue());
		assertEquals("�P�ʂ�����Ă��܂��B", 1L, dest.getUnit().getId().longValue());
	}
	
	public void test�ϊ���̐��l�������̃P�[�X�Ő������P�ʕϊ����s���邱��() throws Exception {
		Unit unit = bService.findByPK(Unit.class, 1L);
		Amount srcAmount = new Amount();
		srcAmount.setUnit(unit);
		srcAmount.setQuantity(new ScalableNumber("100"));
		Amount dest = service.convertUnit(srcAmount, bService.findByPK(Unit.class, 2L));
		assertEquals("���ʂ�����Ă��܂��B", 0.1D, dest.getQuantity().doubleValue());
		assertEquals("�P�ʂ�����Ă��܂��B", 2L, dest.getUnit().getId().longValue());
	}
	
	public void test�ϊ��W���ɏ������܂܂��P�[�X�Ő������P�ʕϊ����s���邱��() throws Exception {
		Unit unit = bService.findByPK(Unit.class, 1L);
		Amount srcAmount = new Amount();
		srcAmount.setUnit(unit);
		srcAmount.setQuantity(new ScalableNumber("2000"));
		System.out.println(2000 / 1609.344);
		Amount dest = service.convertUnit(srcAmount, bService.findByPK(Unit.class, 3L));
		assertEquals("���ʂ�����Ă��܂��B", 1.24D, dest.getQuantity().doubleValue());
		assertEquals("�P�ʂ�����Ă��܂��B", 3L, dest.getUnit().getId().longValue());
		dest = service.convertUnit(srcAmount, bService.findByPK(Unit.class, 3L), BigDecimal.ROUND_UP);
		assertEquals("���ʂ�����Ă��܂��B", 1.25D, dest.getQuantity().doubleValue());
		assertEquals("�P�ʂ�����Ă��܂��B", 3L, dest.getUnit().getId().longValue());
	}

	public void test�X�P�[���������̃P�[�X�Ő������P�ʕϊ����s���邱��() throws Exception {
		Unit unit = bService.findByPK(Unit.class, 1L);
		Amount srcAmount = new Amount();
		srcAmount.setUnit(unit);
		srcAmount.setQuantity(new ScalableNumber("1.5"));
		Amount dest = service.convertUnit(srcAmount, bService.findByPK(Unit.class, 4L));
		assertEquals("���ʂ�����Ă��܂��B", 200, dest.getQuantity().intValue());
		assertEquals("�P�ʂ�����Ă��܂��B", 4L, dest.getUnit().getId().longValue());
	}
	
	public void test�x�[�X���j�b�g���Ⴄ���̂ɕϊ����悤�Ƃ����ꍇ�͗�O�����o����邱��() throws Exception {
		Unit unit = bService.findByPK(Unit.class, 1L);
		Amount srcAmount = new Amount();
		srcAmount.setUnit(unit);
		srcAmount.setQuantity(new ScalableNumber("100"));
		try {
			service.convertUnit(srcAmount, bService.findByPK(Unit.class, 5L));
			fail("��O�����o����Ă��܂���");
		} catch(UnitConversionService.NotConversionException e) {
		}
	}
	
	public void testAmount�ɒP�ʂ��w�肳��Ă��Ȃ��ꍇ�ł��ϊ���P�ʂ�null�łȂ��ꍇ�͗�O�����o����邱��() throws Exception {
		Amount srcAmount = new Amount(new BigDecimal("100"), null);
		try {
			service.convertUnit(srcAmount, bService.findByPK(Unit.class, 1L));
			fail("��O�����o����Ă��܂���");
		} catch(UnitConversionService.NotConversionException e) {
		}
	}
	
	public void testAmount�ɒP�ʂ��w�肳��Ă��Ȃ��ꍇ�ł��ϊ���̒P�ʂ�null�̏ꍇ��Amount�̃N���[����ԋp���邱��() throws Exception {
		Amount srcAmount = new Amount(new BigDecimal("100"), null);
		Amount dest = service.convertUnit(srcAmount, null);
		assertEquals("���ʂ�����Ă��܂��B", 100, dest.getQuantity().intValue());
		assertNull("�P�ʂ��w�肳��Ă��܂�", dest.getUnit());
		assertFalse(srcAmount == dest);
	}
	
	public void test�ϊ���̒P�ʂ�null��Amount�ɒP�ʂ��w�肳��Ă����ꍇ�͒P�ʖ�����Amount�𐶐����ĕԋp���邱��() throws Exception {
		Unit unit = bService.findByPK(Unit.class, 1L);
		Amount srcAmount = new Amount();
		srcAmount.setUnit(unit);
		srcAmount.setQuantity(new ScalableNumber("100"));
		Amount dest = service.convertUnit(srcAmount, null);
		assertEquals("���ʂ�����Ă��܂��B", 100, dest.getQuantity().intValue());
		assertNull("�P�ʂ��w�肳��Ă��܂�", dest.getUnit());
	}
	
	public void testAmount�̒P�ʂƕϊ���̒P�ʂ����l�̏ꍇ��Amount�̃N���[����ԋp���邱��() throws Exception {
		Unit unit = bService.findByPK(Unit.class, 1L);
		Amount srcAmount = new Amount();
		srcAmount.setUnit(unit);
		srcAmount.setQuantity(new ScalableNumber("100"));
		Amount dest = service.convertUnit(srcAmount, bService.findByPK(Unit.class, 1L));
		assertEquals("���ʂ�����Ă��܂��B", 100, dest.getQuantity().intValue());
		assertEquals("�P�ʂ�����Ă��܂��B", unit, dest.getUnit());
		assertFalse("�N���[���ł͂Ȃ����̂��̂�ԋp���Ă��܂��B", srcAmount == dest);
	}
	
	public void testGetRoundingMode() throws Exception {
		int mode = SimpleUnitConversionService.getRoundingMode("ROUND_HALF_UP");
		assertEquals("�l���Ԉ���Ă��܂��B", BigDecimal.ROUND_HALF_UP, mode);
		try {
			SimpleUnitConversionService.getRoundingMode("XXXXX");
			fail("��O�����o����Ă��܂���B");
		} catch(IllegalArgumentException e) {
			
		}
	}
}
