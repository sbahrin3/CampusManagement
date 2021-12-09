package educate.sis.module;

import java.text.SimpleDateFormat;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sis.struct.entity.GraduanItemCheck;
import educate.sis.struct.entity.Graduate;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class GraduanItemCheckModule extends LebahModule {
	
	private String path = "apps/util/graduation_item";
	private DbPersistence db = new DbPersistence();
	
	public void preProcess() {
		context.put("df", new SimpleDateFormat("dd-MM-yyyy"));
	}

	@Override
	public String start() {
		// TODO Auto-generated method stub
		return path + "/start.vm";
	}
	
	@Command("getItemCheck")
	public String getItemCheck() throws Exception {
		String matricNo = getParam("matricNo");
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		context.put("student", student);
		if ( student == null ) return path + "/studentNotFound.vm";
		
		GraduanItemCheck graduanItemCheck = (GraduanItemCheck) db.get("select g from GraduanItemCheck g where g.student.matricNo = '" + matricNo + "'");
		if ( graduanItemCheck != null ) context.put("itemCheck", graduanItemCheck);
		else context.remove("itemCheck");
		
		Graduate graduate = (Graduate) db.get("select g from Graduate g where g.student.matricNo = '" + matricNo + "'");
		if ( graduate != null ) context.put("graduate", graduate);
		else context.remove("graduate");
		
		return path + "/getItemCheck.vm";
	}
	
	@Command("changeStatus")
	public String changeStatus() throws Exception {
		String item = getParam("item");
		String studentId = getParam("studentId");
		String itemCheckId = getParam("itemCheckId");
		
		GraduanItemCheck itemCheck = null;
		if ( "".equals(itemCheckId)) {
			Student student = db.find(Student.class, studentId);
			itemCheck = (GraduanItemCheck) db.get("select g from GraduanItemCheck g where g.student.id = '" + studentId + "'");
			if ( itemCheck == null ) {
				itemCheck = new GraduanItemCheck();
				db.begin();
				itemCheck.setStudent(student);
				db.persist(itemCheck);
				db.commit();
			}
		} else {
			itemCheck = db.find(GraduanItemCheck.class, itemCheckId);
		}

		db.begin();
		if ( "robe".equals(item)) {
			itemCheck.setRobeStatus(Integer.parseInt(getParam("robeStatus")));
		}
		else if ( "scroll".equals(item)) {
			itemCheck.setScrollStatus(Integer.parseInt(getParam("scrollStatus")));
		}
		else if ( "transcript".equals(item)) {
			itemCheck.setTranscriptStatus(Integer.parseInt(getParam("transcriptStatus")));
		}
		else if ( "photo".equals(item)) {
			itemCheck.setPhotoStatus(Integer.parseInt(getParam("photoStatus")));
		}
		else if ( "alumniCard".equals(item)) {
			itemCheck.setAlumniCardStatus(Integer.parseInt(getParam("alumniCardStatus")));
		}
		else if ( "tracerStudy".equals(item)) {
			itemCheck.setTracerStudyStatus(Integer.parseInt(getParam("tracerStudyStatus")));
		}
		db.commit();
		
		return path + "/empty.vm";
	}
	
	@Command("changeRemark")
	public String changeRemark() throws Exception {
		String item = getParam("item");
		String studentId = getParam("studentId");
		String itemCheckId = getParam("itemCheckId");
		
		GraduanItemCheck itemCheck = null;
		if ( "".equals(itemCheckId)) {
			Student student = db.find(Student.class, studentId);
			itemCheck = (GraduanItemCheck) db.get("select g from GraduanItemCheck g where g.student.id = '" + studentId + "'");
			if ( itemCheck == null ) {
				itemCheck = new GraduanItemCheck();
				db.begin();
				itemCheck.setStudent(student);
				db.persist(itemCheck);
				db.commit();
			}

		} else {
			itemCheck = db.find(GraduanItemCheck.class, itemCheckId);
		}

		db.begin();
		if ( "robe".equals(item)) {
			itemCheck.setRobeRemark(getParam("robeRemark"));
		}
		else if ( "scroll".equals(item)) {
			itemCheck.setScrollRemark(getParam("scrollRemark"));
		}
		else if ( "transcript".equals(item)) {
			itemCheck.setTranscriptRemark(getParam("transcriptRemark"));
		}
		else if ( "photo".equals(item)) {
			itemCheck.setPhotoRemark(getParam("photoRemark"));
		}
		else if ( "alumniCard".equals(item)) {
			itemCheck.setAlumniCardRemark(getParam("alumniCardRemark"));
		}
		else if ( "tracerStudy".equals(item)) {
			itemCheck.setTracerStudyRemark(getParam("tracerStudyRemark"));
		}
		db.commit();
		
		return path + "/empty.vm";
	}
	
	@Command("changeDate")
	public String changeDate() throws Exception {
		String item = getParam("item");
		String studentId = getParam("studentId");
		String itemCheckId = getParam("itemCheckId");
		
		GraduanItemCheck itemCheck = null;
		if ( "".equals(itemCheckId)) {
			Student student = db.find(Student.class, studentId);
			itemCheck = (GraduanItemCheck) db.get("select g from GraduanItemCheck g where g.student.id = '" + studentId + "'");
			if ( itemCheck == null ) {
				itemCheck = new GraduanItemCheck();
				db.begin();
				itemCheck.setStudent(student);
				db.persist(itemCheck);
				db.commit();
			}

		} else {
			itemCheck = db.find(GraduanItemCheck.class, itemCheckId);
		}

		db.begin();
		if ( "robe".equals(item)) {
			itemCheck.setRobeDate(new SimpleDateFormat("dd-MM-yyyy").parse(getParam("robeDate")));
		}
		else if ( "scroll".equals(item)) {
			itemCheck.setScrollDate(new SimpleDateFormat("dd-MM-yyyy").parse(getParam("scrollDate")));
		}
		else if ( "transcript".equals(item)) {
			itemCheck.setTranscriptDate(new SimpleDateFormat("dd-MM-yyyy").parse(getParam("transcriptDate")));
		}
		else if ( "photo".equals(item)) {
			itemCheck.setPhotoDate(new SimpleDateFormat("dd-MM-yyyy").parse(getParam("photoDate")));
		}
		else if ( "alumniCard".equals(item)) {
			itemCheck.setAlumniCardDate(new SimpleDateFormat("dd-MM-yyyy").parse(getParam("alumniCardDate")));
		}
		else if ( "tracerStudy".equals(item)) {
			itemCheck.setTracerStudyDate(new SimpleDateFormat("dd-MM-yyyy").parse(getParam("tracerStudyDate")));
		}
		db.commit();
		
		return path + "/empty.vm";
	}	

}
