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
 * �A�m�e�[�V�������[�e�B���e�B
 */
public class AnnotationUtils {
    /**
     * �N���X�̌p���K�w��H���ăA�m�e�[�V�������擾����
     * �C���^�t�F�[�X�K�w�͒H��Ȃ�
     * @param <A>
     * @param cl
     * @param annotationClass
     * @return
     */
    public static <A extends Annotation> A getAnnotation(Object obj, Class<A> acl) {
        return getAnnotation(obj.getClass(), acl);
    }

    /**
     * �N���X�̌p���K�w��H���ăA�m�e�[�V�������擾����
     * �C���^�t�F�[�X�K�w�͒H��Ȃ�
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
     * ���\�b�h�̌p���K�w��H���ăA�m�e�[�V�������擾����
     * �C���^�t�F�[�X�K�w�͒H��Ȃ�
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
