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
 * GCされる時に対応するファイルも削除します。
 * Webアプリケーション等、長時間起動している前提のアプリケーションの場合、
 * {@link java.io.File#deleteOnExit()}では、ファイルのライフサイクルが長いような場合に利用できます。
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
        	log.debug(getName() + "を削除します。");
        }
        boolean ret = delete();
        //FindBugsが怒るので怒りを鎮めるおまじない
        if(ret) ret = !ret;
    }
}
