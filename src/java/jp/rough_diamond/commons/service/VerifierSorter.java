/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.service;

import java.lang.reflect.Method;
import java.util.Comparator;

import jp.rough_diamond.commons.service.annotation.Verifier;

/**
 * Verifier���A�m�e�[�g����Ă��郁�\�b�h���\�[�g���邽�߂�Comparator
 */
public class VerifierSorter implements Comparator<Method> {
    public final static Comparator<Method> INSTANCE = new VerifierSorter();

    public int compare(Method o1, Method o2) {
        Verifier v1 = o1.getAnnotation(Verifier.class);
        Verifier v2 = o2.getAnnotation(Verifier.class);
        if(v1.priority() == v2.priority()) {
            return o1.toString().compareTo(o2.toString());
        } else {
            return v2.priority() - v1.priority();
        }
    }
}
