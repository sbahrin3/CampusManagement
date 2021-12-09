package u.sams;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

public class Migration {

	public static void main(String[] args) throws Exception {

		Database db = new Database("com.mysql.jdbc.Driver","jdbc:mysql://localhost:3306/portal2","root","");
		//Database db2 = new Database("org.hsqldb.jdbcDriver","jdbc:hsqldb:file:data/db_portal","sa","");

		getTables(db.getConnection());



	}

	public static void getTables(Connection conn) throws Exception {
		String TABLE_NAME = "TABLE_NAME";
		String TABLE_SCHEMA = "TABLE_SCHEM";
		String[] TABLE_TYPES = {"TABLE"};
		DatabaseMetaData dbmd = conn.getMetaData();

		ResultSet tables = dbmd.getTables(null, null, null, TABLE_TYPES);
		while (tables.next()) {
			String tableName = tables.getString(TABLE_NAME);
			System.out.println("TABLE NAME = " + tableName);
			getColumns(conn, tableName);
		}
	}

	public static void getColumns(Connection conn, String table) throws Exception {

		DatabaseMetaData metadata = conn.getMetaData();
		ResultSet resultSet = metadata.getColumns(null, null, table, null);
		while (resultSet.next()) {
			String name = resultSet.getString("COLUMN_NAME");
			String type = resultSet.getString("TYPE_NAME");
			int size = resultSet.getInt("COLUMN_SIZE");
			System.out.println(name + ", " + type + ", " + size);
		}
	}

}
