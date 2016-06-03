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
 * �N���X�����ł̂ݗ��p����T�[�r�X�ɑ΂���}�[�J�[�A�m�e�[�V����
 * �i�����I�ɂ͉����g���ǂ��날�邩���ˁB�B�B�j 
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface InnerService {

}
