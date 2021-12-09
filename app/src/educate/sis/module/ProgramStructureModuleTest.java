package educate.sis.module;

import java.util.List;
import java.util.Set;

import educate.db.DbPersistence;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.ProgramStructure;
import educate.sis.struct.entity.SubjectPeriod;
import educate.sis.struct.entity.SubjectReg;

public class ProgramStructureModuleTest {
	
	public static void main(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();
		
		String programStructureId = "1380076908598";
		ProgramUtil pu = new ProgramUtil();
		ProgramStructure structure = db.find(ProgramStructure.class, programStructureId);
		List<Period> periods = structure.getProgram().getPeriodScheme().getFunctionalPeriodList();
		
		System.out.println(structure.getProgram().getName());
		
		System.out.println("period scheme = " + structure.getProgram().getPeriodScheme().getCode());
		for ( Period p : periods ) {
			System.out.println(p.getName());
			Set<SubjectReg> subjectRegs = pu.getSubjectRegs(structure, p.getId());
			if ( subjectRegs != null ) {
				for ( SubjectReg sr : subjectRegs ) {
					System.out.println(sr.getSubject().getCode());
				}
			}
		}
		
		//test2
		System.out.println("TEST 2 -----");
		List<SubjectPeriod> subjectPeriods = structure.getSubjectPeriodList();
		for ( SubjectPeriod sp : subjectPeriods ) {
			//System.out.println(sp.getPeriod().getName() + ", " + sp.getPeriod().getPeriodScheme() != null ? sp.getPeriod().getPeriodScheme().getCode() : "Period Scheme NULL");
			System.out.println(sp.getPeriod().getName() + " " + sp.getPeriod().getParent().getPeriodScheme().getCode());
		}
		
	}

}
