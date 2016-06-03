/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.lang.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * アノテーションユーティリティ
 */
public class AnnotationUtils {
    /**
     * クラスの継承階層を辿ってアノテーションを取得する
     * インタフェース階層は辿らない
     * @param <A>
     * @param cl
     * @param annotationClass
     * @return
     */
    public static <A extends Annotation> A getAnnotation(Object obj, Class<A> acl) {
        return getAnnotation(obj.getClass(), acl);
    }

    /**
     * クラスの継承階層を辿ってアノテーションを取得する
     * インタフェース階層は辿らない
     * @param <A>
     * @param cl
     * @param annotationClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <A extends Annotation> A getAnnotation(Class cl, Class<A> acl) {
        A ret = (A) cl.getAnnotation(acl);
        while(ret == null && cl.getSuperclass() != null) {
            cl = cl.getSuperclass();
            ret = (A)cl.getAnnotation(acl);
        }
        return ret;
    }

    /**
     * メソッドの継承階層を辿ってアノテーションを取得する
     * インタフェース階層は辿らない
     * @param <A>
     * @param cl
     * @param m
     * @param acl
     * @return
     */
    @SuppressWarnings("unchecked")
	public static <A extends Annotation> A getAnnotation(Class cl, Method m, Class<A> acl) {
    	A ret = (A)m.getAnnotation(acl);
    	while(ret == null && cl.getSuperclass() != null) {
    		cl = cl.getSuperclass();
    		try {
				m = cl.getMethod(m.getName(), m.getParameterTypes());
				ret = m.getAnnotation(acl);
			} catch (NoSuchMethodException e) {
				break;
			}
    	}
		return ret;
    }
}
