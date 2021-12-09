package educate.admission.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.velocity.Template;

import educate.admission.entity.StudentMoheData;
import educate.db.DbPersistence;
import lebah.portal.velocity.VTemplate;

public class StudentMoheDataPrint extends VTemplate {
	
	String path = "admission/mohe/";
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
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		Template template = engine.getTemplate(getStudent());	
		return template;		
	}
	
	private String getStudent() {
		String matricNo = request.getParameter("matric_no");
		matricNo = matricNo.replaceAll("_", " ");
		StudentMoheData mohe = (StudentMoheData) db.get("select s from StudentMoheData s where s.matric = '" + matricNo + "'");
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

		return path + "print_student.vm";
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


}
