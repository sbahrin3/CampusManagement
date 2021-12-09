package org.slidemeeting.entity;

import javax.persistence.*;

import educate.lms2.entity.UserProfile;

@Entity
@Table(name="slide")
public class Slide {
	
	@Id
	@Column(length=50)
	private String id;
	@Column(length=255)
	private String location;
	@Column(length=150)
	private String title;
	
	@ManyToOne
	private UserProfile owner;
	
	public Slide() {
		setId(lebah.db.UniqueID.getUID());
	}

	private void setId(String uid) {
		id = uid;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getId() {
		return id;
	}

	public UserProfile getOwner() {
		return owner;
	}

	public void setOwner(UserProfile owner) {
		this.owner = owner;
	}
	
	

}
