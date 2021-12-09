package educate.openoffice;

import java.io.File;
import java.util.ResourceBundle;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;


public class DocumentUtil {
	
	private static ResourceBundle rb;
	
	private void convertPDF(File fileIn, File fileOut) throws Exception {
		OpenOfficeConnection connection = new SocketOpenOfficeConnection(8100);
		try {
			connection.connect();
		} catch(Exception e) {
			throw new Exception("Connection to OpenOffice not available.");
		}
		DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
	    converter.convert(fileIn, fileOut);

	    connection.disconnect();		
		
	}
	
	public void convertToPDF(String fileName) throws Exception {
		rb = ResourceBundle.getBundle("files");
		String dir = rb.getString("dir.doc");
		String dir2 = rb.getString("dir.pdf");
		
		if ( !dir.endsWith("/")) dir = dir + "/";
		if ( !dir2.endsWith("/")) dir2 = dir2 + "/";
			
		File fileIn = new File(dir + fileName);
		File fileOut = new File(dir2 + fileName + ".pdf");
		
		//do conversion only if pdf does not exits in the destination folder
		if ( !fileOut.exists() ) {
			System.out.println("Conversion to PDF for " + fileName);
			convertPDF(fileIn, fileOut);
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		File fileIn = new File("c:/test-jod/abc.html");
		File fileOut = new File("c:/test-jod/abc.doc");
		
		OpenOfficeConnection connection = new SocketOpenOfficeConnection(8100);
		try {
			connection.connect();
		} catch(Exception e) {
			throw new Exception("Connection to OpenOffice not available.");
		}
		DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
	    converter.convert(fileIn, fileOut);

	    connection.disconnect();	
		
		
		
	}

}
