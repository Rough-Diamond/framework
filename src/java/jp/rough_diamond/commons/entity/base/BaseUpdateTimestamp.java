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
 * 更新日時情報のHibernateマッピングベースクラス
**/
public abstract class BaseUpdateTimestamp  implements Serializable {
   private static final long serialVersionUID = 1L;
    /**
     * デフォルトコンストラクタ
    **/
    public BaseUpdateTimestamp() {
    }
    /**
     * 登録日時
    **/ 
    private Date registererDate;
    public final static String REGISTERER_DATE = "registererDate";
    /**
     * 登録日時を取得する
     * @hibernate.property
     *    column="REGISTERER_DATE"
     *    not-null="true"
     * @hibernate.column
     *    name="REGISTERER_DATE"
     *    not-null="true"
     * @return 登録日時
    **/
    @jp.rough_diamond.commons.service.annotation.NotNull(property="UpdateTimestamp.registererDate")
    public Date getRegistererDate() {
        return registererDate;
    }

    /**
     * 登録日時を設定する
     * @param registererDate  登録日時
    **/
    public void setRegistererDate(Date registererDate) {
        this.registererDate = registererDate;
    }
    /**
     * 最終更新日
    **/ 
    private Date lastModifiedDate;
    public final static String LAST_MODIFIED_DATE = "lastModifiedDate";
    /**
     * 最終更新日を取得する
     * @hibernate.property
     *    column="LAST_MODIFIED_DATE"
     *    not-null="true"
     * @hibernate.column
     *    name="LAST_MODIFIED_DATE"
     *    not-null="true"
     * @return 最終更新日
    **/
    @jp.rough_diamond.commons.service.annotation.NotNull(property="UpdateTimestamp.lastModifiedDate")
    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    /**
     * 最終更新日を設定する
     * @param lastModifiedDate  最終更新日
    **/
    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
//ForeignProperties.vm start.

    
//ForeignProperties.vm finish.
}
