package educate.chat;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lebah.servlets.IServlet;

public class CometServlet implements IServlet {

	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		System.out.println("Invoked!");
		
	}

}
