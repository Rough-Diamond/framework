/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.di;

import java.io.File;

import junit.framework.TestCase;

public class SpringFrameworkTest extends TestCase {
	public void testGenerics() throws Exception {
		DIContainer di = new SpringFramework("beans.xml");
		String s = di.getObject(String.class, "dummyText");
		assertEquals("value unmuch.", "hogehoge", s);
		
		di = new SpringFramework(this.getClass().getClassLoader().getResource("beans.xml"));
		s = di.getObject(String.class, "dummyText");
		assertEquals("value unmuch.", "hogehoge", s);

		di = new SpringFramework(new File("src/java/beans.xml"));
		s = di.getObject(String.class, "dummyText");
		assertEquals("value unmuch.", "hogehoge", s);

		//�X�g���[�����Ƒ��΃p�X�����f�t���Ȃ��̂�domain�̕���ǂ�
		//�G���[���ł錴���͕s��
//		di = new SpringFramework(new FileInputStream("src/java/beans-domain.xml"));
//		s = di.getObject(String.class, "dummyText");
//		assertEquals("value unmuch.", "hogehoge", s);
	}
}
