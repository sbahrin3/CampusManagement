package educate.sis.module;

import javax.servlet.http.HttpServletRequest;

import educate.hostel.entity.Floor;
import util.ImgSrcServlet;

public class FloorPlanImgServlet extends ImgSrcServlet {
	
	@Override
	public byte[] getBinaryData(HttpServletRequest req) {
		String id = req.getParameter("id");
		Floor floor = db.find(Floor.class, id);
		return floor.getFloorPlanImage();
	}	

}
