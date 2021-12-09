package educate.lms2.entity;

import javax.persistence.*;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalb
 * @version 1.0
 */
@Entity
@Table(name="lms2_assignment")
@DiscriminatorValue("A")
public class Assignment extends Posting {
	
	public Assignment() {
		setId(lebah.db.UniqueID.getUID());
		setType("assignment");
	}

}
