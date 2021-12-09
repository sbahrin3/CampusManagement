package educate.sis.exam.module;

public class SpecialSubjectStatus {
	
	public static SubjectStatus[] listOfStatus = {
		new SubjectStatus("CT", "Credit Transfer"), 
		new SubjectStatus("EX", "Exempted"),
		new SubjectStatus("CE", "Credit Earned"),
		new SubjectStatus("AU", "Audit")};
	
	public static boolean isMatch(String code) {
		for ( SubjectStatus s : listOfStatus ) {
			if ( code != null && code.equals(s.getCode())) return true;
		}
		return false;
	}
	
	public static String getDescription(String code) {
		for ( SubjectStatus s : listOfStatus ) {
			if ( code != null && code.equals(s.getCode())) return s.getDescription();
		}
		return "";
	}

	public static SubjectStatus[] getListOfStatus() {
		return listOfStatus;
	}

}
