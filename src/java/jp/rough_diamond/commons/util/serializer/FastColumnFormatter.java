/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.util.serializer;

/**
 * 常にエスケープを行なわず「"」で囲まない。（カラム内に「"」や改行が含まれている場合には使用しないこと）
 */
public class FastColumnFormatter implements ColumnFormatter {
	public String getColumn(String column) {
		return column;
	}
}
