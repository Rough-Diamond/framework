/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class ObjectToJAXBElementTest extends TestCase {
	public void testTransformFromJavaBeans() throws Exception {
		jp.rough_diamond.commons.util.mule.transformer.test.ParentBean bean = 
			new jp.rough_diamond.commons.util.mule.transformer.test.ParentBean();
		bean.setXxx("Yamane");
		jp.rough_diamond.commons.util.mule.transformer.test.ChildBean child = 
			new jp.rough_diamond.commons.util.mule.transformer.test.ChildBean();
		bean.setChild(child);
		child.setYyy("Eiji");
		child.setZzz("abc");
		ParentBean afterBean = (ParentBean)(new ObjectToJAXBElement_DoIt().transform(bean));
		assertEquals("�l������Ă��܂��B", "Yamane", afterBean.getXxx().getValue());
		assertEquals("�l������Ă��܂��B", "Eiji", afterBean.getChild().getValue().getYyy().getValue());
		assertEquals("�l������Ă��܂��B", "abc", afterBean.getChild().getValue().getZzz().getValue());
	}
	
	public void testTransformFromList() throws Exception {
		List<jp.rough_diamond.commons.util.mule.transformer.test.ChildBean> list = new ArrayList<jp.rough_diamond.commons.util.mule.transformer.test.ChildBean>();
		jp.rough_diamond.commons.util.mule.transformer.test.ChildBean bean1 = new jp.rough_diamond.commons.util.mule.transformer.test.ChildBean();
		bean1.setYyy("yyy1");
		bean1.setZzz("zzz1");
		jp.rough_diamond.commons.util.mule.transformer.test.ChildBean bean2 = new jp.rough_diamond.commons.util.mule.transformer.test.ChildBean();
		bean2.setYyy("yyy2");
		bean2.setZzz("zzz2");
		list.add(bean1);
		list.add(bean2);
		ArrayOfChildBean after = (ArrayOfChildBean)(new ObjectToJAXBElement_ListToList().transform(list));
		assertEquals("�v�f��������Ă��܂��B", 2, after.getChildBean().size());
		assertEquals("�l������Ă��܂��B", "yyy1", after.getChildBean().get(0).getYyy().getValue());
		assertEquals("�l������Ă��܂��B", "zzz1", after.getChildBean().get(0).getZzz().getValue());
		assertEquals("�l������Ă��܂��B", "yyy2", after.getChildBean().get(1).getYyy().getValue());
		assertEquals("�l������Ă��܂��B", "zzz2", after.getChildBean().get(1).getZzz().getValue());
	}

	public void testTransformFromArray() throws Exception {
		jp.rough_diamond.commons.util.mule.transformer.test.ChildBean bean1 = new jp.rough_diamond.commons.util.mule.transformer.test.ChildBean();
		bean1.setYyy("yyy1");
		bean1.setZzz("zzz1");
		jp.rough_diamond.commons.util.mule.transformer.test.ChildBean bean2 = new jp.rough_diamond.commons.util.mule.transformer.test.ChildBean();
		bean2.setYyy("yyy2");
		bean2.setZzz("zzz2");
		jp.rough_diamond.commons.util.mule.transformer.test.ChildBean[] array = new jp.rough_diamond.commons.util.mule.transformer.test.ChildBean[]{bean1, bean2}; 
		ArrayOfChildBean after = (ArrayOfChildBean)(new ObjectToJAXBElement_ListToList().transform(array));
		assertEquals("�v�f��������Ă��܂��B", 2, after.getChildBean().size());
		assertEquals("�l������Ă��܂��B", "yyy1", after.getChildBean().get(0).getYyy().getValue());
		assertEquals("�l������Ă��܂��B", "zzz1", after.getChildBean().get(0).getZzz().getValue());
		assertEquals("�l������Ă��܂��B", "yyy2", after.getChildBean().get(1).getYyy().getValue());
		assertEquals("�l������Ă��܂��B", "zzz2", after.getChildBean().get(1).getZzz().getValue());
	}
	
	public void testTransformFromMap() throws Exception {
		jp.rough_diamond.commons.util.mule.transformer.test.ChildBean bean1 = new jp.rough_diamond.commons.util.mule.transformer.test.ChildBean();
		bean1.setYyy("yyy1");
		bean1.setZzz("zzz1");
		jp.rough_diamond.commons.util.mule.transformer.test.ChildBean bean2 = new jp.rough_diamond.commons.util.mule.transformer.test.ChildBean();
		bean2.setYyy("yyy2");
		bean2.setZzz("zzz2");
		Map<String, jp.rough_diamond.commons.util.mule.transformer.test.ChildBean> map = new HashMap<String, jp.rough_diamond.commons.util.mule.transformer.test.ChildBean>();
		map.put("1", bean1);
		map.put("2", bean2);
		String2ChildBeanMap after = (String2ChildBeanMap)(new ObjectToJAXBElement_MapToMap().transform(map));
		assertEquals("�v�f��������Ă��܂��B", 2, after.getEntry().size());
		Map<String, ChildBean> mapAfter = new HashMap<String, ChildBean>();
		for(String2ChildBeanMap.Entry entry : after.getEntry()) {
			mapAfter.put(entry.getKey(), entry.getValue());
			mapAfter.put(entry.getKey(), entry.getValue());
		}
		assertEquals("�l������Ă��܂��B", "yyy1", mapAfter.get("1").getYyy().getValue());
		assertEquals("�l������Ă��܂��B", "zzz1", mapAfter.get("1").getZzz().getValue());
		assertEquals("�l������Ă��܂��B", "yyy2", mapAfter.get("2").getYyy().getValue());
		assertEquals("�l������Ă��܂��B", "zzz2", mapAfter.get("2").getZzz().getValue());
	}
	
	public void testTransformFromHasMapJavaBeans() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("111", "222");
		map.put("333", "444");
		jp.rough_diamond.commons.util.mule.transformer.test.HasMapBean before = new jp.rough_diamond.commons.util.mule.transformer.test.HasMapBean();
		before.setMap(map);
		HasMapBean after = (HasMapBean)(new ObjectToJAXBElement_HasMapToHasMap().transform(before));
		assertEquals("�v�f��������Ă��܂��B", 2, after.getMap().getValue().getEntry().size());
		Map<String, String> map2 = new HashMap<String, String>();
		for(String2StringMap.Entry entry : after.getMap().getValue().getEntry()) {
			map2.put(entry.getKey(), entry.getValue());
		}
		assertEquals("�l������Ă��܂��B", "222", map2.get("111"));
		assertEquals("�l������Ă��܂��B", "444", map2.get("333"));
	}
	
	public void testTransformFromNestedGenerics() throws Exception {
		Map<String, List<jp.rough_diamond.commons.util.mule.transformer.test.ChildBean>> map = 
			new HashMap<String, List<jp.rough_diamond.commons.util.mule.transformer.test.ChildBean>>();
		jp.rough_diamond.commons.util.mule.transformer.test.ChildBean bean11 = new jp.rough_diamond.commons.util.mule.transformer.test.ChildBean();
		bean11.setYyy("yyy11");
		bean11.setZzz("zzz11");
		jp.rough_diamond.commons.util.mule.transformer.test.ChildBean bean12 = new jp.rough_diamond.commons.util.mule.transformer.test.ChildBean();
		bean12.setYyy("yyy12");
		bean12.setZzz("zzz12");
		jp.rough_diamond.commons.util.mule.transformer.test.ChildBean bean21 = new jp.rough_diamond.commons.util.mule.transformer.test.ChildBean();
		bean21.setYyy("yyy21");
		bean21.setZzz("zzz21");
		jp.rough_diamond.commons.util.mule.transformer.test.ChildBean bean22 = new jp.rough_diamond.commons.util.mule.transformer.test.ChildBean();
		bean22.setYyy("yyy22");
		bean22.setZzz("zzz22");
		map.put("1", Arrays.asList(new jp.rough_diamond.commons.util.mule.transformer.test.ChildBean[]{bean11, bean12}));
		map.put("2", Arrays.asList(new jp.rough_diamond.commons.util.mule.transformer.test.ChildBean[]{bean21, bean22}));
		String2ArrayOfChildBeanMap after = (String2ArrayOfChildBeanMap)(new ObjectToJAXBElement_NestedGenerics().transform(map));
		assertEquals("��������Ă��܂��B", 2, after.getEntry().size());
		Map<String, String2ArrayOfChildBeanMap.Entry> afterMap = new HashMap<String, String2ArrayOfChildBeanMap.Entry>();
		for(String2ArrayOfChildBeanMap.Entry entry : after.getEntry()) {
			afterMap.put(entry.getKey(), entry);
		}
		assertEquals("�v�f��������Ă��܂��B", 2, afterMap.get("1").getValue().getChildBean().size());
		assertEquals("�l������Ă��܂��B", "yyy11", afterMap.get("1").getValue().getChildBean().get(0).getYyy().getValue());
		assertEquals("�l������Ă��܂��B", "zzz11", afterMap.get("1").getValue().getChildBean().get(0).getZzz().getValue());
		assertEquals("�l������Ă��܂��B", "yyy12", afterMap.get("1").getValue().getChildBean().get(1).getYyy().getValue());
		assertEquals("�l������Ă��܂��B", "zzz12", afterMap.get("1").getValue().getChildBean().get(1).getZzz().getValue());
		assertEquals("�v�f��������Ă��܂��B", 2, afterMap.get("2").getValue().getChildBean().size());
		assertEquals("�l������Ă��܂��B", "yyy21", afterMap.get("2").getValue().getChildBean().get(0).getYyy().getValue());
		assertEquals("�l������Ă��܂��B", "zzz21", afterMap.get("2").getValue().getChildBean().get(0).getZzz().getValue());
		assertEquals("�l������Ă��܂��B", "yyy22", afterMap.get("2").getValue().getChildBean().get(1).getYyy().getValue());
		assertEquals("�l������Ă��܂��B", "zzz22", afterMap.get("2").getValue().getChildBean().get(1).getZzz().getValue());
	}
	
	public void testTransformDimArray() throws Exception {
		String[][] dim = new String[][]{{"123", "456", "789"},{"abc", "def"}};
		ArrayOfArrayOfString after = (ArrayOfArrayOfString)(new ObjectToJAXBElement_DimArray().transform(dim));
		assertEquals("��������Ă��܂��B", 2, after.getArrayOfString().size());
		assertEquals("��������Ă��܂��B", 3, after.getArrayOfString().get(0).getString().size());
		assertEquals("�l������Ă��܂��B", "123", after.getArrayOfString().get(0).getString().get(0));
		assertEquals("�l������Ă��܂��B", "456", after.getArrayOfString().get(0).getString().get(1));
		assertEquals("�l������Ă��܂��B", "789", after.getArrayOfString().get(0).getString().get(2));
		assertEquals("��������Ă��܂��B", 2, after.getArrayOfString().get(1).getString().size());
		assertEquals("�l������Ă��܂��B", "abc", after.getArrayOfString().get(1).getString().get(0));
		assertEquals("�l������Ă��܂��B", "def", after.getArrayOfString().get(1).getString().get(1));
	}
}
