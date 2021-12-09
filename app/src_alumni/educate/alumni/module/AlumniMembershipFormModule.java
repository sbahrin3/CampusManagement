package educate.alumni.module;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import educate.alumni.entity.AlumniMember;
import educate.db.DbPersistence;
import lebah.db.Db;
import lebah.db.SQLRenderer;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class AlumniMembershipFormModule extends LebahModule {
	
	protected DbPersistence db = new DbPersistence();
	protected String path = "alumni/membership";
	protected String memberId = "";
	
	
	public void preProcess() {
		context.remove("member");
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
		String uploadDir = "c:/uploaded/";
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
		for ( FileItem item : files ) {
			String fileName = item.getName();
			String savedName = uploadDir + fileName;
			context.put("serverFileName", savedName);
			item.write(new File(savedName));
			
			if ( "memberPhoto".equals(documentId)) { 
				System.out.println("uploaded photo!");
				
				String avatarName = savedName.substring(0, savedName.lastIndexOf(".")) + "_avatar" + savedName.substring(savedName.lastIndexOf("."));
				lebah.repository.Thumbnail.create(savedName, savedName, 200, 160, 100);
				lebah.repository.Thumbnail.create(savedName, avatarName, 50, 40, 100);
				context.put("avatarName", avatarName);
				
				String membershipId = getParam("membershipId");
				if ( !"".equals(membershipId)) {

				}
				break;
			}
		}
		return path + "/uploaded.vm";
	}
	
	public static boolean createPortalLogin(String login, String password, String name) throws Exception {
		String sql = "";
		Connection conn = null;
		Db db = null;
		try {
			db = new Db();
			conn = db.getConnection();
			conn.setAutoCommit(false);			
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			boolean found = false;
			{
				sql = "select user_login from users where user_login = '" + login + "'";
				ResultSet rs = stmt.executeQuery(sql);
				if ( rs.next() ) found = true;
			}
			if ( found ) {
				return false;
			}
			if ( !found ) {
				{
					r.add("user_login", login);
					r.add("user_password", lebah.util.PasswordService.encrypt(password));
					r.add("user_name", name);
					r.add("user_role", "alumni");
					sql = r.getSQLInsert("users");
					stmt.executeUpdate(sql);
				}
				{
					String css_name = "default.css";
					sql = "insert into user_css (user_login, css_name) values ('" + login + "', '" + css_name + "')";
					stmt.executeUpdate(sql);
				}
			}
			else {
				r.add("user_name", name);
				r.update("user_login", login);	
				sql = r.getSQLUpdate("users");		
				stmt.executeUpdate(sql);	
			}
			conn.commit();
		} catch ( Exception ex ) {
			try {
				conn.rollback();
			} catch ( SQLException rollex ) {}
			throw ex;
		} finally {
			if ( db != null ) db.close();
		}
		return true;
	}	
	
	private void saveAvatar(String login, String avatar) throws Exception {
		String sql = "";
		Connection conn = null;
		Db db = null;
		try {
			db = new Db();
			conn = db.getConnection();
			conn.setAutoCommit(false);			
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			r.add("avatar", avatar);
			r.update("user_login", login);	
			sql = r.getSQLUpdate("users");		
			stmt.executeUpdate(sql);	
			conn.commit();
		} catch ( Exception ex ) {
			try {
				conn.rollback();
			} catch ( SQLException rollex ) {}
			throw ex;
		} finally {
			if ( db != null ) db.close();
		}
	}
	
	@Command("addMembership")
	public String addMembership() throws Exception {
		String login = getParam("email");
		String name = getParam("fullName");
		String password = lebah.db.UniqueID.getUID();
		password = password.substring(password.length()-5);
		if ( createPortalLogin(login, password, name) ) {
			AlumniMember member = new AlumniMember();
			db.begin();
			saveMember(member);
			member.setUserId(login);
			member.setStatus("pending");
			member.setInitPassword(password);
			member.setApplyDate(new Date());
			member.setRemoteAddress(request.getRemoteAddr());			
			db.persist(member);
			db.commit();
			
			String avatar = getParam("avatarName");
			saveAvatar(login, avatar);
			return path + "/addMembership.vm";
		}
		return path + "/addMembershipFailed.vm";
	}
	
	@Command("updateMembership")
	public String updateMembership() throws Exception {
		String memberId = getParam("memberId");
		AlumniMember member = db.find(AlumniMember.class, memberId);
		db.begin();
		saveMember(member);
		db.commit();
		String avatar = getParam("avatarName");
		saveAvatar(member.getEmail(), avatar);
		return path + "/updateMembership.vm";
	}

	private void saveMember(AlumniMember member) {
		member.setFullName(getParam("fullName"));
		member.setIcno(getParam("icno"));
		member.setTitle(getParam("title"));
		member.setCitizenship(getParam("citizenship"));
		member.setCitizenshipOther(getParam("citizenshipOther"));
		member.setPermanentAddress(getParam("permanentAddress"));
		member.setEmail(getParam("email"));
		member.setMobileNo(getParam("mobileNo"));
		member.setTelephoneNo(getParam("telephoneNo"));
		member.setDiploma(getParam("diploma"));
		member.setFirstDegree(getParam("firstDegree"));
		member.setMasterDegree(getParam("masterDegree"));
		member.setPhd(getParam("phd"));
		member.setOtherDegree(getParam("otherDegree"));
		member.setDiplomaYear(getInt(getParam("diplomaYear")));
		member.setFirstDegreeYear(getInt(getParam("firstDegreeYear")));
		member.setMasterDegreeYear(getInt(getParam("masterDegreeYear")));
		member.setPhdYear(getInt(getParam("phdYear")));
		member.setOtherDegreeYear(getInt(getParam("otherDegreeYear")));
		
		int unemployed = getInt(getParam("unemployed"));
		member.setUnemployed(unemployed == 1);
		int entrepreneur = getInt(getParam("entrepreneur"));
		member.setEntrepreneur(entrepreneur == 1);
		
		member.setPhotoFileName(getParam("photoFileName"));
		member.setAvatarName(getParam("avatarName"));
		
		
		try {
			InputStream is1 = new FileInputStream(getParam("photoFileName"));
			member.setProfileImage(IOUtils.toByteArray(is1));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			InputStream is2 = new FileInputStream(getParam("avatarName"));
			member.setAvatarImage(IOUtils.toByteArray(is2));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private int getInt(String param) {
		if ( "".equals(param)) return 0;
		else return Integer.parseInt(param);
	}
	
	
}
