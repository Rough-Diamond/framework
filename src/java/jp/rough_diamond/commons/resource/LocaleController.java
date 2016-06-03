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
 * �K�؂ȃ��[�P�����Ǘ�����R���g���[���[
 */
abstract public class LocaleController {
    /**
     * ���[�P�����擾����
     * @return ���[�P��
     */
    abstract public Locale getLocale();
    
    /**
     * ���[�P����ݒ肷��
     * @param locale ���[�P��
     */
    abstract public void setLocale(Locale locale);
    
    /**
     * ���[�P���R���g���[���[���擾����
     * @return ���[�P���R���g���[���[
     */
    public static LocaleController getController() {
        return (LocaleController)DIContainerFactory.getDIContainer().getObject("localeController");
    }
}
