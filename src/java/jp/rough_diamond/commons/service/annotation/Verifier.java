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

import jp.rough_diamond.commons.service.WhenVerifier;

/**
 * �e�G���e�B�e�B�ɑ΂��Č��؂��g�����郁�\�b�h�̒��߁i�A�m�e�[�V�����j
 * �{�A�m�e�[�V�����������\�b�h�͈ȉ��̃��[�������炵�Ȃ���΂Ȃ�Ȃ�
 *   �E�߂�l�̃^�C�v��jp.adirect.trace.util.Messages��������void�ł��邱��
 *   �@�i�A��void�̏ꍇ�͌��؎��s����ʂɒʒm�ł��Ȃ��̂Œ��ӂ��邱�Ɓj
 *   �E�����͖����������́AVerifier.When��L���邱��
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Verifier {
    /**
     * ���؃^�C�~���O
     * @return When.UPDATE���܂܂�Ă���ꍇ�͍X�V���AWhen.INSERT���܂܂�Ă���ꍇ�͓o�^��
     */
    WhenVerifier[] when() default {WhenVerifier.UPDATE, WhenVerifier.INSERT};

    /**
     * ���ؗD��x
     * @return ���l���������؂����ɌĂяo�����
     * ����D��x�̏ꍇ��toString()�̕������r�̏����ƂȂ�
     */
    int priority() default 0;
    
    /**
     * ���O���؂𖳎����邩���Ȃ����H
     * @return true�̏ꍇ�͈ȑO�ɃG���[�������Ă����؂��p������B
     * �������A�ΏۂƂȂ錟�؃��\�b�h�����D��x�̍������؃��\�b�h�����O���؂𖳎����Ȃ��ꍇ�́A
     * �ȍ~�̌��؂͍s���Ȃ�
     */
    boolean isForceExec() default false;
}
