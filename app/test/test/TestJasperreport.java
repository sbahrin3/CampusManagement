package test;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

public class TestJasperreport {
	

	
	public static void main(String[] args) {
		
		try {
			JasperDesign jasperDesign = JRXmlLoader.load("C:/workspace/educate4/WebContent/reports/report1.jrxml");
			JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
