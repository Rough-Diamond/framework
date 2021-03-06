/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.extractor;

/**
 * 日時型から文字列型へ変換する
 * 日付変換関数はRDBMSによって異なるのでここで吸収する
 */
public class DateToString extends Function {
	public final String format;
	/**
	 * @param value  変換前の値
	 * @param format　フォーマット。
	 * 　　java.text.SimpleDateFormat準拠。ただし、y,M,d,H,m,s,Sのみ
	 */
	public DateToString(Value value, String format) {
		super(value);
		this.format = format;
	}
}
