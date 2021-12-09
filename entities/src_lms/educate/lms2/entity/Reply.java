package educate.lms2.entity;

import javax.persistence.*;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalb
 * @version 1.0
 */
@Entity
@Table(name="lms2_reply")
@DiscriminatorValue("R")
public class Reply extends Posting {
	
	@ManyToOne(cascade=CascadeType.PERSIST)
	private Posting posting;
	
	public Reply() {
		setId(lebah.db.UniqueID.getUID());
		setType("reply");
	}

	public Posting getPosting() {
		return posting;
	}

	public void setPosting(Posting posting) {
		this.posting = posting;
	}

}
