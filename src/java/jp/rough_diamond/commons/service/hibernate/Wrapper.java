/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.service.hibernate;

import jp.rough_diamond.commons.extractor.Value;

/**
 * エンティティをキャストする
 */
class Wrapper implements Value {
	public final Class<?> returnType;
	Wrapper(Class<?> returnType) {
		this.returnType = returnType;
	}
}
