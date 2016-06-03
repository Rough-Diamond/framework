/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package org.dbunit.database;

import java.sql.Connection;
import java.sql.SQLException;

import org.dbunit.database.statement.IStatementFactory;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;

public class DatabaseConnectionExt implements IDatabaseConnection {
	final IDatabaseConnection base;
	private IDataSet dataSet;
	public DatabaseConnectionExt(IDatabaseConnection base) {
		this.base = base;
	}
	@Override
	public void close() throws SQLException {
		base.close();
	}
	@Override
	public IDataSet createDataSet() throws SQLException {
		if(dataSet == null) {
			dataSet = new DatabaseDataSet(base);
		}
		return dataSet;
	}
	@Override
	public IDataSet createDataSet(String[] tableNames) throws SQLException {
		return base.createDataSet(tableNames);
	}
	@Override
	public ITable createQueryTable(String resultName, String sql)
			throws DataSetException, SQLException {
		return base.createQueryTable(resultName, sql);
	}
	@Override
	public DatabaseConfig getConfig() {
		return base.getConfig();
	}
	@Override
	public Connection getConnection() throws SQLException {
		return base.getConnection();
	}
	@Override
	public int getRowCount(String tableName) throws SQLException {
		// TODO Auto-generated method stub
		return base.getRowCount(tableName);
	}
	@Override
	public int getRowCount(String tableName, String whereClause) throws SQLException {
		return base.getRowCount(tableName, whereClause);
	}
	@Override
	public String getSchema() {
		return base.getSchema();
	}
	@SuppressWarnings("deprecation")
	@Override
	public IStatementFactory getStatementFactory() {
		return base.getStatementFactory();
	}
	
	public void resetDataSet() {
		dataSet = null;
	}
}
