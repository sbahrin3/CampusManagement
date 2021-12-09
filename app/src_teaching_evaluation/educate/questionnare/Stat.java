package educate.questionnare;

import java.text.DecimalFormat;

public class Stat {
	
	double mean;
	double median;
	double sd;
	int size;
	
	public double getMean() {
		return mean;
	}
	public void setMean(double mean) {
		this.mean = mean;
	}
	public double getMedian() {
		return median;
	}
	public void setMedian(double median) {
		this.median = median;
	}
	public double getSd() {
		return sd;
	}
	public void setSd(double sd) {
		this.sd = sd;
	}
	
	public String getMean_() {
		return new DecimalFormat("#.00").format(mean);
	}
	
	public String getMedian_() {
		return new DecimalFormat("#.00").format(median);
	}
	public String getSd_() {
		return new DecimalFormat("#.00").format(sd);
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	


}
