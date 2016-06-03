/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.entity.base;

import  java.io.Serializable;



/**
 * 数量尺度のHibernateマッピングベースクラス
 * @hibernate.class
 *    table="UNIT"
 *    realClass="jp.rough_diamond.commons.entity.Unit"
 *  @hibernate.cache usage="read-only"
**/
public abstract class BaseUnit  implements Serializable {
   private static final long serialVersionUID = 1L;
    /**
     * デフォルトコンストラクタ
    **/
    public BaseUnit() {
    }
    /**
     * OID
    **/ 
    private Long id;
    public final static String ID = "id";
    /**
     * OIDを取得する
     * @hibernate.id
     *    generator-class="assigned"
     *    column="ID"
     *    not-null="true"
     * @return OID
    **/
    public Long getId() {
        return id;
    }

    /**
     * OIDを設定する
     * @param id  OID
    **/
    public void setId(Long id) {
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
        if(o instanceof BaseUnit) {
            if(hashCode() == o.hashCode()) {
                BaseUnit obj = (BaseUnit)o;
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
     * オブジェクトを永続化する
     * 永続化ルールは以下の通りです。
     * <ul>
     *   <li>newした直後のオブジェクトの場合はinsert</li>
     *   <li>loadされたオブジェクトの場合はupdate</li>
     *   <li>loadされたオブジェクトでも主キーを差し替えた場合はinsert</li>
     *   <li>insertしたオブジェクトを再度saveした場合はupdate</li>
     *   <li>setLoadingFlagメソッドを呼び出した場合は強制的にupdate（非推奨）</li>
     * </ul>
     * @throws VersionUnmuchException   楽観的ロッキングエラー
     * @throws MessagesIncludingException 検証例外
    **/
    public void save() throws jp.rough_diamond.framework.transaction.VersionUnmuchException, jp.rough_diamond.commons.resource.MessagesIncludingException {
        if(isThisObjectAnUpdateObject()) {
            update();
        } else {
            insert();
        }
    }

    /**
     * このオブジェクトを永続化する方法を返却する。
     * 永続化処理実行時、本メソッドがtrueを返却された場合は更新(UPDATE)、falseの場合は登録(INSERT)して振る舞う
     * @return trueの場合は更新、falseの場合は登録として振る舞う
    **/
    protected boolean isThisObjectAnUpdateObject() {
        return isLoaded;
    }

    /**
     * オブジェクトを永続化する
     * @throws MessagesIncludingException 検証例外
    **/
    protected void insert() throws jp.rough_diamond.commons.resource.MessagesIncludingException {
        jp.rough_diamond.commons.service.BasicService.getService().insert(this);
    }

    /**
     * 永続化オブジェクトを更新する
     * @throws MessagesIncludingException 検証例外
     * @throws VersionUnmuchException   楽観的ロッキングエラー
    **/
    protected void update() throws jp.rough_diamond.framework.transaction.VersionUnmuchException, jp.rough_diamond.commons.resource.MessagesIncludingException {
        jp.rough_diamond.commons.service.BasicService.getService().update(this);
    }

    /**
     * オブジェクトの永続可能性を検証する
     * @return 検証結果。msgs.hasError()==falseが成立する場合は検証成功とみなす
    */
    public jp.rough_diamond.commons.resource.Messages validateObject() {
        if(isThisObjectAnUpdateObject()) {
            return validateObject(jp.rough_diamond.commons.service.WhenVerifier.UPDATE);
        } else {
            return validateObject(jp.rough_diamond.commons.service.WhenVerifier.INSERT);
        }
    }

    /**
     * オブジェクトの永続可能性を検証する
     * @return 検証結果。msgs.hasError()==falseが成立する場合は検証成功とみなす
    */
    protected jp.rough_diamond.commons.resource.Messages validateObject(jp.rough_diamond.commons.service.WhenVerifier when) {
        return jp.rough_diamond.commons.service.BasicService.getService().validate(this, when);
    }

    /**
     * 数量尺度名
    **/ 
    private String name;
    public final static String NAME = "name";
    /**
     * 数量尺度名を取得する
     * @hibernate.property
     *    column="NAME"
     *    not-null="true"
     *    length="32"
     * @hibernate.column
     *    name="NAME"
     *    length="32"
     *    not-null="true"
     * @return 数量尺度名
    **/
    @jp.rough_diamond.commons.service.annotation.MaxLength(length=32, property="Unit.name")
    @jp.rough_diamond.commons.service.annotation.MaxCharLength(length=16, property="Unit.name")
    @jp.rough_diamond.commons.service.annotation.NotNull(property="Unit.name")
    public String getName() {
        return name;
    }

    /**
     * 数量尺度名を設定する
     * @param name  数量尺度名
    **/
    public void setName(String name) {
        this.name = name;
    }
    /**
     * 単位説明
    **/ 
    private String description;
    public final static String DESCRIPTION = "description";
    /**
     * 単位説明を取得する
     * @hibernate.property
     *    column="DESCRIPTION"
     *    not-null="false"
     *    length="64"
     * @hibernate.column
     *    name="DESCRIPTION"
     *    length="64"
     *    not-null="false"
     * @return 単位説明
    **/
    @jp.rough_diamond.commons.service.annotation.MaxLength(length=64, property="Unit.description")
    public String getDescription() {
        return description;
    }

    /**
     * 単位説明を設定する
     * @param description  単位説明
    **/
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * 変換係数
    **/ 
    private jp.rough_diamond.commons.entity.ScalableNumber rate =  new jp.rough_diamond.commons.entity.ScalableNumber();

    public final static String RATE = "rate.";

    /**
     * 変換係数を取得する
     * @hibernate.component
     *    prefix="RATE_"
     *    not-null="true"
     * @return 変換係数
    **/
    @jp.rough_diamond.commons.service.annotation.NotNull(property="Unit.rate")
    @jp.rough_diamond.commons.service.annotation.NestedComponent(property="Unit.rate")
    public jp.rough_diamond.commons.entity.ScalableNumber getRate() {
        return rate;
    }

    /**
     * 変換係数を設定する
     * @param rate  変換係数
    **/
    public void setRate(jp.rough_diamond.commons.entity.ScalableNumber rate) {
        this.rate = rate;
    }

    /**
     * 変換時に保持する小数精度。負数を指定すると整数の切捨て判断する
    **/ 
    private Integer scale;
    public final static String SCALE = "scale";
    /**
     * 変換時に保持する小数精度。負数を指定すると整数の切捨て判断するを取得する
     * @hibernate.property
     *    column="SCALE"
     *    not-null="true"
     * @hibernate.column
     *    name="SCALE"
     *    not-null="true"
     * @return 変換時に保持する小数精度。負数を指定すると整数の切捨て判断する
    **/
    @jp.rough_diamond.commons.service.annotation.NotNull(property="Unit.scale")
    public Integer getScale() {
        return scale;
    }

    /**
     * 変換時に保持する小数精度。負数を指定すると整数の切捨て判断するを設定する
     * @param scale  変換時に保持する小数精度。負数を指定すると整数の切捨て判断する
    **/
    public void setScale(Integer scale) {
        this.scale = scale;
    }
    /**
     * 楽観的ロッキングキー
    **/ 
    private Long version;
    public final static String VERSION = "version";
    /**
     * 楽観的ロッキングキーを取得する
     * @hibernate.version
     *    column="VERSION"
     * @return 楽観的ロッキングキー
    **/
    public Long getVersion() {
        return version;
    }

    /**
     * 楽観的ロッキングキーを設定する
     * @param version  楽観的ロッキングキー
    **/
    public void setVersion(Long version) {
        this.version = version;
    }
//ForeignProperties.vm start.

    
    private jp.rough_diamond.commons.entity.Unit base;
    public final static String BASE = "base";

    /**
     * Get the associated Unit object
     * @hibernate.many-to-one
     *   outer-join = "true"
     * @hibernate.column name = "BASE_UNIT_ID"
     *
     * @return the associated Unit object
     */
    public jp.rough_diamond.commons.entity.Unit getBase() {
        if(jp.rough_diamond.commons.service.BasicService.isProxy(this.base)) {
            this.base = jp.rough_diamond.commons.service.BasicService.getService().replaceProxy(this.base);
        }
        return this.base;
    }

    /**
     * Declares an association between this object and a Unit object
     *
     * @param v Unit
     */
    public void setBase(jp.rough_diamond.commons.entity.Unit v) {
        this.base = v;
    }

//ForeignProperties.vm finish.
}
