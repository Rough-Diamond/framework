/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.util.codec;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.StringEncoder;

import junit.framework.TestCase;

public class SimpleStringEncoderTest extends TestCase {
	public void testIt() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("abc", "xyz");
		map.put("a", "qaz");
		StringEncoder encoder = new SimpleStringEncoder(map);
		//ちゃんとコピーしてることを確認
		map.clear();
		assertEquals("誤っています。", "あqazxyz", encoder.encode("あaabc"));
	}
}
