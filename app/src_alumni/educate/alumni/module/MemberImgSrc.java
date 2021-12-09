package educate.alumni.module;

import javax.servlet.http.HttpServletRequest;

import educate.alumni.entity.AlumniMember;
import util.ImgSrcServlet;

public class MemberImgSrc extends ImgSrcServlet {
	
	@Override
	public byte[] getBinaryData(HttpServletRequest req) {

		String id = req.getParameter("id");
		String type = req.getParameter("type");
		AlumniMember member = db.find(AlumniMember.class, id);
		if ( "profile".equals(type)) {
			return member.getProfileImage();
		}
		else {
			return member.getAvatarImage();
		}
	}	

}
