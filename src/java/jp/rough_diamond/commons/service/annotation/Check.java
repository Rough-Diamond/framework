/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Check {
	/**
	 * ���j�[�N��
	 * @return
	 */
	String name() default "";
	
	/**
	 * ���j�[�N�`�F�b�N���s���v���p�e�B�Q
	 * �G���[���b�Z�[�W�Ƃ��ẮA�ŏ��̃v���p�e�B���g����
	 * @return
	 */
	String[] properties();
}
