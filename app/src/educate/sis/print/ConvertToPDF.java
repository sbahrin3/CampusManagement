package educate.sis.print;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;


public class ConvertToPDF {
	
    public static void main(String[] args) throws IOException, DocumentException {
//		String File_To_Convert = "d:/html2pdf/sample.html";
    	
		//String url = new File(File_To_Convert).toURI().toURL().toString();
    	String url = "http://localhost:8181/educate4/v/educate.sis.print.AdmissionFormPrint?matric_no=BA0004";
		System.out.println(""+url);
		String HTML_TO_PDF = "d:/html2pdf/admission_form.pdf";
		OutputStream os = new FileOutputStream(HTML_TO_PDF);
		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocument(url);      
		renderer.layout();
		renderer.createPDF(os);    
		os.flush();
		os.close();
    }

}
