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
 * �v���p�e�B�t�@�C���̑Ó������؃c�[��
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
     * �`�F�b�N�J�n
     * @return true�̏ꍇ�͊��S��v���Ă���
     */
    public boolean doIt() {
        return PropertyChecker.doIt(baseProperty, targetProperty);
    }

    /**
     * �`�F�b�N�J�n
     * @return true�̏ꍇ�͊��S��v���Ă���
     */
    @SuppressWarnings("unchecked")
    public static boolean doIt(Properties baseProperty, Properties targetProperty) {
        System.out.println("�v���p�e�B�̃`�F�b�N���J�n���܂��B");
        Enumeration<String> keys = (Enumeration<String>) baseProperty.propertyNames();
        boolean ret = true;
        while(keys.hasMoreElements()) {
            String key = keys.nextElement();
            if(targetProperty.getProperty(key) == null) {
                System.out.println(key + "�����݂��Ă��܂���");
                ret = false;
            }
            targetProperty.remove(key);
        }
        keys = (Enumeration<String>)targetProperty.propertyNames();
        while(keys.hasMoreElements()) {
            ret = false;
            String key = keys.nextElement();
            System.out.println(key + "�͌��ؗp�v���p�e�B�ɑ��݂��Ă��܂���B");
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
