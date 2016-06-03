/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.entity.base;

import  java.io.Serializable;



/**
 * �ʂ�Hibernate�}�b�s���O�x�[�X�N���X
**/
public abstract class BaseQuantity extends java.lang.Number implements Serializable {
   private static final long serialVersionUID = 1L;
    /**
     * �f�t�H���g�R���X�g���N�^
    **/
    public BaseQuantity() {
    }
    /**
     * ��
    **/ 
    private jp.rough_diamond.commons.entity.ScalableNumber amount =  new jp.rough_diamond.commons.entity.ScalableNumber();

    public final static String A = "amount.";

    /**
     * �ʂ��擾����
     * @hibernate.component
     *    prefix="A_"
     *    not-null="true"
     * @return ��
    **/
    @jp.rough_diamond.commons.service.annotation.NotNull(property="Quantity.amount")
    @jp.rough_diamond.commons.service.annotation.NestedComponent(property="Quantity.amount")
    public jp.rough_diamond.commons.entity.ScalableNumber getAmount() {
        return amount;
    }

    /**
     * �ʂ�ݒ肷��
     * @param amount  ��
    **/
    public void setAmount(jp.rough_diamond.commons.entity.ScalableNumber amount) {
        this.amount = amount;
    }

//ForeignProperties.vm start.

    
    private jp.rough_diamond.commons.entity.Unit unit;
    public final static String UNIT = "unit";

    /**
     * Get the associated Unit object
     * @hibernate.many-to-one
     *   outer-join = "true"
     * @hibernate.column name = "UNIT_ID"
     *
     * @return the associated Unit object
     */
    public jp.rough_diamond.commons.entity.Unit getUnit() {
        if(jp.rough_diamond.commons.service.BasicService.isProxy(this.unit)) {
            this.unit = jp.rough_diamond.commons.service.BasicService.getService().replaceProxy(this.unit);
        }
        return this.unit;
    }

    /**
     * Declares an association between this object and a Unit object
     *
     * @param v Unit
     */
    public void setUnit(jp.rough_diamond.commons.entity.Unit v) {
        this.unit = v;
    }

//ForeignProperties.vm finish.
}
