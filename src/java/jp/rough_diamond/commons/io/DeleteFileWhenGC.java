/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.io;

import java.io.File;
import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * GC����鎞�ɑΉ�����t�@�C�����폜���܂��B
 * Web�A�v���P�[�V�������A�����ԋN�����Ă���O��̃A�v���P�[�V�����̏ꍇ�A
 * {@link java.io.File#deleteOnExit()}�ł́A�t�@�C���̃��C�t�T�C�N���������悤�ȏꍇ�ɗ��p�ł��܂��B
 */
public class DeleteFileWhenGC extends File {
	private static final long serialVersionUID = 1L;
	private final static Log log = LogFactory.getLog(FileInputStreamDeletingWhenGC.class);

    public DeleteFileWhenGC(String pathname) {
        super(pathname);
    }

    public DeleteFileWhenGC(String parent, String child) {
        super(parent, child);
    }

    public DeleteFileWhenGC(File parent, String child) {
        super(parent, child);
    }

    public DeleteFileWhenGC(URI uri) {
        super(uri);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if(log.isDebugEnabled()) {
        	log.debug(getName() + "���폜���܂��B");
        }
        boolean ret = delete();
        //FindBugs���{��̂œ{�����߂邨�܂��Ȃ�
        if(ret) ret = !ret;
    }
}
