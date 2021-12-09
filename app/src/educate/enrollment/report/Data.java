package educate.enrollment.report;

import educate.enrollment.entity.StudentStatus;
import educate.sis.finance.entity.SponsorStudent;

public class Data {
	
	private SponsorStudent sponsorStudent;
	private StudentStatus status;
	
	public Data() {
		
	}
	
	public Data(StudentStatus status, SponsorStudent sponsorStudent) {
		this.status = status;
		this.sponsorStudent = sponsorStudent;
	}

	public SponsorStudent getSponsorStudent() {
		return sponsorStudent;
	}

	public void setSponsorStudent(SponsorStudent sponsorStudent) {
		this.sponsorStudent = sponsorStudent;
	}

	public StudentStatus getStatus() {
		return status;
	}

	public void setStatus(StudentStatus status) {
		this.status = status;
	}
	

}
