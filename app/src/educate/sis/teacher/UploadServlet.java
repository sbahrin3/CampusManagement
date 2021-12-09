package educate.sis.teacher;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UploadServlet extends HttpServlet {
	
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String subDir = request.getParameter("sub_dir");
		String uploadDir = "d:/upload_location";
		try {
//			String filename = "";
//			PrintWriter out = response.getWriter();
			List<String> files = FileUploadUtil.uploadByServletRequest(uploadDir + "/" + subDir, request);
//			if ( files.size() > 0 ) filename = files.get(0);
//			System.out.println(filename);
//			filename = filename.substring(uploadDir.length());
//			System.out.println(filename);
//			String txt = "<script>top.done_upload('" + filename + "');</script>";
//			out.print(txt);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

}
