/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * �ǂݍ��ݍς݃T�C�Y����ێ����邽�߂̃X�g���[���̃��b�p�[�ł��B
 * �ǂݍ��ݍς݃T�C�Y�𗘗p���ăC���W�P�[�^�[����\������ꍇ�ɗ��p���Ă��������B
 */
public class ReadCountInputStream extends InputStream {
	private InputStream 		is;
	private volatile long				count;
	private volatile long				markCount;
	
	/**
	 * �ǂݍ��ݍς݃T�C�Y��ێ�����X�g���[���𐶐����܂��B
	 * @param is
	 */
	public ReadCountInputStream(InputStream is) {
		this.is = is;
		this.count = 0L;
		this.markCount = 0L;
	}
	
	@Override
	public boolean markSupported() {
		return is.markSupported();
	}
	
	@Override
	public synchronized void mark(int readlimit) {
		is.mark(readlimit);
		this.markCount = this.count;
	}

	@Override
	public synchronized void reset() throws IOException {
		is.reset();
		this.count = this.markCount;
	}

	@Override
	public long skip(long n) throws IOException {
		long ret = is.skip(n);
		this.count+= ret;
		return ret;
	}

	/**
	 * ���݂̓ǂݍ��ݍς݃T�C�Y��ԋp���܂��B
	 * @return
	 */
	public long getReadSize() {
		return count;
	}
	
	@Override
	public int read() throws IOException {
		int ret = is.read();
		if(ret != -1) {
			count++;
		}
		return ret;
	}
}
