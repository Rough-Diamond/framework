/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.entity.base;

import  java.io.Serializable;
import  java.util.Date;


/**
 * �X�V��������Hibernate�}�b�s���O�x�[�X�N���X
**/
public abstract class BaseUpdateTimestamp  implements Serializable {
   private static final long serialVersionUID = 1L;
    /**
     * �f�t�H���g�R���X�g���N�^
    **/
    public BaseUpdateTimestamp() {
    }
    /**
     * �o�^����
    **/ 
    private Date registererDate;
    public final static String REGISTERER_DATE = "registererDate";
    /**
     * �o�^�������擾����
     * @hibernate.property
     *    column="REGISTERER_DATE"
     *    not-null="true"
     * @hibernate.column
     *    name="REGISTERER_DATE"
     *    not-null="true"
     * @return �o�^����
    **/
    @jp.rough_diamond.commons.service.annotation.NotNull(property="UpdateTimestamp.registererDate")
    public Date getRegistererDate() {
        return registererDate;
    }

    /**
     * �o�^������ݒ肷��
     * @param registererDate  �o�^����
    **/
    public void setRegistererDate(Date registererDate) {
        this.registererDate = registererDate;
    }
    /**
     * �ŏI�X�V��
    **/ 
    private Date lastModifiedDate;
    public final static String LAST_MODIFIED_DATE = "lastModifiedDate";
    /**
     * �ŏI�X�V�����擾����
     * @hibernate.property
     *    column="LAST_MODIFIED_DATE"
     *    not-null="true"
     * @hibernate.column
     *    name="LAST_MODIFIED_DATE"
     *    not-null="true"
     * @return �ŏI�X�V��
    **/
    @jp.rough_diamond.commons.service.annotation.NotNull(property="UpdateTimestamp.lastModifiedDate")
    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    /**
     * �ŏI�X�V����ݒ肷��
     * @param lastModifiedDate  �ŏI�X�V��
    **/
    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
//ForeignProperties.vm start.

    
//ForeignProperties.vm finish.
}
