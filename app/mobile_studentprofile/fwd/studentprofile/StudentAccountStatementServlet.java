package fwd.studentprofile;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sis.finance.module.AccountStatement;
import educate.sis.finance.module.AccountStatementUtil;
import educate.sis.finance.module.Record;
import lebah.servlets.IServlet;

public class StudentAccountStatementServlet implements IServlet {
	
	DbPersistence db = new DbPersistence();

	@Override
	public void doService(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		PrintWriter out = res.getWriter();
		String studentId = req.getParameter("studentId");
		try {
			createXML(out, studentId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createXML(PrintWriter out, String studentId) throws Exception {
		Student student = db.find(Student.class, studentId);

		AccountStatementUtil util = new AccountStatementUtil();
		AccountStatement acct = util.getAccountStatement(student);
		List<Record> items = acct.getRecords();
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		
		Element rootElement = doc.createElement("items");
		doc.appendChild(rootElement);

		double totalLeft = 0.0d;
		double totalRight = 0.0d;
		double totalBalance = 0.0d;
		for ( Record item : items ) {
			Element parent = doc.createElement("item");
			rootElement.appendChild(parent);
			
			item.getDate();
			item.getDescription();
			item.getAmount();
			item.getBalance();
			item.getSide();
			if ( item.getSide() == 0 ) totalLeft += item.getAmount();
			if ( item.getSide() == 1 ) totalRight += item.getAmount();
			totalBalance += item.getBalance();
			
			X.add(doc, parent, "date", new SimpleDateFormat("d MMM, yyyy").format(item.getDate()));
			X.add(doc, parent, "description", item.getDescription());
			X.add(doc, parent, "amount", Double.toString(item.getAmount()));
			X.add(doc, parent, "balance", Double.toString(item.getBalance()));
			X.add(doc, parent, "side", item.getSide() == 0 ? "left" : "right");

		}
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);

		StreamResult result = new StreamResult(out);
		transformer.transform(source, result);
		
	}
	

	


}
