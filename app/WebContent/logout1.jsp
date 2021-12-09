<%

	String visitor = session.getAttribute("_portal_visitor") != null ? (String) session.getAttribute("_portal_visitor") : "anon";
	
	String campus = session.getAttribute("_portal_campus") != null ? (String) session.getAttribute("_portal_campus") : "";
	
	System.out.println("logout campus = " + campus);
	
	session.invalidate();
	
	String randomNo = lebah.db.UniqueID.getUID();
	
%>

<a href="c/campus=<%=campus%>">
Back to Portal <%=campus %>
</a>