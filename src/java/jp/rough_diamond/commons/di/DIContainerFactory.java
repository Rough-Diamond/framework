/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.di;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * DI�R���e�i�̎������B������DIContainer���Ǘ�����N���X�ł��B
 * <p>
 *   �f�t�H���g�����ł́ASpringFramework�����b�v����DIContainer��ێ����܂��B
 * </p>
 * <p>
 *   �Ȃ��ASpringFramework�����b�v����DIContainer�̓��t���N�V�����o�R�Ő������s�����߁A
 *   SpringFramrwork�Ƃ̐ÓI�Ȋ֘A�͂���܂���B<br />
 *   SpringFramework�ȊO��DIContainer�����b�v����DIContainer���g�p����ꍇ�́A
 *   getDIContainer()���Ăяo���O��setDIContainer()���Ăяo����DIContainer�����O�ɃZ�b�g���邱�ƂŁA
 *   SpringFramework�Ƃ̓��I�Ȋ֘A�������Ȃ��Ȃ�ASpringFramework�֘A��jar��classpath�ɉ�����K�v�͂Ȃ��Ȃ�܂��B
 * </p>
 */
public class DIContainerFactory {
	private final static Log log = LogFactory.getLog(DIContainerFactory.class);
	
	private static DIContainer instance;
	private final static String DEFAULT_DI_CONTAINER = "jp.rough_diamond.commons.di.SpringFramework";
	
	/**
	 * DIContainer���擾����
	 * @return DIContainer
	 */
	synchronized public static DIContainer getDIContainer() {
		if(instance == null) {
			try {
				instance = (DIContainer)Class.forName(DEFAULT_DI_CONTAINER).newInstance();
			} catch (Exception e) {
				log.warn(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}
		return instance;
	}
	
	/**
	 * DIContainer��ݒ肷��
	 * �ȍ~�AgetDIContainer()�ŕԋp�����DIContainer�͐ݒ肳�ꂽDIContainer�ƂȂ�B
	 * null���Z�b�g�����ꍇ�̓f�t�H���g��DIContainer�ł���SpringFramework���ԋp�����B
	 * @param container DIContainer
	 */
	public synchronized static void setDIContainer(DIContainer container) {
		instance = container;
	}
}
