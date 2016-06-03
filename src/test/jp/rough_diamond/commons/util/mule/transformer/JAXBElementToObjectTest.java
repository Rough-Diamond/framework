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
		assertEquals("値が誤っています。", "Yamane", after.getXxx());
		assertEquals("値が誤っています。", "Eiji", after.getChild().getYyy());
		assertEquals("値が誤っています。", "abc", after.getChild().getZzz());
		assertEquals("値が誤っています。", "2009/05/11", sdf.format(after.getCal().getTime()));
		assertEquals("配列サイズが誤っています。", 2, after.getArray().length);
		assertEquals("値が誤っています。", "abc", after.getArray()[0]);
		assertEquals("値が誤っています。", "xyz", after.getArray()[1]);
		assertTrue("値が誤っています。", after.isBoolean1());
		assertEquals("値が誤っています。", "2009/05/13", sdf.format(after.getDate()));
		assertEquals("値が誤っています。", 10, after.getInt1().intValue());
		assertEquals("リストサイズが誤っています。", 3, after.getList().size());
	}
	
	//Generics付くのTypeを取得するためのダミープロパティ（アクセスしません。publicにしないとFindBugsが怒るのでpublic）
	public List<String> stringList;
	@SuppressWarnings("unchecked")
	public void testTransformStringList() throws Exception {
		ObjectFactory of = new ObjectFactory();
		ArrayOfString aos = of.createArrayOfString();
		aos.getString().addAll(Arrays.asList(new String[]{"abc", "xyz"}));
		Field f = this.getClass().getDeclaredField("stringList");
		List<String> ret = (List<String>)new JAXBElementToObject().transform(aos, f.getGenericType());
		assertEquals("配列サイズが誤っています。", 2, ret.size());
		assertEquals("値が誤っています。", "abc", ret.get(0));
		assertEquals("値が誤っています。", "xyz", ret.get(1));
	}
	
	public void testTransformStringArray() throws Exception {
		ObjectFactory of = new ObjectFactory();
		ArrayOfString aos = of.createArrayOfString();
		aos.getString().addAll(Arrays.asList(new String[]{"abc", "xyz"}));
		String[] ret = (String[])new JAXBElementToObject().transform(aos, String[].class);
		assertEquals("配列サイズが誤っています。", 2, ret.length);
		assertEquals("値が誤っています。", "abc", ret[0]);
		assertEquals("値が誤っています。", "xyz", ret[1]);
	}

	//Generics付くのTypeを取得するためのダミープロパティ（アクセスしません。publicにしないとFindBugsが怒るのでpublic）
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
		assertEquals("要素数が誤っています。", 2, ret.size());
		assertEquals("値が誤っています。", "yyy1", ret.get("xyz").getYyy());
		assertEquals("値が誤っています。", "zzz1", ret.get("xyz").getZzz());
		assertEquals("値が誤っています。", "yyy2", ret.get("123").getYyy());
		assertEquals("値が誤っています。", "zzz2", ret.get("123").getZzz());
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
		assertEquals("要素数が誤っています。", 2, after.getMap().size());
		assertEquals("値が誤っています。", "hogehoge1", after.getMap().get("1"));
		assertEquals("値が誤っています。", "hogehoge2", after.getMap().get("2"));
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
		assertEquals("要素数が誤っています。", 2, ret.size());
		assertEquals("要素数が誤っています。", 2, ret.get("xyz").size());
		assertEquals("値が誤っています。", "yyy11", ret.get("xyz").get(0).getYyy());
		assertEquals("値が誤っています。", "zzz11", ret.get("xyz").get(0).getZzz());
		assertEquals("値が誤っています。", "yyy12", ret.get("xyz").get(1).getYyy());
		assertEquals("値が誤っています。", "zzz12", ret.get("xyz").get(1).getZzz());
		assertEquals("要素数が誤っています。", 2, ret.get("123").size());
		assertEquals("値が誤っています。", "yyy21", ret.get("123").get(0).getYyy());
		assertEquals("値が誤っています。", "zzz21", ret.get("123").get(0).getZzz());
		assertEquals("値が誤っています。", "yyy22", ret.get("123").get(1).getYyy());
		assertEquals("値が誤っています。", "zzz22", ret.get("123").get(1).getZzz());
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
		assertEquals("要素数が誤っています。", 2, dim.length);
		assertEquals("要素数が誤っています。", 3, dim[0].length);
		assertEquals("値が誤っています。", "123", dim[0][0]);
		assertEquals("値が誤っています。", "456", dim[0][1]);
		assertEquals("値が誤っています。", "789", dim[0][2]);
		assertEquals("要素数が誤っています。", 2, dim[1].length);
		assertEquals("値が誤っています。", "abc", dim[1][0]);
		assertEquals("値が誤っています。", "def", dim[1][1]);
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
		assertEquals("返却値が誤っています。", "20100402112531000", sdf.format(destCal.getTime()));
	}
}
