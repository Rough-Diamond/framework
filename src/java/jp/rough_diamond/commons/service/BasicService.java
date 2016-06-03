/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.service;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import jp.rough_diamond.commons.di.DIContainerFactory;
import jp.rough_diamond.commons.extractor.Condition;
import jp.rough_diamond.commons.extractor.Extractor;
import jp.rough_diamond.commons.extractor.Property;
import jp.rough_diamond.commons.resource.Message;
import jp.rough_diamond.commons.resource.Messages;
import jp.rough_diamond.commons.resource.MessagesIncludingException;
import jp.rough_diamond.commons.resource.ResourceManager;
import jp.rough_diamond.commons.service.annotation.Check;
import jp.rough_diamond.commons.service.annotation.MaxCharLength;
import jp.rough_diamond.commons.service.annotation.MaxLength;
import jp.rough_diamond.commons.service.annotation.NestedComponent;
import jp.rough_diamond.commons.service.annotation.NotNull;
import jp.rough_diamond.commons.service.annotation.Unique;
import jp.rough_diamond.commons.service.annotation.Verifier;
import jp.rough_diamond.framework.service.Service;
import jp.rough_diamond.framework.service.ServiceLocator;
import jp.rough_diamond.framework.transaction.TransactionAttribute;
import jp.rough_diamond.framework.transaction.TransactionAttributeType;
import jp.rough_diamond.framework.transaction.VersionUnmuchException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * DAO��{�T�[�r�X
 */
@SuppressWarnings("unchecked")
abstract public class BasicService implements Service {
    private final static Log log = LogFactory.getLog(BasicService.class);
    
    public final static String 	BOOLEAN_CHAR_T = "Y";
    public final static String 	BOOLEAN_CHAR_F = "N";

    /**
     * ���R�[�h�̃��b�N���[�h
     * @author e-yamane
     */
    public static enum RecordLock {
    	NONE,				//���b�N���Ȃ�
    	FOR_UPDATE,			//�r�����b�N
    	FOR_UPDATE_NOWAIT,	//�r�����b�N�i�҂��Ȃ��j
    }
    
    private final static String DEFAULT_BASIC_SERVICE_CLASS_NAME = "jp.rough_diamond.commons.service.hibernate.HibernateBasicService";
    
    /**
     * Basic�T�[�r�X���擾����
     * @return  Basic�T�[�r�X
     */
    public static BasicService getService() {
    	return ServiceLocator.getService(BasicService.class, DEFAULT_BASIC_SERVICE_CLASS_NAME);
    }

    /**
     * �w�肳�ꂽ��L�[�ɑΉ�����I�u�W�F�N�g���擾����
     * �擾�����I�u�W�F�N�g�͉i�����G���W���i��FHibernate�j���L���b�V������悤�Ɏw������
     * �܂��A�擾�������R�[�h�ɑ΂��郍�b�N�͍s��Ȃ�
     * @param <T>	�擾����N���X�̃^�C�v
     * @param type	�擾����N���X�̃^�C�v
     * @param pk	��L�[
     * @return		��L�[�ɑΉ�����I�u�W�F�N�g�B�Ή�����I�u�W�F�N�g�������ꍇ��null��ԋp����
     */
    public <T> T findByPK(Class<T> type, Serializable pk) {
    	return findByPK(type, pk, false);
    }
    
    /**
     * �w�肳�ꂽ��L�[�ɑΉ�����I�u�W�F�N�g���擾����
     * �擾�����I�u�W�F�N�g�͉i�����G���W���i��FHibernate�j���L���b�V������悤�Ɏw������
     * @param <T>	�擾����N���X�̃^�C�v
     * @param type	�擾����N���X�̃^�C�v
     * @param pk	��L�[
     * @param lock	�擾�I�u�W�F�N�g�̃��b�N���[�h���w�肷��
     * @return		��L�[�ɑΉ�����I�u�W�F�N�g�B�Ή�����I�u�W�F�N�g�������ꍇ��null��ԋp����
     */
    public <T> T findByPK(Class<T> type, Serializable pk, RecordLock lock) {
    	return findByPK(type, pk, false, lock);
    }

    /**
     * �w�肳�ꂽ��L�[�ɑΉ�����I�u�W�F�N�g���擾����
     * �擾�������R�[�h�ɑ΂��郍�b�N�͍s��Ȃ�
     * @param <T>		�擾����N���X�̃^�C�v
     * @param type		�擾����N���X�̃^�C�v
     * @param pk		��L�[
     * @param isNoCache	true�F�i�����G���W���i��FHibernate�j���L���b�V�����Ȃ� false:�L���b�V������	
     * @return			��L�[�ɑΉ�����I�u�W�F�N�g�B�Ή�����I�u�W�F�N�g�������ꍇ��null��ԋp����
     */
    public <T> T findByPK(Class<T> type, Serializable pk, boolean isNoCache) {
    	return findByPK(type, pk, isNoCache, RecordLock.NONE);
    }
    
    /**
     * �w�肳�ꂽ��L�[�ɑΉ�����I�u�W�F�N�g���擾����
     * �Ȃ��A���R�[�h���b�N�A�I�u�W�F�N�g�L���b�V���Ɋւ��Ă͎g�p����i�����G���W���ɂ���Ă�
     * �������K�p����Ȃ��ꍇ������܂��B
     * @param <T>		�擾����N���X�̃^�C�v
     * @param type		�擾����N���X�̃^�C�v
     * @param pk		��L�[
     * @param isNoCache	true�F�i�����G���W���i��FHibernate�j���L���b�V�����Ȃ� false:�L���b�V������	
     * @param lock		�擾�I�u�W�F�N�g�̃��b�N���[�h���w�肷��
     * @return			��L�[�ɑΉ�����I�u�W�F�N�g�B�Ή�����I�u�W�F�N�g�������ꍇ��null��ԋp����
     */
    abstract public <T> T findByPK(Class<T> type, Serializable pk, boolean isNoCache, RecordLock lock);

    /**
     * ���������ɑΉ�����I�u�W�F�N�g�ꗗ���擾����
     * �擾�����I�u�W�F�N�g�͉i�����G���W���i��FHibernate�j���L���b�V������悤�Ɏw������
     * �܂��A�擾�������R�[�h�ɑ΂��郍�b�N�͍s��Ȃ�
     * @param <T>		�擾����I�u�W�F�N�g�̃^�C�v
     * @param extractor	��������
     * @return			���������ɑΉ�����I�u�W�F�N�g�ꗗ�B�P�����Y������f�[�^��������Ηv�f���O�̃��X�g��ԋp����
     */
    public <T> List<T> findByExtractor(Extractor extractor) {
    	return findByExtractor(extractor, false);
    }

    /**
     * ���������ɑΉ�����I�u�W�F�N�g�ꗗ���擾����
     * �擾�����I�u�W�F�N�g�͉i�����G���W���i��FHibernate�j���L���b�V������悤�Ɏw������
     * @param <T>		�擾����I�u�W�F�N�g�̃^�C�v
     * @param extractor	��������
     * @param lock		�擾�I�u�W�F�N�g�̃��b�N���[�h���w�肷��
     * @return			���������ɑΉ�����I�u�W�F�N�g�ꗗ�B�P�����Y������f�[�^��������Ηv�f���O�̃��X�g��ԋp����
     */
    public <T> List<T> findByExtractor(Extractor extractor, RecordLock lock) {
    	return findByExtractor(extractor, false, lock);
    }

    /**
     * ���������ɑΉ�����I�u�W�F�N�g�ꗗ���擾����
     * �擾�������R�[�h�ɑ΂��郍�b�N�͍s��Ȃ�
     * @param <T>		�擾����I�u�W�F�N�g�̃^�C�v
     * @param extractor	��������
     * @param isNoCache	true�F�i�����G���W���i��FHibernate�j���L���b�V�����Ȃ� false:�L���b�V������	
     * @return			���������ɑΉ�����I�u�W�F�N�g�ꗗ�B�P�����Y������f�[�^��������Ηv�f���O�̃��X�g��ԋp����
     */
    public <T> List<T> findByExtractor(Extractor extractor, boolean isNoCache) {
    	return findByExtractor(getReturnType(extractor), extractor, isNoCache, RecordLock.NONE);
    }
    
    /**
     * ���������ɑΉ�����I�u�W�F�N�g�ꗗ���擾����
     * @param <T>		�擾����I�u�W�F�N�g�̃^�C�v
     * @param extractor	��������
     * @param isNoCache	true�F�i�����G���W���i��FHibernate�j���L���b�V�����Ȃ� false:�L���b�V������	
     * @param lock		�擾�I�u�W�F�N�g�̃��b�N���[�h���w�肷��
     * @return			���������ɑΉ�����I�u�W�F�N�g�ꗗ�B�P�����Y������f�[�^��������Ηv�f���O�̃��X�g��ԋp����
     */
    public <T> List<T> findByExtractor(Extractor extractor, boolean isNoCache, RecordLock lock) {
    	return findByExtractor(getReturnType(extractor), extractor, isNoCache, lock);
    }

    Class getReturnType(Extractor extractor) {
    	if(extractor.returnType != null) {
    		return extractor.returnType;
    	} else if(extractor.getValues().size() != 0) {
    		return Map.class;
    	} else {
    		return extractor.target;
    	}
    }
    
    abstract protected <T> List<T> findByExtractor(Class<T> type, Extractor extractor, boolean isNoCache, RecordLock lock);

    /**
     * ���������ɑΉ�����I�u�W�F�N�g�ꗗ�ƁA���������ɍ��v���鑍�������擾����
     * �擾�����I�u�W�F�N�g�͉i�����G���W���i��FHibernate�j���L���b�V������悤�Ɏw������
     * �܂��A�擾�������R�[�h�ɑ΂��郍�b�N�͍s��Ȃ�
     * @param <T>		�擾����I�u�W�F�N�g�̃^�C�v
     * @param extractor	��������
     * @return			��������
     */
	public <T> FindResult<T> findByExtractorWithCount(Extractor extractor) {
    	return findByExtractorWithCount(extractor, false);
    }

	/**
     * ���������ɑΉ�����I�u�W�F�N�g�ꗗ�ƁA���������ɍ��v���鑍�������擾����
     * �擾�����I�u�W�F�N�g�͉i�����G���W���i��FHibernate�j���L���b�V������悤�Ɏw������
     * @param <T>		�擾����I�u�W�F�N�g�̃^�C�v
     * @param extractor	��������
     * @param lock		�擾�I�u�W�F�N�g�̃��b�N���[�h���w�肷��
     * @return			��������
	 */
	public <T> FindResult<T> findByExtractorWithCount(Extractor extractor, RecordLock lock) {
    	return findByExtractorWithCount(extractor, false, lock);
    }

	/**
     * ���������ɑΉ�����I�u�W�F�N�g�ꗗ�ƁA���������ɍ��v���鑍�������擾����
     * �擾�������R�[�h�ɑ΂��郍�b�N�͍s��Ȃ�
     * @param <T>		�擾����I�u�W�F�N�g�̃^�C�v
     * @param extractor	��������
     * @param isNoCache	true�F�i�����G���W���i��FHibernate�j���L���b�V�����Ȃ� false:�L���b�V������	
     * @return			��������
	 */
	public <T> FindResult<T> findByExtractorWithCount(Extractor extractor, boolean isNoCache) {
    	return findByExtractorWithCount(extractor, isNoCache, RecordLock.NONE);
    }

	/**
     * ���������ɑΉ�����I�u�W�F�N�g�ꗗ�ƁA���������ɍ��v���鑍�������擾����
     * @param <T>		�擾����I�u�W�F�N�g�̃^�C�v
     * @param extractor	��������
     * @param isNoCache	true�F�i�����G���W���i��FHibernate�j���L���b�V�����Ȃ� false:�L���b�V������	
     * @param lock		�擾�I�u�W�F�N�g�̃��b�N���[�h���w�肷��
     * @return			��������
	 */
	public <T> FindResult<T> findByExtractorWithCount(Extractor extractor, boolean isNoCache, RecordLock lock) {
    	return findByExtractorWithCount(getReturnType(extractor), extractor, isNoCache, lock);
    }

    protected <T> FindResult<T> findByExtractorWithCount(Class<T> type, Extractor extractor, boolean isNoCache, RecordLock lock) {
    	List<T> list = (extractor.getLimit() == 0) ? new ArrayList<T>() : findByExtractor(type, extractor, isNoCache, lock);
    	long count = getCountByExtractor(extractor);
    	return new FindResult<T>(list, count);
    }
    
	/**
	 * ���������ɍ��v����i���I�u�W�F�N�g�̌������擾����
	 * @param <T>		�����擾�ΏۃI�u�W�F�N�g�^�C�v
	 * @param extractor	��������
	 * @return			���������ɍ��v����i���I�u�W�F�N�g�̌���
	 */
	abstract public <T> long getCountByExtractor(Extractor extractor);
	
	abstract public <T> T replaceProxy(T t);

    /**
     * �w�肳�ꂽ�N���X�̉i�����I�u�W�F�N�g��S�Ď擾����
     * �擾�����I�u�W�F�N�g�͉i�����G���W���i��FHibernate�j���L���b�V������悤�Ɏw������
     * �܂��A�擾�������R�[�h�ɑ΂��郍�b�N�͍s��Ȃ��A�t�F�b�`�T�C�Y�͉��ʃ��C�u�����Ɉˑ�����
     * @param <T>	�擾�ΏۃN���X�^�C�v
     * @param type	�擾�ΏۃN���X�^�C�v
     * @return		�擾�ΏۃN���X�̉i���I�u�W�F�N�g�ꗗ�B�P���������ꍇ�͗v�f���O�̃��X�g��ԋp����
     */
    public <T> List<T> findAll(Class<T> type) {
    	return findAll(type, false, Extractor.DEFAULT_FETCH_SIZE, RecordLock.NONE);
    }

    /**
     * �w�肳�ꂽ�N���X�̉i�����I�u�W�F�N�g��S�Ď擾����
     * �擾�������R�[�h�ɑ΂��郍�b�N�͍s��Ȃ����A�t�F�b�`�T�C�Y�͉��ʃ��C�u�����Ɉˑ�����
     * @param <T>		�擾�ΏۃN���X�^�C�v
     * @param type		�擾�ΏۃN���X�^�C�v
     * @param isNoCache	true�F�i�����G���W���i��FHibernate�j���L���b�V�����Ȃ� false:�L���b�V������	
     * @return			�擾�ΏۃN���X�̉i���I�u�W�F�N�g�ꗗ�B�P���������ꍇ�͗v�f���O�̃��X�g��ԋp����
     */
    public <T> List<T> findAll(Class<T> type, boolean isNoCache) {
    	return findAll(type, isNoCache, Extractor.DEFAULT_FETCH_SIZE, RecordLock.NONE);
    }

    /**
     * �w�肳�ꂽ�N���X�̉i�����I�u�W�F�N�g��S�Ď擾����
     * �擾�����I�u�W�F�N�g�͉i�����G���W���i��FHibernate�j���L���b�V������悤�Ɏw������
     * �܂��A�擾�������R�[�h�ɑ΂��郍�b�N�͍s��Ȃ�
     * @param <T>		�擾�ΏۃN���X�^�C�v
     * @param type		�擾�ΏۃN���X�^�C�v
     * @param fetchSize	�t�F�b�`�T�C�Y�i�����I�ȐU�镑���j
     * @return			�擾�ΏۃN���X�̉i���I�u�W�F�N�g�ꗗ�B�P���������ꍇ�͗v�f���O�̃��X�g��ԋp����
     */
    public <T> List<T> findAll(Class<T> type, int fetchSize) {
    	return findAll(type, false, fetchSize, RecordLock.NONE);
    }
    
    /**
     * �w�肳�ꂽ�N���X�̉i�����I�u�W�F�N�g��S�Ď擾����
     * �擾�������R�[�h�ɑ΂��郍�b�N�͍s��Ȃ����A�t�F�b�`�T�C�Y�͉��ʃ��C�u�����Ɉˑ�����
     * @param <T>		�擾�ΏۃN���X�^�C�v
     * @param type		�擾�ΏۃN���X�^�C�v
     * @param lock		�擾�I�u�W�F�N�g�̃��b�N���[�h���w�肷��
     * @return			�擾�ΏۃN���X�̉i���I�u�W�F�N�g�ꗗ�B�P���������ꍇ�͗v�f���O�̃��X�g��ԋp����
     */
    public <T> List<T> findAll(Class<T> type, RecordLock lock) {
    	return findAll(type, false, Extractor.DEFAULT_FETCH_SIZE, lock);
    }

    /**
     * �w�肳�ꂽ�N���X�̉i�����I�u�W�F�N�g��S�Ď擾����
     * �擾�������R�[�h�ɑ΂��郍�b�N�͍s��Ȃ�
     * @param <T>		�擾�ΏۃN���X�^�C�v
     * @param type		�擾�ΏۃN���X�^�C�v
     * @param isNoCache	true�F�i�����G���W���i��FHibernate�j���L���b�V�����Ȃ� false:�L���b�V������	
     * @param fetchSize	�t�F�b�`�T�C�Y�i�����I�ȐU�镑���j
     * @return			�擾�ΏۃN���X�̉i���I�u�W�F�N�g�ꗗ�B�P���������ꍇ�͗v�f���O�̃��X�g��ԋp����
     */
    public <T> List<T> findAll(Class<T> type, boolean isNoCache, int fetchSize) {
    	return findAll(type, isNoCache, fetchSize, RecordLock.NONE);
    }
    
    /**
     * �w�肳�ꂽ�N���X�̉i�����I�u�W�F�N�g��S�Ď擾����
     * �擾�������R�[�h�ɑ΂��郍�b�N�͍s��Ȃ�
     * @param <T>		�擾�ΏۃN���X�^�C�v
     * @param type		�擾�ΏۃN���X�^�C�v
     * @param isNoCache	true�F�i�����G���W���i��FHibernate�j���L���b�V�����Ȃ� false:�L���b�V������	
     * @param lock		�擾�I�u�W�F�N�g�̃��b�N���[�h���w�肷��
     * @return			�擾�ΏۃN���X�̉i���I�u�W�F�N�g�ꗗ�B�P���������ꍇ�͗v�f���O�̃��X�g��ԋp����
     */
    public <T> List<T> findAll(Class<T> type, boolean isNoCache, RecordLock lock) {
    	return findAll(type, isNoCache, Extractor.DEFAULT_FETCH_SIZE, lock);
    }
    
    /**
     * �w�肳�ꂽ�N���X�̉i�����I�u�W�F�N�g��S�Ď擾����
     * �擾�������R�[�h�ɑ΂��郍�b�N�͍s��Ȃ�
     * @param <T>		�擾�ΏۃN���X�^�C�v
     * @param type		�擾�ΏۃN���X�^�C�v
     * @param fetchSize	�t�F�b�`�T�C�Y�i�����I�ȐU�镑���j
     * @param lock		�擾�I�u�W�F�N�g�̃��b�N���[�h���w�肷��
     * @return			�擾�ΏۃN���X�̉i���I�u�W�F�N�g�ꗗ�B�P���������ꍇ�͗v�f���O�̃��X�g��ԋp����
     */
    public <T> List<T> findAll(Class<T> type, int fetchSize, RecordLock lock) {
    	return findAll(type, false, fetchSize, lock);
    }
    
    /**
     * �w�肳�ꂽ�N���X�̉i�����I�u�W�F�N�g��S�Ď擾����
     * �擾�������R�[�h�ɑ΂��郍�b�N�͍s��Ȃ�
     * @param <T>		�擾�ΏۃN���X�^�C�v
     * @param type		�擾�ΏۃN���X�^�C�v
     * @param isNoCache	true�F�i�����G���W���i��FHibernate�j���L���b�V�����Ȃ� false:�L���b�V������	
     * @param fetchSize	�t�F�b�`�T�C�Y�i�����I�ȐU�镑���j
     * @param lock		�擾�I�u�W�F�N�g�̃��b�N���[�h���w�肷��
     * @return			�擾�ΏۃN���X�̉i���I�u�W�F�N�g�ꗗ�B�P���������ꍇ�͗v�f���O�̃��X�g��ԋp����
     */
    public <T> List<T> findAll(Class<T> type, boolean isNoCache, int fetchSize, RecordLock lock) {
		Extractor ex = new Extractor(type);
		ex.setFetchSize(fetchSize);
		return findByExtractor(type, ex, isNoCache, lock);
    }
    
    /**
     * �w�肳�ꂽ�I�u�W�F�N�g�i�Q�j���i����(INSERT)����
     * ��L�[��String��������Number���p������I�u�W�F�N�g�ł���null�̏ꍇ�͎�L�[�͎����I��
     * ���j�[�N�Ȓl���̔Ԃ����
     * @param <T>		�i�����I�u�W�F�N�g�̃^�C�v
     * @param objects	�i�����I�u�W�F�N�g�Q
     * @throws MessagesIncludingException	���ؗ�O�i�P�ȏ�̃v���p�e�B�̌��؂Ɏ��s�j
     */
    abstract public <T> void insert(T... objects) throws MessagesIncludingException;

    /**
     * �w�肳�ꂽ�I�u�W�F�N�g�i�Q�j���i�����iUPDATE�j����
     * @param <T>		�i�����I�u�W�F�N�g�̃^�C�v
     * @param objects	�i�����I�u�W�F�N�g�Q
     * @throws MessagesIncludingException	���ؗ�O�i�P�ȏ�̃v���p�e�B�̌��؂Ɏ��s�j
     */
    abstract public <T> void update(T... objects) throws VersionUnmuchException, MessagesIncludingException;

    /**
     * ���������ɍ��v����I�u�W�F�N�g�Q���폜����
     * @param extractor						�폜�I�u�W�F�N�g�̌�������
     * @throws VersionUnmuchException		�_���폜���̊y�ϓI���b�L���O�G���[
     * @throws MessagesIncludingException	�_���폜���̌��ؗ�O
     */
    public void deleteByExtractor(Extractor extractor) throws VersionUnmuchException, MessagesIncludingException {
    	List list = findByExtractor(extractor, true);
    	delete(list.toArray());
    }
    
    /**
     * �w�肵���N���X�̑S�i���I�u�W�F�N�g���폜����
     * @param <T>	�폜�Ώۉi�����^�C�v
     * @param cl	�폜�Ώۉi�����^�C�v
     */
    public <T> void deleteAll(Class<T> cl) {
    	List<T> list = findAll(cl);
    	try {
			delete(list.toArray(new Object[list.size()]));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }

    /**
     * �w�肳�ꂽ�I�u�W�F�N�g�Q���폜����
     * @param objects						�폜�ΏۃI�u�W�F�N�g
     * @throws VersionUnmuchException		�_���폜���̊y�ϓI���b�L���O�G���[
     * @throws MessagesIncludingException	�_���폜���̌��ؗ�O
     */
    abstract public void delete(Object... objects) throws VersionUnmuchException, MessagesIncludingException;

    /**
     * �w�肳�ꂽ�N���X�̎w�肳�ꂽ��L�[�Ɉ�v����I�u�W�F�N�g���폜����
     * @param <T>	�폜�Ώۉi�����^�C�v
     * @param type	�폜�Ώۉi�����^�C�v
     * @param pk	�폜�ΏۃI�u�W�F�N�g�̎�L�[
     * @throws VersionUnmuchException		�_���폜���̊y�ϓI���b�L���O�G���[
     * @throws MessagesIncludingException	�_���폜���̌��ؗ�O
     */
    public <T> void deleteByPK(Class<T> type, Serializable pk) throws VersionUnmuchException, MessagesIncludingException {
        T o = findByPK(type, pk, true);
        if(o != null) {
            delete(o);
        }
    }

    /**
     * �I�u�W�F�N�g�̉i�����ۂ����؂��s��
     * @param o		���ؑΏۃI�u�W�F�N�g
     * @param when	�i���������i�ǉ�/�X�V�j
     * @return		���؎��s�����ꍇ�̌������b�Z�[�W�Q
     */
    public Messages validate(Object o, WhenVerifier when) {
        Messages ret = new Messages();
    	try {
	    	ret.add(unitPropertyValidate(o, when));
            ret.add(customValidate(o, when, ret.hasError()));
    		return ret;
    	} catch(Exception ex) {
    		throw new RuntimeException(ex);
    	}
    }
    
    /**
     * �w�肳��Ă����I�u�W�F�N�g��DAO�ɃL���b�V������Ă���ꍇ�ɍ폜����
     * @param o
     */
    @TransactionAttribute(TransactionAttributeType.NOP)
    abstract public void clearCache(Object o);
    
    protected void fireEvent(CallbackEventType eventType, List objects) throws VersionUnmuchException, MessagesIncludingException {
        if(objects.size() == 0) {
            return;
        }
        Map<Class, SortedSet<CallbackEventListener>> map = new HashMap<Class, SortedSet<CallbackEventListener>>();
        for(Object o : objects) {
            if(o == null) {
                continue;
            }
            SortedSet<CallbackEventListener> set = map.get(o.getClass());
            if(set == null) {
            	set = getEventListener(o.getClass(), eventType);
            	map.put(o.getClass(), set);
            }
        }
        if(map.size() == 0) {
            return;
        }
        try {
            for(Object o : objects) {
                SortedSet<CallbackEventListener> set = map.get(o.getClass());
                for(CallbackEventListener listener : set) {
                	listener.callback(o, eventType);
                }
                fireEvent(eventType, getNestedComponents(o));
            }
        } catch(InvocationTargetException ite) {
            Throwable t = ite.getTargetException();
            if(t instanceof VersionUnmuchException) {
                throw (VersionUnmuchException)t;
            } else if(t instanceof MessagesIncludingException) {
            	throw (MessagesIncludingException)t;
            } else {
                throw new RuntimeException(t);
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private List getNestedComponents(Object o) {
		try {
	    	List<PropertyDescriptor> list = getNestedComponentGetters(o.getClass());
	    	List ret = new ArrayList();
	    	for(PropertyDescriptor pd : list) {
	    		Method m = pd.getReadMethod();
	    		Object val;
					val = m.invoke(o);
	    		if(val != null) {
	    			ret.add(val);
	    		}
	    	}
	    	return ret;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
    
    private List<PropertyDescriptor> getNestedComponentGetters(Class cl) {
    	List<PropertyDescriptor> list = nestedComponentGetterMap.get(cl);
    	if(list == null) {
    		list = makeNestedComponentGetters(cl);
    	}
    	return list;
    }
    
    private List<PropertyDescriptor> makeNestedComponentGetters(Class cl) {
    	List<PropertyDescriptor> list = new ArrayList<PropertyDescriptor>();
    	PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(cl);
    	for(PropertyDescriptor pd : pds) {
    		Method m = pd.getReadMethod();
    		if(m == null) {
    			continue;
    		}
    		NestedComponent nc = m.getAnnotation(NestedComponent.class);
    		if(nc != null) {
    			list.add(pd);
    		}
    	}
    	nestedComponentGetterMap.put(cl, list);
    	return list;
	}

	private Map<Class, List<PropertyDescriptor>> nestedComponentGetterMap =
    		new HashMap<Class, List<PropertyDescriptor>>();
    
    private Map<List<?>, Map<Class, Map<CallbackEventType, SortedSet<CallbackEventListener>>>> eventListenrsCache =
    				new IdentityHashMap<List<?>, Map<Class,Map<CallbackEventType,SortedSet<CallbackEventListener>>>>();
    
	Map<Class, Map<CallbackEventType, SortedSet<CallbackEventListener>>> getEventListenerCache() {
		List<?> listeners = getPersistenceEventListeners();
		Map<Class, Map<CallbackEventType, SortedSet<CallbackEventListener>>> ret = 
															eventListenrsCache.get(listeners);
		if(ret == null) {
			ret = new HashMap<Class, Map<CallbackEventType, SortedSet<CallbackEventListener>>>();
			eventListenrsCache.put(listeners, ret);
		}
		return ret;
	}

	SortedSet<CallbackEventListener> getEventListener(Class cl, CallbackEventType eventType) {
        Map<CallbackEventType, SortedSet<CallbackEventListener>> map = getEventListenerCache().get(cl);
        if(map == null) {
            map = new HashMap<CallbackEventType, SortedSet<CallbackEventListener>>();
            getEventListenerCache().put(cl, map);
        }
        SortedSet<CallbackEventListener> set = map.get(eventType);
        if(set == null) {
            set = findEventListener(cl, eventType);
            map.put(eventType, set);
        }
        return set;
    }

    SortedSet<CallbackEventListener> findEventListener(Class cl, CallbackEventType eventType) {
        Class annotationType = eventType.getAnnotation();
        SortedSet<CallbackEventListener> set = new TreeSet<CallbackEventListener>();
        Method[] methods = cl.getMethods();
        for(Method m : methods) {
        	if(CallbackEventListener.isEventListener(null, cl, m, annotationType)) {
                set.add(new CallbackEventListener.SelfEventListener(m, annotationType));
        	}
        }
        List<?> listeners = getPersistenceEventListeners();
        for(Object listener : listeners) {
        	Class listenerType = listener.getClass();
        	Method[] listenerMethods = listenerType.getMethods();
        	for(Method m : listenerMethods) {
            	if(CallbackEventListener.isEventListener(listener, cl, m, annotationType)) {
                    set.add(new CallbackEventListener.EventAdapter(listener, m, annotationType));
            	}
        	}
        }
        return set;
    }
    
    final static List<?> NULL_LISTENERS = new ArrayList<Object>();
    public final static String PERSISTENCE_EVENT_LISTENERS = "persistenceEventListeners";
    List<?> getPersistenceEventListeners() {
    	List list = (List<?>)DIContainerFactory.getDIContainer().getObject(PERSISTENCE_EVENT_LISTENERS);
    	return (list == null) ? NULL_LISTENERS : list;
    }
    
    protected Messages unitPropertyValidate(Object o, WhenVerifier when) throws Exception {
    	Messages ret = new Messages();
    	Class cl = o.getClass();
    	PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(cl);
    	for(PropertyDescriptor pd : pds) {
    		Method m = pd.getReadMethod();
    		if(m != null) {
	    		NotNull nn = m.getAnnotation(NotNull.class);
	    		if(nn != null) {
	    			Object val = m.invoke(o);
	    			if(val == null) {
	    				if(log.isDebugEnabled()) {
	    					log.debug("�K�{�����G���[:" + nn.property());
	    				}
	    				ret.add(nn.property(), new Message("errors.required", ResourceManager.getResource().getString(nn.property())));
	    				//�K�{�G���[�Ȃ̂Œ����̃`�F�b�N�͕s�v
	    				continue;
	    			} else if(val instanceof String) {
	    				if(getLength((String)val) == 0) {
	    					if(log.isDebugEnabled()) {
	    						log.debug("�K�{�����G���[:" + nn.property());
	    					}
		    				ret.add(nn.property(), new Message("errors.required", ResourceManager.getResource().getString(nn.property())));
		    				//�K�{�G���[�Ȃ̂Œ����̃`�F�b�N�͕s�v
		    				continue;
	    				}
	    			}
	    		}
	    		MaxCharLength mcl = m.getAnnotation(MaxCharLength.class);
	    		if(mcl != null) {
	    			Object val = m.invoke(o);
	    			if(val != null && val.toString().length() > mcl.length()) {
	    				if(log.isDebugEnabled()) {
	    					log.debug("�ő啶�������߃G���[:" + mcl.property());
	    				}
	    				ret.add(mcl.property(), new Message("errors.maxcharlength", ResourceManager.getResource().getString(mcl.property()), "" + mcl.length()));
	    				//�����񒷒��߂̏ꍇ�̓o�C�g���̃`�F�b�N�͍s���܂���
	    				continue;
	    			}
	    		}
	    		MaxLength ml = m.getAnnotation(MaxLength.class);
	    		if(ml != null) {
	    			Object val = m.invoke(o);
	    			if(getLength(val) > ml.length()) {
	    				if(log.isDebugEnabled()) {
	    					log.debug("�ő咷���߃G���[:" + ml.property());
	    				}
	    				ret.add(ml.property(), new Message("errors.maxlength", ResourceManager.getResource().getString(ml.property()), "" + ml.length()));
	    			}
	    		}
    		}
    	}
		List<PropertyDescriptor> list = getNestedComponentGetters(cl);
		for(PropertyDescriptor pd : list) {
			Method m = pd.getReadMethod();
			Object val = m.invoke(o);
    		if(val != null) {
    			Messages tmp = unitPropertyValidate(val, when);
        		NestedComponent nc = m.getAnnotation(NestedComponent.class);
        		ret.add(replacePropertyName(tmp, nc));
    		}
		}
    	return ret;
    }
    
    Messages replacePropertyName(Messages msgs, NestedComponent nc) {
    	Messages ret = new Messages();
		if(!msgs.hasError()) {
			return ret;
		}
		for(String property : msgs.getProperties()) {
			List<Message> tmpMsgs = msgs.get(property);
			property = property.replaceAll("^[^\\.]+\\.", nc.property() + ".");
			for(Message msg : tmpMsgs) {
				ret.add(property, msg);
			}
		}
		return ret;
    }
    
    private Messages customValidate(Object o, WhenVerifier when, boolean hasError) throws Exception {
    	Messages ret = new Messages();
    	SortedSet<CallbackEventListener> listeners = getEventListener(o.getClass(), CallbackEventType.VERIFIER);
    	for(CallbackEventListener listener : listeners) {
			Verifier v = listener.method.getAnnotation(Verifier.class);
			if(!v.isForceExec() && hasError) {
				break;
			}
			for(WhenVerifier w : v.when()) {
				if(w == when) {
					ret.add(listener.validate(o, when));
					hasError = ret.hasError();
					break;
				}
			}
    	}
    	List<PropertyDescriptor> list = getNestedComponentGetters(o.getClass());
		for(PropertyDescriptor pd : list) {
			Method m = pd.getReadMethod();
			Object val = m.invoke(o);
    		if(val != null) {
    			Messages tmp = customValidate(val, when, hasError);
        		NestedComponent nc = m.getAnnotation(NestedComponent.class);
        		ret.add(replacePropertyName(tmp, nc));
    		}
		}
		return ret;
	}

    Map<Class, List<Unique>> uniqueMap = new HashMap<Class, List<Unique>>(); 
	private List<Unique> findUnique(Class cl) {
		List<Unique> ret = uniqueMap.get(cl);
		if(ret != null) {
			return ret;
		}
		ret = new ArrayList<Unique>();
		Unique u = (Unique)cl.getAnnotation(Unique.class);
		if(u != null) {
			ret.add(u);
		}
		Class parent = cl.getSuperclass();
		if(parent != null) {
			ret.addAll(findUnique(parent));
			uniqueMap.put(cl, ret);
		}
		return ret;
    }

	public final static String SKIP_UNIQUE_CHECK_TYPES = "skipUniqueCheckTypes";
	Set<Class<?>> skipUniqueCheckTypes = new HashSet<Class<?>>();
	Set<Class<?>> notSkipUniqueCheckTypes = new HashSet<Class<?>>();
	boolean isSkipUniqueCheckType(Class<?> cl, List<String> skipTypes) {
		if(skipUniqueCheckTypes.contains(cl)) {
			return true;
		}
		if(notSkipUniqueCheckTypes.contains(cl)) {
			return false;
		}
		boolean ret = isSkipUniqueCheckType2(cl, skipTypes);
		if(ret) {
			skipUniqueCheckTypes.add(cl);
		} else {
	    	notSkipUniqueCheckTypes.add(cl);
		}
    	return ret;
	}
	
	boolean isSkipUniqueCheckType2(Class<?> cl, List<String> skipTypes) {
		if(skipTypes == null) {
			return false;
		}
    	for(String type : skipTypes) {
    		try {
				if(Class.forName(type).isAssignableFrom(cl)) {
					return true;
				}
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
    	}
    	return false;
	}

	/**
	 * �i���ΏۃI�u�W�F�N�g�̃��j�[�N�������؂���
	 * @param o		���ؑΏۃI�u�W�F�N�g
     * @param when	�i���������i�ǉ�/�X�V�j
     * @return		���؎��s�����ꍇ�̌������b�Z�[�W�Q
	 */
    public Messages checkUnique(Object o, WhenVerifier when) {
    	Messages ret = new Messages();
    	if(isSkipUniqueCheckType(o.getClass(), (List<String>)DIContainerFactory.getDIContainer().getObject(SKIP_UNIQUE_CHECK_TYPES))) {
    		return ret;
    	}
    	List<Unique> uniqueList = findUnique(o.getClass());
    	if(uniqueList.size() == 0) {
    		return ret;
    	}
        for(Unique u : uniqueList) {
	    	Messages msgs = checkUnique(o, when, u);
	    	ret.add(msgs);
        }
    	return ret;
	}
    
	protected Messages checkUnique(Object o, WhenVerifier when, Unique u) {
		Messages ret = new Messages();
		for(Check check : u.groups()) {
			List list = getMutchingObjects(o, check);
			if(list.size() == 0) {
				continue;	//�T�C�Y�O�Ȃ̂Ŗ������ɂn�j
			}
			if(when == WhenVerifier.INSERT) {
				//�o�^���ɂP���ȏ゠��̂Ŗ������ɃG���[
				ret.add(makeUniqueErrorMessage(o, u, check));
			} else {
				//�X�V��
				if(list.size() > 1) {
					ret.add(makeUniqueErrorMessage(o, u, check));
				} else if(!compareUniqueObject(o, list.get(0))){
					ret.add(makeUniqueErrorMessage(o, u, check));
				}
			}
		}
		return ret;
	}

	protected boolean compareUniqueObject(Object target, Object org) {
		return target.equals(org);
	}
	
	protected static Messages makeUniqueErrorMessage(Object o, Unique u, Check c) {
		Messages ret = new Messages();
		final String key = "errors.duplicate";
		ResourceBundle rb = ResourceManager.getResource();
		String targetProperty = u.entity() + "." + c.properties()[0];
		String uniqueDescription = u.entity() + "._UNQ_." + c.name();
		if(rb.containsKey(uniqueDescription)) {
			ret.add(targetProperty, new Message(key, rb.getString(uniqueDescription)));
			return ret;
		}
		String[] strArray = c.properties()[0].split("\\.");
		if(strArray.length == 1) {
			ret.add(targetProperty, new Message(key, rb.getString(targetProperty)));
			return ret;
		} else {
			int i = 0;
			Object base = o;
			for( ; i < strArray.length - 1 ; i++) {
				base = jp.rough_diamond.commons.util.PropertyUtils.getProperty(base, strArray[0]);
			}
			ret.add(targetProperty, new Message(key,
					rb.getString(base.getClass().getSimpleName() + "." + strArray[i])));
			return ret;
		}
	}

	protected Extractor getMutchingExtractor(Object o, Check check) {
		Extractor ex = new Extractor(o.getClass());
		for(String property : check.properties()) {
			Object value = jp.rough_diamond.commons.util.PropertyUtils.getProperty(o, property);
			if(value == null) {
				ex.add(Condition.isNull(new Property(property)));
			} else {
				ex.add(Condition.eq(new Property(property), value));
			}
		}		
		return ex;
	}
	
	protected List getMutchingObjects(Object o, Check check) {
		Extractor ex = getMutchingExtractor(o, check);
		//XXX HibernateBasicService���g�p����ƃ��b�N������Ȃ��E�E�E�ł����Q�Ȃ��C�����Ă���B�B�B
		return findByExtractor(ex, RecordLock.FOR_UPDATE);
	}
	
	private int getLength(Object target) throws Exception {
    	if(target == null) {
    		return 0;
    	} else if (target instanceof String){
    		String charset = (String)DIContainerFactory.getDIContainer().getObject("databaseCharset");
    		byte[] array = ((String)target).getBytes(charset);
    		return array.length;
    	} else if(target instanceof Integer) {
    	    return getLength(target.toString());
        } else {
            throw new RuntimeException();
        }
    }
    
    public static boolean isProxy(Object target) {
    	return BasicService.getService().getProxyChecker().isProxy(target);
    }
    
    abstract protected ProxyChecker getProxyChecker();
    
    protected static interface ProxyChecker {
    	public boolean isProxy(Object target);
    }
}
