/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.io;

import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * �f�b�����ۂɑΉ�����t�@�C�����폜���܂��B
 * {@link java.io.File#deleteOnExit()}�ł́A�t�@�C���̃��C�t�T�C�N���������悤�ȏꍇ�ɗ��p�ł��܂��B
 */
public class FileInputStreamDeletingWhenGC extends FileInputStream {
    private final static Log log = LogFactory.getLog(FileInputStreamDeletingWhenGC.class);
    
    private File f;
    public FileInputStreamDeletingWhenGC(File file) throws FileNotFoundException {
        super(file);
        this.f = file;
    }

    @Override
    protected void finalize() throws IOException {
        super.finalize();
        if(log.isDebugEnabled()) {
        	log.debug(f.getCanonicalPath() + "���폜���܂��B");
        }
        boolean ret = f.delete();
        //FindBugs���{��̂œ{�����߂邨�܂��Ȃ�
        if(ret) ret = !ret;
    }
    
}
