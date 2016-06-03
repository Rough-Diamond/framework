/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.util.serializer;

import org.apache.commons.lang.StringUtils;

/**
 * 常に「"」でカラムを囲むフォーマッター
 */
public class DefaultColumnFormatter implements ColumnFormatter {
	public String getColumn(String column) {
        if(!StringUtils.containsNone(column, "\n\",")) {
            if(column.indexOf('\"') != -1) {
                column = CSVContainer.getSupplementQuote(column);
            }
            return "\"" +  column + "\"";
        } else {
            return column;
        }
	}
}
