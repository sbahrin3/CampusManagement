package educate.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import lebah.db.Database;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class HsqlInsertDataModule extends LebahModule {
	
	private String path = "hsql/";
	
	@Override
	public String start() {

		return path + "start.vm";
	}
	
	@Command("runSQL")
	public String runSQL() throws Exception {
		try {
			readSql();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return path + "runSQL.vm";
		
	}
	
	public void copyToHsql() throws Exception {
		
		Database d = new Database("org.hsqldb.jdbcDriver", "jdbc:hsqldb:file:data/db_educate", "sa", "");
		
	}
	
	private void readSql() throws Exception {
		
		Database d = new Database("org.hsqldb.jdbcDriver", "jdbc:hsqldb:file:data/db_educate", "sa", "");
		
		//String filename = "C:/MySQL5/bin/tbl_struc_period_data.sql";
		String filename = getParam("filename");
		FileInputStream fstream = new FileInputStream(filename);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String sqlDisplay = "";
		context.put("sql", sqlDisplay);
		String strLine;
		while ((strLine = br.readLine()) != null)   {
			System.out.println (strLine);
			sqlDisplay += strLine + "<br>";
			d.getStatement().executeUpdate(strLine);
		}
		in.close();
		
	}
	
	
	

}
