package educate.poi;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.IteratorUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;

public class Excel1 implements Excel {
	
	private HSSFWorkbook workbook;
	private HSSFSheet worksheet;
	private List<Row> rows;
	private int rowSize;
	
	public Excel1() {
		workbook = new HSSFWorkbook();
		worksheet = workbook.createSheet("Worksheet 1");
	}
	
	public Excel1(String fileName) throws Exception {
	    FileInputStream file;
		file = new FileInputStream(new File(fileName));
	    workbook = new HSSFWorkbook(file);
	    worksheet = workbook.getSheetAt(0);			
	    Iterator<Row> rowIterator = worksheet.iterator();
	    rows = IteratorUtils.toList(rowIterator);
	    rowSize = rows.size();
	}
	
	public Cell getCell(int r, int c) {
		Row row = worksheet.getRow(r);
		return row.getCell(c);
	}
	
	public double getDouble(int r, int c) {
		Row row = worksheet.getRow(r);
		Cell cell = row.getCell(c);
		if ( cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
			return cell.getNumericCellValue();
		else
			return 0.0;
	}
	
	public String getString(int r, int c) {
		Row row = worksheet.getRow(r);
		Cell cell = row.getCell(c);
		
		if ( cell == null )
			return "";
		
		if ( cell.getCellType() == Cell.CELL_TYPE_STRING )
			return cell.getStringCellValue();
		else if ( cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
			return Double.toString(cell.getNumericCellValue());
		else
			return "";
	}	
	
	public String getStringValue(int r, int c) {
		Row row = worksheet.getRow(r);
		Cell cell = row.getCell(c);
		
		if ( cell == null )
			return "";
		
		if ( cell.getCellType() == Cell.CELL_TYPE_STRING )
			return cell.getStringCellValue();
		else if ( cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			double d = cell.getNumericCellValue();
			String s = new DecimalFormat("#").format(d);
			return s;
		}
		else
			return "";
	}
	
	public String getPostcodeValue(int r, int c) {
		Row row = worksheet.getRow(r);
		Cell cell = row.getCell(c);
		
		if ( cell == null )
			return "";
		
		if ( cell.getCellType() == Cell.CELL_TYPE_STRING )
			return cell.getStringCellValue();
		else if ( cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			double d = cell.getNumericCellValue();
			String s = new DecimalFormat("00000").format(d);
			return s;
		}
		else
			return "";
	}
	
	public Object getValue(int r, int c) {
		Object value = null;
		Row row = worksheet.getRow(r);
		Cell cell = row.getCell(c);
		if ( cell.getCellType() == Cell.CELL_TYPE_NUMERIC ) value = cell.getNumericCellValue();
		else value = cell.getStringCellValue();
		return value;
	}
	
	public void createXLS(OutputStream out, List<List> rows) throws Exception {
		createXLS(out, null, rows);
	}
	
	public void createXLS(OutputStream out, List<String> headers, List<List> rows) throws Exception {
		createXLS(out, headers, null, null, rows);
	}
	
	public void createXLS(OutputStream out, List<String> headers, List<String> headers2, List<List> rows) throws Exception {
		createXLS(out, headers, headers2, null, rows);
	}

	
	public void createXLS(OutputStream out, List<String> headers, List<String> headers2, List<String> headers3, List<List> rows) throws Exception {
		createXLS(out, headers, headers2, headers3, null, null, rows, null);
		
	}
	
	
	public void createXLS(OutputStream out, List<String> headers, List<String> headers2, List<String> headers3, List<String> textGroup1, List<String> textGroup2, List<List> rows, List<List> rows2) throws Exception {

		HSSFCellStyle cellStyle = workbook.createCellStyle();
		int r = 0;
		String type = "string";
		
		
		if ( textGroup1 != null ) {
			
			
			int c = 0;
			HSSFRow row = worksheet.createRow(r);
			HSSFCell cell = row.createCell(c);
			
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue("");
			cellStyle = workbook.createCellStyle();
			HSSFFont font = workbook.createFont();
			cellStyle.setFont(font);
			cell.setCellStyle(cellStyle);
			r++;
			
			
			for ( String t : textGroup1 ) {
				row = worksheet.createRow(r);
				cell = row.createCell(c);
				
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(t);
				cellStyle = workbook.createCellStyle();
				font = workbook.createFont();
				cellStyle.setFont(font);
				cell.setCellStyle(cellStyle);
				r++;
			}
			
		}
		
		if ( headers != null ) {
			HSSFRow row = worksheet.createRow(r);
			int c = 0;
			for ( String header : headers ) {
				HSSFCell cell = row.createCell(c);

				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(header);
				cellStyle = workbook.createCellStyle();
				HSSFFont font = workbook.createFont();
				font.setBoldweight(Font.BOLDWEIGHT_BOLD);
				cellStyle.setFont(font);
				cell.setCellStyle(cellStyle);
				c++;
			}
			r++;
		}
		
		if ( headers2 != null ) {
			HSSFRow row = worksheet.createRow(r);
			int c = 0;
			for ( String header : headers2 ) {
				if ( !"".equals(header)) {
					HSSFCell cell = row.createCell(c);
	
					cell.setCellType(Cell.CELL_TYPE_STRING);
					cell.setCellValue(header);
					cellStyle = workbook.createCellStyle();
					HSSFFont font = workbook.createFont();
					font.setBoldweight(Font.BOLDWEIGHT_BOLD);
					cellStyle.setFont(font);
					cell.setCellStyle(cellStyle);
				}
				c++;
			}
			r++;
		}	
		
		if ( headers3 != null ) {
			HSSFRow row = worksheet.createRow(r);
			int c = 0;
			for ( String header : headers3 ) {
				if ( !"".equals(header)) {
					HSSFCell cell = row.createCell(c);
	
					cell.setCellType(Cell.CELL_TYPE_STRING);
					cell.setCellValue(header);
					cellStyle = workbook.createCellStyle();
					HSSFFont font = workbook.createFont();
					font.setBoldweight(Font.BOLDWEIGHT_BOLD);
					cellStyle.setFont(font);
					cell.setCellStyle(cellStyle);
				}
				
				c++;
			}
			r++;
		}		
		
		if ( rows != null ) {
			for ( List<List> cols : rows ) {
				HSSFRow row = worksheet.createRow((short) r);
				int c = 0;
				for ( Object data : cols ) {
					if ( data == null ) {
						HSSFCell cell = cell = row.createCell(c);
					} else {
						if ( data instanceof String ) type = "string";
						else if ( data instanceof Date ) type = "date";
						else if ( data instanceof Integer ) type = "integer";
						else if ( data instanceof Long ) type = "long";
						else if ( data instanceof Double ) type = "double";
						else if ( data instanceof Float ) type = "double";
						if ( type == null || "".equals(type)) type = "string";
						HSSFCell cell = cell = row.createCell(c);
						if ( "string".equals(type)) {
							cell.setCellType(Cell.CELL_TYPE_STRING);
							if ( data != null )
								cell.setCellValue((String) data);
						}
						else if ( "integer".equals(type)) {
							cell.setCellType(Cell.CELL_TYPE_NUMERIC);
							if ( data != null )
								cell.setCellValue((Integer) data);
						}
						else if ( "long".equals(type)) {
							cell.setCellType(Cell.CELL_TYPE_NUMERIC);
							if ( data != null )
								cell.setCellValue((Long) data);
						}				
						else if ( "double".equals(type)) {
							cell.setCellType(Cell.CELL_TYPE_NUMERIC);
							if ( data != null )
								cell.setCellValue((Double) data);
						}				
						else if ( "date".equals(type)) {
							cellStyle = workbook.createCellStyle();
							//cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));
							cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
							cell.setCellStyle(cellStyle);
							cell.setCellValue((Date) data);
						}
					}
					c++;
				}
				r++;
			}
		
		}
		
		
		
		if ( textGroup2 != null ) {
			
			
			int c = 0;
			HSSFRow row = worksheet.createRow(r);
			HSSFCell cell = row.createCell(c);
			
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue("");
			cellStyle = workbook.createCellStyle();
			HSSFFont font = workbook.createFont();
			cellStyle.setFont(font);
			cell.setCellStyle(cellStyle);
			r++;
			
			
			for ( String t : textGroup2 ) {
				row = worksheet.createRow(r);
				cell = row.createCell(c);
				
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(t);
				cellStyle = workbook.createCellStyle();
				font = workbook.createFont();
				cellStyle.setFont(font);
				cell.setCellStyle(cellStyle);
				r++;
			}
			
		}
		
		
		if ( rows2 != null ) {
			
			for ( List<List> cols : rows2 ) {
				HSSFRow row = worksheet.createRow((short) r);
				int c = 0;
				for ( Object data : cols ) {
					if ( data == null ) {
						HSSFCell cell = cell = row.createCell(c);
					} else {
						if ( data instanceof String ) type = "string";
						else if ( data instanceof Date ) type = "date";
						else if ( data instanceof Integer ) type = "integer";
						else if ( data instanceof Long ) type = "long";
						else if ( data instanceof Double ) type = "double";
						else if ( data instanceof Float ) type = "double";
						if ( type == null || "".equals(type)) type = "string";
						HSSFCell cell = cell = row.createCell(c);
						if ( "string".equals(type)) {
							cell.setCellType(Cell.CELL_TYPE_STRING);
							if ( data != null )
								cell.setCellValue((String) data);
						}
						else if ( "integer".equals(type)) {
							cell.setCellType(Cell.CELL_TYPE_NUMERIC);
							if ( data != null )
								cell.setCellValue((Integer) data);
						}
						else if ( "long".equals(type)) {
							cell.setCellType(Cell.CELL_TYPE_NUMERIC);
							if ( data != null )
								cell.setCellValue((Long) data);
						}				
						else if ( "double".equals(type)) {
							cell.setCellType(Cell.CELL_TYPE_NUMERIC);
							if ( data != null )
								cell.setCellValue((Double) data);
						}				
						else if ( "date".equals(type)) {
							cellStyle = workbook.createCellStyle();
							//cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));
							cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
							cell.setCellStyle(cellStyle);
							cell.setCellValue((Date) data);
						}
					}
					c++;
				}
				r++;
			}
			
		}
		
		

		
		workbook.write(out);
	}
	
	
	public HSSFWorkbook getWorkbook() {
		return workbook;
	}

	public void setWorkbook(HSSFWorkbook workbook) {
		this.workbook = workbook;
	}

	public HSSFSheet getWorksheet() {
		return worksheet;
	}

	public void setWorksheet(HSSFSheet worksheet) {
		this.worksheet = worksheet;
	}

	public int getRowSize() {
		return rowSize;
	}

	public void setRowSize(int rowSize) {
		this.rowSize = rowSize;
	}

	public List<Row> getRows() {
		return rows;
	}



}
