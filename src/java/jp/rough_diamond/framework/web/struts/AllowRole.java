/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.web.struts;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * �A�N�Z�X���� 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AllowRole {
	/**
	 * ��ɃA�N�Z�X�\���ǂ����̔���
	 * @return
	 */
	boolean isAllAccess() default false;
	
	/**
	 * �A�N�Z�X�\�ȃ��[�����ʎq
	 * @return
	 */
	String[] allowedRoles() default {};
}
