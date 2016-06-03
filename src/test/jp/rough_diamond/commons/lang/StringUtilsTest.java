/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.lang;

import jp.rough_diamond.commons.lang.StringUtils;
import junit.framework.TestCase;

public class StringUtilsTest extends TestCase {
	public void testSubstring() {
		assertEquals("cdefg", StringUtils.substring("abcdefg", 5, "Windows-31J", StringUtils.Direction.LEFT));
		assertEquals("abcde", StringUtils.substring("abcdefg", 5, "Windows-31J", StringUtils.Direction.RIGHT));
		assertEquals("abcde", StringUtils.substring("abcdefg", 5, "Windows-31J"));

		assertEquals("����", StringUtils.substring("����������", 5, "Windows-31J", StringUtils.Direction.LEFT));
		assertEquals("����", StringUtils.substring("����������", 5, "Windows-31J", StringUtils.Direction.RIGHT));
	}
	
	public void testTrimWhitespace() {
		assertNull(StringUtils.trimWhitespace(null));
		assertEquals("ab�@c", StringUtils.trimWhitespace(" �@ab�@c�@ �@"));
		assertEquals("", StringUtils.trimWhitespace(" �@�@�@ �@"));
	}

	public void testTrimWhitespaceToNull() {
		assertNull(StringUtils.trimWhitespaceToNull(null));
		assertEquals("ab�@c", StringUtils.trimWhitespaceToNull(" �@ab�@c�@ �@"));
		assertNull(StringUtils.trimWhitespaceToNull(" �@�@�@ �@"));
	}

	public void testTrimWhitespaceToEmpty() {
		assertEquals("", StringUtils.trimWhitespaceToEmpty(null));
		assertEquals("ab�@c", StringUtils.trimWhitespaceToEmpty(" �@ab�@c�@ �@"));
		assertEquals("", StringUtils.trimWhitespaceToEmpty(" �@�@�@ �@"));
	}
	
	public void testIsHalfCharacterString() {
		assertFalse(StringUtils.isHalfCharacterString(null));
		assertTrue(StringUtils.isHalfCharacterString("abc"));
		assertFalse(StringUtils.isHalfCharacterString("������"));
	}
	
	public void testIsParseInt() {
		assertFalse(StringUtils.isParseInt(null));
		assertTrue(StringUtils.isParseInt("123"));
		assertTrue(StringUtils.isParseInt("-123"));
		assertFalse(StringUtils.isParseInt("�P�Q�R"));
		assertFalse(StringUtils.isParseInt("-1�Q�R"));
	}

	public void testIsParseLong() {
		assertFalse(StringUtils.isParseLong(null));
		assertTrue(StringUtils.isParseLong("123"));
		assertTrue(StringUtils.isParseLong("-123"));
		assertFalse(StringUtils.isParseLong("�P�Q�R"));
		assertFalse(StringUtils.isParseLong("-1�Q�R"));
	}
}
