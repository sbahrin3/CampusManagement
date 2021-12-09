package educate.poi;

public class ExcelDocument {
	
	public static Excel getDocument(String filename) throws Exception {
		String extension = filename.substring(filename.lastIndexOf("."));
		return ".xlsx".equals(extension) ? new Excel2(filename) : new Excel1(filename);
	}

}
