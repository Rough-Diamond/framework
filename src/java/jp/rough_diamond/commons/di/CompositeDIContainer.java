/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.di;

import java.util.Arrays;
import java.util.List;

/**
 * ������DI�R���e�i���P��DI�R���e�i�Ƃ݂Ȃ��N���X
 * �T���́A�����q�ɓn���ꂽ���X�g�̏��ɍs��
 */
public class CompositeDIContainer extends AbstractDIContainer {
	private List<DIContainer> containers;
	public CompositeDIContainer(DIContainer... containers) {
		this(Arrays.asList(containers));
	}
	
	public CompositeDIContainer(List<DIContainer> containers) {
		this.containers = containers;
	}
	public <T> T getObject(Class<T> type, Object key) {
		for(DIContainer di : containers) {
			T ret = di.getObject(type, key);
			if(ret != null) {
				return ret;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T> T getSource(Class<T> type) {
		return (T)containers;
	}
}
