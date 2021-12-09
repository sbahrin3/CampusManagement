package educate.fusionchart;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Chart {
	
	private String[] colorCodes = {
			"AFD8F8", "F6BD0F", "8BBA00", "FF8E46", "008E8E", 
			"D64646", "8E468E", "588526", "B3AA00", "008ED6", 
			"9D080D", "A186BE", "AFD8F8", "F6BD0F", "8BBA00", 
			"FF8E46", "008E8E", "D64646", "8E468E", "588526", 
			"B3AA00", "008ED6", "9D080D", "A186BE", 
			"AFD8F8", "F6BD0F", "8BBA00", "FF8E46", "008E8E", 
			"D64646", "8E468E", "588526", "B3AA00", "008ED6", 
			"9D080D", "A186BE", "AFD8F8", "F6BD0F", "8BBA00", 
			"FF8E46", "008E8E", "D64646", "8E468E", "588526", 
			"B3AA00", "008ED6", "9D080D", "A186BE",
			"AFD8F8", "F6BD0F", "8BBA00", "FF8E46", "008E8E", 
			"D64646", "8E468E", "588526", "B3AA00", "008ED6", 
			"9D080D", "A186BE", "AFD8F8", "F6BD0F", "8BBA00", 
			"FF8E46", "008E8E", "D64646", "8E468E", "588526", 
			"B3AA00", "008ED6", "9D080D", "A186BE",
			"AFD8F8", "F6BD0F", "8BBA00", "FF8E46", "008E8E", 
			"D64646", "8E468E", "588526", "B3AA00", "008ED6", 
			"9D080D", "A186BE", "AFD8F8", "F6BD0F", "8BBA00", 
			"FF8E46", "008E8E", "D64646", "8E468E", "588526", 
			"B3AA00", "008ED6", "9D080D", "A186BE", 
			"AFD8F8", "F6BD0F", "8BBA00", "FF8E46", "008E8E", 
			"D64646", "8E468E", "588526", "B3AA00", "008ED6", 
			"9D080D", "A186BE", "AFD8F8", "F6BD0F", "8BBA00", 
			"FF8E46", "008E8E", "D64646", "8E468E", "588526", 
			"B3AA00", "008ED6", "9D080D", "A186BE",
			"AFD8F8", "F6BD0F", "8BBA00", "FF8E46", "008E8E", 
			"D64646", "8E468E", "588526", "B3AA00", "008ED6", 
			"9D080D", "A186BE", "AFD8F8", "F6BD0F", "8BBA00", 
			"FF8E46", "008E8E", "D64646", "8E468E", "588526", 
			"B3AA00", "008ED6", "9D080D", "A186BE",
			"AFD8F8", "F6BD0F", "8BBA00", "FF8E46", "008E8E", 
			"D64646", "8E468E", "588526", "B3AA00", "008ED6", 
			"9D080D", "A186BE", "AFD8F8", "F6BD0F", "8BBA00", 
			"FF8E46", "008E8E", "D64646", "8E468E", "588526", 
			"B3AA00", "008ED6", "9D080D", "A186BE",
			"AFD8F8", "F6BD0F", "8BBA00", "FF8E46", "008E8E", 
			"D64646", "8E468E", "588526", "B3AA00", "008ED6", 
			"9D080D", "A186BE", "AFD8F8", "F6BD0F", "8BBA00", 
			"FF8E46", "008E8E", "D64646", "8E468E", "588526", 
			"B3AA00", "008ED6", "9D080D", "A186BE"
	};
	private int counter = 0; 
	private List<String> values = new ArrayList<String>();
	
	private String id = "";
	private int width = 500, height = 400;
	private int dataSize = 0;
	private String fusionChartPath = "/fusionchart";
	private String fusionChartName = "";
	private String caption = "";
	private String xAxisName = "";
	private String yAxisName = "";
	private String decimalPrecision = "0";
	private String formatNumberScale = "0";
	private String elementName = "graph";
	private String functionCall = "";
	private String commandCall = "";
	private Vector<Item> items = null;
	private boolean useEnterprise = false;
	
	
	public Chart() {
		setId(lebah.db.UniqueID.getUID());
		elementName = "graph";
	}
	
	public Chart(boolean e) {
		setId(lebah.db.UniqueID.getUID());
		useEnterprise = e;
		if (e) elementName = "chart";
		else elementName = "graph";
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public String getFusionChartPath() {
		return fusionChartPath;
	}

	public void setFusionChartPath(String fusionChartPath) {
		this.fusionChartPath = fusionChartPath;
	}

	public String getFusionChartName() {
		return fusionChartName;
	}

	public void setFusionChartName(String fusionChartName) {
		this.fusionChartName = fusionChartName;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public String getFusionChart() {
		if ( !"".equals(fusionChartName)) return fusionChartName;
		
		if ( !useEnterprise ) return "FCF_Column3D.swf";
		else return "Column3D.swf";
	}
	
	public int getDataSize() {
		return dataSize;
	}

	private String beginChart() {
		String s =
		"<" + elementName + " caption='" + caption + "'" +
		" xAxisName='" + xAxisName + "'" +
		" yAxisName='" + yAxisName + "'" +
		" decimalPrecision='" + decimalPrecision + "'" +
		" formatNumberScale='" + formatNumberScale + "'>";
		return s;
	}
	
	private String endChart() {
		return "</" + elementName + ">";
	}
	
	private void _add(String name, double d, String link, String color) {
		dataSize++;
		String value = "0";
		try {
			value = Double.toString(d);
		} catch ( Exception e ) {}
		if ( !"".equals(functionCall)) link = functionCall;
		values.add("<set name='" + name + "' value='" + value + "' link='" + link + "' color='" + color + "' />");
	}
	

	public void add(String name, double d, String link, String color) {
		if ( items == null ) items = new Vector<Item>();
		Item item = new Item();
		item.name = name;
		item.value = d;
		item.link = link;
		item.color = color;
		items.addElement(item);
	}
	
	public void add(String name, double d, String link) {
		add(name, d, link, colorCodes[counter]);
		counter++;
	}
	
	
	public String getDataXML() {
		StringBuffer s = new StringBuffer(beginChart()).append("\n");
		for ( String value : values ) s.append(value).append("\n");
		s.append(endChart()).append("\n");
		return s.toString();
	}
	
	public String getFusionChartObject() {
		if ( items != null ) {
			for ( Item item : items ) {
				_add(item.name, item.value, item.link, item.color);
			}
		}
		if ( height == 0 ) {
			height = (dataSize * 30) + 60;
		}
		String path = fusionChartPath + "/" + getFusionChart();
		String s =
			"<OBJECT classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" codebase=http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,0,0\" width=\"" + width + "\" height=\"" + height + "\" id=\"" + id + "\" > ";
			s += "<param name=\"movie\" value=\"" + path + "\" /> ";
			s += "<param name=\"FlashVars\" value=\"&dataXML=" + getDataXML() + "\">";
			s += "<param name=\"quality\" value=\"high\" />";
			s += "<embed src=\"" + path + "\" flashVars=\"&dataXML=" + getDataXML() + "\" quality=\"high\" width=\"" + width + "\" height=\"" + height + "\" name=\"" + id + "\" type=\"application/x-shockwave-flash\" pluginspage=\"http://www.macromedia.com/go/getflashplayer\" />";
			s += "</object>";
		return s;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getDecimalPrecision() {
		return decimalPrecision;
	}

	public void setDecimalPrecision(String decimalPrecision) {
		this.decimalPrecision = decimalPrecision;
	}

	public String getFormatNumberScale() {
		return formatNumberScale;
	}

	public void setFormatNumberScale(String formatNumberScale) {
		this.formatNumberScale = formatNumberScale;
	}

	public String getXAxisName() {
		return xAxisName;
	}

	public void setXAxisName(String axisName) {
		xAxisName = axisName;
	}

	public String getYAxisName() {
		return yAxisName;
	}

	public void setYAxisName(String axisName) {
		yAxisName = axisName;
	}

	public String getFunctionCall() {
		return functionCall;
	}

	public void setFunctionCall(String functionCall) {
		this.functionCall = functionCall;
	}
	
	
	
	class Item {
		String name;
		double value;
		String link;
		String color;
	}



	public String getCommandCall() {
		return commandCall;
	}

	public void setCommandCall(String commandCall) {
		this.commandCall = commandCall;
	}
	
	

}
