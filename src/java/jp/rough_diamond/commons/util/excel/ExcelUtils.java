package jp.rough_diamond.commons.util.excel;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;

@SuppressWarnings("deprecation")
public class ExcelUtils {
	private final static Log log = LogFactory.getLog(ExcelUtils.class);

	private final HSSFSheet sheet;
	private final HSSFWorkbook wb;

	public ExcelUtils(HSSFWorkbook wb, HSSFSheet sheet) {
		this.wb = wb;
		this.sheet = sheet;
	}


	public int getRowNumber(HSSFCell cell, int inspectColumnNumber) {
		return getRowNumber(sheet, cell, inspectColumnNumber, 0);
	}
	
	private static int getRowNumber(HSSFSheet sheet, HSSFCell cell, int inspectColumnNumber, int startRowNumber) {
		int max = sheet.getLastRowNum();
		for(int i = startRowNumber ; i < max ; i++) {
			HSSFCell tmp = sheet.getRow(i).getCell((short)inspectColumnNumber);
			if(tmp == cell) {
				return i;
			}
		}
		return -1;
	}

	public void addRegion(int yFrom, int xFrom, int yTo, int xTo) {
		Region region = new Region(yFrom, (short)xFrom, yTo, (short)xTo);
		for(int i = 0 ; i < sheet.getNumMergedRegions() ; i++) {
			Region tmp = sheet.getMergedRegionAt(i);
			if(region.equals(tmp)) {
				return;
			}
		}
		sheet.addMergedRegion(region);
	}
	
	public void borderBottom(int y, int xFrom, int xTo, int borderStyle) {
		for(int x = xFrom ; x <= xTo ; x++) {
			HSSFCell cell = getCell(y, x);
			HSSFCellStyle style = cloneStyle(cell);
			style.setBorderBottom((short)borderStyle);
			cell.setCellStyle(style);
		}
	}

	public void borderCheck(int yFrom, int xFrom, int yTo, int xTo, int borderStyle) {
		for(int x = xFrom ; x <= xTo ; x++) {
			for(int y = yFrom ; y <= yTo ; y++) {
				HSSFCell cell = getCell(y, x);
				HSSFCellStyle style = cloneStyle(cell);
				style.setBorderLeft((short)borderStyle);
				style.setBorderRight((short)borderStyle);
				style.setBorderTop((short)borderStyle);
				style.setBorderBottom((short)borderStyle);
				cell.setCellStyle(style);
			}
		}
	}
	
	public void borderBox(int yFrom, int xFrom, int yTo, int xTo, int borderStyle) {
		for(int y = yFrom ; y <= yTo ; y++) {
			HSSFCell cell = getCell(y, xFrom);
			HSSFCellStyle style = cloneStyle(cell);
			style.setBorderLeft((short)borderStyle);
			cell.setCellStyle(style);
			cell = getCell(y, xTo);
			style = cloneStyle(cell);
			style.setBorderRight((short)borderStyle);
			cell.setCellStyle(style);
		}
		for(int x = xFrom ; x <= xTo ; x++) {
			HSSFCell cell = getCell(yFrom, x);
			HSSFCellStyle style = cloneStyle(cell);
			style.setBorderTop((short)borderStyle);
			cell.setCellStyle(style);
			cell = getCell(yTo, x);
			style = cloneStyle(cell);
			style.setBorderBottom((short)borderStyle);
			cell.setCellStyle(style);
		}
	}
	
	public void setBackgroundColor(int y, int x, short bgColor) {
		HSSFCell cell = getCell(y, x);
		HSSFCellStyle style = cloneStyle(cell);
		style.setFillForegroundColor(bgColor);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		cell.setCellStyle(style);
	}

	Set<HSSFCell> alwaysClonedCells = new HashSet<HSSFCell>();
	HSSFCellStyle cloneStyle(HSSFCell cell) {
		try {
			HSSFCellStyle oldStyle = cell.getCellStyle();
			if(alwaysClonedCells.contains(cell)) {
				return oldStyle;
			}
			HSSFCellStyle newStyle = wb.createCellStyle();
			BeanUtils.copyProperties(newStyle, oldStyle);
			HSSFFont font = wb.getFontAt(oldStyle.getFontIndex());
			newStyle.setFont(font);
			alwaysClonedCells.add(cell);
			return newStyle;
		} catch(Exception e) {
			log.warn(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	public static String getExcelCell(int y, int x) {
		return getExcelColumn(x) + getExcelRow(y);
	}
	
	public static String getExcelRow(int y) {
		return "" + (y + 1);
	}
	
	public static String getExcelColumn(int x) {
		final String radix = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		int base = x;
		int div = radix.length();
		StringBuffer buf = new StringBuffer();
		while(true) {
			int mod = x % div;
			buf.insert(0, radix.charAt(mod));
			base = x / div;
			if(base == 0) {
				break;
			}
		}
		String ret = buf.toString();
		return ret;
	}
	
	public void removeRow(int rowNumber) {
		for(RowInspector ri : ris) {
			ri.remove(rowNumber);
		}
		sheet.shiftRows(rowNumber + 1, sheet.getLastRowNum(), -1, true, true);
	}

	/**
	 * 同一シート内の指定行にコピーする。
	 * @param org コピー元の行番号
	 * @param dest コピー先の行番号
	 * @param isShift
	 */
	public void copyRow(int org, int dest, boolean isShift) {
		if(isShift) {
			for(RowInspector ri : ris) {
				ri.shift(dest);
			}
			sheet.shiftRows(dest, sheet.getLastRowNum(), 1, true, true);
		}
		HSSFRow newRow = sheet.createRow(dest);
		HSSFRow oldRow = sheet.getRow(org);
		newRow.setHeight(oldRow.getHeight());
		newRow.setHeightInPoints(oldRow.getHeightInPoints());
		for(short i = oldRow.getFirstCellNum() ; i < oldRow.getLastCellNum() ; i++) {
			HSSFCell newCell = newRow.createCell(i);
			HSSFCell oldCell = oldRow.getCell(i);
			copyCell(oldCell, newCell);
		}
	}

	/**
	 * 指定されたシートの指定行にコピーする。
	 * @param org コピー元の行番号
	 * @param destSheet コピー先のシート
	 * @param dest コピー先の行番号
	 */
	public void copyRow(int org, HSSFSheet destSheet, int dest) {
		HSSFRow newRow = destSheet.createRow(dest);
		HSSFRow oldRow = sheet.getRow(org);
		newRow.setHeight(oldRow.getHeight());
		newRow.setHeightInPoints(oldRow.getHeightInPoints());
		for(short i = oldRow.getFirstCellNum() ; i < oldRow.getLastCellNum() ; i++) {
			HSSFCell newCell = newRow.createCell(i);
			HSSFCell oldCell = oldRow.getCell(i);
			copyCell(oldCell, newCell);
		}
	}

	public void setFormula(int y, int x, String formula) {
		log.debug(formula);
		HSSFCell cell = getCell(sheet, y, x);
		cell.setCellFormula(formula);
	}

	public void setString(int y, int x, String value) {
		HSSFCell cell = getCell(sheet, y, x);
		cell.setEncoding(HSSFWorkbook.ENCODING_UTF_16);
		cell.setCellValue(value);
	}

	public void setNumber(int y, int x, double value) {
		HSSFCell cell = getCell(sheet, y, x);
		cell.setCellValue(value);
	}

	public void setDate(int y, int x, Date date) {
		HSSFCell cell = getCell(sheet, y, x);
		cell.setCellValue(date);
	}
	
	public HSSFCell getCell(int y, int x) {
		return getCell(sheet, y, x);
	}
	
	public static HSSFCell getCell(HSSFSheet sheet, int y, int x) {
		HSSFRow row = sheet.getRow(y);
		if(row == null) {
			row = sheet.createRow(y); 
		}
		HSSFCell cell = row.getCell((short) x);
		if(cell == null) {
			cell = row.createCell((short)x);
		}
		return cell;
	}
	
	public static void copyCell(HSSFCell org, HSSFCell dest) {
		if(org == null) {
			return;
		}
		dest.setCellStyle(org.getCellStyle());
		switch(org.getCellType()) {
		case HSSFCell.CELL_TYPE_BLANK:
			dest.setCellValue("");
			break;
		case HSSFCell.CELL_TYPE_BOOLEAN:
			dest.setCellValue(org.getBooleanCellValue());
			break;
		case HSSFCell.CELL_TYPE_ERROR:
			dest.setCellValue(org.getErrorCellValue());
			break;
		case HSSFCell.CELL_TYPE_FORMULA:
			dest.setCellValue(org.getCellFormula());
			break;
		case HSSFCell.CELL_TYPE_STRING:
			dest.setCellValue(org.getStringCellValue());
			break;
		case HSSFCell.CELL_TYPE_NUMERIC:
			short dataFormat = org.getCellStyle().getDataFormat();
			if(HSSFDateUtil.isInternalDateFormat(dataFormat) || dataFormat == 56) {
				dest.setCellValue(org.getDateCellValue());
			} else {
				dest.setCellValue(org.getNumericCellValue());
			}
			break;
		default:
			throw new RuntimeException("予期しないCellType:" + org.getCellType());
		}
	}

	private Set<RowInspector> ris = new HashSet<RowInspector>();
	
	public void addInspector(RowInspector ri) {
		ris.add(ri);
	}

	public static class RowInspector {
		private int current;
		private int real;
		public RowInspector(int current) {
			this.current = current;
			this.real = current;
		}
		
		public int getCurrent() {
			return current;
		}
		
		public int getReal() {
			return real;
		}
		
		void shift(int dest) {
			if(real != -1 && real >= dest) {
				real++;
			}
		}
		
		void remove(int target) {
			if(real > target) {
				real--;
			} else if(real == target) {
				real = -1;
			}
		}
	}

    public HSSFSheet getSheet() {
        return sheet;
    }

    public HSSFWorkbook getWb() {
        return wb;
    }
}
