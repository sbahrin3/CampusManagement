package educate.sis.exam.entity;

import java.io.Serializable;

public class FinalResultPK implements Serializable {
	private String studentId;
	private String sessionCode;
	public FinalResultPK(){
		
	}
	public FinalResultPK(String matricNo,String sessionCode){
		this.studentId = matricNo;
		this.sessionCode = sessionCode;
	}
	@Override
	public boolean equals(Object o){
		return((o instanceof FinalResultPK) && studentId == ((FinalResultPK)o).getStudentId() && sessionCode == ((FinalResultPK)o).getSessionCode());
			
	}
	public String getStudentId() {
		return studentId;
	}
	public String getSessionCode() {
		return sessionCode;
	}
	@Override
	public int hashCode(){
		return hash(getStudentId()+"-"+getSessionCode());
	}
	private static int hash(String s) {  
	        int h = 0;  
		        int len = s.length();  
	         for (int i = 0; i < len; i++)  
	             h = 31 * h + s.charAt(i);  
	         return h;  
	} 
	
}
