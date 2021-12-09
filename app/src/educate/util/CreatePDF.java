package educate.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

public class CreatePDF {
	
	public static void main(String[] args) throws Exception {
		
		String url = "http://127.0.0.1:8080/educate4/v/educate.timetabling.module.PrintTimeTableModule";
		//String url = "http://127.0.0.1:8080/bheuu/html/list.html";
	    URL u = new URL(url);
	    HttpURLConnection uc = (HttpURLConnection) u.openConnection();

		OutputStream os = new FileOutputStream("c:/temp/tt.pdf");
		
		Tidy tidy = new Tidy();
		
		BufferedInputStream in = new BufferedInputStream(uc.getInputStream());
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream("c:/temp/tt.html"));
		tidy.parse(in, out);

		in.close();
		out.close();
		
		//ByteArrayOutputStream byte1 = new ByteArrayOutputStream();  
		
		ITextRenderer renderer = new ITextRenderer();
		//renderer.setDocument(url);  
		File file = new File("c:/TEMP/tt.html");
		renderer.setDocument(file);
		
		renderer.layout();
		renderer.createPDF(os);     
		os.flush();
		os.close();
		
		//file.delete();
		
	}

}
