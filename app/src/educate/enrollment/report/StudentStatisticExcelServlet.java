package educate.enrollment.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import educate.db.DbPersistence;
import educate.enrollment.entity.StudentStatus;
import educate.poi.Excel1;
import educate.sis.finance.entity.SponsorStudent;
import lebah.servlets.IServlet;

public class StudentStatisticExcelServlet  implements IServlet {
	private DbPersistence db = new DbPersistence();
	
	@Override
	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String filename = "student_list";
		response.setContentType("application/x-msdownload");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xls\"");
		
		String _filterSponsor = request.getParameter("filterSponsor") != null ? request.getParameter("filterSponsor") : "false";
		boolean filterSponsor = "true".equals(_filterSponsor);
		
		String[] fieldNames = (String[]) request.getSession().getAttribute("fieldNames");
		List<StudentStatus> students = (List) request.getSession().getAttribute("students");
		
		List<String> headers = new ArrayList<String>();
		for ( String fieldName : fieldNames ) {
			if ( "sponsor".equals(fieldName) )  { if ( filterSponsor ) headers.add(fieldName); }
			else headers.add(fieldName);
		}
		
		List<List> rows =  new ArrayList<List>();
		
		StudentStatus s = null;
		SponsorStudent sp = null;
		educate.enrollment.report.Data data = null;
		for ( Object o : students ) {
			
			
			if ( !filterSponsor ) {
				s = (StudentStatus) o;
			} else {
				data = (educate.enrollment.report.Data) o;
				s = data.getStatus();
				sp = data.getSponsorStudent();
			}
			
			List cols = new ArrayList();
			String value = "";
			for ( String fieldName : fieldNames ) {
				if ( fieldName.equals("matricNo" )) value = s.getStudent().getMatricNo();
				else if( fieldName.equals("name" )) value = s.getStudent().getBiodata().getName(); 
				else if( fieldName.equals("icno" )) value= s.getStudent().getBiodata().getIcno(); 
				else if( fieldName.equals("gender" )) value = s.getStudent().getBiodata().getGender() != null ? s.getStudent().getBiodata().getGender().getCode() : "";
				else if( fieldName.equals("telephoneNo" )) value = s.getStudent().getBiodata().getTelephoneNo(); 
				else if( fieldName.equals("mobileNo" )) value = s.getStudent().getBiodata().getMobileNo(); 
				else if( fieldName.equals("email" )) value = s.getStudent().getBiodata().getEmail(); 
				else if( fieldName.equals("race" )) value = s.getStudent().getBiodata().getRace() != null ? s.getStudent().getBiodata().getRace().getName() : ""; 
				else if( fieldName.equals("religion" )) value =  s.getStudent().getBiodata().getReligion() != null ? s.getStudent().getBiodata().getReligion().getName(): ""; 
				else if( fieldName.equals("nationality" )) value = s.getStudent().getBiodata().getNationality() != null ? s.getStudent().getBiodata().getNationality().getName() : ""; 
				else if( fieldName.equals("program" )) value = s.getStudent().getProgram().getName(); 
				else if( fieldName.equals("intake" )) value = s.getStudent().getIntake() != null ? s.getStudent().getIntake().getName() : ""; 
				else if( fieldName.equals("centre" )) value = s.getStudent().getLearningCenter() != null ? s.getStudent().getLearningCenter().getName() : ""; 
				else if( fieldName.equals("period" )) {
					value = s.getPeriod() != null ? s.getPeriod().getCode() != null ? s.getPeriod().getParent().getName() + ", " + s.getPeriod().getName() : s.getPeriod().getName() : "";
				}
				else if( fieldName.equals("status" )) {
					value = s.getPeriod() != null ? s.getType().getName() : "";
				}
				
				else if( fieldName.equals("institutionSPM" )) {
					if ( s.getStudent().getInstitutionSPM() != null && !s.getStudent().getInstitutionSPM().equals(""))
						value = s.getStudent().getInstitutionTypeSPM() + " " + s.getStudent().getInstitutionSPM();
					else
						value = "";
				}

				else if( fieldName.equals("institutionSTPM" )) {
					if ( s.getStudent().getInstitutionSTPM() != null && !s.getStudent().getInstitutionSTPM().equals(""))
						value = s.getStudent().getInstitutionTypeSTPM() + " " + s.getStudent().getInstitutionSTPM();
					else
						value = "";
				}
				else if( fieldName.equals("address")) {
					value = s.getStudent().getAddress().getAddress1();
					if ( s.getStudent().getAddress().getAddress2() != null && !s.getStudent().getAddress().getAddress2().equals("")) value += ", " + s.getStudent().getAddress().getAddress2();
					if ( s.getStudent().getAddress().getAddress3() != null && !s.getStudent().getAddress().getAddress3().equals("")) value += ", " + s.getStudent().getAddress().getAddress3();
					if ( s.getStudent().getAddress().getAddress4() != null && !s.getStudent().getAddress().getAddress4().equals("")) value += ", " + s.getStudent().getAddress().getAddress4();
					if ( s.getStudent().getAddress().getCity() != null && !s.getStudent().getAddress().getCity().equals("")) value += ", " + s.getStudent().getAddress().getCity();
					if ( s.getStudent().getAddress().getPostcode() != null && !s.getStudent().getAddress().getPostcode().equals("")) value += ", " + s.getStudent().getAddress().getPostcode();
					if ( s.getStudent().getAddress().getCountry() != null ) value += ", " + s.getStudent().getAddress().getCountry().getName();
				}
				else if (fieldName.equals("sponsor")) {
					if ( filterSponsor ) {
						if ( sp != null ) {
							value = sp.getSponsor().getName();
						}
					}
				}
				cols.add(value);
			}
			rows.add(cols);
		}
		
		ServletOutputStream os = response.getOutputStream();
		Excel1 x = new Excel1();
		try {
			x.createXLS(os, headers, rows);
		} catch (Exception e) {
			e.printStackTrace();
		}
		os.flush();
		os.close();
		
	}

}
