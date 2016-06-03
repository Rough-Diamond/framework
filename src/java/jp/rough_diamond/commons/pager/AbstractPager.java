/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
/*
 * $Id: AbstractPager.java 577 2006-03-17 05:51:25Z Matsuda_Kazuto@ogis-ri.co.jp $
 * $Header$
 */
package jp.rough_diamond.commons.pager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* serialize処理用
import  java.io.*;
import  java.util.zip.*;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
*/

/**
 * コレクションをページングする抽象クラス
 * @author $Author: Matsuda_Kazuto@ogis-ri.co.jp $
 * @see jp.gr.java_conf.e_yamane.j2ee.util.pager.Pager
 */
public abstract class AbstractPager<E> implements Pager<E> {
	private static final long serialVersionUID = 1L;

	public final static Log log = LogFactory.getLog(AbstractPager.class); 
    
    private int currentPage;

    protected AbstractPager() {
        currentPage = 1;
    }
    
    public final int getCurrentPage() {
        return currentPage;
    }
    
    public int getPageSize() {
        long size = getSize();
        int sizePerPage = getSizePerPage();
        if(size == 0) {
            return 1;
        }
        int page = (int)size / sizePerPage;
        return (size % sizePerPage == 0) ? page : page + 1;
    }
    
    public boolean isFirst() {
        return (getCurrentPage() == 1);
    }
    
    public boolean isLast() {
        return getCurrentPage() == getPageSize();
    }
    
    public void previous() {
        gotoPage(getCurrentPage() - 1);
    }
    
    public void next() {
        gotoPage(getCurrentPage() + 1);
    }
    
    public void gotoPage(int page) {
        if((1    <= page) &&
           (page <= getPageSize())) {
            preGotoPage(page);
            currentPage = page;
            postGotoPage(page);
        } else if(page == 1) {
            preGotoPage(page);
            currentPage = page;
            postGotoPage(page);
        } else {
        	if(log.isInfoEnabled()) {
	            log.info("ページ番号：" + page);
	            log.info("最大ページ数：" + getPageSize());
        	}
            throw new IllegalArgumentException("ページ遷移に失敗");
        }
    }
    
    //とりあえず、カレントページ-５まで表示
    public int getWindFirst() {
        int ret = Math.min(getCurrentPage() - 5, getPageSize() - 9);
        return (ret < 1) ? 1 : ret;
    }
    
    //とりあえず、最大１０個表示
    public int getWindFinish() {
        int ret = getWindFirst() + 9;
        return (ret > getPageSize()) ? getPageSize() : ret;
    }
    
    public long getIndexAtFirstElement() {
        if(getSize() == 0L) {
            return 0L;
        } else {
            return (getCurrentPage() - 1L) * getSizePerPage() + 1L;
        }
    }
    
    public long getIndexAtLastElement() {
        long lastIndex = getIndexAtFirstElement() + getSizePerPage() - 1;
        return Math.min(lastIndex, getSize());
    }

    /**
     * シリアライズ
     * @param   pager   シリアライズ対象ページャー
     * @return  シリアライズ文字列(Zip圧縮-BASE64)
    **/
/*
    public static String serializePager(Pager pager) 
                                        throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream zos = new GZIPOutputStream(baos);
        ObjectOutputStream oos = new ObjectOutputStream(zos);
        oos.writeObject(pager);
        oos.close();
        zos.close();
        baos.close();
        byte[] binary = baos.toByteArray();
        BASE64Encoder base64Encoder = new BASE64Encoder();
        String ret = base64Encoder(binary);
        System.out.println("Serialized size:" + ret);
        return ret;
    }
*/
    
    /**
     * デシリアライズ
     * @param   serializedPager シリアライズされたページャー
     * @return デシリアライズされたページャー
    **/
/*
    public static Pager deserializePager(String serializedPager)
                                                throws IOException  {
        BASE64Decoder base64Decoder = new BASE64Decoder();
    }
*/

    /**
     * ページ遷移前コールバック
     * 具象クラスは必要に応じてオーバーライドすること
     * @param   page    遷移後ページ
    **/
    protected void preGotoPage(int page) {
    }
    
    /**
     * ページ遷移後コールバック
     * 具象クラスは必要に応じてオーバーライドすること
     * @param   page    遷移後ページ
    **/
    protected void postGotoPage(int page) {
    }

}
