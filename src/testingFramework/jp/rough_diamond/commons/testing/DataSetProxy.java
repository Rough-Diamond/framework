/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.testing;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.ITableMetaData;

/**
 *
 */
public class DataSetProxy implements IDataSet {
	final IDataSet base;
	final String resourceName;
	DataSetProxy(String resourceName, IDataSet base) {
		this.base = base;
		this.resourceName = resourceName;
	}
	@Override
	public ITable getTable(String tableName) throws DataSetException {
		return base.getTable(tableName);
	}

	@Override
	public ITableMetaData getTableMetaData(String tableName) throws DataSetException {
		return base.getTableMetaData(tableName);
	}

	@Override
	public String[] getTableNames() throws DataSetException {
		return base.getTableNames();
	}

	@SuppressWarnings("deprecation")
	@Override
	public ITable[] getTables() throws DataSetException {
		return base.getTables();
	}

	@Override
	public ITableIterator iterator() throws DataSetException {
		return base.iterator();
	}

	@Override
	public ITableIterator reverseIterator() throws DataSetException {
		return base.reverseIterator();
	}
}
