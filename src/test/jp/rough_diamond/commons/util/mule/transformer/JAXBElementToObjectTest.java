/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.util.mule.transformer;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import jp.rough_diamond.commons.util.mule.transformer.JAXBElementToObject;
import jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.ArrayOfArrayOfString;
import jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.ArrayOfChildBean;
import jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.ArrayOfString;
import jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.ChildBean;
import jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.HasMapBean;
import jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.ObjectFactory;
import jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.ParentBean;
import jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.String2ArrayOfChildBeanMap;
import jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.String2ChildBeanMap;
import jp.rough_diamond.commons.util.mule.transformer.test.stub.sampleservice.String2StringMap;
import junit.framework.TestCase;

public class JAXBElementToObjectTest extends TestCase {
	public void testTransformObject() throws Exception {
		ObjectFactory of = new ObjectFactory();
		ParentBean base = of.createParentBean();
		base.setXxx(of.createParentBeanXxx("Yamane"));
		ChildBean child = of.createChildBean();
		child.setYyy(of.createChildBeanYyy("Eiji"));
		child.setZzz(of.createChildBeanZzz("abc"));
		base.setChild(of.createParentBeanChild(child));
		DatatypeFactory dtf = DatatypeFactory.newInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		GregorianCalendar cal1 = (GregorianCalendar)Calendar.getInstance();
		cal1.setTime(sdf.parse("2009/05/11"));
		base.setCal(of.createParentBeanCal(dtf.newXMLGregorianCalendar(cal1)));
		ArrayOfString aos = of.createArrayOfString();
		aos.getString().addAll(Arrays.asList(new String[]{"abc", "xyz"}));
		base.setArray(of.createParentBeanArray(aos));
		base.setBoolean1(Boolean.TRUE);
		GregorianCalendar cal2 = (GregorianCalendar)Calendar.getInstance();
		cal2.setTime(sdf.parse("2009/05/13"));
		base.setDate(dtf.newXMLGregorianCalendar(cal2));
		base.setInt1(of.createParentBeanInt1(10));
		ArrayOfString aos2 = of.createArrayOfString();
		aos2.getString().addAll(Arrays.asList(new String[]{"123", "456", "789"}));
		base.setList(of.createParentBeanArray(aos2));
		
		jp.rough_diamond.commons.util.mule.transformer.test.ParentBean after = (jp.rough_diamond.commons.util.mule.transformer.test.ParentBean)(new JAXBElementToObject().transform(base, jp.rough_diamond.commons.util.mule.transformer.test.ParentBean.class));
		assertEquals("�l������Ă��܂��B", "Yamane", after.getXxx());
		assertEquals("�l������Ă��܂��B", "Eiji", after.getChild().getYyy());
		assertEquals("�l������Ă��܂��B", "abc", after.getChild().getZzz());
		assertEquals("�l������Ă��܂��B", "2009/05/11", sdf.format(after.getCal().getTime()));
		assertEquals("�z��T�C�Y������Ă��܂��B", 2, after.getArray().length);
		assertEquals("�l������Ă��܂��B", "abc", after.getArray()[0]);
		assertEquals("�l������Ă��܂��B", "xyz", after.getArray()[1]);
		assertTrue("�l������Ă��܂��B", after.isBoolean1());
		assertEquals("�l������Ă��܂��B", "2009/05/13", sdf.format(after.getDate()));
		assertEquals("�l������Ă��܂��B", 10, after.getInt1().intValue());
		assertEquals("���X�g�T�C�Y������Ă��܂��B", 3, after.getList().size());
	}
	
	//Generics�t����Type���擾���邽�߂̃_�~�[�v���p�e�B�i�A�N�Z�X���܂���Bpublic�ɂ��Ȃ���FindBugs���{��̂�public�j
	public List<String> stringList;
	@SuppressWarnings("unchecked")
	public void testTransformStringList() throws Exception {
		ObjectFactory of = new ObjectFactory();
		ArrayOfString aos = of.createArrayOfString();
		aos.getString().addAll(Arrays.asList(new String[]{"abc", "xyz"}));
		Field f = this.getClass().getDeclaredField("stringList");
		List<String> ret = (List<String>)new JAXBElementToObject().transform(aos, f.getGenericType());
		assertEquals("�z��T�C�Y������Ă��܂��B", 2, ret.size());
		assertEquals("�l������Ă��܂��B", "abc", ret.get(0));
		assertEquals("�l������Ă��܂��B", "xyz", ret.get(1));
	}
	
	public void testTransformStringArray() throws Exception {
		ObjectFactory of = new ObjectFactory();
		ArrayOfString aos = of.createArrayOfString();
		aos.getString().addAll(Arrays.asList(new String[]{"abc", "xyz"}));
		String[] ret = (String[])new JAXBElementToObject().transform(aos, String[].class);
		assertEquals("�z��T�C�Y������Ă��܂��B", 2, ret.length);
		assertEquals("�l������Ă��܂��B", "abc", ret[0]);
		assertEquals("�l������Ă��܂��B", "xyz", ret[1]);
	}

	//Generics�t����Type���擾���邽�߂̃_�~�[�v���p�e�B�i�A�N�Z�X���܂���Bpublic�ɂ��Ȃ���FindBugs���{��̂�public�j
	public Map<String, jp.rough_diamond.commons.util.mule.transformer.test.ChildBean> string2ChildBeanMap;
	@SuppressWarnings("unchecked")
	public void testTransformMap() throws Exception {
		ObjectFactory of = new ObjectFactory();
		String2ChildBeanMap map = of.createString2ChildBeanMap();
		String2ChildBeanMap.Entry entry1 = of.createString2ChildBeanMapEntry();
		entry1.setKey("xyz");
		ChildBean bean1 = new ChildBean();
		bean1.setYyy(of.createChildBeanYyy("yyy1"));
		bean1.setZzz(of.createChildBeanZzz("zzz1"));
		entry1.setValue(bean1);
		String2ChildBeanMap.Entry entry2 = of.createString2ChildBeanMapEntry();
		entry2.setKey("123");
		ChildBean bean2 = new ChildBean();
		bean2.setYyy(of.createChildBeanYyy("yyy2"));
		bean2.setZzz(of.createChildBeanZzz("zzz2"));
		entry2.setValue(bean2);
		map.getEntry().add(entry1);
		map.getEntry().add(entry2);
		Field f = this.getClass().getDeclaredField("string2ChildBeanMap");
		Map<String, jp.rough_diamond.commons.util.mule.transformer.test.ChildBean> ret = 
			(Map<String, jp.rough_diamond.commons.util.mule.transformer.test.ChildBean>)new JAXBElementToObject().transform(map, f.getGenericType());
		assertEquals("�v�f��������Ă��܂��B", 2, ret.size());
		assertEquals("�l������Ă��܂��B", "yyy1", ret.get("xyz").getYyy());
		assertEquals("�l������Ă��܂��B", "zzz1", ret.get("xyz").getZzz());
		assertEquals("�l������Ă��܂��B", "yyy2", ret.get("123").getYyy());
		assertEquals("�l������Ă��܂��B", "zzz2", ret.get("123").getZzz());
	}
	
	public void testTransformHasMap() throws Exception {
		ObjectFactory of = new ObjectFactory();
		HasMapBean bean = of.createHasMapBean();
		String2StringMap map = of.createString2StringMap();
		List<String2StringMap.Entry> list = map.getEntry();
		String2StringMap.Entry entry1 = of.createString2StringMapEntry();
		entry1.setKey("1");
		entry1.setValue("hogehoge1");
		String2StringMap.Entry entry2 = of.createString2StringMapEntry();
		entry2.setKey("2");
		entry2.setValue("hogehoge2");
		list.add(entry1);
		list.add(entry2);
		bean.setMap(of.createHasMapBeanMap(map));
		jp.rough_diamond.commons.util.mule.transformer.test.HasMapBean after =
			(jp.rough_diamond.commons.util.mule.transformer.test.HasMapBean)new JAXBElementToObject().transform(bean, 
					jp.rough_diamond.commons.util.mule.transformer.test.HasMapBean.class);
		assertEquals("�v�f��������Ă��܂��B", 2, after.getMap().size());
		assertEquals("�l������Ă��܂��B", "hogehoge1", after.getMap().get("1"));
		assertEquals("�l������Ă��܂��B", "hogehoge2", after.getMap().get("2"));
	}
	
	public Map<String, List<jp.rough_diamond.commons.util.mule.transformer.test.ChildBean>> string2ArrayOfChildBeanMap;
	@SuppressWarnings("unchecked")
	public void testTransformNestedGenerics() throws Exception {
		ObjectFactory of = new ObjectFactory();
		String2ArrayOfChildBeanMap map = of.createString2ArrayOfChildBeanMap();
		String2ArrayOfChildBeanMap.Entry entry1 = of.createString2ArrayOfChildBeanMapEntry();
		entry1.setKey("xyz");
		ArrayOfChildBean list1 = of.createArrayOfChildBean();
		List<ChildBean> children1 = list1.getChildBean();
		ChildBean bean1 = new ChildBean();
		bean1.setYyy(of.createChildBeanYyy("yyy11"));
		bean1.setZzz(of.createChildBeanZzz("zzz11"));
		children1.add(bean1);
		ChildBean bean2 = new ChildBean();
		bean2.setYyy(of.createChildBeanYyy("yyy12"));
		bean2.setZzz(of.createChildBeanZzz("zzz12"));
		children1.add(bean2);
		entry1.setValue(list1);

		String2ArrayOfChildBeanMap.Entry entry2 = of.createString2ArrayOfChildBeanMapEntry();
		entry2.setKey("123");
		ArrayOfChildBean list2 = of.createArrayOfChildBean();
		List<ChildBean> children2 = list2.getChildBean();
		ChildBean bean3 = new ChildBean();
		bean3.setYyy(of.createChildBeanYyy("yyy21"));
		bean3.setZzz(of.createChildBeanZzz("zzz21"));
		children2.add(bean3);
		ChildBean bean4 = new ChildBean();
		bean4.setYyy(of.createChildBeanYyy("yyy22"));
		bean4.setZzz(of.createChildBeanZzz("zzz22"));
		children2.add(bean4);
		entry2.setValue(list2);
		
		map.getEntry().add(entry1);
		map.getEntry().add(entry2);

		Field f = this.getClass().getDeclaredField("string2ArrayOfChildBeanMap");
		Map<String, List<jp.rough_diamond.commons.util.mule.transformer.test.ChildBean>> ret = 
			(Map<String, List<jp.rough_diamond.commons.util.mule.transformer.test.ChildBean>>)new JAXBElementToObject().transform(map, f.getGenericType());
		assertEquals("�v�f��������Ă��܂��B", 2, ret.size());
		assertEquals("�v�f��������Ă��܂��B", 2, ret.get("xyz").size());
		assertEquals("�l������Ă��܂��B", "yyy11", ret.get("xyz").get(0).getYyy());
		assertEquals("�l������Ă��܂��B", "zzz11", ret.get("xyz").get(0).getZzz());
		assertEquals("�l������Ă��܂��B", "yyy12", ret.get("xyz").get(1).getYyy());
		assertEquals("�l������Ă��܂��B", "zzz12", ret.get("xyz").get(1).getZzz());
		assertEquals("�v�f��������Ă��܂��B", 2, ret.get("123").size());
		assertEquals("�l������Ă��܂��B", "yyy21", ret.get("123").get(0).getYyy());
		assertEquals("�l������Ă��܂��B", "zzz21", ret.get("123").get(0).getZzz());
		assertEquals("�l������Ă��܂��B", "yyy22", ret.get("123").get(1).getYyy());
		assertEquals("�l������Ă��܂��B", "zzz22", ret.get("123").get(1).getZzz());
	}
	
	public void testTransformFromDimArray() throws Exception {
		ObjectFactory of = new ObjectFactory();
		ArrayOfArrayOfString aoaos = of.createArrayOfArrayOfString();
		ArrayOfString aos1 = of.createArrayOfString();
		aos1.getString().add("123");
		aos1.getString().add("456");
		aos1.getString().add("789");
		aoaos.getArrayOfString().add(aos1);
		ArrayOfString aos2 = of.createArrayOfString();
		aos2.getString().add("abc");
		aos2.getString().add("def");
		aoaos.getArrayOfString().add(aos2);
		String[][] dim = (String[][])new JAXBElementToObject().transform(aoaos, String[][].class);
		assertEquals("�v�f��������Ă��܂��B", 2, dim.length);
		assertEquals("�v�f��������Ă��܂��B", 3, dim[0].length);
		assertEquals("�l������Ă��܂��B", "123", dim[0][0]);
		assertEquals("�l������Ă��܂��B", "456", dim[0][1]);
		assertEquals("�l������Ă��܂��B", "789", dim[0][2]);
		assertEquals("�v�f��������Ă��܂��B", 2, dim[1].length);
		assertEquals("�l������Ă��܂��B", "abc", dim[1][0]);
		assertEquals("�l������Ă��܂��B", "def", dim[1][1]);
	}
	
	public void testXmlCalendarToCalendar() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Date org = sdf.parse("20100402112531000");
		GregorianCalendar orgCal = (GregorianCalendar)Calendar.getInstance();
		orgCal.setTime(org);
		DatatypeFactory df = DatatypeFactory.newInstance();
		XMLGregorianCalendar xmlCal = df.newXMLGregorianCalendar(orgCal);
		JAXBElementToObject transformer = new JAXBElementToObject();
		Calendar destCal = transformer.xmlCalendarToCalendar(xmlCal);
		assertEquals("�ԋp�l������Ă��܂��B", "20100402112531000", sdf.format(destCal.getTime()));
	}
}
