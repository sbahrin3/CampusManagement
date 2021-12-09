package educate.sis.exam.entity;
import educate.sis.struct.entity.*;
import javax.persistence.*;

@Entity
@Table(name="exam_gradingscheme")
public class GradingScheme {
	
	@Id
	private String id;
	private String name;
	
	@OneToOne
	private Program program;
	
	//@OneToMany
	//private Set<Grade> grade;
	

}
