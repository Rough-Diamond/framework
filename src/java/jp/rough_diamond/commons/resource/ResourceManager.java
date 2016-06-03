/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import jp.rough_diamond.commons.di.DIContainerFactory;

/**
 * ÉäÉ\Å[ÉXä«óù
 */
public class ResourceManager {
    private static Map<Locale, ResourceBundle> bundleMap = new HashMap<Locale, ResourceBundle>();

    public static ResourceBundle getResource(Locale locale) {
        ResourceBundle rb = bundleMap.get(locale);
        if(rb == null) {
            String name = (String)DIContainerFactory.getDIContainer().getObject("resourceName");
            rb = new MultiResourceBundle(name, locale);
            bundleMap.put(locale, rb);
        }
        return rb;
    }
    
    public static ResourceBundle getResource() {
        Locale locale = LocaleController.getController().getLocale();
        return getResource(locale);
    }

    protected static class MultiResourceBundle extends ResourceBundle {
        private List<ResourceBundle> list;
        MultiResourceBundle(String name, Locale locale) {
            list = new ArrayList<ResourceBundle>();
            StringTokenizer tokenizer = new StringTokenizer(name, ", \n");
            while(tokenizer.hasMoreTokens()) {
                list.add(ResourceBundle.getBundle(tokenizer.nextToken(), locale));
            }
        }

        @Override
        public Enumeration<String> getKeys() {
            Set<String> set = new HashSet<String>();
            for(ResourceBundle rb : list) {
                Enumeration<String> en = rb.getKeys();
                while(en.hasMoreElements()) {
                    set.add(en.nextElement());
                }
            }
            return Collections.enumeration(set);
        }

        @Override
        protected Object handleGetObject(String key) {
            for(ResourceBundle rb : list) {
                try {
                    Object o = rb.getObject(key);
                    return o;
                } catch(MissingResourceException e) {
                    //ignore...
                }
            }
            return null;
        }

    }
}
