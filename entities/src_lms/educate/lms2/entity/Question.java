package educate.lms2.entity;

import javax.persistence.*;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalb
 * @version 1.0
 */
@Entity
@Table(name="lms2_question")
@DiscriminatorValue("Q")
public class Question  extends Posting {
	
	public Question() {
		setId(lebah.db.UniqueID.getUID());
		setType("discussion");
	}

}
