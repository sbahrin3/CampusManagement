package educate.sis.module;

import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.StudentStatus;
import lebah.portal.action.AjaxModule;

public class ListDeferedStudentsModule extends AjaxModule {
	
	
	public static void main(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();
		String sql = "";
		sql = "select st from Student s join s.status st where st.type.code = 'DES'";
		List<StudentStatus> statuses = db.list(sql);
		for ( StudentStatus status : statuses ) {
			System.out.println(status.getStudent().getMatricNo() + ", " + status.getStudent().getBiodata().getName() + ", " + status.getSession().getName());
		}
		
	}

	
	@Override
	public String doAction() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
