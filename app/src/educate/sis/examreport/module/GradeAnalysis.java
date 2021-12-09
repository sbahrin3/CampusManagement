package educate.sis.examreport.module;

import educate.sis.exam.entity.FinalResult;

public class GradeAnalysis {

	private String noMatric = null;
	private String name = null;	
	private FinalResult finalresult = null;
	private double pngkBefore = 0;
	private double pngk = 0;
	private double pngs = 0;
	private String status = null;
		
	public String getNoMatric() {
		return noMatric;
	}
	public void setNoMatric(String noMatric) {
		this.noMatric = noMatric;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
	public FinalResult getFinalresult() {
		return finalresult;
	}
	public void setFinalresult(FinalResult finalresult) {
		this.finalresult = finalresult;
	}
	public double getPngkBefore() {
		return pngkBefore;
	}
	public void setPngkBefore(double pngkBefore) {
		this.pngkBefore = pngkBefore;
	}
	public double getPngk() {
		return pngk;
	}
	public void setPngk(double pngk) {
		this.pngk = pngk;
	}
	public double getPngs() {
		return pngs;
	}
	public void setPngs(double pngs) {
		this.pngs = pngs;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
