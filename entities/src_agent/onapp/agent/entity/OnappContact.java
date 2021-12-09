/**
 * 
 */
package onapp.agent.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */

@Entity
@Table(name = "onapp_contact")
public class OnappContact {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=100)
	private String name;
	@Column(length=20)
	private String phoneNo;
	
	@ManyToOne @JoinColumn(name="agent_id")
	private OnappAgent agent;
	
	public OnappContact() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public OnappAgent getAgent() {
		return agent;
	}

	public void setAgent(OnappAgent agent) {
		this.agent = agent;
	}
	
	

}
