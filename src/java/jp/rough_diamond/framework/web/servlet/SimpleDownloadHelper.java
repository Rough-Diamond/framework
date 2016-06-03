/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.web.servlet;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

/**
 * ダウンロードコンテンツの生成と出力を分離するためのヘルパークラス
 */
public class SimpleDownloadHelper {
	public void execute(HttpServletResponse response) throws IOException {
		response.setContentType(getContentType());
		String fileName = getFileName();
		if(fileName != null) {
			response.setHeader("Content-Disposition",
					"attachment; filename=\"" + fileName + "\"");
		}
		InputStream in = getContent ();
		BufferedOutputStream out = null;
        try {
    		out = new BufferedOutputStream(response.getOutputStream()){
    			//XXX HttpServletResponse#getOutputStream()の戻りはこっちでクローズするとほげることがある
    			//だけど、closeを呼び出さないとfindBug君が怒るのでclose処理をキャンセルさせる
				@Override
				public void close() {
				}
    		};
    		int n;
    		int count = 0;
    		while ((n = in.read()) >= 0) {
    			count++;
    			out.write(n);
    		}
    		response.setContentLength(count);
    		out.flush();
        } finally {
            in.close();
            if(out != null) {
            	out.close();
            }
        }
	}

	private String fileName;
	private String contentType;
	private InputStream content;

	public InputStream getContent() {
		return content;
	}

	public String getContentType() {
		if(contentType == null) {
			return "application/octet-stream";
		} else {
			return contentType;
		}
	}

	public String getFileName() {
		return fileName;
	}

	public void setContent(InputStream stream) {
		content = stream;
	}

	public void setContentType(String string) {
		contentType = string;
	}

	public void setFileName(String string) {
		fileName = string;
	}
}
