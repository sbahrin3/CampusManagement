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
@Table(name="struc_programstructure")
public class ProgramStructure {
	@Id
	private String id;
	@OneToOne
	private Program program;
	@OneToMany(cascade=CascadeType.ALL, mappedBy="programStructure")
	private Set<SubjectPeriod> subjectPeriod;
	@OneToOne
	private Session session;
	@ManyToOne
	private LearningCentre learningCenter;
	
	public ProgramStructure() {
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
	}

	public Set<SubjectPeriod> getSubjectPeriod() {
		return subjectPeriod;
	} 
	
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

	public void setSubjectPeriod(Set<SubjectPeriod> subjectPeriod) {
		for ( Iterator<SubjectPeriod> it = subjectPeriod.iterator(); it.hasNext(); ) {
			SubjectPeriod sp = it.next();
			sp.setProgramStructure(this);
		}
		this.subjectPeriod = subjectPeriod;
	}
	
	public void addSubjectPeriod(SubjectPeriod item) {
		if ( subjectPeriod == null ) subjectPeriod = new HashSet<SubjectPeriod>();
		item.setProgramStructure(this);
		subjectPeriod.remove(item); //remove old 
		subjectPeriod.add(item); //add current
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public Session getSession() {
		return session;
	}
	
	public List<SubjectPeriod> getSubjectPeriodList(){
		List<SubjectPeriod> list = new ArrayList();
		list.addAll(subjectPeriod);
		Collections.sort(list);
		return list;
	}
	
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
	
	public Set<Subject> getOfferedSubjects(String periodId) {
		Set<Subject> offeredSubjects = new HashSet<Subject>();
		if ( subjectPeriod != null ) {
			for ( SubjectPeriod sp : subjectPeriod ) {
				if ( sp.getPeriod() != null && sp.getPeriod().getId().equals(periodId)) {
					Set<SubjectReg> subjectRegs = sp.getSubjectRegs();
					for ( SubjectReg sr : subjectRegs ) {
						if ( sr.getOffered() ) {
							offeredSubjects.add(sr.getSubject());
						}
					}
					return offeredSubjects;
				}
			}
		}
		return null;
	}
	
	public Set<SubjectReg> getSubjectRegisters(String periodId) {
		if ( subjectPeriod != null ) {
			for ( SubjectPeriod sp : subjectPeriod ) {
				if ( sp.getPeriod() != null && sp.getPeriod().getId().equals(periodId)) {
					return sp.getSubjectRegs();
				}
			}
		}
		return null;
	}
	
	
	public Set<Subject> getCoreSubjects(String periodId) {
		if ( subjectPeriod != null ) {
			for ( SubjectPeriod sp : subjectPeriod ) {
				if ( sp.getPeriod() != null && sp.getPeriod().getId().equals(periodId)) {
					
					sp.getSubjectRegs();
					
					return sp.getSubjects();
				}
			}
		}
		return null;
	}
	
	public boolean hasChild() {
		return subjectPeriod != null ? subjectPeriod.size() > 0 : false;
	}

	public LearningCentre getLearningCenter() {
		return learningCenter;
	}

	public void setLearningCenter(LearningCentre learningCenter) {
		this.learningCenter = learningCenter;
	}
	
}
