/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.util;

import java.io.Serializable;
/**
 * キーと値が文字列のエントリー
 * カプセル隠蔽を行ってないので必要であれば保持元で保障すること
**/
public class StringEntry implements Serializable {
	private static final long serialVersionUID = 1L;
	public StringEntry() { }

    public StringEntry(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String key;
    public String name;
}
