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
 * �ʂ�Hibernate�}�b�s���O�N���X
 * @deprecated	Quantity���g�p���Ă�������
**/
public class Amount extends jp.rough_diamond.commons.entity.base.BaseAmount {
    private static final long serialVersionUID = 1L;
    public Amount() { }
    public Amount(BigDecimal bd, Unit unit) {
    	this(new ScalableNumber(bd), unit);
    }
    
    public Amount(ScalableNumber sn, Unit unit) {
    	setQuantity(sn);
    	setUnit(unit);
    }
    
    /**
     * UnitConversionService�ւ̃��b�p�[���\�b�h
     * @param destUnit
     * @return
     * @throws UnitConversionService.NotConversionException
     */
	public Amount convertUnit(Unit destUnit) throws UnitConversionService.NotConversionException{
    	return UnitConversionService.getService().convertUnit(this, destUnit);
    }

    /**
     * UnitConversionService�ւ̃��b�p�[���\�b�h
     * @param destUnit
     * @param d
     * @return
     * @throws UnitConversionService.NotConversionException
     */
    public Amount convertUnit(Unit destUnit, Date d) throws UnitConversionService.NotConversionException{
    	return UnitConversionService.getService().convertUnit(this, destUnit, d);
    }

    /**
     * UnitConversionService�ւ̃��b�p�[���\�b�h
     * @param destUnit
     * @param roundingMode
     * @return
     * @throws UnitConversionService.NotConversionException
     */
    public Amount convertUnit(Unit destUnit, int roundingMode) throws UnitConversionService.NotConversionException{
    	return UnitConversionService.getService().convertUnit(this, destUnit, roundingMode);
    }

    /**
     * UnitConversionService�ւ̃��b�p�[���\�b�h
     * @param destUnit
     * @param roundingMode
     * @param d
     * @return
     * @throws UnitConversionService.NotConversionException
     */
    public Amount convertUnit(Unit destUnit, int roundingMode, Date d) throws UnitConversionService.NotConversionException{
    	return UnitConversionService.getService().convertUnit(this, destUnit, roundingMode, d);
    }
    
	@Override
	public double doubleValue() {
		return (getQuantity() == null) ? 0.0D : getQuantity().doubleValue();
	}

	@Override
	public float floatValue() {
		return (getQuantity() == null) ? 0.0F : getQuantity().floatValue();
	}
	@Override
	public int intValue() {
		return (getQuantity() == null) ? 0 : getQuantity().intValue();
	}
	@Override
	public long longValue() {
		return (getQuantity() == null) ? 0L : getQuantity().longValue();
	}
	/**
	 * getQuantity().decimal()�̃V���[�g�J�b�g���\�b�h
	 * getQuantity()==null�̏ꍇ��BigDecimal.ZERO��ԋp
	 * @return
	 */
	public BigDecimal decimal() {
		return (getQuantity() == null) ? BigDecimal.ZERO : getQuantity().decimal();
	}
}
