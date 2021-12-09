package educate.enrollment.report;

public class Record {
	
	public String s1, s2, s3, s4, s5;
	public long cnt;

	
	public Record(String s1) {
		this.s1 = s1;
	}
	
	public Record(String s1, long cnt ) {
		this.s1 = s1;
		this.cnt = cnt;
	}
	
	public Record(String s1, String s2, long cnt) {
		this.s1 = s1;
		this.s2 = s2;
		this.cnt = cnt;
	}
	
	public Record(String s1, String s2) {
		this.s1 = s1;
		this.s2 = s2;
	}
	
	public Record(String s1, String s2, String s3) {
		this.s1 = s1;
		this.s2 = s2;
		this.s3 = s3;
	}
	
	public Record(String s1, String s2, String s3, String s4) {
		this.s1 = s1;
		this.s2 = s2;
		this.s3 = s3;
		this.s4 = s4;
	}
	
	public Record(String s1, String s2, String s3, String s4, String s5) {
		this.s1 = s1;
		this.s2 = s2;
		this.s3 = s3;
		this.s4 = s4;
		this.s5 = s5;
	}

	public String getS1() {
		return s1;
	}

	public String getS2() {
		return s2;
	}

	public String getS3() {
		return s3;
	}

	public String getS4() {
		return s4;
	}

	public String getS5() {
		return s5;
	}

	public long getCnt() {
		return cnt;
	}

	public void setCnt(long cnt) {
		this.cnt = cnt;
	}

	
	

}
