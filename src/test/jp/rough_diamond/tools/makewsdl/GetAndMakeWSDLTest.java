/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.tools.makewsdl;

import junit.framework.TestCase;

public class GetAndMakeWSDLTest extends TestCase {
	public void testGetWsdlName() throws Exception {
		assertEquals("Hello.wsdl", GetAndMakeWSDL.getWsdlName("http://localhost:20080/services/Hello"));
	}
}
