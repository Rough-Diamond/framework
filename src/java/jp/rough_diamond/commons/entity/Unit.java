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
 * ���ʎړx��Hibernate�}�b�s���O�N���X
**/
public class Unit extends jp.rough_diamond.commons.entity.base.BaseUnit {
    private static final long serialVersionUID = 1L;
    
    //base�����ȎQ�Ƃ̏ꍇrateStr�͋����I�ɂP���Z�b�g����
    //XXX �G���[�ɂ��Ă��ǂ��������ǃ��\�[�X����̂��ʓ|��������
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
    		//�Q�ƒl����v���Ă����BaseUnit
    		return true;
    	}
    	if(getId() == null || base.getId() == null) {
    		//�Q�ƒl����v���Ă��Ȃ��Ăǂ��炩��ID��null�Ȃ�s��Ȃ̂�false
    		return false;
    	}
    	return getId().longValue() == base.getId().longValue();
    }
}
