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
 * 各エンティティに対して検証を拡張するメソッドの注釈（アノテーション）
 * 本アノテーションを持つメソッドは以下のルールを遵守しなければならない
 *   ・戻り値のタイプがjp.adirect.trace.util.Messagesもしくはvoidであること
 *   　（但しvoidの場合は検証失敗を上位に通知できないので注意すること）
 *   ・引数は無しもしくは、Verifier.Whenを有すること
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Verifier {
    /**
     * 検証タイミング
     * @return When.UPDATEが含まれている場合は更新時、When.INSERTが含まれている場合は登録時
     */
    WhenVerifier[] when() default {WhenVerifier.UPDATE, WhenVerifier.INSERT};

    /**
     * 検証優先度
     * @return 数値が高い検証から先に呼び出される
     * 同一優先度の場合はtoString()の文字列比較の昇順となる
     */
    int priority() default 0;
    
    /**
     * 事前検証を無視するかしないか？
     * @return trueの場合は以前にエラーがあっても検証を継続する。
     * ただし、対象となる検証メソッドよりも優先度の高い検証メソッドが事前検証を無視しない場合は、
     * 以降の検証は行われない
     */
    boolean isForceExec() default false;
}
