package educate.sis.struct.entity;


import java.util.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="struc_default_programstructure")

public class DefaultProgramStructure extends ProgramStructure {
	@Id
	private String id;
	@OneToOne
	private Program program;
	@OneToMany(cascade=CascadeType.ALL)
	private Set<SubjectPeriod> subjectPeriod;
	@OneToOne
	private Session session;
	@ManyToOne
	private LearningCentre learningCenter;
	
	public DefaultProgramStructure() {
		setId(lebah.db.UniqueID.getUID());
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public Program getProgram() {
		return program;
	}

	@Override
	public void setProgram(Program program) {
		this.program = program;
	}

	@Override
	public Set<SubjectPeriod> getSubjectPeriod() {
		return subjectPeriod;
	} 
	
	@Override
	public SubjectPeriod getSubjectPeriodByPeriodId(String periodId) {
		if ( subjectPeriod != null ) {
			for ( SubjectPeriod sp : subjectPeriod ) {
				if ( sp.getPeriod() != null && sp.getPeriod().getId().equals(periodId)) {
					return sp;
				}
			}
		}
		return null;
	}

	@Override
	public void setSubjectPeriod(Set<SubjectPeriod> subjectPeriod) {
		for ( Iterator<SubjectPeriod> it = subjectPeriod.iterator(); it.hasNext(); ) {
			SubjectPeriod sp = it.next();
			sp.setProgramStructure(this);
		}
		this.subjectPeriod = subjectPeriod;
	}
	
	@Override
	public void addSubjectPeriod(SubjectPeriod item) {
		if ( subjectPeriod == null ) subjectPeriod = new HashSet<SubjectPeriod>();
		item.setProgramStructure(this);
		subjectPeriod.remove(item); //remove old 
		subjectPeriod.add(item); //add current
	}

	@Override
	public void setSession(Session session) {
		this.session = session;
	}

	@Override
	public Session getSession() {
		return session;
	}
	
	@Override
	public List<SubjectPeriod> getSubjectPeriodList(){
		List<SubjectPeriod> list = new ArrayList();
		list.addAll(subjectPeriod);
		Collections.sort(list);
		return list;
	}
	
	@Override
	public Set<Subject> getSubjects(String periodId) {
		if ( subjectPeriod != null ) {
			for ( SubjectPeriod sp : subjectPeriod ) {
				if ( sp.getPeriod() != null && sp.getPeriod().getId().equals(periodId)) {
					return sp.getSubjects();
				}
			}
		}
		return null;
	}
	
	@Override
	public boolean hasChild() {
		return subjectPeriod != null ? subjectPeriod.size() > 0 : false;
	}

	@Override
	public LearningCentre getLearningCenter() {
		return learningCenter;
	}

	@Override
	public void setLearningCenter(LearningCentre learningCenter) {
		this.learningCenter = learningCenter;
	}

}
