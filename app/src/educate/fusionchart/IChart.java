package educate.fusionchart;

public interface IChart {

	String getDataXML();
	void add(String name, double value, String color);
	void add(String name, double value);
	void setCaption(String caption);
	void setDecimalPrecision(String decimalPrecision);
	void setFormatNumberScale(String formatNumberScale);
	void setXAxisName(String axisName);
	void setYAxisName(String axisName);
}
