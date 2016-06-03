/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.entity.base;

import  java.io.Serializable;



/**
 * �i���o�����O�e�[�u����Hibernate�}�b�s���O�x�[�X�N���X
 * @hibernate.class
 *    table="NUMBERING"
 *    realClass="jp.rough_diamond.commons.entity.Numbering"
**/
public abstract class BaseNumbering  implements Serializable {
   private static final long serialVersionUID = 1L;
    /**
     * �f�t�H���g�R���X�g���N�^
    **/
    public BaseNumbering() {
    }
    /**
     * �h�c
    **/ 
    private String id;
    public final static String ID = "id";
    /**
     * �h�c���擾����
     * @hibernate.id
     *    generator-class="assigned"
     *    column="ID"
     *    not-null="true"
     *    length="128"
     * @return �h�c
    **/
    public String getId() {
        return id;
    }

    /**
     * �h�c��ݒ肷��
     * @param id  �h�c
    **/
    public void setId(String id) {
        this.id = id;
        isLoaded = false;
    }

    public int hashCode() {
        if(getId() == null) {
            return super.hashCode();
        } else {
            return getId().hashCode();
        }
    }
    
    public boolean equals(Object o) {
        if(o instanceof BaseNumbering) {
            if(hashCode() == o.hashCode()) {
                BaseNumbering obj = (BaseNumbering)o;
                if(getId() == null) {
                    return super.equals(o);
                }
                return getId().equals(obj.getId());
            }
        }
        return false;
    }
    protected boolean isLoaded;
    @jp.rough_diamond.commons.service.annotation.PostLoad
    @jp.rough_diamond.commons.service.annotation.PostPersist
    public void setLoadingFlag() {
        isLoaded = true;
    }

    /**
     * �I�u�W�F�N�g���i��������
     * �i�������[���͈ȉ��̒ʂ�ł��B
     * <ul>
     *   <li>new��������̃I�u�W�F�N�g�̏ꍇ��insert</li>
     *   <li>load���ꂽ�I�u�W�F�N�g�̏ꍇ��update</li>
     *   <li>load���ꂽ�I�u�W�F�N�g�ł���L�[�������ւ����ꍇ��insert</li>
     *   <li>insert�����I�u�W�F�N�g���ēxsave�����ꍇ��update</li>
     *   <li>setLoadingFlag���\�b�h���Ăяo�����ꍇ�͋����I��update�i�񐄏��j</li>
     * </ul>
     * @throws VersionUnmuchException   �y�ϓI���b�L���O�G���[
     * @throws MessagesIncludingException ���ؗ�O
    **/
    public void save() throws jp.rough_diamond.framework.transaction.VersionUnmuchException, jp.rough_diamond.commons.resource.MessagesIncludingException {
        if(isThisObjectAnUpdateObject()) {
            update();
        } else {
            insert();
        }
    }

    /**
     * ���̃I�u�W�F�N�g���i����������@��ԋp����B
     * �i�����������s���A�{���\�b�h��true��ԋp���ꂽ�ꍇ�͍X�V(UPDATE)�Afalse�̏ꍇ�͓o�^(INSERT)���ĐU�镑��
     * @return true�̏ꍇ�͍X�V�Afalse�̏ꍇ�͓o�^�Ƃ��ĐU�镑��
    **/
    protected boolean isThisObjectAnUpdateObject() {
        return isLoaded;
    }

    /**
     * �I�u�W�F�N�g���i��������
     * @throws MessagesIncludingException ���ؗ�O
    **/
    protected void insert() throws jp.rough_diamond.commons.resource.MessagesIncludingException {
        jp.rough_diamond.commons.service.BasicService.getService().insert(this);
    }

    /**
     * �i�����I�u�W�F�N�g���X�V����
     * @throws MessagesIncludingException ���ؗ�O
     * @throws VersionUnmuchException   �y�ϓI���b�L���O�G���[
    **/
    protected void update() throws jp.rough_diamond.framework.transaction.VersionUnmuchException, jp.rough_diamond.commons.resource.MessagesIncludingException {
        jp.rough_diamond.commons.service.BasicService.getService().update(this);
    }

    /**
     * �I�u�W�F�N�g�̉i���\�������؂���
     * @return ���،��ʁBmsgs.hasError()==false����������ꍇ�͌��ؐ����Ƃ݂Ȃ�
    */
    public jp.rough_diamond.commons.resource.Messages validateObject() {
        if(isThisObjectAnUpdateObject()) {
            return validateObject(jp.rough_diamond.commons.service.WhenVerifier.UPDATE);
        } else {
            return validateObject(jp.rough_diamond.commons.service.WhenVerifier.INSERT);
        }
    }

    /**
     * �I�u�W�F�N�g�̉i���\�������؂���
     * @return ���،��ʁBmsgs.hasError()==false����������ꍇ�͌��ؐ����Ƃ݂Ȃ�
    */
    protected jp.rough_diamond.commons.resource.Messages validateObject(jp.rough_diamond.commons.service.WhenVerifier when) {
        return jp.rough_diamond.commons.service.BasicService.getService().validate(this, when);
    }

    /**
     * ���݊��蓖�ĂĂ���ԍ�
    **/ 
    private Long nextNumber;
    public final static String NEXT_NUMBER = "nextNumber";
    /**
     * ���݊��蓖�ĂĂ���ԍ����擾����
     * @hibernate.property
     *    column="NEXT_NUMBER"
     *    not-null="true"
     * @hibernate.column
     *    name="NEXT_NUMBER"
     *    not-null="true"
     * @return ���݊��蓖�ĂĂ���ԍ�
    **/
    @jp.rough_diamond.commons.service.annotation.NotNull(property="Numbering.nextNumber")
    public Long getNextNumber() {
        return nextNumber;
    }

    /**
     * ���݊��蓖�ĂĂ���ԍ���ݒ肷��
     * @param nextNumber  ���݊��蓖�ĂĂ���ԍ�
    **/
    public void setNextNumber(Long nextNumber) {
        this.nextNumber = nextNumber;
    }
//ForeignProperties.vm start.

    
//ForeignProperties.vm finish.
}
