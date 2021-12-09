package educate.lms2.entity;


import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name="lms2_user_profile")

public class UserProfile {
	
	@Id
	@Column(length=50)
	private String id;

	@Column(length=50)
	private String userId;
	@Column(length=150)
	private String userName;
	@Temporal(TemporalType.DATE)
	private Date birthDate;
	
	private String userPhotoName;
	private String userAvatarName;
	
	public UserProfile() {
		
	}
	
	public UserProfile(String userId) {
		setId(userId);
	}
	
	private void setId(String uid) {
		this.id = uid;
	}

	public Date getBirthDate() {
		return birthDate;
	}


	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
		this.id = userId;
	}

	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getId() {
		return id;
	}

	public String getUserAvatarName() {
		return userAvatarName;
	}

	public void setUserAvatarName(String userAvatarName) {
		this.userAvatarName = userAvatarName;
	}

	public String getUserPhotoName() {
		return userPhotoName;
	}

	public void setUserPhotoName(String userPhotoName) {
		this.userPhotoName = userPhotoName;
	}
	
	@Override
	public boolean equals(Object o) {
		UserProfile result = (UserProfile)o;
		if(result.id.equals(id)) return true;
		else return false;
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	

}
