package educate.facilities.module;

import lebah.portal.db.User;
import lebah.portal.db.UserData;

public class TestUserProfile {
	
	public static void main(String[] args) throws Exception {
		
		String userId = "gan";
		User user = UserData.getUser(userId);
		System.out.println(user.getName());
	}

}
