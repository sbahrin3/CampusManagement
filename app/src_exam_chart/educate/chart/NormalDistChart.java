package educate.chart;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.function.NormalDistributionFunction2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;

import lebah.servlets.IServlet;

public class NormalDistChart  implements IServlet {

	@Override
	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		String mean_ = request.getParameter("mean");
		String sd_ = request.getParameter("sd");
		String low_ = request.getParameter("low");
		String high_ = request.getParameter("high");
		
		double mean = Double.parseDouble(mean_);
		double sd = Double.parseDouble(sd_);
		double low = Double.parseDouble(low_);
		double high = Double.parseDouble(high_);
		
		OutputStream out = response.getOutputStream(); 
		try {
			
			NormalDistributionFunction2D normalDistributionFunction2D = new NormalDistributionFunction2D(mean, sd);
			XYDataset dataset = DatasetUtilities.sampleFunction2D(normalDistributionFunction2D, low, high, 1000, "Normal");
			JFreeChart mychart = ChartFactory.createXYLineChart("", "Result", "", dataset, PlotOrientation.VERTICAL, false, false, false);
			response.setContentType("image/png"); 
			ChartUtilities.writeChartAsPNG(out, mychart, 400, 200);
		} catch (Exception e) {
			System.err.println(e.toString()); 
		} finally {
			out.close();
		}



	}


}
