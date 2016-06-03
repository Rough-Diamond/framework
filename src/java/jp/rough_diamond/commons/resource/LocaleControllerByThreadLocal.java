/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.resource;

import java.util.Locale;

/**
 * スレッドローカル毎にローケルを管理するローケルコントローラー
 * ローケルをセットされない場合はデフォルトローケルを返却する
 */
public class LocaleControllerByThreadLocal extends LocaleController {
    private final ThreadLocal<Locale> locale = new ThreadLocal<Locale>() {
        @Override
        protected Locale initialValue() {
            return Locale.getDefault();
        }
    };
    
    @Override
    public Locale getLocale() {
        return locale.get();
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale.set(locale);
    }
}
