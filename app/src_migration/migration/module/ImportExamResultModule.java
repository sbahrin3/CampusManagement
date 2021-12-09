package migration.module;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.enrollment.entity.StudentSubject;
import educate.exam.module.Result;
import educate.poi.Excel1;
import educate.sis.exam.entity.MarkingGrade;
import educate.sis.module.ResultEntryUtil;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class ImportExamResultModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "migration/exam_result/xl2";

	private static void saveResult(DbPersistence db, String matricNo, String sessionCode, String subjectCode, double mark) throws Exception {
		StudentStatus studentStatus = (StudentStatus) db.get("select s from StudentStatus s where s.student.matricNo = '" + matricNo + "' and s.session.code = '" + sessionCode + "'");
		
		Student student = studentStatus.getStudent();
		
		Subject subject = null;
		//get subject from studentStatus first
		for ( StudentSubject sub : studentStatus.getStudentSubjects() ) {
			if ( sub.getSubject().getCode().equals(subjectCode)) {
				subject = sub.getSubject();
				break;
			}
		}
		if ( subject == null )
			subject = (Subject) db.get("select s from Subject s where s.code = '" + subjectCode + "'");
		
		if ( studentStatus != null && subject != null ) {
			

				ResultEntryUtil.saveResult(db, studentStatus, subject, mark);
				
				//create student subject registration
				//createStudentSubjectRegister(db, studentStatus, subject);

		}
	}

	private static void createStudentSubjectRegister(DbPersistence db, StudentStatus studentStatus, Subject subject) throws Exception {
		StudentSubject ss = null;
		String sql = "select ss from StudentStatus s Join s.studentSubjects ss where s.id = '" + studentStatus.getId() + "' " +
				"and ss.subject.id = '" + subject.getId() + "'";
		
		ss = (StudentSubject) db.get(sql);
		if ( ss == null ) {
			db.begin();
			ss = new StudentSubject();
			ss.setSubject(subject);
			ss.setSubjectStatus("RG");
			ss.setSection(null);
			studentStatus.getStudentSubjects().add(ss);
			ss.setStudentStatus(studentStatus);
			db.persist(ss);
			db.commit();
		}
		
	}
	
	public void preProcess() {
		System.out.println(command);
	}

	@Override
	public String start() {
		// TODO Auto-generated method stub
		return path + "/start.vm";
	}
	
	@Command("uploadFile")
	public String uploadFile() throws Exception {
		String divUploadStatusName = getParam("divUploadStatusName");
		context.put("divUploadStatusName", divUploadStatusName);
		
		String documentId = getParam("documentId");
		String uploadDir = "/Users/Admin/Documents/xls/";
		
    	ResourceBundle rb;
    	try {
    		rb = ResourceBundle.getBundle("files");
    	} catch (java.util.MissingResourceException ex) {
    		throw new Exception("Unable to find files.properties file.");
    	}
    	uploadDir = rb.getString("examResultLocation");
		
		File dir = new File(uploadDir);
		if ( !dir.exists() ) dir.mkdir();
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List items = upload.parseRequest(request);
		Iterator itr = items.iterator();
		List<FileItem> files = new ArrayList<FileItem>();
		while (itr.hasNext()) {
			FileItem item = (FileItem)itr.next();
			if ((!(item.isFormField())) && (item.getName() != null) && (!("".equals(item.getName())))) {
				if ( documentId.equals(item.getFieldName())) {
					files.add(item);
				}
			}
		}
		
		List<String> filenames = new ArrayList<String>();
		context.put("filenames", filenames);
		
		for ( FileItem item : files ) {
			String fileName = item.getName();
			filenames.add(fileName);
			String savedName = uploadDir + fileName;
			context.put("XLSFileName", savedName);
			request.getSession().setAttribute("XLSFileName", savedName);
			item.write(new File(savedName));
		}
		
		return path + "/uploaded.vm";
	}	
	
	@Command("displayData")
	public String displayData() throws Exception {
		String fileName = (String) request.getSession().getAttribute("XLSFileName");
		Excel1 excel = new Excel1(fileName);
		List<Result> results = new ArrayList<Result>();
		context.put("results", results);
		request.getSession().setAttribute("results", results);
		for ( int i=0; i < excel.getRowSize(); i++ ) {
			if ( i > 0 ) {
				String matricNo = excel.getString(i, 0);
				String sessionCode = excel.getString(i, 1);
				String subjectCode = excel.getString(i, 2);
				double mark = 0.0d;
				try {
					mark = excel.getDouble(i, 3);
				} catch ( Exception e ) {
					e.printStackTrace();
				}
				
				Result r = new Result(matricNo, sessionCode, subjectCode, mark);
				results.add(r);
			}
		}
		context.put("totalCnt", results.size());
		return path + "/displayData.vm";
	}
	
	@Command("saveResults")
	public String saveResults() throws Exception {
		String fileName = (String) request.getSession().getAttribute("XLSFileName");
		Excel1 excel = new Excel1(fileName);
		List<Result> results = new ArrayList<Result>();
		context.put("results", results);
		request.getSession().setAttribute("results", results);
		for ( int i=0; i < excel.getRowSize(); i++ ) {
			if ( i > 0 ) {
				String matricNo = excel.getString(i, 0);
				String sessionCode = excel.getString(i, 1);
				String subjectCode = excel.getString(i, 2);
				double mark = 0.0d;
				try {
					mark = excel.getDouble(i, 3);
				} catch ( Exception e ) {
					e.printStackTrace();
				}
				
				Result r = new Result(matricNo, sessionCode, subjectCode, mark);
				results.add(r);
			}
		}
		return path + "/saveResults.vm";
	}	

	@Command("saveMark")
	public String saveMark() throws Exception {
		int cnt = Integer.parseInt(getParam("cnt"));
		int totalCnt = 0;
		try {
			totalCnt = Integer.parseInt(getParam("totalCnt"));
		} catch ( Exception e ) { }
		
		
		List<Result> results = (List<Result>) request.getSession().getAttribute("results");
		Result result = results.get(cnt-1);
		String matricNo = result.getMatricNo();
		String sessionCode = result.getSessionCode();
		String subjectCode = result.getSubjectCode();
		double mark = result.getMark();
		
		Student student = null;
		Subject subject = null;
		Session session = null;
		
		
		boolean proceed = true;
		student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		//
		if ( student == null ) {
			System.out.println("Can't find student: " + matricNo);
			context.put("import_status", "ERR: matricNo");
			proceed = false;
		}
		if ( proceed ) {
			subject = (Subject) db.get("select s from Subject s where s.code = '" + subjectCode + "'");
			if ( subject == null ) {
				System.out.println("Can't find subject: " + subjectCode);
				context.put("import_status", "ERR: subjectCode");
				proceed = false;
			}
		}
		if ( proceed ) {
			System.out.println(student.getProgram().getCode() + ", " + student.getProgram().getName());
			int pathNo = student.getProgram().getLevel().getPathNo();
			session = (Session) db.get("select s from Session s where s.code = '" + sessionCode + "' and s.pathNo = " + pathNo);
			if ( session == null ) {
				System.out.println("Can't find session: " + sessionCode);
				context.put("import_status", "ERR: sessionCode");
				proceed = false;
			}
		}
		if ( proceed ) {
			StudentStatus studentStatus = (StudentStatus) db.get("select s from StudentStatus s where s.student.id = '" + student.getId() + "' and s.session.id = '" + session.getId() + "'");
			if ( studentStatus == null ) {
				System.out.println("Can't find student status: " + matricNo + ", " + sessionCode);
				context.put("import_status", "ERR: studentStatus");
				proceed = false;
			}
		}
		if ( proceed ) {
			if ( subject.getMarkingGrade() == null ) {
				MarkingGrade markingGrade = (MarkingGrade) db.get("select g from MarkingGrade g where g.name = 'DEFAULT'");
				if ( markingGrade == null ) {
					System.out.println("Can't find Marking Grade Scheme: " + subjectCode);
					context.put("import_status", "ERR: Marking Grade");
					proceed = false;
				}
				subject.setMarkingGrade(markingGrade);
			}
		}
		
		
		if ( proceed ) {
			saveResult(db, matricNo, sessionCode, subjectCode, mark);
			context.put("import_status", "OK");
		}
		
		if ( cnt < totalCnt ) {
			int nextCnt = cnt + 1;
			context.put("nextCnt", nextCnt);
			return path + "/saveMark.vm";
		} else {
			return path + "/saveMarkDone.vm";
		}
	}
	
}
