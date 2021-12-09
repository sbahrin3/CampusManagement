package educate.chart;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import lebah.servlets.IServlet;

public class GPAPieChart implements IServlet {

	@Override
	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		String values = request.getParameter("values");
		StringTokenizer st = new StringTokenizer(values, ",");
		Map<Integer, String> legend = new HashMap<Integer, String>();
		legend.put(0, "3.75-4.0");
		legend.put(1, "3.5-3.75");
		legend.put(2, "3.0-3.5");
		legend.put(3, "2.5-3.0");
		legend.put(4, "2.0-2.5");
		legend.put(5, "0-2.0");		

		OutputStream out = response.getOutputStream(); 
		try {
			DefaultPieDataset chart = new DefaultPieDataset();
			int i = 0;
			while ( st.hasMoreTokens() ) {
				chart.setValue(legend.get(i), Integer.parseInt(st.nextToken()));
				i++;
			}
			JFreeChart mychart = ChartFactory.createPieChart("", chart, true,true,false);

			response.setContentType("image/png"); 
			ChartUtilities.writeChartAsPNG(out, mychart, 400, 300);
		} catch (Exception e) {
			System.err.println(e.toString()); 
		} finally {
			out.close();
		}



	}

}


