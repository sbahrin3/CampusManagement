package migration.module;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import educate.poi.Excel1;

public class GraduateListExcelToXML {
	
	public static void main(String[] args) throws Exception {
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("graduates");
		doc.appendChild(rootElement);
		
		String filename = "/Users/Admin/Documents/graduates-list3.xls";
		Excel1 e = new Excel1(filename);
		for ( int i=0; i < e.getRowSize(); i++ ) {
			if ( i > 0 ) {
				
				String matric = "";
				if ( e.getCell(i, 0).getCellType() == HSSFCell.CELL_TYPE_NUMERIC ) {
					matric = Integer.toString((int) e.getDouble(i, 0));
				} else {
					matric = e.getString(i, 0);
				}
				String name = e.getString(i, 1);
				String icno = e.getString(i, 2);
				String program = e.getString(i, 3);
				
				System.out.println(matric + ", " + name);
				
				Element graduate = doc.createElement("graduate");
				rootElement.appendChild(graduate);
				
				Element eName = doc.createElement("name");
				eName.appendChild(doc.createTextNode(name));
				graduate.appendChild(eName);
				
				Element eIcno = doc.createElement("icno");
				eIcno.appendChild(doc.createTextNode(icno));
				graduate.appendChild(eIcno);
				
				Element eMatric = doc.createElement("matric");
				eMatric.appendChild(doc.createTextNode(matric));
				graduate.appendChild(eMatric);
				
				Element eProgram = doc.createElement("program");
				eProgram.appendChild(doc.createTextNode(program));
				graduate.appendChild(eProgram);
	
			}
		}
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File("/Users/Admin/Documents/UTP-CONVO/graduates.xml"));
		transformer.transform(source, result);
		
	}

}
