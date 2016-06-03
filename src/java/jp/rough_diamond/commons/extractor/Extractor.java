/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.extractor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * ���o�����i�[�I�u�W�F�N�g
 */
@SuppressWarnings("unchecked")
public class Extractor implements Serializable, Value {
	private static final long serialVersionUID = 1L;

    public final static int		DEFAULT_FETCH_SIZE = -1;

    /**
	 * ���o�ΏۃG���e�B�e�B�N���X
	 */
	public final Class target;

	/**
	 * �߂�I�u�W�F�N�g�̃^�C�v
	 */
	public Class<?> returnType;
	
	/**
     * ���o�ΏۃG�C���A�X
     */
    public final String targetAlias;
    
	private List<Order<? extends Value>> 	 			orders = new ArrayList<Order<? extends Value>>();
	private List<Condition<? extends Value>>			condition = new ArrayList<Condition<? extends Value>>();
    private List<InnerJoin>     						innerJoins = new ArrayList<InnerJoin>();
    private List<ExtractValue>  						values = new ArrayList<ExtractValue>();
    private List<Condition<? extends Value>>			having = new ArrayList<Condition<? extends Value>>();
    
	private int 	offset = 0;
	private int		limit = -1;
	private int		fetchSize = DEFAULT_FETCH_SIZE;
	private int		fetchDepth = -1;

	private boolean	distinct = false;
	
	private boolean isCachable = false;
	
	/**
	 * ���o�����i�[�I�u�W�F�N�g�𐶐�����
	 * @param target ���o�ΏۃG���e�B�e�B�N���X null�̏ꍇ��NullPointerException�𑗏o����
	 */
	public Extractor(Class target) {
        this(target, null);
	}

    /**
     * ���o�����i�[�I�u�W�F�N�g�𐶐�����
     * @param target
     * @param alias
     */
    public Extractor(Class target, String alias) {
        target.getClass();  //NOP NullPointerException�𑗏o������������
        this.target = target;
        this.targetAlias = alias;
    }
    
	/**
	 * ���o������ǉ�����
	 * @param con	���o���� null�̏ꍇ��NullPointerException�𑗏o����
	 */
	public Extractor add(Condition con) {
		con.getClass();
		condition.add(con);
		return this;
	}
	
	/**
	 * �\�[�g������ǉ�����
	 * @param order �\�[�g���� null�̏ꍇ��NullPointerException�𑗏o����
	 */
	public Extractor addOrder(Order<? extends Value> order) {
		order.getClass();
		orders.add(order);
		return this;
	}
	
	/**
	 * �\�[�g������Iterator��ԋp����
	 * @return ���o������Iterator
	 */
	public List<Order<? extends Value>> getOrderIterator() {
		return orders;
	}

	/**
	 * �I�[�_�[������Iterator��ԋp����
	 * @return �I�[�_�[������Iterator
	 */
	public List<Condition<? extends Value>> getConditionIterator() {
		return condition;
	}

	/**
	 * ���o��������擾����
	 * @return ���o�����
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * ���o�������ݒ肷��
	 * @param limit	���o����� �O�ȉ��̏ꍇ�͒��o��������Ƃ���
	 */
	public void setLimit(int limit) {
		this.limit = limit;
	}

	/**
	 * ���o�J�n�ʒu���擾����
	 * @return ���o�J�n�ʒu
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * ���o�J�n�ʒu��ݒ肷��
	 * @param offset	���o�J�n�ʒu �O�ȉ��̏ꍇ�͐擪����擾����
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * �t�F�b�`�T�C�Y���擾����
	 * @return	�t�F�b�`�T�C�Y
	 */
	public int getFetchSize() {
		return fetchSize;
	}

	/**
	 * �t�F�b�`�T�C�Y��ݒ肷��
	 * @param fetchSize
	 */
	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	/**
	 * ����Extractor�Ő��������N�G���[�̕ԋp�l�̃I�u�W�F�N�g�̎擾����[����ԋp����
	 * @return the fetchDepth
	 */
	public int getFetchDepth() {
		return fetchDepth;
	}

	/**
	 * ����Extractor�Ő��������N�G���[�̕ԋp�l�̃I�u�W�F�N�g�̎擾����[�����w�肷��
	 * �����̏ꍇ�͎����N���X�̃f�t�H���g�̒l���g�p����
	 * @param fetchDepth �Q�ƃI�u�W�F�N�g�̎擾����[��
	 */
	public void setFetchDepth(int fetchDepth) {
		this.fetchDepth = fetchDepth;
	}

	/**
     * ���������I�u�W�F�N�g��ǉ�����
     * @param join
     */
    public void addInnerJoin(InnerJoin join) {
        innerJoins.add(join);
    }
    
    /**
     * ���������I�u�W�F�N�g�Q��ԋp����
     */
    public List<InnerJoin> getInnerJoins() {
        return innerJoins;
    }
    
    /**
     * ���o�l��ǉ�����
     * @param value
     */
    public Extractor addExtractValue(ExtractValue value) {
        values.add(value);
        return this;
    }
    
    /**
     * ���o�l�Q��ԋp����
     */
    public List<ExtractValue> getValues() {
        return values;
    }

	/**
	 * ���o����(having)��ǉ�����
	 * @param con	���o���� null�̏ꍇ��NullPointerException�𑗏o����
	 */
	public Extractor addHaving(Condition<? extends Value> con) {
		con.getClass();
		having.add(con);
		return this;
	}
	
	/**
	 * �I�[�_�[������Iterator��ԋp����
	 * @return �I�[�_�[������Iterator
	 */
	public List<Condition<? extends Value>> getHavingIterator() {
		return having;
	}

	/**
	 * �߂�^�C�v��ݒ肷��
	 * @param returnType
	 */
	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}

	/**
	 * true�̏ꍇ�d���f�[�^�̎擾�͍s��Ȃ�
	 * �������A�ȉ��̏ꍇ�͂��̌���łȂ�
	 *  - ExtractValue���w�肵�Ă��炸LockMode��NONE�̏ꍇ��false�ł�true�ƈ���
	 *  �@�@�@�i�����I�ɏd���͂Ȃ��Ǝv���邪�E�E�E�����R�[�h�ɂ��邽�߁j
	 * @return
	 */
	public boolean isDistinct() {
		return distinct;
	}

	/**
	 * �d���f�[�^�̎擾�ۂ�ݒ肷��
	 */
	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	/**
	 * ����Extractor�Ő��������N�G���[�̕ԋp�l���L���b�V�����邩�ۂ���ԋp����
	 * @return
	 */
	public boolean isCachable() {
		return isCachable;
	}

	/**
	 * ����Extractor�Ő��������N�G���[�̕ԋp�l�̃L���b�V���ۂ��w�肷��
	 * @return
	 */
	public void setCachable(boolean isCachable) {
		this.isCachable = isCachable;
	}
}
