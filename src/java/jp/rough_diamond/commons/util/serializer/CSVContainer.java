/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */
package jp.rough_diamond.commons.util.serializer;

import  java.util.StringTokenizer;

public class CSVContainer {
    private String[][] csvData = null;
    private ColumnFormatter formatter;
    
    private final static ColumnFormatter DEFAULT_FORMATTER = new DefaultColumnFormatter();
    
    public CSVContainer(){
    	this(null, DEFAULT_FORMATTER);
    }
    
	public CSVContainer(ColumnFormatter formatter){
		this(null, formatter);
	}

    public CSVContainer(String[][] csvData){
    	this(csvData, DEFAULT_FORMATTER);
    }
    
    public CSVContainer(String[][] csvData, ColumnFormatter formatter) {
		setCsvData(csvData);
		this.formatter = formatter;    	
    }
    
    public String[][] getCsvData(){
        return csvData;
    }
    
    public void setCsvData(String[][] csvData){
        this.csvData = csvData;
    }
    
    public String toString(){
        StringBuilder buf = new StringBuilder();
        for(int i = 0 ; i < csvData.length ; i++){
            for(int j = 0 ; j < csvData[i].length ; j++){
                if(j != 0){
                    buf.append(",");
                }
                buf.append(formatter.getColumn(csvData[i][j]));
            }
            buf.append("\r\n");
        }
        return buf.toString();
    }
    
    public static String getSupplementQuote(String rowData){
        StringBuilder buf = new StringBuilder();
        if(rowData != null){
            StringTokenizer tokenizer = 
                    new StringTokenizer(rowData, "\"", true);
            while(tokenizer.hasMoreTokens()){
                String token = tokenizer.nextToken();
                if("\"".equals(token)){
                    buf.append("\"\"");
                } else {
                    buf.append(token);
                }
            }
        }
        return buf.toString();
    }
 }