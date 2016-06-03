/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.lang;

import java.io.InputStream;

/**
 * ƒRƒ“ƒeƒ“ƒg
 */
public class Content {
    public final InputStream    content;
    public final String         contentType;
    public final String		  contentName;

    public Content(InputStream content, String contentType) {
    	this(content, contentType, "");
    }

    public Content(InputStream content, String contentType, String contentName) {
        this.content = content;
        this.contentType = contentType;
        this.contentName = contentName;
    }

    public boolean isImage() {
        return ImageContentUtils.isImageContentType(contentType);
    }
}
