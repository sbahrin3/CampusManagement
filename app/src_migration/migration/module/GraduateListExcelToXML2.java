package migration.module;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import educate.poi.Excel1;

public class GraduateListExcelToXML2 {
	
	public static void main(String[] args) throws Exception {
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("graduates");
		doc.appendChild(rootElement);
		
		String filename = "/Users/Admin/Documents/UTP-CONVO/graduate-list4.xls";
		Excel1 e = new Excel1(filename);
		for ( int i=0; i < e.getRowSize(); i++ ) {
			if ( i > 0 ) {
				
				
				String name = e.getString(i, 0);
				String programEN = e.getString(i, 1);
				String programBM = e.getString(i, 2);
				
				System.out.println(name + ", " + programEN);
				
				Element graduate = doc.createElement("graduate");
				rootElement.appendChild(graduate);
				
				Element eName = doc.createElement("name");
				eName.appendChild(doc.createTextNode(name));
				graduate.appendChild(eName);
				
				Element eProgramEN = doc.createElement("programEN");
				eProgramEN.appendChild(doc.createTextNode(programEN));
				graduate.appendChild(eProgramEN);
				
				Element eProgramBM = doc.createElement("programBM");
				eProgramBM.appendChild(doc.createTextNode(programBM));
				graduate.appendChild(eProgramBM);
	
			}
		}
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File("/Users/Admin/Documents/UTP-CONVO/graduate_list.xml"));
		transformer.transform(source, result);
		
	}

}
