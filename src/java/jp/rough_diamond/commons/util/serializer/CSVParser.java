/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.util.serializer;

import  java.io.*;

import org.apache.commons.logging.*;

/**
 * CSVフォーマットの文字列を二次元配列へ変換するパーザー。
**/
public final class CSVParser extends SVParser {
    public static Log log = LogFactory.getLog(CSVParser.class); 

    /**
     * 生成子
     * @param   text    パーズ対象文字列
     * @exception       IOException 読み込み失敗（あがりません）
    **/
    public CSVParser(String text) throws IOException {
        this(new StringReader(text));
    }

    /**
     * 生成子
     * @param   reader  文字列入力リーダー
     * @exception       IOException 読み込み失敗
    **/
    public CSVParser(Reader reader) throws IOException {
        super(reader, ",", "\"", "\n", " 　\t");
    }
}