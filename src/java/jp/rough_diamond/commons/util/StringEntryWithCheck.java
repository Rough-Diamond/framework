/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.util;


public class StringEntryWithCheck extends ObjectWithCheck {
	private static final long serialVersionUID = 1L;

	//デシリアライズ用
    public StringEntryWithCheck() { }

    public StringEntryWithCheck(
            StringEntry[] entries, StringEntry[] subEntries) {
        super(entries, subEntries, "getKey", "getName");
    }

    public StringEntryWithCheck(
            StringEntry[] entries, String selectedKey) {
        super(entries, selectedKey, "getKey", "getName");
    }
}
