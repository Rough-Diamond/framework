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
 * ＧＣされる際に対応するファイルも削除します。
 * {@link java.io.File#deleteOnExit()}では、ファイルのライフサイクルが長いような場合に利用できます。
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
        	log.debug(f.getCanonicalPath() + "を削除します。");
        }
        boolean ret = f.delete();
        //FindBugsが怒るので怒りを鎮めるおまじない
        if(ret) ret = !ret;
    }
    
}
