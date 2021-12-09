package educate.xml;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Test {

	private static String getValue(String tag, Element element) {
		NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodes.item(0);
		return node.getNodeValue();
	}

	public static void main(String[] args) throws Exception {
		URL url = new URL("http://127.0.0.1:8080/educate4/servlet/educate.xml.AnnouncementModule");
		URLConnection x = url.openConnection();
		HttpURLConnection httpConnection = (HttpURLConnection)x;
		int responseCode = httpConnection.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			System.out.println("I AM OK!!!");
			readData(x.getInputStream());
		}
	}


	public static void readData(InputStream in) throws Exception {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(in);
		doc.getDocumentElement().normalize();

		NodeList nodes = doc.getElementsByTagName("announcement");
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				System.out.println("Date: " + getValue("ann_date", element));
				System.out.println("Message: " + getValue("ann_message", element));
			}
		}
	}

}
