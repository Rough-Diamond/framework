/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.entity;

import java.math.BigDecimal;

import junit.framework.TestCase;

public class ScalableNumberTest extends TestCase {
	public void testIt() throws Exception {
		BigDecimal bd = new BigDecimal("10.01");
		ScalableNumber sn = new ScalableNumber();
		sn.setDecimal(bd);
		assertEquals("valueが誤っています。", 1001L, sn.getValue().longValue());
		assertEquals("scaleが誤っています。", 2, sn.getScale().intValue());
		sn.setScale(-1);
		assertEquals("数値が誤っています。", 10010L, sn.longValue());
		
		//生成子のテスト
		sn = new ScalableNumber();
		assertEquals("int値が誤っています。", 0, sn.intValue());
		sn = new ScalableNumber(1L, -1);
		assertEquals("long値が誤っています。", 10L, sn.longValue());
		sn = new ScalableNumber(new BigDecimal("100"));
		assertEquals("float値が誤っています。", 100F, sn.floatValue());
		assertEquals("double値が誤っています。", 100D, sn.doubleValue());
		
		sn = new ScalableNumber(new BigDecimal(0.0105D));
		assertEquals("unscaledValueが誤っています。", 1050000000000000065L, sn.getValue().longValue());
		assertEquals("scaleが誤っています。", 20, sn.getScale().intValue());

		sn = new ScalableNumber(new BigDecimal(0.93D));
		assertEquals("unscaledValueが誤っています。", 930000000000000049L, sn.getValue().longValue());
		assertEquals("scaleが誤っています。", 18, sn.getScale().intValue());

		System.out.println(new BigDecimal(Long.MAX_VALUE).toPlainString());
		System.out.println(new BigDecimal("612.75869662999998109342987030745408816301278420723974704742431640625").negate());

		sn = new ScalableNumber(new BigDecimal("612.75869662999998109342987030745408816301278420723974704742431640625"));
		assertEquals("文字列表現が誤っています。", "612.7586966299999811", sn.decimal().toPlainString());

		sn = new ScalableNumber(new BigDecimal("612.75869662999998109342987030745408816301278420723974704742431640625").negate());
		assertEquals("文字列表現が誤っています。", "-612.7586966299999811", sn.decimal().toPlainString());

		sn = new ScalableNumber(new BigDecimal("0.00000000061275869662999998109342987030745408816301278420723974704742431640625"));
		assertEquals("文字列表現が誤っています。", "0.0000000006127586966299999811", sn.decimal().toPlainString());
	}
}
