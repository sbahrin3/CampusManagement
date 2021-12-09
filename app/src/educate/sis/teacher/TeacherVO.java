package educate.sis.teacher;

import educate.sis.struct.entity.Teacher;

public class TeacherVO {
	
	public String username;
	public String password;
	public String code;
	public String name;
	public String email;
	public String avatar;
	public Teacher teacher;
	public boolean hasEntity;
	public boolean hasLogin;
	
	
	
	public String getPassword() {
		return password;
	}
	public String getAvatar() {
		return avatar;
	}
	public String getEmail() {
		return email;
	}
	public boolean isHasEntity() {
		return hasEntity;
	}
	public boolean isHasLogin() {
		return hasLogin;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public Teacher getTeacher() {
		return teacher;
	}
	public String getUsername() {
		return username;
	}

}
