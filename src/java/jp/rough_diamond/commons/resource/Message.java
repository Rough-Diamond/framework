/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.resource;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * メッセージ
 */
public class Message  implements Serializable{
	private static final long serialVersionUID = 1L;
	public final String 	key;
	public final String[]	values;

	public Message(String key) {
		this.key = key;
		this.values = new String[0];
	}

	public Message(String key, String... values) {
		this.key = key;
		if(values == null) {
			this.values = new String[0];
		} else {
			this.values = new String[values.length];
			for(int i = 0 ; i < this.values.length ; i++) {
				this.values[i] = values[i];
			}
		}
	}

	public String getKey() {
		return key;
	}

    public String toString() {
        return MessageFormat.format(
                ResourceManager.getResource().getString(key), (Object[])values);
    }
}
