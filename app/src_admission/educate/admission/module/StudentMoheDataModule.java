package educate.admission.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import educate.admission.entity.StudentMoheData;
import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.util.BirthPlacePartException;
import educate.util.ContainsOnlyNumbersException;
import educate.util.DatePartNotValidException;
import educate.util.MyKad;
import educate.util.NumberLengthException;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class StudentMoheDataModule extends LebahModule {
	
	protected boolean studentMode = false;
	protected DbPersistence db = new DbPersistence();
	protected String path = "admission/mohe";
	protected String matricNo = "";
	protected Student student = null;
	
	public void preProcess() {
		context.put("studentMode", studentMode);
		
		context.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		
		lebah.util.Util util = new lebah.util.Util();
		context.put("util", util);	
		
	}

	@Override
	public String start() {
		if ( studentMode ) {
			matricNo = (String) request.getSession().getAttribute("_portal_login");
			//for testing only
			//matricNo = "PHARM 1308-3357";
			Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
			context.put("student", student);
			getStudentMoheData(student);			
		}
		else context.remove("student");
		return path + "/start.vm";
	}
	
	@Command("getStudentData")
	public String getStudentData() throws Exception {
		String matricNo = getParam("matricNo");
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		context.put("student", student);	
		getStudentMoheData(student);
		return path + "/student.vm";
	}

	private void getStudentMoheData(Student student) {
		StudentMoheData mohe = (StudentMoheData) db.get("select s from StudentMoheData s where s.matric = '" + student.getMatricNo() + "'");
		if ( mohe == null ) {
			db.begin();
			mohe = new StudentMoheData();
			mohe.setNokp(student.getBiodata().getIcno());
			mohe.setMatric(student.getMatricNo());
			mohe.setNama_penuh(student.getBiodata().getName());
			mohe.setEmail(student.getBiodata().getEmail());
			if ( student.getBiodata().getGender() != null )
				mohe.setJantina(student.getBiodata().getGender().getCode().equals("M") ? "1" : student.getBiodata().getGender().getCode().equals("M") ? "2" : "");
			if ( student.getBiodata().getDob() != null )
				mohe.setTarikh_lahir(new SimpleDateFormat("dd-MM-yyyy").format(student.getBiodata().getDob()));
			mohe.setAlamat_tetap1(student.getAddress().getAddress1());
			mohe.setAlamat_tetap2(student.getAddress().getAddress2());
			mohe.setAlamat_tetap3(student.getAddress().getAddress3());
			mohe.setPoskod_tetap(student.getAddress().getPostcode());

			mohe.setFakulti(student.getProgram() != null ? student.getProgram().getCourse().getFaculty().getName() : "");
			mohe.setIjazah(student.getProgram() != null ? student.getProgram().getDisplayName() : "");
			mohe.setIjazah_singkatan(student.getProgram() != null ? student.getProgram().getAbbrev() : "");
			mohe.setKod_kursus(student.getProgram() != null ? student.getProgram().getCode() : "");
			mohe.setProgram(student.getProgram() != null ? student.getProgram().getName() : "");
			
			if ( student.getIntake() != null ) {
				Calendar c = new GregorianCalendar();
				c.setTime(student.getIntake().getStartDate());
				mohe.setTahun_mula_pengajian(Integer.toString(c.get(Calendar.YEAR)));		
				mohe.setBulan_mula_pengajian(Integer.toString(c.get(Calendar.MONTH) + 1));
			}

			//mohe.setBulan_tamat_pengajian(getParam("bulan_tamat_pengajian"));
			//mohe.setTahun_tamat_pengajian(getParam("tahun_tamat_pengajian"));
			
			db.persist(mohe);
			try {
				db.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		context.put("mohe", mohe);
		listMoheCodes();
		
		context.put("kodDaerahList", new ArrayList<String>());
		MoheCodeData code = new MoheCodeData(getServletContext());
		try {
			if ( mohe.getNegeri_tetap() != null && !"".equals(mohe.getNegeri_tetap()))
				context.put("kodDaerahList", code.getMoheCodes("kod_daerah_dewan_" + mohe.getNegeri_tetap() + ".txt"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		context.put("kodSubbidangList", new ArrayList<String>());
		try {
			if ( mohe.getBidang_pengajian() != null && !"".equals(mohe.getBidang_pengajian()))
				context.put("kodSubbidangList", code.getMoheCodes("kod_bidang_" + mohe.getBidang_pengajian() + ".txt"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void listMoheCodes() {
		try {
			MoheCodeData mohe = new MoheCodeData(getServletContext());
			context.put("kodNegeriList", mohe.getMoheCodes("kod_negeri_tetap.txt"));
			context.put("kodKeturunanList", mohe.getMoheCodes("kod_keturunan.txt"));
			context.put("kodPeringkatPengajianList", mohe.getMoheCodes("kod_peringkat_pengajian.txt"));
			context.put("kodBidangList", mohe.getMoheCodes("kod_bidang.txt"));
			context.put("kodNecList", mohe.getMoheCodes("kod_nec.txt"));
			context.put("kodPenajaList", mohe.getMoheCodes("kod_penaja.txt"));
			context.put("kodKelayakanBahasaList", mohe.getMoheCodes("kod_kelayakan_bahasa.txt"));
			context.put("kodKelayakanMasukList", mohe.getMoheCodes("kod_kelayakan_masuk.txt"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Command("listSubbidang")
	public String listSubbidang() throws Exception {
		MoheCodeData mohe = new MoheCodeData(getServletContext());
		String kodBidang = getParam("bidang_pengajian");
		context.put("kodSubbidangList", mohe.getMoheCodes("kod_bidang_" + kodBidang + ".txt"));
		return path + "/list_subbidang.vm";
	}
	
	@Command("listDaerah")
	public String listDaerah() throws Exception {
		String kodNegeri = getParam("negeri_tetap");
		MoheCodeData mohe = new MoheCodeData(getServletContext());
		context.remove("kodDaerahList");
		context.put("kodDaerahList", mohe.getMoheCodes("kod_daerah_dewan_" + kodNegeri + ".txt"));		
		return path + "/list_daerah.vm";
	}	
	
	@Command("getStudent")
	public String getStudent() {
		matricNo = (String) request.getSession().getAttribute("_portal_login");
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		context.put("student", student);
		
		String icno = student.getBiodata().getIcno();
		boolean icnoValid = true;
		try {
			MyKad myKad = new MyKad("icno");
		} catch (ContainsOnlyNumbersException e) {
			icnoValid = false;
		} catch (DatePartNotValidException e) {
			icnoValid = false;
		} catch (NumberLengthException e) {
			icnoValid = false;
		} catch (BirthPlacePartException e) {
			icnoValid = false;
		}
		if ( icnoValid ) {
			
		}
		
		return path + "/student.vm";
	}
	
	@Command("saveMoheData")
	public String saveMoheData() throws Exception {
		String matric = getParam("matric");
		
		boolean update = true;
		StudentMoheData mohe = (StudentMoheData) db.get("select s from StudentMoheData s where s.matric = '" + matric + "'");
		if ( mohe == null ) {
			mohe = new StudentMoheData();
			update = false;
		}
		
		int cnt = 0;
		db.begin();
		mohe.setNokp(getParam("nokp")); if ( !"".equals(mohe.getNokp())) cnt++; 
		mohe.setMatric(getParam("matric")); if ( !"".equals(mohe.getMatric())) cnt++;
		mohe.setNama_penuh(getParam("nama_penuh")); if ( !"".equals(mohe.getNama_penuh())) cnt++;
		mohe.setEmail(getParam("email")); if ( !"".equals(mohe.getEmail())) cnt++;
		mohe.setJantina(getParam("jantina")); if ( !"".equals(mohe.getJantina())) cnt++;
		mohe.setTarikh_lahir(getParam("tarikh_lahir")); if ( !"".equals(mohe.getTarikh_lahir())) cnt++;
		mohe.setWarganegara(getParam("warganegara")); if ( !"".equals(mohe.getWarganegara())) cnt++;
		mohe.setNegara_asal(getParam("negara_asal")); if ( !"".equals(mohe.getNegara_asal())) cnt++;
		mohe.setAlamat_tetap1(getParam("alamat_tetap1")); if ( !"".equals(mohe.getAlamat_tetap1())) cnt++;
		mohe.setAlamat_tetap2(getParam("alamat_tetap2")); if ( !"".equals(mohe.getAlamat_tetap2())) cnt++;
		mohe.setAlamat_tetap3(getParam("alamat_tetap3")); if ( !"".equals(mohe.getAlamat_tetap3())) cnt++;
		mohe.setNegeri_tetap(getParam("negeri_tetap")); if ( !"".equals(mohe.getNegeri_tetap())) cnt++;
		mohe.setDaerah_tetap(getParam("daerah_tetap")); if ( !"".equals(mohe.getDaerah_tetap())) cnt++;
		mohe.setPoskod_tetap(getParam("poskod_tetap")); if ( !"".equals(mohe.getPoskod_tetap())) cnt++;
		mohe.setKeturunan(getParam("keturunan")); if ( !"".equals(mohe.getKeturunan())) cnt++;
		mohe.setPeringkat_pengajian(getParam("peringkat_pengajian")); if ( !"".equals(mohe.getPeringkat_pengajian())) cnt++;
		mohe.setBidang_pengajian(getParam("bidang_pengajian")); if ( !"".equals(mohe.getBidang_pengajian())) cnt++;
		mohe.setSubbidang_pengajian(getParam("subbidang_pengajian")); if ( !"".equals(mohe.getSubbidang_pengajian())) cnt++;
		mohe.setNational_education_code(getParam("national_education_code")); if ( !"".equals(mohe.getNational_education_code())) cnt++;
		mohe.setFakulti(getParam("fakulti")); if ( !"".equals(mohe.getFakulti())) cnt++;
		mohe.setIjazah(getParam("ijazah")); if ( !"".equals(mohe.getIjazah())) cnt++;
		mohe.setIjazah_singkatan(getParam("ijazah_singkatan")); if ( !"".equals(mohe.getIjazah_singkatan())) cnt++;
		mohe.setKod_kursus(getParam("kod_kursus")); if ( !"".equals(mohe.getKod_kursus())) cnt++;
		mohe.setProgram(getParam("program")); if ( !"".equals(mohe.getProgram())) cnt++;
		mohe.setPengkhususan(getParam("pengkhususan")); if ( !"".equals(mohe.getPengkhususan())) cnt++;
		mohe.setCgpa(getParam("cgpa")); if ( !"".equals(mohe.getCgpa())) cnt++;
		mohe.setGred(getParam("gred")); if ( !"".equals(mohe.getGred())) cnt++;
		mohe.setLokasi_pengajian(getParam("lokasi_pengajian")); if ( !"".equals(mohe.getLokasi_pengajian())) cnt++;
		mohe.setMod_pengajian(getParam("mod_pengajian")); if ( !"".equals(mohe.getMod_pengajian())) cnt++;
		mohe.setKaedah_pengajian(getParam("kaedah_pengajian")); if ( !"".equals(mohe.getKaedah_pengajian())) cnt++;
		mohe.setBulan_mula_pengajian(getParam("bulan_mula_pengajian")); if ( !"".equals(mohe.getBulan_mula_pengajian())) cnt++;
		mohe.setTahun_mula_pengajian(getParam("tahun_mula_pengajian")); if ( !"".equals(mohe.getTahun_mula_pengajian())) cnt++;
		mohe.setBulan_tamat_pengajian(getParam("bulan_tamat_pengajian")); if ( !"".equals(mohe.getBulan_tamat_pengajian())) cnt++;
		mohe.setTahun_tamat_pengajian(getParam("tahun_tamat_pengajian")); if ( !"".equals(mohe.getTahun_tamat_pengajian())) cnt++;
		mohe.setPenaja(getParam("penaja")); if ( !"".equals(mohe.getPenaja())) cnt++;
		mohe.setSpm_bm(getParam("spm_bm")); if ( !"".equals(mohe.getSpm_bm())) cnt++;
		mohe.setSpm_bi(getParam("spm_bi")); if ( !"".equals(mohe.getSpm_bi())) cnt++;
		mohe.setMuet(getParam("muet")); if ( !"".equals(mohe.getMuet())) cnt++;
		mohe.setCawangan(getParam("cawangan")); if ( !"".equals(mohe.getCawangan())) cnt++;
		mohe.setFrancais(getParam("francais")); if ( !"".equals(mohe.getFrancais())) cnt++;
		mohe.setNama_institusi_francais(getParam("nama_institusi_francais")); if ( !"".equals(mohe.getNama_institusi_francais())) cnt++;
		mohe.setKelayakan_masuk(getParam("kelayakan_masuk")); if ( !"".equals(mohe.getKelayakan_masuk())) cnt++;
		mohe.setKelayakan_lain(getParam("kelayakan_lain")); if ( !"".equals(mohe.getKelayakan_lain())) cnt++;
		mohe.setStatus_latihan_industri(getParam("status_latihan_industri")); if ( !"".equals(mohe.getStatus_latihan_industri())) cnt++;
		mohe.setStatus_oku(getParam("status_oku")); if ( !"".equals(mohe.getStatus_oku())) cnt++;
		mohe.setJenis_oku(getParam("jenis_oku")); if ( !"".equals(mohe.getJenis_oku())) cnt++;
		
		//System.out.println("total fields = 47");
		//System.out.println("filled fields = " + cnt);

		mohe.setFilledFields(cnt);
		
		if ( !update ) db.persist(mohe);
		db.commit();

		
		
		
		context.put("mohe", mohe);
		
		return path + "/saveMoheData.vm";
	}
	

}
