package fwd.studentprofile;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class X {
	
	public static void add(Document doc, Element el, String elName, String elValue) {
		Element child = doc.createElement(elName);
		child.appendChild(doc.createTextNode(elValue));
		el.appendChild(child);
	}	

}
