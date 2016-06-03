/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.resource;

import java.util.Locale;

import jp.rough_diamond.commons.di.DIContainerFactory;

/**
 * 適切なローケルを管理するコントローラー
 */
abstract public class LocaleController {
    /**
     * ローケルを取得する
     * @return ローケル
     */
    abstract public Locale getLocale();
    
    /**
     * ローケルを設定する
     * @param locale ローケル
     */
    abstract public void setLocale(Locale locale);
    
    /**
     * ローケルコントローラーを取得する
     * @return ローケルコントローラー
     */
    public static LocaleController getController() {
        return (LocaleController)DIContainerFactory.getDIContainer().getObject("localeController");
    }
}
