/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.framework.transaction;

import java.sql.SQLException;

public class VersionUnmuchException extends SQLException {
	public final static long serialVersionUID = -1L;
	
	public VersionUnmuchException(String reason, String SQLState, int vendorCode) {
		super(reason, SQLState, vendorCode);
	}

	public VersionUnmuchException(String reason, String SQLState) {
		super(reason, SQLState);
	}

	public VersionUnmuchException(String reason) {
		super(reason);
	}

	public VersionUnmuchException() {
		super();
	}

}
