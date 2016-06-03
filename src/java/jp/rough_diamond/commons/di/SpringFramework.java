/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.di;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

/**
 * SpringFramework�𗘗p����DI�R���e�i�̎����ł��B<br />
 * @see <a href="http://www.springframework.org/" target="_blank">Spring Framework</a> 
 */
public class SpringFramework 
		extends AbstractDIContainer { 
	
	private final static Log log = LogFactory.getLog(SpringFramework.class);

	private BeanFactory factory;

	/**
	 * ClassPath���beans.xml��p����XMLBeanFactory����������BeanFactory��DI�R���e�i�����Ƃ���I�u�W�F�N�g�𐶐����܂��B
	 */
	public SpringFramework() {
		this("beans.xml");
	}
	
	/**
	 * �w�肳�ꂽ���\�[�X��ClassPath�����`�t�@�C����Ǎ����XMLBeanFactory����������BeanFactory��DI�R���e�i�����Ƃ���I�u�W�F�N�g�𐶐����܂��B
	 * @param configName	classPath��ɑ��݂���DI��`�t�@�C��
	 */
	public SpringFramework(String configName) {
		this(new ClassPathResource(configName));
	}

	/**
	 * �w�肳�ꂽ�t�@�C���̒�`�t�@�C����Ǎ����XMLBeanFactory����������BeanFactory��DI�R���e�i�����Ƃ���I�u�W�F�N�g�𐶐����܂��B
	 * @param f	DI��`�t�@�C���̃t�@�C���I�u�W�F�N�g
	 */
	public SpringFramework(File f) throws FileNotFoundException {
		this(new FileSystemResource(f));
	}
	
	/**
	 * �w�肳�ꂽURL�̒�`�t�@�C����Ǎ����XMLBeanFactory����������BeanFactory��DI�R���e�i�����Ƃ���I�u�W�F�N�g�𐶐����܂��B
	 * @param url	DI��`�t�@�C����URL�I�u�W�F�N�g
	 */
	public SpringFramework(URL url) throws IOException {
		this(new UrlResource(url));
	}
	
	/**
	 * �w�肳�ꂽ���̓X�g���[����Ǎ����XMLBeanFactory����������BeanFactory��DI�R���e�i�����Ƃ���I�u�W�F�N�g�𐶐����܂��B
	 * @param is	DI��`���̓��̓X�g���[��
	 * @deprecated �G���[���o�邱�Ƃ�����̂Ŏg�p���Ȃ����Ƃ𐄏�
	 */
	public SpringFramework(InputStream is) {
		this(new InputStreamResource(is, ""));
	}
	
	/**
	 * �w�肳�ꂽ���\�[�X�I�u�W�F�N�g����XMLBeanFactory�𐶐�����B
	 * @param resource
	 * @since 1.3
	 */
	public SpringFramework(Resource resource) {
		factory = new XmlBeanFactory(resource);
	}

	@SuppressWarnings("unchecked")
	public <T> T getObject(Class<T> type, Object key) {
		if(log.isDebugEnabled()) {
			log.debug("getObject calling[" + key + "]");
		}
		try {
			return (T)factory.getBean((String)key);
		} catch(Exception ex) {
			if(log.isDebugEnabled()) {
				log.debug(key + " is not bean.", ex);
			}
			return null;
		} catch(Throwable t) {
			log.error(key + " is not bean.", t);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getSource(Class<T> type) {
		return (T)factory;
	}
}
