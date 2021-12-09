package pdfbox;

import org.apache.pdfbox.PDFToImage;

public class Test {

	public static void main(String[] args) {

		String [] args_1 =  new String[3];
		args_1[0]  = "-outputPrefix";
		args_1[1]  = "c:/MyProjects/CUCMS/matric_card";
		args_1[2]  = "c:/MyProjects/CUCMS/matric_card.pdf";


		try {
			PDFToImage.main(args_1);
		} catch (Exception e) { 
			e.printStackTrace();
		}
	}

}
