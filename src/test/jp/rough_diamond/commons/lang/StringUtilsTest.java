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

		assertEquals("えお", StringUtils.substring("あいうえお", 5, "Windows-31J", StringUtils.Direction.LEFT));
		assertEquals("あい", StringUtils.substring("あいうえお", 5, "Windows-31J", StringUtils.Direction.RIGHT));
	}
	
	public void testTrimWhitespace() {
		assertNull(StringUtils.trimWhitespace(null));
		assertEquals("ab　c", StringUtils.trimWhitespace(" 　ab　c　 　"));
		assertEquals("", StringUtils.trimWhitespace(" 　　　 　"));
	}

	public void testTrimWhitespaceToNull() {
		assertNull(StringUtils.trimWhitespaceToNull(null));
		assertEquals("ab　c", StringUtils.trimWhitespaceToNull(" 　ab　c　 　"));
		assertNull(StringUtils.trimWhitespaceToNull(" 　　　 　"));
	}

	public void testTrimWhitespaceToEmpty() {
		assertEquals("", StringUtils.trimWhitespaceToEmpty(null));
		assertEquals("ab　c", StringUtils.trimWhitespaceToEmpty(" 　ab　c　 　"));
		assertEquals("", StringUtils.trimWhitespaceToEmpty(" 　　　 　"));
	}
	
	public void testIsHalfCharacterString() {
		assertFalse(StringUtils.isHalfCharacterString(null));
		assertTrue(StringUtils.isHalfCharacterString("abc"));
		assertFalse(StringUtils.isHalfCharacterString("ａｂｃ"));
	}
	
	public void testIsParseInt() {
		assertFalse(StringUtils.isParseInt(null));
		assertTrue(StringUtils.isParseInt("123"));
		assertTrue(StringUtils.isParseInt("-123"));
		assertFalse(StringUtils.isParseInt("１２３"));
		assertFalse(StringUtils.isParseInt("-1２３"));
	}

	public void testIsParseLong() {
		assertFalse(StringUtils.isParseLong(null));
		assertTrue(StringUtils.isParseLong("123"));
		assertTrue(StringUtils.isParseLong("-123"));
		assertFalse(StringUtils.isParseLong("１２３"));
		assertFalse(StringUtils.isParseLong("-1２３"));
	}
}
