/**
 * @author Shaiful
 * @since Oct 5, 2009
 */
package educate.sis.tools;

import java.util.List;

import org.apache.velocity.Template;

import educate.admission.entity.Applicant;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.db.PersistenceManager;
import lebah.portal.velocity.VTemplate;

public class ApplicantProgramOfferCleanup extends VTemplate {
	private String className = this.getClass().getName();
	private final String TEMPLATE_SELECT_INTAKE = "vtl/sis/tools/select_intake.vm";
	private final String TEMPLATE_EXECUTE = "vtl/sis/tools/execute.vm";
	private PersistenceManager pm = new PersistenceManager();
	// The subject Id that needs to be change.
	private String subjectId = "1220703318094"; // MASTER OF EDUCATION.
	//private String intakeId = "1217468953675"; // 0908.
	
	@Override
	public Template doTemplate() throws Exception {
		String template = "";
		String command = getParam("command");
		if (command == null) {
			command = "";
		}
		System.out.println("["+className+"] command = "+command);
		
		if("".equals(command)) {
			List<Session> list = getSessionList();
			context.put("sessionList", list);
			template = this.TEMPLATE_SELECT_INTAKE;
			
		} else if("Execute".equals(command)) {
			String intakeId = request.getParameter("intake_id");
			if (intakeId == null || "".equals(intakeId)) {
				throw new Exception("Intake Id is missing. Unable to continue process.");
			} else {
				System.out.println("["+className+"] intakeId = "+intakeId);
				List<Applicant> applicantList = getApplicantList(intakeId);
				cleanUp(applicantList);
				
				Session session = (Session)pm.find(Session.class, intakeId);
				context.put("session", session);
				applicantList = getApplicantList(intakeId);
				context.put("applicantList", applicantList);
				context.put("totalApplicants", applicantList.size());
				template = this.TEMPLATE_EXECUTE;
			}
			
		} else if("Get Applicant".equals(command)) {
			String intakeId = request.getParameter("intake_id");
			//System.out.println("["+className+"] intakeId = "+intakeId);
			if(intakeId == null) {
				List<Applicant> applicantList = new java.util.ArrayList<Applicant>();
				context.put("applicantList", applicantList);
				context.put("totalApplicants", applicantList.size());
				context.put("intakeId", "");
				context.put("session", "");
			} else {
				Session session = (Session)pm.find(Session.class, intakeId);
				context.put("session", session);
				List<Applicant> applicantList = getApplicantList(intakeId);
				context.put("applicantList", applicantList);
				context.put("totalApplicants", applicantList.size());
				context.put("intakeId", intakeId);
			}
			template = this.TEMPLATE_EXECUTE;
		}
		
		return engine.getTemplate(template);
	}

	private synchronized void cleanUp(List<Applicant> list) throws Exception {
		for(Applicant applicant : list) {
			//System.out.println("["+className+"] Updating "+applicant.getBiodata().getName()+"...");

			if(applicant.getChoice1() != null && !subjectId.equals(applicant.getChoice1().getId())) {
				//System.out.println("["+className+"] with Choice 1.");
				updateOfferedProgram(applicant.getId(),applicant.getChoice1());
				
			} else {
				if(applicant.getChoice2() != null && !subjectId.equals(applicant.getChoice2().getId())) {
					//System.out.println("["+className+"] with Choice 2.");
					updateOfferedProgram(applicant.getId(),applicant.getChoice2());
					
				} else {
					if(applicant.getChoice3() != null && !subjectId.equals(applicant.getChoice3().getId())) {
						//System.out.println("["+className+"] with Choice 3.");
						updateOfferedProgram(applicant.getId(),applicant.getChoice3());
					} else {
						//System.out.println("["+className+"] No suitable choices available.");
					}
				}
			}
		}
	}
	
	private synchronized void updateOfferedProgram(String applicantId, Program offeredProgram) throws Exception {
		Applicant applicant = (Applicant)pm.find(Applicant.class).whereId(applicantId).forUpdate();
		applicant.setProgramOffered(offeredProgram);
		pm.update();
	}
	
	private List<Applicant> getApplicantList(String intakeId) throws Exception {
		String pql = "SELECT e FROM Applicant e " +
				//"WHERE e.programOffered.id = '"+subjectId+"' " +
				"WHERE e.intake.id = '" +intakeId+"' " +
				"ORDER BY e.intake.startDate";
		
		return pm.list(pql);
	}
	
	private List<Session> getSessionList() throws Exception {
		String pql = "SELECT e FROM Session e ORDER BY e.startDate";
		return pm.list(pql);
	}
}
