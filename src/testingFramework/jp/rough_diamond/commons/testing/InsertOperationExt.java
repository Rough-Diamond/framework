package jp.rough_diamond.commons.testing;

import java.io.File;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import jp.rough_diamond.framework.transaction.hibernate.HibernateUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnectionExt;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.operation.AbstractOperation;
import org.hibernate.cfg.Environment;

public class InsertOperationExt extends AbstractOperation {
	private final static Log log = LogFactory.getLog(InsertOperationExt.class);
	
	private final DBInitializer initializer;
	InsertOperationExt(DBInitializer initializer) {
		this.initializer = initializer;
	}
	
    public void execute(IDatabaseConnection connection,
            IDataSet dataSet) throws DatabaseUnitException, SQLException {
    	ITableIterator iterator = dataSet.iterator();
    	while(iterator.next()) {
    		execute(connection, dataSet, iterator.getTable());
    	}
    }

    private static Map<String, String> alreadyCheckResource = new HashMap<String, String>();
	void execute(IDatabaseConnection connection, IDataSet dataSet, ITable table) throws SQLException, DatabaseUnitException {
    	DataSetProxy dsp = (DataSetProxy)dataSet;
		String tableName = table.getTableMetaData().getTableName();
		String resourceName = dsp.resourceName + "#" + tableName;
		log.debug(resourceName);
		boolean dataReset;
		String testDataTableName = alreadyCheckResource.get(resourceName);
		if(testDataTableName != null) {
			dataReset = false;
		} else {
			testDataTableName = "TT_" + resourceName.hashCode();
			//hashCodeが負数だと「-」がテーブル名に使われてしまうのでM(マイナス）に置換する
			testDataTableName = testDataTableName.replaceAll("-", "M");
			dataReset = checkTestData(connection, dsp, resourceName, testDataTableName);
			alreadyCheckResource.put(resourceName, testDataTableName);
		}
		if(dataReset){
			DatabaseConnectionExt con = new DatabaseConnectionExt(connection);
			resetData(con, dsp, table, testDataTableName);
		} else {
			copyData(connection, table, tableName, testDataTableName);
		}
		initializer.addInitializedObject(tableName);
	}

	private static Map<String, Column[]> columnMap = new HashMap<String, Column[]>();
	void copyData(IDatabaseConnection connection, ITable table, String baseTableName, String testDataTableName) throws DataSetException, SQLException {
		StringBuilder colSB = new StringBuilder();
		Column[] cols = columnMap.get(testDataTableName);
		if(cols == null) {
			cols = connection.createDataSet().getTableMetaData(testDataTableName).getColumns();
			columnMap.put(testDataTableName, cols);
		}
		String delimitter = "";
		for(Column col : cols) {
            String columnName = getQualifiedName(null,
                    col.getColumnName(), connection);
			colSB.append(delimitter);
			colSB.append(columnName);
			delimitter = ",";
		}
		//データがあれば抹消する
		String sql1 = String.format("delete from %s", baseTableName);
		PreparedStatement pstmt1 = connection.getConnection().prepareStatement(sql1);
    	try {
    		pstmt1.execute();
    	} finally {
    		pstmt1.close();
    	}
		String sql2 = String.format("insert into %s(%s) select %s from %s", 
				baseTableName, colSB.toString(), colSB.toString(), testDataTableName);
		PreparedStatement pstmt2 = connection.getConnection().prepareStatement(sql2);
    	try {
    		pstmt2.execute();
    	} finally {
    		pstmt2.close();
    	}
	}

	void resetData(DatabaseConnectionExt connection, DataSetProxy dsp, ITable table, String testDataTableName) throws SQLException, DatabaseUnitException {
		dropTmpTable(connection, testDataTableName);
		createTmpTable(connection, table.getTableMetaData().getTableName(), testDataTableName);
		connection.resetDataSet();
		IDataSet tmpDS = new DefaultDataSet(table);
    	INSERT.execute(connection, tmpDS);
    	//デフォルト制約はcreate table selectではコピーできないので実データは実テーブルに突っ込んだ後テンポラリにコピーする
    	copyData(connection, table, testDataTableName, table.getTableMetaData().getTableName());
	}

	void createTmpTable(IDatabaseConnection connection, String realTableName, String tmpTableName) throws SQLException {
		//TODO Oracle/PostgreSQL専用
    	PreparedStatement pstmt = connection.getConnection().prepareStatement(
    			String.format("create table %s as select * from %s", tmpTableName, realTableName));
    	try {
    		pstmt.execute();
    	} finally {
    		pstmt.close();
    	}
	}
	
	void dropTmpTable(IDatabaseConnection connection, String tmpTableName) throws SQLException {
    	String schema = HibernateUtils.getConfig().getProperty(Environment.DEFAULT_SCHEMA);
    	if(DatabaseUtils.isExistsTable(connection.getConnection(), schema, tmpTableName)) {
	    	PreparedStatement pstmt = connection.getConnection().prepareStatement(
					"drop table " + tmpTableName);
	    	try {
	    		pstmt.execute();
	    	} finally {
	    		pstmt.close();
	    	}
    	}
	}

	boolean checkTestData(IDatabaseConnection connection, DataSetProxy dsp, String resourceName, String tempTableName) throws SQLException {
    	URL url = this.getClass().getClassLoader().getResource(dsp.resourceName);
    	File f = new File(url.getPath());
    	long ts = f.lastModified();
    	PreparedStatement pstmt = connection.getConnection().prepareStatement(
    			String.format("select ts from %s where name = ?", DBInitializer.TEST_DATA_CONTROLER));
    	try {
    		pstmt.setString(1, resourceName);
    		ResultSet rs = pstmt.executeQuery();
    		if(rs.next()) {
    			long modified = Long.parseLong(rs.getString(1));
    			if(modified != ts) {
    				updateTestDataRecord(connection, resourceName, ts);
            		return true;
    			}
    		} else {
    			createTestDataRecord(connection, resourceName, tempTableName, ts);
        		return true;
    		}
    		return false;
    	} finally {
    		pstmt.close();
    	}
	}

	void createTestDataRecord(IDatabaseConnection connection, String resourceName, String tempTableName, long ts) throws SQLException {
    	PreparedStatement pstmt = connection.getConnection().prepareStatement(
				String.format("insert into %s(test_table, ts, name) values(?, ?, ?)", DBInitializer.TEST_DATA_CONTROLER));
    	try {
    		pstmt.setString(1, tempTableName);
    		pstmt.setString(2, "" + ts);
    		pstmt.setString(3, resourceName);
    		pstmt.execute();
    	} finally {
    		pstmt.close();
    	}
	}

	void updateTestDataRecord(IDatabaseConnection connection, String resourceName, long ts) throws SQLException {
    	PreparedStatement pstmt = connection.getConnection().prepareStatement(
				String.format("update %s set ts = ? where name = ?", DBInitializer.TEST_DATA_CONTROLER));
    	try {
    		pstmt.setString(1, "" + ts);
    		pstmt.setString(2, resourceName);
    		pstmt.execute();
    	} finally {
    		pstmt.close();
    	}
	}
}
