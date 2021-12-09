package educate.db;

import lebah.jpa.DbLebahPersistence2;
import lebah.jpa.Properties;


public class DbPersistence extends DbLebahPersistence2 {

	
	public DbPersistence() {
		super(Properties.url(lebah.util.Util.getInstance().getCampusFromSession()), Properties.user("all"), Properties.password("all"));

	}
	

	public DbPersistence(String urlkey, String userkey, String passwordkey) {
		super(Properties.url(urlkey), Properties.user(userkey), Properties.password(passwordkey));
		
	}
	
	public DbPersistence(String key) {
		super(Properties.url(key), Properties.user("all"), Properties.password("all"));
		
	}

}
