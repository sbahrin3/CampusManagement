package educate.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

public class TestCreatePDF {
	
	public static void main(String[] args) throws Exception {
		
		String tempLocation = "/Users/Admin/temp/";
		
		String url = "http://127.0.0.1:8080/educate4/v/educate.sis.module.PrintStudentExamTranscriptModule?studentId=1404116812653&twocolumns=false&;summary=false&smode=no&logo=yes&ahideF=yes&pdfName=transcript";
		//url = "http://127.0.0.1:8080/MMDIS_UI/useraccess2#!/";
		URL u = new URL(url);
	    HttpURLConnection uc = (HttpURLConnection) u.openConnection();
	    Tidy tidy = new Tidy();
		BufferedInputStream in = new BufferedInputStream(uc.getInputStream());
		String filename = tempLocation + lebah.db.UniqueID.getUID() + ".html";
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filename));
		tidy.parse(in, out);
		in.close();
		out.close();
		
		ITextRenderer renderer = new ITextRenderer();
		File file = new File(filename);
		renderer.setDocument(file);
		renderer.layout();
		
		FileOutputStream os = new FileOutputStream("/Users/Admin/temp/generated.pdf");
		renderer.createPDF(os);     
		os.flush();
		os.close();
		
		
	}

}
