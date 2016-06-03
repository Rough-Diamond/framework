/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.extractor;

/**
 * �����^���當����^�֕ϊ�����
 * ���t�ϊ��֐���RDBMS�ɂ���ĈقȂ�̂ł����ŋz������
 */
public class DateToString extends Function {
	public final String format;
	/**
	 * @param value  �ϊ��O�̒l
	 * @param format�@�t�H�[�}�b�g�B
	 * �@�@java.text.SimpleDateFormat�����B�������Ay,M,d,H,m,s,S�̂�
	 */
	public DateToString(Value value, String format) {
		super(value);
		this.format = format;
	}
}
