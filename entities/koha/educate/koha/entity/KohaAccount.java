package educate.koha.entity;


import javax.persistence.*;

import educate.enrollment.entity.Student;

@Entity
@Table(name="opac_account")
public class KohaAccount {
	
	@Id
	@Column(length=50)
	private String id;  //this is userId
	@Column(length=50)
	private String password;
	@OneToOne
	private Student student;
	
	public KohaAccount() {
		
	}
	
	public KohaAccount(String userId) {
		this.id = userId;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	
	
	

}
