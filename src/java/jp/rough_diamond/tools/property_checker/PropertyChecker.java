/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.tools.property_checker;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

/**
 * プロパティファイルの妥当性検証ツール
 * @author TISK33645
 */
public class PropertyChecker {
    private Properties baseProperty;
    private Properties targetProperty;
    public PropertyChecker(
            String basePropertyFileName, String targetPropertyFileName) 
                    throws IOException {
        baseProperty = new Properties();
        InputStream is = new FileInputStream(basePropertyFileName);
        try {
        	baseProperty.load(is);
        } finally {
        	is.close();
        }
        targetProperty = new Properties();
        is = new FileInputStream(targetPropertyFileName);
        try {
        	targetProperty.load(is);
        } finally {
        	is.close();
        }
    }

    /**
     * チェック開始
     * @return trueの場合は完全一致している
     */
    public boolean doIt() {
        return PropertyChecker.doIt(baseProperty, targetProperty);
    }

    /**
     * チェック開始
     * @return trueの場合は完全一致している
     */
    @SuppressWarnings("unchecked")
    public static boolean doIt(Properties baseProperty, Properties targetProperty) {
        System.out.println("プロパティのチェックを開始します。");
        Enumeration<String> keys = (Enumeration<String>) baseProperty.propertyNames();
        boolean ret = true;
        while(keys.hasMoreElements()) {
            String key = keys.nextElement();
            if(targetProperty.getProperty(key) == null) {
                System.out.println(key + "が存在していません");
                ret = false;
            }
            targetProperty.remove(key);
        }
        keys = (Enumeration<String>)targetProperty.propertyNames();
        while(keys.hasMoreElements()) {
            ret = false;
            String key = keys.nextElement();
            System.out.println(key + "は検証用プロパティに存在していません。");
        }
        return ret;
    }

    public static void main(String[] args) {
        try {
            if(new PropertyChecker(args[0], args[1]).doIt()) {
                System.exit(0);
            }
        } catch(Exception e) {
            e.printStackTrace(System.out);
        }
        System.exit(-1);
    }
}
