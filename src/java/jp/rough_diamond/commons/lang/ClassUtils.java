/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.lang;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * �N���X�֘A�̃��[�e�B���e�B�N���X
 * �{�N���X�́Ajakarta commons lang��ClassUtils�̊g��API�ł��B
 *
 */
public class ClassUtils extends org.apache.commons.lang.ClassUtils {
	/**
	 * �N���X�I�u�W�F�N�g�̃��\�[�X����ԋp����
	 * �Ȃ��A�擪�Ɂu/�v�͕t�^���Ă��܂���B�K�v�ɉ����ĕt�^���Ă��������B
	 * @param �擾�ΏۃN���X
	 * @return �N���X�ɑΉ����郊�\�[�X��
	 */
	public static String translateResourceName(Class<?> cl) {
		String className = cl.getName();
		String resourceName = className.replaceAll("\\.", "/") + ".class";
		return resourceName;
	}
	
	/**
	 * �N���X�I�u�W�F�N�g�ɑΉ�����URL��ԋp����
	 * @param cl
	 * @return
	 */
	public static URL getClassURL(Class<?> cl) {
		return getClassURL(cl.getClassLoader(), translateResourceName(cl));
	}
	
	static URL getClassURL(ClassLoader cl, String resourceName) {
		return cl.getResource(resourceName);
	}
	
	/**
	 * �N���X�I�u�W�F�N�g����������File�I�u�W�F�N�g��ԋp����
	 * File�I�u�W�F�N�g���f�B���N�g���̏ꍇ�̓f�B���N�g���Ƃ��āA�t�@�C���̏ꍇ��jar�t�@�C������
	 * �w�肳�ꂽ�N���X�I�u�W�F�N�g���z�u����Ă��锻�f�ł��܂��B
	 * @param cl
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static File getClassPathRoot(Class<?> cl) {
		try {
			String resourceName = translateResourceName(cl);
			URL url = getClassURL(cl.getClassLoader(), resourceName);
			if(url.getProtocol().equals("file")) {
				String fileName = URLDecoder.decode(url.getPath());
				String rootDirName = fileName.replaceAll(resourceName + "$", "");
				return new File(rootDirName);
			} else {
				String jarFileName = url.getPath().replaceAll("\\!.*", "");
				return new File(URLDecoder.decode(new URL(jarFileName).getPath()));
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}
