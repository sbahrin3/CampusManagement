package educate.admission.module;

import javax.servlet.http.HttpServletRequest;

import educate.enrollment.entity.Student;
import util.ImgSrcServlet;

public class PhotoImgSrc extends ImgSrcServlet {
	
	@Override
	public byte[] getBinaryData(HttpServletRequest req) {
		String id = req.getParameter("studentId");
		Student student = db.find(Student.class, id);
		return student.getPhoto();
	}	

}
