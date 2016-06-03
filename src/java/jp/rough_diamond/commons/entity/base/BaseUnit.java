/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.entity.base;

import  java.io.Serializable;



/**
 * ���ʎړx��Hibernate�}�b�s���O�x�[�X�N���X
 * @hibernate.class
 *    table="UNIT"
 *    realClass="jp.rough_diamond.commons.entity.Unit"
 *  @hibernate.cache usage="read-only"
**/
public abstract class BaseUnit  implements Serializable {
   private static final long serialVersionUID = 1L;
    /**
     * �f�t�H���g�R���X�g���N�^
    **/
    public BaseUnit() {
    }
    /**
     * OID
    **/ 
    private Long id;
    public final static String ID = "id";
    /**
     * OID���擾����
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
     * OID��ݒ肷��
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
     * ���ʎړx��
    **/ 
    private String name;
    public final static String NAME = "name";
    /**
     * ���ʎړx�����擾����
     * @hibernate.property
     *    column="NAME"
     *    not-null="true"
     *    length="32"
     * @hibernate.column
     *    name="NAME"
     *    length="32"
     *    not-null="true"
     * @return ���ʎړx��
    **/
    @jp.rough_diamond.commons.service.annotation.MaxLength(length=32, property="Unit.name")
    @jp.rough_diamond.commons.service.annotation.MaxCharLength(length=16, property="Unit.name")
    @jp.rough_diamond.commons.service.annotation.NotNull(property="Unit.name")
    public String getName() {
        return name;
    }

    /**
     * ���ʎړx����ݒ肷��
     * @param name  ���ʎړx��
    **/
    public void setName(String name) {
        this.name = name;
    }
    /**
     * �P�ʐ���
    **/ 
    private String description;
    public final static String DESCRIPTION = "description";
    /**
     * �P�ʐ������擾����
     * @hibernate.property
     *    column="DESCRIPTION"
     *    not-null="false"
     *    length="64"
     * @hibernate.column
     *    name="DESCRIPTION"
     *    length="64"
     *    not-null="false"
     * @return �P�ʐ���
    **/
    @jp.rough_diamond.commons.service.annotation.MaxLength(length=64, property="Unit.description")
    public String getDescription() {
        return description;
    }

    /**
     * �P�ʐ�����ݒ肷��
     * @param description  �P�ʐ���
    **/
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * �ϊ��W��
    **/ 
    private jp.rough_diamond.commons.entity.ScalableNumber rate =  new jp.rough_diamond.commons.entity.ScalableNumber();

    public final static String RATE = "rate.";

    /**
     * �ϊ��W�����擾����
     * @hibernate.component
     *    prefix="RATE_"
     *    not-null="true"
     * @return �ϊ��W��
    **/
    @jp.rough_diamond.commons.service.annotation.NotNull(property="Unit.rate")
    @jp.rough_diamond.commons.service.annotation.NestedComponent(property="Unit.rate")
    public jp.rough_diamond.commons.entity.ScalableNumber getRate() {
        return rate;
    }

    /**
     * �ϊ��W����ݒ肷��
     * @param rate  �ϊ��W��
    **/
    public void setRate(jp.rough_diamond.commons.entity.ScalableNumber rate) {
        this.rate = rate;
    }

    /**
     * �ϊ����ɕێ����鏬�����x�B�������w�肷��Ɛ����̐؎̂Ĕ��f����
    **/ 
    private Integer scale;
    public final static String SCALE = "scale";
    /**
     * �ϊ����ɕێ����鏬�����x�B�������w�肷��Ɛ����̐؎̂Ĕ��f������擾����
     * @hibernate.property
     *    column="SCALE"
     *    not-null="true"
     * @hibernate.column
     *    name="SCALE"
     *    not-null="true"
     * @return �ϊ����ɕێ����鏬�����x�B�������w�肷��Ɛ����̐؎̂Ĕ��f����
    **/
    @jp.rough_diamond.commons.service.annotation.NotNull(property="Unit.scale")
    public Integer getScale() {
        return scale;
    }

    /**
     * �ϊ����ɕێ����鏬�����x�B�������w�肷��Ɛ����̐؎̂Ĕ��f�����ݒ肷��
     * @param scale  �ϊ����ɕێ����鏬�����x�B�������w�肷��Ɛ����̐؎̂Ĕ��f����
    **/
    public void setScale(Integer scale) {
        this.scale = scale;
    }
    /**
     * �y�ϓI���b�L���O�L�[
    **/ 
    private Long version;
    public final static String VERSION = "version";
    /**
     * �y�ϓI���b�L���O�L�[���擾����
     * @hibernate.version
     *    column="VERSION"
     * @return �y�ϓI���b�L���O�L�[
    **/
    public Long getVersion() {
        return version;
    }

    /**
     * �y�ϓI���b�L���O�L�[��ݒ肷��
     * @param version  �y�ϓI���b�L���O�L�[
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
