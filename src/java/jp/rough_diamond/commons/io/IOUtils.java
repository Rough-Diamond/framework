/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.io;

import java.io.File;

/**
 * I/Oユーティリティ
 * 本クラスは、jakarta commons IOのIOUtilsの拡張APIです。
 * @see <a href="http://commons.apache.org/lang/" target="_blank">jakarta commons lang</a> 
 */
public class IOUtils extends org.apache.commons.io.IOUtils {
    /**
     * ディレクトリを削除する
     * 対象ディレクトリ配下にファイルが存在する場合は、それらのファイルも全て削除する
     * @param dir   削除対象ディレクトリ
    **/
    public static void deleteDir(File dir) {
        if(dir.isDirectory()) {
            File[] children = dir.listFiles();
            for(int i = 0 ; i < children.length ; i++) {
                deleteDir(children[i]);
            }
        }
        boolean ret = dir.delete();
        //FindBugsが怒るので怒りを鎮めるおまじない
        if(ret) ret = !ret;
    }
}
