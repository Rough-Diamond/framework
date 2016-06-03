/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */


package jp.rough_diamond.commons.entity;

import java.math.BigDecimal;
import java.util.Date;

import jp.rough_diamond.commons.service.UnitConversionService;

/**
 * 量のHibernateマッピングクラス
**/
public class Quantity extends jp.rough_diamond.commons.entity.base.BaseQuantity {
    private static final long serialVersionUID = 1L;

    public Quantity() { }
    public Quantity(BigDecimal bd, Unit unit) {
    	this(new ScalableNumber(bd), unit);
    }
    
    public Quantity(ScalableNumber sn, Unit unit) {
    	setAmount(sn);
    	setUnit(unit);
    }

    /**
     * UnitConversionServiceへのラッパーメソッド
     * @param destUnit
     * @return
     * @throws UnitConversionService.NotConversionException
     */
    public Quantity convertUnit(Unit destUnit) throws UnitConversionService.NotConversionException{
    	return UnitConversionService.getService().convertUnit(this, destUnit);
    }

    /**
     * UnitConversionServiceへのラッパーメソッド
     * @param destUnit
     * @param d
     * @return
     * @throws UnitConversionService.NotConversionException
     */
    public Quantity convertUnit(Unit destUnit, Date d) throws UnitConversionService.NotConversionException{
    	return UnitConversionService.getService().convertUnit(this, destUnit, d);
    }

    /**
     * UnitConversionServiceへのラッパーメソッド
     * @param destUnit
     * @param roundingMode
     * @return
     * @throws UnitConversionService.NotConversionException
     */
    public Quantity convertUnit(Unit destUnit, int roundingMode) throws UnitConversionService.NotConversionException{
    	return UnitConversionService.getService().convertUnit(this, destUnit, roundingMode);
    }

    /**
     * UnitConversionServiceへのラッパーメソッド
     * @param destUnit
     * @param roundingMode
     * @param d
     * @return
     * @throws UnitConversionService.NotConversionException
     */
    public Quantity convertUnit(Unit destUnit, int roundingMode, Date d) throws UnitConversionService.NotConversionException{
    	return UnitConversionService.getService().convertUnit(this, destUnit, roundingMode, d);
    }

    @Override
	public double doubleValue() {
		return (getAmount() == null) ? 0.0D : getAmount().doubleValue();
	}

	@Override
	public float floatValue() {
		return (getAmount() == null) ? 0.0F : getAmount().floatValue();
	}
	@Override
	public int intValue() {
		return (getAmount() == null) ? 0 : getAmount().intValue();
	}
	@Override
	public long longValue() {
		return (getAmount() == null) ? 0L : getAmount().longValue();
	}

	/**
	 * getQuantity().decimal()のショートカットメソッド
	 * getQuantity()==nullの場合はBigDecimal.ZEROを返却
	 * @return
	 */
	public BigDecimal decimal() {
		return (getAmount() == null) ? BigDecimal.ZERO : getAmount().decimal();
	}
}
