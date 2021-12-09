package migration.module;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import educate.admission.entity.Address;
import educate.admission.entity.Biodata;
import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.poi.Excel1;
import educate.sis.general.entity.Gender;
import educate.sis.general.entity.Nationality;
import educate.sis.general.entity.Race;
import educate.sis.general.entity.State;
import educate.sis.module.StudentStatusUtil;
import educate.sis.struct.entity.LearningCentre;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import educate.util.MyKad;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class ImportStudentDataModule extends LebahModule {

	private DbPersistence db = new DbPersistence();
	private String path = "migration/student/xl";

	
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
		List<StudentItem> items = new ArrayList<StudentItem>();
		context.put("items", items);
		request.getSession().setAttribute("items", items);
		for ( int i=0; i < excel.getRowSize(); i++ ) {
			if ( i > 0 ) {
				
				String sessionCode = excel.getString(i, 0);
				String programCode = excel.getString(i, 1);
				String matricNo = excel.getString(i, 2);
				String name = excel.getString(i, 3);
				String icno = excel.getString(i, 4);
				String gender = excel.getString(i, 5);
				String raceCode = excel.getString(i, 6);
				String birthDate = excel.getString(i, 7);
				String phoneNo = excel.getString(i, 8);
				String address = excel.getString(i, 9);
				String stateCode = excel.getString(i, 10);
				String nationalityCode = excel.getString(i, 11);
				
				StudentItem d = new StudentItem(sessionCode, programCode, matricNo, name, icno, gender,
						raceCode, birthDate, phoneNo, address, stateCode, nationalityCode);
				
				items.add(d);
			}
		}
		context.put("totalCnt", items.size());
		return path + "/displayData.vm";
	}

	
	@Command("saveItem")
	public String saveItem() throws Exception {
		int cnt = Integer.parseInt(getParam("cnt"));
		int totalCnt = 0;
		try {
			totalCnt = Integer.parseInt(getParam("totalCnt"));
		} catch ( Exception e ) { }
		
		List<Gender> genders = db.list("select g from Gender g");
		Map<String, Gender> genderMap = new HashMap<String, Gender>();
		for ( Gender g : genders ) {
			genderMap.put(g.getCode(), g);
		}
		
		List<Race> races = db.list("select r from Race r");
		Map<String, Race> raceMap = new HashMap<String, Race>();
		for ( Race r : races ) {
			raceMap.put(r.getCode(), r);
		}
		
		LearningCentre centre = (LearningCentre) db.get("select c from LearningCentre c where c.mainCampus = 1");
		
		
		List<StudentItem> results = (List<StudentItem>) request.getSession().getAttribute("items");
		StudentItem item = results.get(cnt-1);
		String sessionCode = item.getSessionCode();
		String matricNo = item.getMatricNo();
		String programCode = item.getProgramCode();
		String raceCode = item.getRaceCode();
		String genderCode = item.getGender();
		String icno = item.getIcno();
		String stateCode = item.getStateCode();
		String nationalityCode = item.getNationalityCode();
		String name = item.getName();
		String address = item.getAddress();
		String phoneNo = item.getPhoneNo();

		
		Student student = null;
		Session session = null;
		Program program = null;
		
		boolean proceed = true;
		student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		if ( student != null ) {
			System.out.println("Already exist: " + matricNo);
			context.put("import_status", "ERR: matricNo");
			proceed = false;
		}

		if ( proceed ) {
			session = (Session) db.get("select s from Session s where s.code = '" + sessionCode + "'");
			if ( session == null ) {
				System.out.println("Can't find session: " + sessionCode);
				context.put("import_status", "ERR: sessionCode");
				proceed = false;
			}
		}
		
		if ( proceed ) {
			program = (Program) db.get("select p from Program p where p.code = '" + programCode + "'");
			if ( program == null ) {
				context.put("import_status", "ERR: programCode");
				proceed = false;
			}
		}
		
		Race race = raceMap.get(raceCode);
		Gender gender = genderMap.get(genderCode);
		if ( gender == null ) {
			try {
				MyKad mykad = new MyKad(icno);
				String g = mykad.getGender();
				gender = genderMap.get(genderCode);
			} catch ( Exception e ) {
				
			}
		}
		
		State state = (State) db.get("select s from State s where s.code = '" + stateCode + "'");
		Nationality nationality = (Nationality) db.get("select n from Nationality n where n.code = '" + nationalityCode + "'");
		
		if ( proceed ) {
			
			String sep = !"".equals(getParam("dateSeparator")) ? getParam("dateSeparator") : "-";
			String dateStyle = "dd" + sep + "MM" + sep + "yyyy";
			
			student = new Student();
			student.setAddress(new Address());
			student.setBiodata(new Biodata());
			String birthDate = item.getBirthDate();
			if ( birthDate != null ) {
				birthDate = birthDate.replace(".", "-");
				Date date = new SimpleDateFormat(dateStyle).parse(birthDate);
				System.out.println("birth date = " + date);
				student.getBiodata().setDob(date);
			}
			student.getBiodata().setName(name);
			student.getBiodata().setIcno(icno);
			student.getBiodata().setGender(gender);
			student.getAddress().setAddress1(address);
			student.getAddress().setState(state);
			student.getBiodata().setNationality(nationality);
			student.getBiodata().setTelephoneNo(phoneNo);
			
			student.setIntake(session);
			student.setMatricNo(matricNo);
			student.setLearningCenter(centre);
			student.setProgram(program);
			
			db.begin();
			db.persist(student);
			db.commit();
			
	        if ( student.getIntake() != null && student.getProgram() != null ) {
	        	StudentStatusUtil u = new StudentStatusUtil();
	        	u.generateStatus(student);
	        }

			
			context.put("import_status", "OK");
		}
		
		if ( cnt < totalCnt ) {
			int nextCnt = cnt + 1;
			context.put("nextCnt", nextCnt);
			return path + "/saveItem.vm";
		} else {
			return path + "/saveItemDone.vm";
		}
	}
}
