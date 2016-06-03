/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.service;

import java.util.Date;

import jp.rough_diamond.commons.entity.Amount;
import jp.rough_diamond.commons.entity.Quantity;
import jp.rough_diamond.commons.entity.Unit;
import jp.rough_diamond.commons.util.DateManager;
import jp.rough_diamond.framework.service.Service;
import jp.rough_diamond.framework.service.ServiceLocator;

/**
 * �P�ʕϊ��T�[�r�X
 */
@SuppressWarnings("deprecation")
abstract public class UnitConversionService implements Service {
	public final static String DEFAULT_SERVICE_CLASS_NAME = 
				"jp.rough_diamond.commons.service.SimpleUnitConversionService";
	
	public static UnitConversionService getService() {
		return ServiceLocator.getService(UnitConversionService.class, DEFAULT_SERVICE_CLASS_NAME);
	}

	/**
	 * �P�ʂ�ϊ�����
	 * @param srcAmount					���ƂȂ��
	 * @param destUnit					�ϊ�����P��
	 * @param roundingMode				����؂�Ȃ��ꍇ�̊ۂ߃��[�h
	 * @param d							�ϊ����[�g�K�p����
	 * @return							�ϊ������P�ʂ̗�
	 * @throws NotConversionException 	�ϊ����s
	 * @deprecated						Amount�ł͂Ȃ�Quantity���g�p���Ă��������B
	 */
	@Deprecated
	public Amount convertUnit(Amount srcAmount, Unit destUnit, int roundingMode, Date d) throws NotConversionException {
		Quantity q = new Quantity(srcAmount.getQuantity(), srcAmount.getUnit());
		Quantity ret = convertUnit(q, destUnit, roundingMode, d);
		return new Amount(ret.getAmount(), ret.getUnit());
	}

	/**
	 * �P�ʂ�ϊ�����
	 * �ϊ����[�g�͌����_�Ƃ���
	 * @param srcAmount					���ƂȂ��
	 * @param destUnit					�ϊ�����P��
	 * @param roundingMode				����؂�Ȃ��ꍇ�̊ۂ߃��[�h
	 * @return							�ϊ������P�ʂ̗�
	 * @throws NotConversionException 	�ϊ����s
	 * @deprecated						Amount�ł͂Ȃ�Quantity���g�p���Ă��������B
	 */
	@Deprecated
	public Amount convertUnit(Amount srcAmount, Unit destUnit, int roundingMode) throws NotConversionException {
		return convertUnit(srcAmount, destUnit, roundingMode, DateManager.DM.newDate());
	}

	/**
	 * �P�ʂ�ϊ�����
	 * �ۂ߃��[�h�͎����T�[�r�X�̃f�t�H���g�l�Ƃ���
	 * �ϊ����[�g�͌����_�Ƃ���
	 * @param srcAmount	���ƂȂ��
	 * @param destUnit	�ϊ�����P��
	 * @param d			�ϊ����[�g�K�p����
	 * @return			�ϊ������P�ʂ̗�
	 * @throws NotConversionException �ϊ����s
	 * @deprecated						Amount�ł͂Ȃ�Quantity���g�p���Ă��������B
	 */
	@Deprecated
	public Amount convertUnit(Amount srcAmount, Unit destUnit, Date d) throws NotConversionException {
		Quantity q = new Quantity(srcAmount.getQuantity(), srcAmount.getUnit());
		Quantity ret = convertUnit(q, destUnit, d);
		return new Amount(ret.getAmount(), ret.getUnit());
	}
	
	/**
	 * �P�ʂ�ϊ�����
	 * �ۂ߃��[�h�͎����T�[�r�X�̃f�t�H���g�l�Ƃ���
	 * @param srcAmount	���ƂȂ��
	 * @param destUnit	�ϊ�����P��
	 * @return			�ϊ������P�ʂ̗�
	 * @throws NotConversionException �ϊ����s
	 * @deprecated						Amount�ł͂Ȃ�Quantity���g�p���Ă��������B
	 */
	@Deprecated
	public Amount convertUnit(Amount srcAmount, Unit destUnit) throws NotConversionException {
		return convertUnit(srcAmount, destUnit, DateManager.DM.newDate());
	}

	/**
	 * �P�ʂ�ϊ�����
	 * @param srcAmount					���ƂȂ��
	 * @param destUnit					�ϊ�����P��
	 * @param roundingMode				����؂�Ȃ��ꍇ�̊ۂ߃��[�h
	 * @param d							�ϊ����[�g�K�p����
	 * @return							�ϊ������P�ʂ̗�
	 * @throws NotConversionException 	�ϊ����s
	 */
	abstract public Quantity convertUnit(Quantity srcQuantity, Unit destUnit, int roundingMode, Date d) throws NotConversionException;

	/**
	 * �P�ʂ�ϊ�����
	 * �ϊ����[�g�͌����_�Ƃ���
	 * @param srcAmount					���ƂȂ��
	 * @param destUnit					�ϊ�����P��
	 * @param roundingMode				����؂�Ȃ��ꍇ�̊ۂ߃��[�h
	 * @return							�ϊ������P�ʂ̗�
	 * @throws NotConversionException 	�ϊ����s
	 */
	public Quantity convertUnit(Quantity srcQuantity, Unit destUnit, int roundingMode) throws NotConversionException {
		return convertUnit(srcQuantity, destUnit, roundingMode, DateManager.DM.newDate());
	}

	/**
	 * �P�ʂ�ϊ�����
	 * �ۂ߃��[�h�͎����T�[�r�X�̃f�t�H���g�l�Ƃ���
	 * �ϊ����[�g�͌����_�Ƃ���
	 * @param srcAmount	���ƂȂ��
	 * @param destUnit	�ϊ�����P��
	 * @param d			�ϊ����[�g�K�p����
	 * @return			�ϊ������P�ʂ̗�
	 * @throws NotConversionException �ϊ����s
	 */
	abstract public Quantity convertUnit(Quantity srcAmount, Unit destUnit, Date d) throws NotConversionException;
	
	/**
	 * �P�ʂ�ϊ�����
	 * �ۂ߃��[�h�͎����T�[�r�X�̃f�t�H���g�l�Ƃ���
	 * @param srcAmount	���ƂȂ��
	 * @param destUnit	�ϊ�����P��
	 * @return			�ϊ������P�ʂ̗�
	 * @throws NotConversionException �ϊ����s
	 */
	public Quantity convertUnit(Quantity srcAmount, Unit destUnit) throws NotConversionException {
		return convertUnit(srcAmount, destUnit, DateManager.DM.newDate());
	}

	/**
	 * �P�ʕϊ��G���[
	 * @author e-yamane
	 */
	public static class NotConversionException extends IllegalArgumentException {
		private static final long serialVersionUID = 1L;

		public NotConversionException() {
			super();
		}

		public NotConversionException(String message, Throwable cause) {
			super(message, cause);
		}

		public NotConversionException(String s) {
			super(s);
		}

		public NotConversionException(Throwable cause) {
			super(cause);
		}
	}
}
