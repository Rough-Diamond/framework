/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.util.mule.transformer;

import java.beans.PropertyDescriptor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.beanutils.PropertyUtils;

import jp.rough_diamond.commons.util.mule.transformer.AbstractObjectToJAXBElement;
import jp.rough_diamond.commons.util.mule.transformer.test.ArrayOfDetails;
import jp.rough_diamond.commons.util.mule.transformer.test.Details;
import jp.rough_diamond.commons.util.mule.transformer.test.JAXBElementBean;
import jp.rough_diamond.commons.util.mule.transformer.test.ObjectFactory;
import jp.rough_diamond.commons.util.mule.transformer.test.Sample;
import junit.framework.TestCase;

public class AbstractObjectToJAXBElementTest extends TestCase {
	public void testGetMethod() throws Exception {
		TransformerExt t = new TransformerExt();
		assertNotNull("���\�b�h���ԋp����Ă��܂���B", t.getMethod());
	}
	
	public void testCreateObjectFactory() {
		TransformerExt t = new TransformerExt();
		assertEquals("ObjectFactory�N���X������Ă��܂��B", 
				ObjectFactory.class, 
				t.createObjectFactory(t.getMethod().getParameterTypes()[0]).getClass());
	}
	
	public void testCreateObjectByType() {
		TransformerExt t = new TransformerExt();
		Class<?> type = t.getMethod().getParameterTypes()[0];
		Object factory = t.createObjectFactory(type);
		Object bean = t.createObjectByType(factory, type);
		assertEquals("�ԋp�I�u�W�F�N�g�̃^�C�v������Ă��܂��B", type, bean.getClass());
	}
	
	public void testCopyDateObject() throws Exception {
		TransformerExt t = new TransformerExt();
		Class<?> type = t.getMethod().getParameterTypes()[0];
		Object factory = t.createObjectFactory(type);
		JAXBElementBean dest = (JAXBElementBean)t.createObjectByType(factory, type);
		PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(dest, "acceptDate");
		t.copyDateObject(pd, null, dest);
		assertNull("Null�ł͂���܂���B", dest.getAcceptDate());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Date d = sdf.parse("20080102030405006");
		t.copyDateObject(pd, d, dest);
		XMLGregorianCalendar xCal = dest.getAcceptDate();
		assertEquals("�N������Ă��܂��B", 2008, 	xCal.getYear());
		assertEquals("��������Ă��܂��B", 1,    	xCal.getMonth());
		assertEquals("��������Ă��܂��B", 2, 		xCal.getDay());
		assertEquals("��������Ă��܂��B", 3, 		xCal.getHour());
		assertEquals("��������Ă��܂��B", 4, 		xCal.getMinute());
		assertEquals("�b������Ă��܂��B", 5, 		xCal.getSecond());
		assertEquals("�~���b������Ă��܂��B", 6, 		xCal.getMillisecond());
		assertEquals("�S�̂Ƃ��Č���Ă��܂��B", "20080102030405006", sdf.format(xCal.toGregorianCalendar().getTime()));
	}
	
	public void testCopyJAXBElement() throws Exception {
		TransformerExt t = new TransformerExt();
		Class<?> type = t.getMethod().getParameterTypes()[0];
		Object factory = t.createObjectFactory(type);
		JAXBElementBean dest = (JAXBElementBean)t.createObjectByType(factory, type);
		PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(dest, "acceptId");
		t.copyJAXBElement(factory, pd, "xyz", dest);
		assertEquals("�l������Ă��܂��B", "xyz", dest.getAcceptId().getValue());
		
		pd = PropertyUtils.getPropertyDescriptor(dest, "details");

		InBeanDetails detail1 = new InBeanDetails();
		detail1.setItemId(1L);
		InBeanDetails detail2 = new InBeanDetails();
		detail2.setItemId(2L);
		InBeanDetails detail3 = new InBeanDetails2();
		detail3.setItemId(3L);
		
		t.copyJAXBElement(factory, pd, new InBeanDetails[]{detail1, detail2, detail3}, dest);
		JAXBElement<ArrayOfDetails> details = dest.getDetails();
		assertNotNull("Null�ł��B", details);
		List<Details> list = details.getValue().getAcceptDetails();
		assertEquals("�v�f��������Ă��܂��B", 3, list.size());
		assertEquals("�l������Ă��܂��B", 1L, list.get(0).getItemId().getValue().longValue());
		assertEquals("�l������Ă��܂��B", 2L, list.get(1).getItemId().getValue().longValue());
		assertNull("�l������Ă��܂��B", list.get(2).getItemId());

		List<InBeanDetails> baseList = new ArrayList<InBeanDetails>();
		baseList.add(detail2);
		baseList.add(detail1);
		t.copyJAXBElement(factory, pd, baseList, dest);
		details = dest.getDetails();
		assertNotNull("Null�ł��B", details);
		list = details.getValue().getAcceptDetails();
		assertEquals("�v�f��������Ă��܂��B", 2, list.size());
		assertEquals("�l������Ă��܂��B", 2L, list.get(0).getItemId().getValue().longValue());
		assertEquals("�l������Ă��܂��B", 1L, list.get(1).getItemId().getValue().longValue());
	}
	
	public static class InBeanDetails {
		private Long itemId;

		public Long getItemId() {
			return itemId;
		}

		public void setItemId(Long itemId) {
			this.itemId = itemId;
		}
	}
	
	public static class InBeanDetails2 extends InBeanDetails {
		@Override
		@jp.rough_diamond.commons.util.PropertyUtils.SkipProperty
		public Long getItemId() {
			return super.getItemId();
		}
	}
	
	public static class TransformerExt extends AbstractObjectToJAXBElement {
		@Override
		protected String getOperation() {
			return "doIt";
		}

		@Override
		protected Class<?> getPortType() {
			return Sample.class;
		}
	}
}
