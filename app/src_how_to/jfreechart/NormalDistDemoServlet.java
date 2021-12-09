package jfreechart;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.function.Function2D;
import org.jfree.data.function.NormalDistributionFunction2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;

import lebah.servlets.IServlet;

public class NormalDistDemoServlet implements IServlet {

	@Override
	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	       OutputStream out = response.getOutputStream(); 
	       
	       try {
	           Function2D normal = new NormalDistributionFunction2D(0.0, 1.0);
	           XYDataset dataset = DatasetUtilities.sampleFunction2D(normal, -5.0, 5.0, 100, "Normal");
	           
           
	           final JFreeChart chart = ChartFactory.createXYLineChart(
	               "XY Series Demo",
	               "X", 
	               "Y", 
	               dataset,
	               PlotOrientation.VERTICAL,
	               true,
	               true,
	               false
	           );
	    	   response.setContentType("image/png"); 
	    	   ChartUtilities.writeChartAsPNG(out, chart, 400, 300);
	       } catch (Exception e) {
	    	   System.err.println(e.toString()); 
	       } finally {
	    	   out.close();
	       }
	       
	       
		
	}
	
}
		
	
