/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.service.hibernate;

import org.hibernate.Hibernate;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;

/**
 *
 */
public class H2DialectExt extends H2Dialect {
	public H2DialectExt() {
		super();
		registerFunction("formatdatetime", new StandardSQLFunction("formatdatetime", Hibernate.STRING));
	}
}
