/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import junit.framework.TestCase;

public class ObjectWithCheckTest extends TestCase {
	public void testSerializable() throws Exception {
		JavaBean bean = new JavaBean();
		ObjectWithCheck owc = new ObjectWithCheck(new Object[]{bean}, bean, "getCode", "getName");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(owc);
		oos.flush();
		oos.close();
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bais);
		owc = (ObjectWithCheck)ois.readObject();
		List<ObjectWithCheck.Item> list = owc.getList();
		assertEquals("名前が誤っています。", "name", list.get(0).getName());
		assertEquals("コードが誤っています。", "code", list.get(0).getCode());
	}
	
	public static class JavaBean implements Serializable {
		private static final long serialVersionUID = 1L;
		public String getName() {
			return "name";
		}
		
		public String getCode() {
			return "code";
		}
	}
}
