/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.service;

import java.math.BigDecimal;
import java.util.Date;

import jp.rough_diamond.commons.entity.Quantity;
import jp.rough_diamond.commons.entity.ScalableNumber;
import jp.rough_diamond.commons.entity.Unit;

/**
 * �P�ʕϊ��T�[�r�X�̃V���v������
 */
public class SimpleUnitConversionService extends UnitConversionService {
	/**
	 * �����q
	 * �ۂߌ덷�v�Z���[�h���ȗ����ꂽ�ꍇ�͎l�̌ܓ��iROUND_HALF_UP�j���s���܂�
	 */
	public SimpleUnitConversionService() {
		this("ROUND_HALF_UP");
	}
	
	/**
	 * �����q
	 * �w�肳�ꂽ������ɑΉ�����ۂߌ덷���[�h���f�t�H���g�Ƃ��܂��B
	 * @param mode
	 * @see java.math.BigDecimal
	 */
	public SimpleUnitConversionService(String mode) {
		this.defaultRoundingMode = getRoundingMode(mode);
	}
	
	static int getRoundingMode(String mode) {
		try {
			return BigDecimal.class.getField(mode).getInt(null);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	private final int defaultRoundingMode;
	
	@Override
	public Quantity convertUnit(Quantity srcQuantity, Unit destUnit, Date d) {
		return convertUnit(srcQuantity, destUnit, defaultRoundingMode, d);
	}

	@Override
	public Quantity convertUnit(Quantity srcQuantity, Unit destUnit, int roundingMode, Date d) throws NotConversionException {
		if(srcQuantity.getUnit() == null && destUnit != null) {
			throw new NotConversionException();
		}
		if(srcQuantity.getUnit() == null && destUnit == null) {
			return new Quantity(srcQuantity.getAmount(), null);
		}
		if(srcQuantity.getUnit() != null && destUnit == null) {
			return new Quantity(srcQuantity.getAmount(), null);
		}
		if(srcQuantity.getUnit().equals(destUnit)) {
			return new Quantity(srcQuantity.getAmount(), destUnit);
		}
		if(!srcQuantity.getUnit().getBase().equals(destUnit.getBase())) {
			throw new NotConversionException();
		}
		//[���̐�] �~ [���̒P�ʂ̌W��] / [�ϊ���̒P�ʂ̌W��]
		BigDecimal bd = srcQuantity.getAmount().decimal().multiply(
				srcQuantity.getUnit().getRate().decimal()).divide(
						destUnit.getRate().decimal(), destUnit.getScale(), roundingMode);
		return new Quantity(new ScalableNumber(bd), destUnit);
	}
}
