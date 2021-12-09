package educate.xml;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import lebah.servlets.IServlet;

public class AnnouncementModule implements IServlet {

	@Override
	public void doService(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        PrintWriter out = res.getWriter();
        

		try {
			createXML(out);
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		
	}

	private void createXML(PrintWriter out) throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("announcements");
		doc.appendChild(rootElement);
		
		createAnnouncement(doc, rootElement, "1/1/2014", "This is announcement 1");
		createAnnouncement(doc, rootElement, "1/1/2014", "This is announcement 2");
		createAnnouncement(doc, rootElement, "1/1/2014", "This is announcement 3");
		createAnnouncement(doc, rootElement, "1/1/2014", "This is announcement 4");
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		
		StreamResult result = new StreamResult(out);
		transformer.transform(source, result);
		
	}

	private void createAnnouncement(Document doc, Element rootElement, String date, String message) {

		Element staff = doc.createElement("announcement");
		rootElement.appendChild(staff);
		
		Element firstname = doc.createElement("ann_date");
		firstname.appendChild(doc.createTextNode(date));
		staff.appendChild(firstname);
		
		Element nickname = doc.createElement("ann_message");
		nickname.appendChild(doc.createTextNode(message));
		staff.appendChild(nickname);
		
	}

}
