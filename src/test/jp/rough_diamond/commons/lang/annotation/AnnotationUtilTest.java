/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.lang.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import jp.rough_diamond.commons.lang.annotation.AnnotationUtils;
import junit.framework.TestCase;

public class AnnotationUtilTest extends TestCase {
	public void testGetAnnotation() throws Exception {
		Child child = new Child();
		SampleAnno anno1 = AnnotationUtils.getAnnotation(child, SampleAnno.class);
		assertEquals("xyz", anno1.value());

		SampleAnno anno2 = AnnotationUtils.getAnnotation(child.getClass(), SampleAnno.class);
		assertEquals("xyz", anno2.value());
		
		Method m = Child.class.getMethod("foo");
		SampleAnno anno3 = AnnotationUtils.getAnnotation(Child.class, m, SampleAnno.class);
		assertEquals("abc", anno3.value());
		
		DummyAnno anno4 = AnnotationUtils.getAnnotation(child, DummyAnno.class);
		assertNull(anno4);
		
		DummyAnno anno5 = AnnotationUtils.getAnnotation(Child.class, m, DummyAnno.class);
		assertNull(anno5);
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.METHOD})
	public static @interface SampleAnno {
		String value();
	};
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.METHOD})
	public static @interface DummyAnno {
		String value();
	};

	@SampleAnno("xyz")
	public class Parent {
		@SampleAnno("abc")
		public void foo() { }
	}
	
	public class Child extends Parent {
		@Override
		public void foo() { }
	}
}