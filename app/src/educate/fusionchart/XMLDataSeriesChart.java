package educate.fusionchart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLDataSeriesChart {
	
	private String[] colorCodes = {"AFD8F8", "F6BD0F", "8BBA00", "FF8E46", "008E8E", "D64646", "8E468E", "588526", "B3AA00", "008ED6", "9D080D", "A186BE"};
	private int counter = 0;
	private List<String> categories = new ArrayList<String>();
	private List<String> series = new ArrayList<String>();
	private Map<String, List<String>> values = new HashMap<String, List<String>>();
	private String caption = "";
	private String yAxisName = "";
	private String xAxisName = "";
	
	
	public void addCategory(String name) {
		categories.add(name);
	}
	
	public void addSeries(String name) {
		
		series.add(name);
	}
	
	public void addValue(String seriesName, double value) {
		List<String> valueList = values.get(seriesName);
		if ( valueList == null ) valueList = new ArrayList<String>();
		valueList.add(Double.toString(value));
		values.put(seriesName, valueList);
	}
	
	public String getDataXML() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("<chart caption='" + caption + "' shownames='1' showvalues='0' decimals='0' numberPrefix='' xAxisName='" + xAxisName + "' yAxisName='" + yAxisName + "'>").append("\n");

		
		sb.append("<categories>").append("\n");
		for ( String cat : categories ) {
			sb.append("<category label='" + cat + "'/>").append("\n");
		}
		sb.append("</categories>").append("\n");
		
		for ( String seriesName : series ) {
			sb.append("<dataset seriesName='" + seriesName + "' color='" + colorCodes[counter++] + "' showValues='0'>").append("\n");
			List<String> valueList = values.get(seriesName);
			for ( String value : valueList ) {
				sb.append("<set value='" + value + "'/>").append("\n");
			}
			sb.append("</dataset>").append("\n");
			if ( counter > 11 ) counter = 0;
		}
		return sb.toString();
	}
	
	
	
	
	public void setCaption(String caption) {
		this.caption = caption;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public void setColorCodes(String[] colorCodes) {
		this.colorCodes = colorCodes;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public void setSeries(List<String> series) {
		this.series = series;
	}

	public void setValues(Map<String, List<String>> values) {
		this.values = values;
	}

	public void setXAxisName(String axisName) {
		xAxisName = axisName;
	}

	public void setYAxisName(String axisName) {
		yAxisName = axisName;
	}

	public static void main(String[] args) {
		XMLDataSeriesChart x = new XMLDataSeriesChart();
		
		//create categories
		x.addCategory("");
		
		//create series
		x.addSeries("Interpersonal");
		x.addSeries("Intrapersonal");
		x.addSeries("Linguistic");
		x.addSeries("Musical");
		
		//add values
		x.addValue("Interpersonal", 10);
		x.addValue("Interpersonal", 15);
		x.addValue("Intrapersonal", 20);
		x.addValue("Linguistic", 30);
		x.addValue("Musical", 40);
		
		System.out.println(x.getDataXML());
	}

}
