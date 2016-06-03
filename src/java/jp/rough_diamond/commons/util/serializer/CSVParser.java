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
 * CSV�t�H�[�}�b�g�̕������񎟌��z��֕ϊ�����p�[�U�[�B
**/
public final class CSVParser extends SVParser {
    public static Log log = LogFactory.getLog(CSVParser.class); 

    /**
     * �����q
     * @param   text    �p�[�Y�Ώە�����
     * @exception       IOException �ǂݍ��ݎ��s�i������܂���j
    **/
    public CSVParser(String text) throws IOException {
        this(new StringReader(text));
    }

    /**
     * �����q
     * @param   reader  ��������̓��[�_�[
     * @exception       IOException �ǂݍ��ݎ��s
    **/
    public CSVParser(Reader reader) throws IOException {
        super(reader, ",", "\"", "\n", " �@\t");
    }
}