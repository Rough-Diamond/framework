/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.entity.base;

import  java.io.Serializable;



/**
 * 小数点位置指定数値のHibernateマッピングベースクラス
**/
public abstract class BaseScalableNumber extends java.lang.Number implements Serializable {
   private static final long serialVersionUID = 1L;
    /**
     * デフォルトコンストラクタ
    **/
    public BaseScalableNumber() {
    }
    /**
     * 量(整数)
    **/ 
    private Long value;
    public final static String VALUE = "value";
    /**
     * 量(整数)を取得する
     * @hibernate.property
     *    column="VALUE"
     *    not-null="true"
     * @hibernate.column
     *    name="VALUE"
     *    not-null="true"
     * @return 量(整数)
    **/
    @jp.rough_diamond.commons.service.annotation.NotNull(property="ScalableNumber.value")
    public Long getValue() {
        return value;
    }

    /**
     * 量(整数)を設定する
     * @param value  量(整数)
    **/
    public void setValue(Long value) {
        this.value = value;
    }
    /**
     * 小数点位置。正の数なら左へ、負の数なら右へ移動させる
    **/ 
    private Integer scale;
    public final static String SCALE = "scale";
    /**
     * 小数点位置。正の数なら左へ、負の数なら右へ移動させるを取得する
     * @hibernate.property
     *    column="SCALE"
     *    not-null="true"
     * @hibernate.column
     *    name="SCALE"
     *    not-null="true"
     * @return 小数点位置。正の数なら左へ、負の数なら右へ移動させる
    **/
    @jp.rough_diamond.commons.service.annotation.NotNull(property="ScalableNumber.scale")
    public Integer getScale() {
        return scale;
    }

    /**
     * 小数点位置。正の数なら左へ、負の数なら右へ移動させるを設定する
     * @param scale  小数点位置。正の数なら左へ、負の数なら右へ移動させる
    **/
    public void setScale(Integer scale) {
        this.scale = scale;
    }
//ForeignProperties.vm start.

    
//ForeignProperties.vm finish.
}
