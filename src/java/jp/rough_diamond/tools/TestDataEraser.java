/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.tools;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import jp.rough_diamond.commons.testing.DBInitializer;
import jp.rough_diamond.framework.service.Service;
import jp.rough_diamond.framework.service.ServiceLocator;
import jp.rough_diamond.framework.transaction.ConnectionManager;

/**
 *
 */
public class TestDataEraser implements Service {
	public static void main(String[] args) throws Exception {
		ServiceLocator.getService(TestDataEraser.class).doIt();
	}

	public void doIt() throws Exception {
		Connection con = ConnectionManager.getConnectionManager().getCurrentConnection(null);
		Statement stmt = con.createStatement();
		try {
			ResultSet rs = stmt.executeQuery(String.format("select test_table from %s", DBInitializer.TEST_DATA_CONTROLER));
			while(rs.next()) {
				dropTable(con, rs.getString(1));
			}
			dropTable(con, DBInitializer.TEST_DATA_CONTROLER);
		} finally {
			stmt.close();
		}
	}
	
	void dropTable(Connection con, String name) throws Exception {
		System.out.println(name + "ÇçÌèúÇµÇ‹Ç∑ÅB");
		Statement stmt = con.createStatement();
		try {
			stmt.execute("drop table " + name);
		} finally {
			stmt.close();
		}
	}
}
