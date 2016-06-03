/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.testing;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUtils {
	/**
	 * 指定されたスキーマに指定されたテーブルが存在するか否かをチェックする
	 * tableNameは大文字/小文字を意識しない
	 * @param con
	 * @param schema
	 * @param tableName
	 * @return
	 * @throws SQLException 
	 */
	public static boolean isExistsTable(Connection con, String schema, String tableName) throws SQLException {
    	DatabaseMetaData dmd = con.getMetaData();
    	ResultSet rs = dmd.getTables(null, schema, "%", null);
    	try {
    		tableName = tableName.toUpperCase();
        	while(rs.next()) {
        		String tableNameTmp = rs.getString("TABLE_NAME");
        		if(tableName.equals(tableNameTmp.toUpperCase())) {
        			return true;
        		}
        	}
        	return false;
    	} finally{
    		rs.close();
    	}
	}
}
