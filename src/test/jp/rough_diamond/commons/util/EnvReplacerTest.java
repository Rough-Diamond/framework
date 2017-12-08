/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

/**
 *
 */
public class EnvReplacerTest extends TestCase {
	Map<String, String> envs = System.getenv();
	List<String> keyList = new ArrayList<>(envs.keySet());

	public void testê≥ãKï\åªÇÃämîF() {
		assertTrue(EnvReplacer.p.matcher("{env.fadklsjlkk}").matches());
		assertFalse(EnvReplacer.p.matcher("{env.fadklsjlkk").matches());
		assertFalse(EnvReplacer.p.matcher("env.fadklsjlkk}").matches());
		assertFalse(EnvReplacer.p.matcher("{env.}").matches());
		assertTrue(EnvReplacer.p.matcher("{env.a}").matches());
	}
	public void test() {
		checkIt("abc", "def");
		checkIt("", "");
	}

	private void checkIt(String prefix, String suffix) {
		String text = prefix + "%sÇ©Ç´Ç≠ÇØÇ±{env.fadklsjlkk}Ç≥ÇµÇ∑ÇπÇª%s" + suffix;
		String before = String.format(text, "{env." + keyList.get(0) + "}", "{env." + keyList.get(1) + "}");
		String after = String.format(text, envs.get(keyList.get(0)), envs.get(keyList.get(1)));
		System.out.println(before);
		System.out.println(after);
		assertEquals(after, EnvReplacer.replaceEnv(before));
	}
}
