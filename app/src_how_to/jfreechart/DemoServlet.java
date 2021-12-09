package jfreechart;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import lebah.servlets.IServlet;

public class DemoServlet implements IServlet {

	@Override
	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	       OutputStream out = response.getOutputStream(); 
	       
	       try {
	    	   DefaultPieDataset myServletPieChart = new DefaultPieDataset();
	    	   myServletPieChart.setValue("Maths", 74);
	    	   myServletPieChart.setValue("Physics", 87);
	    	   myServletPieChart.setValue("Chemistry", 62);
	    	   myServletPieChart.setValue("Biology", 92);
	    	   myServletPieChart.setValue("English", 51);        
	    	   JFreeChart mychart = ChartFactory.createPieChart("Programming - Colored Pie Chart Example", myServletPieChart, true,true,false);

	    	   response.setContentType("image/png"); 
	    	   ChartUtilities.writeChartAsPNG(out, mychart, 400, 300);
	       } catch (Exception e) {
	    	   System.err.println(e.toString()); 
	       } finally {
	    	   out.close();
	       }
	       
	       
		
	}
	
}
		
	
