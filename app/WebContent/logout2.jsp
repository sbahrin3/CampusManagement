<%
String visitor = request.getParameter("visitor") != null ? request.getParameter("visitor") : "anon";
%>
<center>
You have successfully logout from the system!!
<br>
<a href="c/">
Back to Portal
</a>
</center>
<%
String randomNo = lebah.db.UniqueID.getUID();
String campus = session.getAttribute("_portal_campus");
response.sendRedirect("c/" + campus + "?logout"); 
%>