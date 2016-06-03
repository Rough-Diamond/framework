/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.io;

import java.io.File;

/**
 * I/O���[�e�B���e�B
 * �{�N���X�́Ajakarta commons IO��IOUtils�̊g��API�ł��B
 * @see <a href="http://commons.apache.org/lang/" target="_blank">jakarta commons lang</a> 
 */
public class IOUtils extends org.apache.commons.io.IOUtils {
    /**
     * �f�B���N�g�����폜����
     * �Ώۃf�B���N�g���z���Ƀt�@�C�������݂���ꍇ�́A�����̃t�@�C�����S�č폜����
     * @param dir   �폜�Ώۃf�B���N�g��
    **/
    public static void deleteDir(File dir) {
        if(dir.isDirectory()) {
            File[] children = dir.listFiles();
            for(int i = 0 ; i < children.length ; i++) {
                deleteDir(children[i]);
            }
        }
        boolean ret = dir.delete();
        //FindBugs���{��̂œ{�����߂邨�܂��Ȃ�
        if(ret) ret = !ret;
    }
}
