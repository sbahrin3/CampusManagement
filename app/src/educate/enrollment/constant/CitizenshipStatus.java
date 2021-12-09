/**
 * @author Shaiful
 * @since Nov 6, 2009
 */
package educate.enrollment.constant;
/*
 * History
 * -------
 * #	Date			Name				Remarks
 * ----	--------------	------------------	---------------------------------------------------
 * 1.	Nov 06, 2009	Shaiful Nizam		File created.
 */

public enum CitizenshipStatus {
	CITIZEN(1,"Citizen","WARGANEGARA"),
	NONE_CITIZEN(2,"None Citizen","BUKAN WARGANEGARA"),
	PERMENANT_RESIDENCE(3,"Permenant Residence","PENDUDUK TETAP"),
	TEMPORARY(4,"Temporary","SEMENTARA");
	
	private int code;
	private String eng;
	private String bm;
	
	CitizenshipStatus(int code, String eng, String bm) {
		this.code = code;
		this.eng = eng;
		this.bm = bm;
	}
	
	public int code() {
		return code;
	}
	
	public String eng() {
		return eng;
	}
	
	public String bm() {
		return bm;
	}
}
