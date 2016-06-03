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
 * 単位変換サービス
 */
@SuppressWarnings("deprecation")
abstract public class UnitConversionService implements Service {
	public final static String DEFAULT_SERVICE_CLASS_NAME = 
				"jp.rough_diamond.commons.service.SimpleUnitConversionService";
	
	public static UnitConversionService getService() {
		return ServiceLocator.getService(UnitConversionService.class, DEFAULT_SERVICE_CLASS_NAME);
	}

	/**
	 * 単位を変換する
	 * @param srcAmount					元となる量
	 * @param destUnit					変換する単位
	 * @param roundingMode				割り切れない場合の丸めモード
	 * @param d							変換レート適用日時
	 * @return							変換した単位の量
	 * @throws NotConversionException 	変換失敗
	 * @deprecated						AmountではなくQuantityを使用してください。
	 */
	@Deprecated
	public Amount convertUnit(Amount srcAmount, Unit destUnit, int roundingMode, Date d) throws NotConversionException {
		Quantity q = new Quantity(srcAmount.getQuantity(), srcAmount.getUnit());
		Quantity ret = convertUnit(q, destUnit, roundingMode, d);
		return new Amount(ret.getAmount(), ret.getUnit());
	}

	/**
	 * 単位を変換する
	 * 変換レートは現時点とする
	 * @param srcAmount					元となる量
	 * @param destUnit					変換する単位
	 * @param roundingMode				割り切れない場合の丸めモード
	 * @return							変換した単位の量
	 * @throws NotConversionException 	変換失敗
	 * @deprecated						AmountではなくQuantityを使用してください。
	 */
	@Deprecated
	public Amount convertUnit(Amount srcAmount, Unit destUnit, int roundingMode) throws NotConversionException {
		return convertUnit(srcAmount, destUnit, roundingMode, DateManager.DM.newDate());
	}

	/**
	 * 単位を変換する
	 * 丸めモードは実装サービスのデフォルト値とする
	 * 変換レートは現時点とする
	 * @param srcAmount	元となる量
	 * @param destUnit	変換する単位
	 * @param d			変換レート適用日時
	 * @return			変換した単位の量
	 * @throws NotConversionException 変換失敗
	 * @deprecated						AmountではなくQuantityを使用してください。
	 */
	@Deprecated
	public Amount convertUnit(Amount srcAmount, Unit destUnit, Date d) throws NotConversionException {
		Quantity q = new Quantity(srcAmount.getQuantity(), srcAmount.getUnit());
		Quantity ret = convertUnit(q, destUnit, d);
		return new Amount(ret.getAmount(), ret.getUnit());
	}
	
	/**
	 * 単位を変換する
	 * 丸めモードは実装サービスのデフォルト値とする
	 * @param srcAmount	元となる量
	 * @param destUnit	変換する単位
	 * @return			変換した単位の量
	 * @throws NotConversionException 変換失敗
	 * @deprecated						AmountではなくQuantityを使用してください。
	 */
	@Deprecated
	public Amount convertUnit(Amount srcAmount, Unit destUnit) throws NotConversionException {
		return convertUnit(srcAmount, destUnit, DateManager.DM.newDate());
	}

	/**
	 * 単位を変換する
	 * @param srcAmount					元となる量
	 * @param destUnit					変換する単位
	 * @param roundingMode				割り切れない場合の丸めモード
	 * @param d							変換レート適用日時
	 * @return							変換した単位の量
	 * @throws NotConversionException 	変換失敗
	 */
	abstract public Quantity convertUnit(Quantity srcQuantity, Unit destUnit, int roundingMode, Date d) throws NotConversionException;

	/**
	 * 単位を変換する
	 * 変換レートは現時点とする
	 * @param srcAmount					元となる量
	 * @param destUnit					変換する単位
	 * @param roundingMode				割り切れない場合の丸めモード
	 * @return							変換した単位の量
	 * @throws NotConversionException 	変換失敗
	 */
	public Quantity convertUnit(Quantity srcQuantity, Unit destUnit, int roundingMode) throws NotConversionException {
		return convertUnit(srcQuantity, destUnit, roundingMode, DateManager.DM.newDate());
	}

	/**
	 * 単位を変換する
	 * 丸めモードは実装サービスのデフォルト値とする
	 * 変換レートは現時点とする
	 * @param srcAmount	元となる量
	 * @param destUnit	変換する単位
	 * @param d			変換レート適用日時
	 * @return			変換した単位の量
	 * @throws NotConversionException 変換失敗
	 */
	abstract public Quantity convertUnit(Quantity srcAmount, Unit destUnit, Date d) throws NotConversionException;
	
	/**
	 * 単位を変換する
	 * 丸めモードは実装サービスのデフォルト値とする
	 * @param srcAmount	元となる量
	 * @param destUnit	変換する単位
	 * @return			変換した単位の量
	 * @throws NotConversionException 変換失敗
	 */
	public Quantity convertUnit(Quantity srcAmount, Unit destUnit) throws NotConversionException {
		return convertUnit(srcAmount, destUnit, DateManager.DM.newDate());
	}

	/**
	 * 単位変換エラー
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
