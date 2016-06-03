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

/**
 * �폜��C�x���g�R�[���o�b�N�A�m�e�[�V����
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PostRemove {
    /**
     * �Ăяo���D��x
     * @return ���l���������؂����ɌĂяo�����
     * ����D��x�̏ꍇ��toString()�̕������r�̏����ƂȂ�
     */
    int priority() default 0;
}
