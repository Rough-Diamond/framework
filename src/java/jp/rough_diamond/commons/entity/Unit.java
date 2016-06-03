/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.entity;

import jp.rough_diamond.commons.service.annotation.PrePersist;
import jp.rough_diamond.commons.service.annotation.PreUpdate;

/**
 * 数量尺度のHibernateマッピングクラス
**/
public class Unit extends jp.rough_diamond.commons.entity.base.BaseUnit {
    private static final long serialVersionUID = 1L;
    
    //baseが自己参照の場合rateStrは強制的に１をセットする
    //XXX エラーにしても良かったけどリソースきるのが面倒だったｗ
    @PrePersist
    @PreUpdate
    public void riviseBaseRate() {
    	if(isBaseUnit()) {
    		setRate(new ScalableNumber(1L, 0));
    	}
    }
    
    public boolean isBaseUnit() {
    	Unit base = getBase();
    	if(base == null) {
    		return false;
    	}
    	if(this == base) {
    		//参照値が一致していればBaseUnit
    		return true;
    	}
    	if(getId() == null || base.getId() == null) {
    		//参照値が一致していなくてどちらかのIDがnullなら不定なのでfalse
    		return false;
    	}
    	return getId().longValue() == base.getId().longValue();
    }
}
