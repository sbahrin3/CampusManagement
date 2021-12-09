package educate.sis.print;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.List;

import org.apache.velocity.Template;

import educate.admission.entity.Applicant;
import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.RegistrationFeeTable;
import educate.sis.struct.entity.StudentAccomodation;
import educate.sis.struct.entity.StudentAccomodationStatus;
import lebah.portal.velocity.VTemplate;

public class StudentAggreementPrint extends VTemplate {
	
	String path = "apps/util/document_print/";
	DbPersistence db = new DbPersistence();
	
	@Override
	public Template doTemplate() throws Exception {
		
		String serverName = request.getServerName();
		int serverPort = request.getServerPort();
		String uri = request.getRequestURI();
		String s1 = uri.substring(1);
        String server = serverPort != 80 ? serverName + ":" + serverPort : serverName;
		String app = s1.substring(0, s1.indexOf("/"));   
		String http = request.getRequestURL().toString().substring(0, request.getRequestURL().toString().indexOf("://") + 3);
		context.put("applicationURL", http + server + "/" + app);
		
		setShowVM(false);
		context.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd"));
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		Template template = engine.getTemplate(getStudent());	
		return template;		
	}
	
	private String getStudent() throws Exception {
		String matricNo = request.getParameter("matric_no");
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		if ( student != null ) {
			Applicant applicant = student.getApplicant();
			if ( applicant == null ) {
				return path + "data_not_found.vm";
			}
			else {
				context.put("applicant", applicant); //admission data comes from applicant information
				context.put("student", student);
				
				//get registration fee table
				Program program = student.getProgram();
				List<Period> periods = program.getPeriodScheme().getFunctionalPeriodList();
				context.put("periods", periods);
				
				List<RegistrationFeeTable> feeTuitions = db.list("select f from RegistrationFeeTable f where f.feeType = 'tuition' and f.program.id = '" + program.getId() + "'");
				Hashtable tuitions = new Hashtable();
				context.put("tuitions", tuitions);
				double totalTuitionFee = 0.0d;
				for ( RegistrationFeeTable fee : feeTuitions ) {
					tuitions.put(fee.getPeriod().getId(), fee.getAmount());
					totalTuitionFee += fee.getAmount();
				}
				context.put("total_tuition_fee", totalTuitionFee);
				
				String address = "";
				if ( applicant.getAddress().getAddress1() != null && !"".equals(applicant.getAddress().getAddress1()) )
					address += applicant.getAddress().getAddress1() + ",";
				if ( applicant.getAddress().getAddress2() != null && !"".equals(applicant.getAddress().getAddress2()) )
					address += applicant.getAddress().getAddress2() + ",";
				if ( applicant.getAddress().getAddress3() != null && !"".equals(applicant.getAddress().getAddress3()) )
					address += applicant.getAddress().getAddress3() + ",";
				if ( applicant.getAddress().getAddress4() != null && !"".equals(applicant.getAddress().getAddress4()) )
					address += applicant.getAddress().getAddress4() + ",";
				if ( applicant.getAddress().getPostcode() != null && !"".equals(applicant.getAddress().getPostcode()) )
					address += applicant.getAddress().getPostcode() + " ";
				else
					address += "\n";
				if ( applicant.getAddress().getCity() != null && !"".equals(applicant.getAddress().getCity()) )
					address += applicant.getAddress().getCity() + ",";
				if ( applicant.getAddress().getState() != null && !"".equals(applicant.getAddress().getState().getName()) )
					address += applicant.getAddress().getState().getName() + ",";
				if ( applicant.getAddress().getCountry() != null && !"".equals(applicant.getAddress().getCountry().getName()) )
					address += applicant.getAddress().getCountry().getName() + "";
				
				context.put("address", address);
				
				//accomodation
				StudentAccomodationStatus accomodationStatus = (StudentAccomodationStatus) db.get("select s from StudentAccomodationStatus s where s.student.id = '" + student.getId() + "' and s.expired = 0 order by s.date desc");
				if ( accomodationStatus != null ) {
					StudentAccomodation accomodation = accomodationStatus.getAccomodation();
					context.put("accomodation", accomodation);
					
					double monthlyFee = accomodation.getMonthlyFee();
					double accomodationFee = accomodation.getTotalFee();
					context.put("accomodation_fee", accomodationFee);
				}
				else {
					context.remove("accomodation");
				}
				
				return path + "aggreement.vm";
			}

			
		}
		else 
			return path + "data_not_found.vm";
		
	}


}
