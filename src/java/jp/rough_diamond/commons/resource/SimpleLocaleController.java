/* * Copyright (c) 2008, 2009 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/ *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/ *  All rights reserved. */package jp.rough_diamond.commons.resource;import java.util.Locale;import org.apache.commons.logging.Log;import org.apache.commons.logging.LogFactory;/** * JavaVM毎にローケルを管理するローケルコントローラー * ローケルがセットされていない場合はデフォルトローケルを返却する */public class SimpleLocaleController extends LocaleController {	private final static Log log = LogFactory.getLog(SimpleLocaleController.class);    private Locale locale = Locale.getDefault();    @Override    public Locale getLocale() {        return locale;    }    @Override    public void setLocale(Locale locale) {        Locale old = getLocale();        if(!old.equals(locale)) {            this.locale = locale;            if(log.isDebugEnabled()) {            	log.debug("デフォルトローケルの変更：" + locale);            }            Locale.setDefault(locale);        }    }}