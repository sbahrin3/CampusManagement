package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.List;

import educate.admission.entity.Applicant;
import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.RegistrationFeeTable;
import educate.sis.struct.entity.StudentAccomodation;
import educate.sis.struct.entity.StudentAccomodationStatus;
import lebah.portal.action.LebahModule;

public class PrintStudentAggreementModule  extends LebahModule {
	
	String path = "apps/util/aggreement_form/";
	DbPersistence db = new DbPersistence();
	
	@Override
	public void preProcess() {
		context.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd"));
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
	}

	@Override
	public String start() {
		try {
			return getStudent();
		} catch (Exception e ) {
			
		}
		return path + "start.vm";
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
					if ( fee.getPeriod() != null ) {
						tuitions.put(fee.getPeriod().getId(), fee.getAmount());
						totalTuitionFee += fee.getAmount();
					}
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
				
				return path + "aggreement_form.vm";
			}

			
		}
		else 
			return path + "data_not_found.vm";
		
	}


}
