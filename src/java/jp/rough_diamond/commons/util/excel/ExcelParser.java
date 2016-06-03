/*
 * Copyright (c) 2008, 2009
 *  Rough Diamond Co., Ltd.              -- http://www.rough-diamond.co.jp/
 *  Information Systems Institute, Ltd.  -- http://www.isken.co.jp/
 *  All rights reserved.
 */

package jp.rough_diamond.commons.util.excel;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.rough_diamond.commons.lang.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

@SuppressWarnings("deprecation")
public class ExcelParser {
	private final static Log log = LogFactory.getLog(ExcelParser.class);
	
	private HSSFWorkbook book;
	private HSSFSheet sheet;
	
	public ExcelParser(InputStream is) throws IOException {
		book = new HSSFWorkbook(is);
		sheet = book.getSheetAt(0);
	}

	public HSSFWorkbook getWorkbook() {
		return book;
	}
	public HSSFSheet getSheet() {
		return sheet;
	}
	
	public Map<String, Integer> makeHeaderMap() {
		Map<String, Integer> ret = new HashMap<String, Integer>();
		HSSFRow row = sheet.getRow(0);
		for(int i = 0 ; i < row.getLastCellNum() ; i++) {
			HSSFCell cell = row.getCell((short) i);
			if(cell == null) {
				continue;
			}
			ret.put(cell.getStringCellValue().trim(), i);
		}
		return ret;
	}
	
	public List<Object[]> getObjectRows() {
		return getRows(Object.class, new GetObjectCellValue());
	}
	
	public List<String[]> getStringRows() {
		return getRows(String.class, new GetStringCellValue());
	}
	
	@SuppressWarnings("unchecked")
	private <T> List<T[]> getRows(Class<T> type, GetCellValue strategy) {
		if(log.isDebugEnabled()) {
			log.debug("getPhysicalNumberOfRows:" + sheet.getPhysicalNumberOfRows());
			log.debug("getFirstRowNum         :" + sheet.getFirstRowNum());
			log.debug("getLastRowNum          :" + sheet.getLastRowNum());
		}		
		List<T[]> ret = new ArrayList<T[]>();
		for(int i = 1 ; i <= sheet.getLastRowNum() ; i++) {
			List<T> row = new ArrayList<T>();
			HSSFRow frow = sheet.getRow(i);
			if(frow == null) {
				continue;
			}
			for(int j = 0 ; j < frow.getLastCellNum() ; j++) {
				HSSFCell cell = frow.getCell((short)j);
				if(cell == null) {
					row.add((T) "");
				} else {
					row.add((T) strategy.getValud(cell));
				}
			}
			T[] array = (T[]) Array.newInstance(type, row.size());
			T[] tmp = (T[]) row.toArray(array);
			if(!isAllEmpty(tmp)) {
				ret.add(tmp);
			}
		}
		return ret;
	}
	
	private <T> boolean isAllEmpty(T[] row) {
		for(T column : row) {
			if(column instanceof String) {
				if(!StringUtils.isEmpty((String)column)) {
					return false;
				}
				
			} else {
				if(column != null) {
					return false;
				}
			}
		}
		return true;
	}
	
	private static Object getCell(HSSFCell cell) {
		switch(cell.getCellType()) {
		case HSSFCell.CELL_TYPE_BLANK:
			log.debug("CELL_TYPE_BLANK");
			return "";
		case HSSFCell.CELL_TYPE_BOOLEAN:
			if(log.isDebugEnabled()){
				log.debug("CELL_TYPE_BOOLEAN:" + cell.getBooleanCellValue());
			}
			return cell.getBooleanCellValue();
		case HSSFCell.CELL_TYPE_ERROR:
			if(log.isDebugEnabled()) {
				log.debug("CELL_TYPE_ERROR:" + cell.getErrorCellValue());
			}
			return cell.getErrorCellValue();
		case HSSFCell.CELL_TYPE_FORMULA:
			if(log.isDebugEnabled()) {
				log.debug("CELL_TYPE_FORMULA:" + cell.getCellFormula());
			}
			return cell.getCellFormula();
		case HSSFCell.CELL_TYPE_STRING:
			if(log.isDebugEnabled()) {
				log.debug("CELL_TYPE_STRING:" + cell.getStringCellValue());
			}
			return cell.getStringCellValue();
		case HSSFCell.CELL_TYPE_NUMERIC:
			short dataFormat = cell.getCellStyle().getDataFormat();
			log.debug(dataFormat);
			if(HSSFDateUtil.isInternalDateFormat(dataFormat) || dataFormat == 56) {
				if(log.isDebugEnabled()) {
					log.debug("CELL_TYPE_NUMERIC(DATE):" + cell.getDateCellValue());
				}
				return cell.getDateCellValue();
			} else {
				if(log.isDebugEnabled()) {
					log.debug("CELL_TYPE_NUMERIC(Number):" + cell.getNumericCellValue());
				}
				return cell.getNumericCellValue();
			}
		default:
			throw new RuntimeException("—\Šú‚µ‚È‚¢CellType:" + cell.getCellType());
		}
		
	}

	private static interface GetCellValue {
		public Object getValud(HSSFCell cell);
	}
	
	private static class GetObjectCellValue implements GetCellValue {
		public Object getValud(HSSFCell cell) {
			return getCell(cell);
		}
		
	}
	
	private static class GetStringCellValue implements GetCellValue {
		public String getValud(HSSFCell cell) {
			return getCell(cell).toString();
		}
	}
}
