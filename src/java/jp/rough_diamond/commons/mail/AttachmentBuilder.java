/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.mail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeUtility;

import jp.rough_diamond.commons.io.DeleteFileWhenGC;

import org.apache.commons.io.IOUtils;

/**
 * 添付ファイルをオブジェクトを作成するためのラッパー
 */
public class AttachmentBuilder {
    public static BodyPart buildMultipart(File f) {
        return buildMultipart(f, f.getName());
    }

    public static BodyPart buildMultipart(byte[] content, String name) throws IOException {
        return buildMultipart(new ByteArrayInputStream(content), name);
    }
    
    public static BodyPart buildMultipart(InputStream is, String name) throws IOException {
        File f = File.createTempFile("attachement", ".dat");
        f = new DeleteFileWhenGC(f.getCanonicalPath());
        f.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(f);
        try {
            IOUtils.copy(is, fos);
        } finally {
            fos.close();
        }
        return buildMultipart(f, name);
    }
    
    public static BodyPart buildMultipart(File f, String name) {
        try {
            MimeBodyPart bodyPart = new MimeBodyPart();
            FileDataSource fds = new FileDataSource(f);
            bodyPart.setDataHandler(new DataHandler(fds));
            bodyPart.setFileName(MimeUtility.encodeWord(name));
            return bodyPart;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
