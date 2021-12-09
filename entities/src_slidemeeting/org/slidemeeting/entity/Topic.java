package org.slidemeeting.entity;

import javax.persistence.*;


@Entity
@Table(name="topic")
public class Topic {
	
	@Id
	@Column(length=50)
	private String id;
	@Column(length=150)
	private String url;
	@Column(length=150)
	private String title;
	
	public Topic() {
		setId(lebah.db.UniqueID.getUID());
	}

	private void setId(String uid) {
		id = uid;
	}
	
	public String getId() {
		return id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	

}
