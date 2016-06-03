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
 * 単位変換サービスのシンプル実装
 */
public class SimpleUnitConversionService extends UnitConversionService {
	/**
	 * 生成子
	 * 丸め誤差計算モードを省略された場合は四捨五入（ROUND_HALF_UP）を行います
	 */
	public SimpleUnitConversionService() {
		this("ROUND_HALF_UP");
	}
	
	/**
	 * 生成子
	 * 指定された文字列に対応する丸め誤差モードをデフォルトとします。
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
		//[元の数] × [元の単位の係数] / [変換後の単位の係数]
		BigDecimal bd = srcQuantity.getAmount().decimal().multiply(
				srcQuantity.getUnit().getRate().decimal()).divide(
						destUnit.getRate().decimal(), destUnit.getScale(), roundingMode);
		return new Quantity(new ScalableNumber(bd), destUnit);
	}
}
