package educate.poi;

import java.io.OutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public interface Excel {

	
	public Cell getCell(int r, int c);
	
	public double getDouble(int r, int c);
	
	public String getString(int r, int c);
	
	public String getStringValue(int r, int c);
	
	public String getPostcodeValue(int r, int c);
	
	public Object getValue(int r, int c);
	
	public void createXLS(OutputStream out, List<List> rows) throws Exception;
	
	public void createXLS(OutputStream out, List<String> headers, List<List> rows) throws Exception;
	
	public void createXLS(OutputStream out, List<String> headers, List<String> headers2, List<List> rows) throws Exception;
	
	public void createXLS(OutputStream out, List<String> headers, List<String> headers2, List<String> headers3, List<List> rows) throws Exception;

	public int getRowSize();

	public void setRowSize(int rowSize);

	public List<Row> getRows();


}
