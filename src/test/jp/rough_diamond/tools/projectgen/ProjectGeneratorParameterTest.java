/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.tools.projectgen;

import java.io.File;

import junit.framework.TestCase;

public class ProjectGeneratorParameterTest extends TestCase {
	public void testFrameworkPath() throws Exception {
		ProjectGeneratorParameter param = new ProjectGeneratorParameter();
		File f = new File("./");
		param.setFrameworkRoot(f.getCanonicalPath());
		param.setProjectRoot(new File(File.listRoots()[0], "hoge").getCanonicalPath());
		assertEquals("Frameworkの相対パスが誤っています。", 
				f.getCanonicalPath().replaceAll("\\\\", "/"), param.getFrameworkPathRelative());
		param.setProjectRoot("workspace/hoge");
		assertEquals("Frameworkの相対パスが誤っています。",
				"../../framework", param.getFrameworkPathRelative());
		param.setProjectRoot("../" + f.getCanonicalFile().getParentFile().getName() + "/hoge");
		assertEquals("Frameworkの相対パスが誤っています。",
				"../framework", param.getFrameworkPathRelative());
	}
}
