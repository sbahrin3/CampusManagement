<%@page import="java.sql.SQLException"%>
<%@page import="lebah.db.Db"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.util.Vector"%>
<html>
<body>
<%
	String[] oldCat = new String[9];
	oldCat[0] = "mes__announce_admin";
	oldCat[1] = "mes__announce_SGS";
	oldCat[2] = "mes__announ_school_education";
	oldCat[3] = "mes__announ_school_management";
	oldCat[4] = "mes__school_announcement";
	oldCat[5] = "mes__staff_announcement_2";
	oldCat[6] = "mes__announce_SOIT";
	oldCat[7] = "mes__announce_SVTS";
	oldCat[8] = "mes__announcement_af";
	
	String[] newCat = new String[9];
	newCat[0] = "announcement_admin";
	newCat[1] = "announcement_SOGS";
	newCat[2] = "announcement_SOECS";
	newCat[3] = "announcement_SOM";
	newCat[4] = "announcement_school";
	newCat[5] = "announcement_staff";
	newCat[6] = "announcement_SOIT";
	newCat[7] = "announcement_SVTS";
	newCat[8] = "announcement_AF2";
	
	Db db = null;
	Connection conn = null;
	PreparedStatement psSelect = null;
	PreparedStatement psInsert = null;
	ResultSet rs = null;
	Vector<String[]> rows = null; 
	String sqlSelect = "SELECT posted_date, title, message_text, member_id FROM forum WHERE category_id = ? AND title <> 'ACTIVATION' AND title <> 'test' AND is_delete = 0 ORDER BY id";
	String sqlInsert = "INSERT INTO portal_announcement (category, headline, content, author, date_of_post) VALUES (?,?,?,?,?)";
	//String sqlCount = "SELECT count(id) FROM forum WHERE category_id = ?";
	try {
		db = new Db();
		conn = db.getConnection();
		psSelect = conn.prepareStatement(sqlSelect);
		psInsert = conn.prepareStatement(sqlInsert);
		
		for (int i = 0; i < oldCat.length; i++) {
%>
[<%=oldCat[i] %>] Begin Select FROM forum.<br>
<%
			psSelect.setString(1, oldCat[i]);
			rs = psSelect.executeQuery();
			
			// Reset rows to 0;
			rows = new Vector<String[]>();
			
			while (rs.next()) {
				String[] col = new String[5];
				col[0] = rs.getString("posted_date");
				col[1] = rs.getString("title");
				col[2] = rs.getString("message_text");
				col[3] = rs.getString("member_id");
				col[4] = newCat[i];
				
				rows.add(col);
			}
%>
[<%=oldCat[i] %>] End Select<br>
[<%=oldCat[i] %>] <%=rows.size()%> row(s) found.<br><br>
<%
			if (!rows.isEmpty()) {
%>
[<%=newCat[i] %>] Begin Insert INTO portal_announcement.<br>
<%
				// Start INSERT.
				for (int j = 0; j < rows.size(); j++) {
					String[] col = rows.get(j);
					
					psInsert.setString(1, col[4]);
					psInsert.setString(2, col[1]);
					psInsert.setString(3, col[2]);
					psInsert.setString(4, col[3]);
					psInsert.setString(5, col[0]);
					psInsert.executeUpdate();
				}
%>
[<%=newCat[i] %>] End Insert<br><br>
<%
			}
		}
		
	} catch(SQLException e) {
		System.out.println("[announcement_data_migration.jsp] Error while doing SELECT statement."+e.getMessage());
	}
%>
</body>
</html>