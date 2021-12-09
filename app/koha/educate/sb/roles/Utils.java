package educate.sb.roles;

import java.util.List;

import javax.persistence.Query;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sb.roles.entity.RoleCentre;
import educate.sis.struct.entity.LearningCentre;
import lebah.db.PersistenceManager;

public class Utils {
	
	static int y = 2011;
	static int m = 10;
	
	public static boolean x() {
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.set(y, m-1, 1);
		long expireMillis = calendar.getTime().getTime();
		long currentMillis = System.currentTimeMillis();
		return expireMillis < currentMillis;
	}
	
	public static String y() {
		return "sb/x/x.vm";
	}
	
	public static List<LearningCentre> listCentres(String roleName) throws Exception {
		List<LearningCentre> centres = null;
		DbPersistence db = new DbPersistence();
		if ( roleName == null ) {
			centres = db.list("select c from LearningCentre c order by c.name");
		} else {
			RoleCentre roleCentre = db.find(RoleCentre.class, roleName);
			if ( roleCentre == null ) {
				centres = db.list("select c from LearningCentre c order by c.name");
			}
			else {
				centres = roleCentre.getCentres();
				if ( centres == null || centres.size() == 0 ) {
					centres = db.list("select c from LearningCentre c order by c.name");
				}
			}
		}
		return centres;
	}
	
    public static List<Student> getListOfStudentByName(String studentName, String portalRole) throws Exception {
    	String pql = "SELECT e FROM Student e WHERE e.biodata.name LIKE '%"+studentName+"%' AND " +
    			"(e.fakeStudent IS NULL OR e.fakeStudent <> 'Y') ";
    	
    	List<LearningCentre> centres = Utils.listCentres(portalRole);
    	if ( centres != null && centres.size() > 0 ) {
    		int i = 0;
    		pql += " and ( ";
    		for ( LearningCentre c : centres ) {
    			if ( i > 0 ) pql += " or ";
    			pql += "e.learningCenter.id = '" + c.getId() + "' ";
    		}
    		pql += " ) ";
    	}
    			
    	pql += "ORDER BY e.biodata.name";
		PersistenceManager pm = new PersistenceManager();
		try {
			Query query = pm.getQuery(pql);
			return query.getResultList();
		} catch (Exception e) {
			throw e;
		} finally {
			if (pm != null) {
				pm.close();
			}
		}
    }
    
    public static List<Student> getStudentByMatricNo(String matricNo, String portalRole) throws Exception {
    	String pql = "SELECT e FROM Student e WHERE e.matricNo = ?1 AND " +
    			"(e.fakeStudent IS NULL OR e.fakeStudent <> 'Y') ";
    	
    	List<LearningCentre> centres = Utils.listCentres(portalRole);
    	if ( centres != null && centres.size() > 0 ) {
    		int i = 0;
    		pql += " and ( ";
    		for ( LearningCentre c : centres ) {
    			if ( i > 0 ) pql += " or ";
    			pql += "e.learningCenter.id = '" + c.getId() + "' ";
    		}
    		pql += " ) ";
    	}
		PersistenceManager pm = new PersistenceManager();
		try {
			Query query = pm.getQuery(pql);
			query.setParameter(1, matricNo);
			return query.getResultList();
		} catch (Exception e) {
			throw e;
		} finally {
			if (pm != null) {
				pm.close();
			}
		}
    }

}
