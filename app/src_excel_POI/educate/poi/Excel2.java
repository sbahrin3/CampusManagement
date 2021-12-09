package educate.poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.IteratorUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Excel2 implements Excel {

	
	private XSSFWorkbook workbook;
	private XSSFSheet worksheet;
	private List<Row> rows;
	private int rowSize;
	
	
	public Excel2() {
		workbook = new XSSFWorkbook();
		worksheet = workbook.createSheet("Worksheet 1");
	}
	
	public Excel2(String fileName) throws Exception {
	    FileInputStream file;
		file = new FileInputStream(new File(fileName));
	    workbook = new XSSFWorkbook(file);
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

		XSSFCellStyle cellStyle = workbook.createCellStyle();
		int r = 0;
		String type = "string";
		
		if ( headers != null ) {
			XSSFRow row = worksheet.createRow(r);
			int c = 0;
			for ( String header : headers ) {
				XSSFCell cell =  row.createCell(c);

				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(header);
				cellStyle = workbook.createCellStyle();
				XSSFFont font = workbook.createFont();
				font.setBoldweight(Font.BOLDWEIGHT_BOLD);
				cellStyle.setFont(font);
				cell.setCellStyle(cellStyle);
				c++;
			}
			r++;
		}
		
		if ( headers2 != null ) {
			XSSFRow row = worksheet.createRow(r);
			int c = 0;
			for ( String header : headers2 ) {
				XSSFCell cell = row.createCell(c);

				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(header);
				cellStyle = workbook.createCellStyle();
				XSSFFont font = workbook.createFont();
				font.setBoldweight(Font.BOLDWEIGHT_BOLD);
				cellStyle.setFont(font);
				cell.setCellStyle(cellStyle);
				c++;
			}
			r++;
		}	
		
		if ( headers3 != null ) {
			XSSFRow row = worksheet.createRow(r);
			int c = 0;
			for ( String header : headers3 ) {
				XSSFCell cell = row.createCell(c);

				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(header);
				cellStyle = workbook.createCellStyle();
				XSSFFont font = workbook.createFont();
				font.setBoldweight(Font.BOLDWEIGHT_BOLD);
				cellStyle.setFont(font);
				cell.setCellStyle(cellStyle);
				c++;
			}
			r++;
		}		
		
		for ( List<List> cols : rows ) {
			XSSFRow row = worksheet.createRow((short) r);
			int c = 0;
			for ( Object data : cols ) {
				if ( data instanceof String ) type = "string";
				else if ( data instanceof Date ) type = "date";
				else if ( data instanceof Integer ) type = "integer";
				else if ( data instanceof Long ) type = "long";
				else if ( data instanceof Double ) type = "double";
				else if ( data instanceof Float ) type = "double";
				if ( type == null || "".equals(type)) type = "string";
				XSSFCell cell = cell = row.createCell(c);
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
					cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
					cell.setCellStyle(cellStyle);
					cell.setCellValue((Date) data);
				}
				c++;
			}
			r++;
		}
		workbook.write(out);
	}

	public XSSFWorkbook getWorkbook() {
		return workbook;
	}

	public void setWorkbook(XSSFWorkbook workbook) {
		this.workbook = workbook;
	}

	public XSSFSheet getWorksheet() {
		return worksheet;
	}

	public void setWorksheet(XSSFSheet worksheet) {
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
