package test;

import educate.db.DbPersistence;

public class Testhsql {
	
	public static void main(String[] args) throws Exception {
		
		//Db db = new Db();
		
		
		DbPersistence db = new DbPersistence();
		
		db.list("select s from Student s where s.program.id = 'ABC'");
		
	}

}
