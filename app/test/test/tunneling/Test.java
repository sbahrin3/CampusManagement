package test.tunneling;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Test {
	
//	<Point>
//	<coordinates>101.7104771,2.9920010,0</coordinates>
//	</Point>
	
	
	public static void main(String[] args) throws Exception {
		Document dom;
		String u = "http://maps.google.com/maps/geo?q=kepong,+Malaysia&output=xml&oe=utf8&sensor=true_or_false&key=ABQIAAAAvzRRTwdGvOJcB6D33XAT2xRXzJuT7ix21wWFQX1q93mzO9ERFRRcnoqLTt5qnp8LtkrZBcttbbKG7Q";
		    
		    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		    
			try {

				//Using factory get an instance of document builder
				DocumentBuilder db = dbf.newDocumentBuilder();

				//parse using builder to get DOM representation of the XML file
				dom = db.parse(u);
				
				Element docEle = dom.getDocumentElement();
				NodeList nl = docEle.getElementsByTagName("Point");
				for(int i = 0 ; i < nl.getLength();i++) {
					Element el = (Element)nl.item(i);
					System.out.println(el.getTextContent());
				}

			}catch(ParserConfigurationException pce) {
				pce.printStackTrace();
			}catch(SAXException se) {
				se.printStackTrace();
			}catch(IOException ioe) {
				ioe.printStackTrace();
			}


	}


}
