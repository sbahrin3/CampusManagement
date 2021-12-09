package educate.lms2.entity;

import javax.persistence.*;


/**
 * 
 * @author Shamsul Bahrin Abd Mutalb
 * @version 1.0
 */
@Entity
@Table(name="lms2_discussion")
@DiscriminatorValue("D")
public class Discussion extends Posting {

	public Discussion() {
		setId(lebah.db.UniqueID.getUID());
		setType("discussion");
	}
}
