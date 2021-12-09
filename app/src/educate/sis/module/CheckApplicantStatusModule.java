package educate.sis.module;

import java.text.SimpleDateFormat;
import java.util.Date;

import educate.admission.entity.Applicant;
import educate.admission.entity.OfferLetterAttribute;
import educate.db.DbPersistence;
import educate.sis.struct.entity.LetterTemplate;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class CheckApplicantStatusModule extends LebahModule {

	String path = "apps/util/check_applicant/";
	DbPersistence db = new DbPersistence();
	protected boolean byAdmin = false;
	protected boolean isPrint = false;
	
	@Override
	public void preProcess() {
		if ( byAdmin ) context.put("admin_mode", true); else context.remove("admin_mode");
		context.put("util", new lebah.util.Util());
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("current_date", new Date());
	}
	
	
	@Override
	public String start() {
		return path + "ref_no.vm";
	}
	
	@Command("print_letter")
	public String printLetter() {
		isPrint = true;
		return getApplicantStatus();
	}
	
	@Command("check_status")
	public String getApplicantStatus() {
		String refNo = request.getParameter("ref_no");
		Applicant applicant = null;
		if ( byAdmin ) {
			applicant = (Applicant) db.get("select a from Applicant a where a.referenceNo = '" + refNo + "' or a.biodata.icno = '" + refNo + "' or a.biodata.passport = '" + refNo + "'");
		} else {
			String identityNo = request.getParameter("identity_no");
			if ( identityNo != null && !"".equals(identityNo)) {
				applicant = (Applicant) db.get("select a from Applicant a where a.referenceNo = '" + refNo + "' and a.biodata.icno = '" + identityNo + "'");
				if ( applicant == null )
					applicant = (Applicant) db.get("select a from Applicant a where a.referenceNo = '" + refNo + "' and a.biodata.passport = '" + identityNo + "'");
			}
		}
		if ( applicant != null ) {
			context.put("applicant", applicant);
			if ( "OFFERED".equals(applicant.getStatus()) || "OFFERED_CONDITIONALLY".equals(applicant.getStatus()) ) {
				LetterTemplate letter = db.find(LetterTemplate.class, "offered");
				String text = letter.getText();
				text = text.replaceAll("applicant.name", applicant.getBiodata().getName());
				String address = "";
				if ( applicant.getAddress().getAddress1() != null && !"".equals(applicant.getAddress().getAddress1()) )
					address += applicant.getAddress().getAddress1() + "\n";
				if ( applicant.getAddress().getAddress2() != null && !"".equals(applicant.getAddress().getAddress2()) )
					address += applicant.getAddress().getAddress2() + "\n";
				if ( applicant.getAddress().getAddress3() != null && !"".equals(applicant.getAddress().getAddress3()) )
					address += applicant.getAddress().getAddress3() + "\n";
				if ( applicant.getAddress().getAddress4() != null && !"".equals(applicant.getAddress().getAddress4()) )
					address += applicant.getAddress().getAddress4() + "\n";
				if ( applicant.getAddress().getPostcode() != null && !"".equals(applicant.getAddress().getPostcode()) )
					address += applicant.getAddress().getPostcode() + " ";
				else
					address += "\n";
				if ( applicant.getAddress().getCity() != null && !"".equals(applicant.getAddress().getCity()) )
					address += applicant.getAddress().getCity() + "\n";
				if ( applicant.getAddress().getState() != null && !"".equals(applicant.getAddress().getState().getName()) )
					address += applicant.getAddress().getState().getName() + "\n";
				if ( applicant.getAddress().getCountry() != null && !"".equals(applicant.getAddress().getCountry().getName()) )
					address += applicant.getAddress().getCountry().getName() + "\n";
				text = text.replaceAll("applicant.address", address);
				text = text.replaceAll("program.name", applicant.getProgramOffered() != null ? applicant.getProgramOffered().getName() : "");
				text = text.replaceAll("session.name", applicant.getIntake() != null ? applicant.getIntake().getName() : "");
								
				context.put("offer_letter", text);
				
				context.put("address", address);
				context.put("program_name", applicant.getProgramOffered() != null ? applicant.getProgramOffered().getName() : "");
				context.put("session_name", applicant.getIntake() != null ? applicant.getIntake().getName() : "");
				
				OfferLetterAttribute attr = (OfferLetterAttribute) db.get("select a from OfferLetterAttribute a");
				context.put("attr", attr);
				
				if ( !isPrint)
					return path + "start.vm";
				else
					return path + "offer_letter.vm";
			}
		}
		else {
			context.remove("applicant");
			return path + "no_data.vm";
		}
		if ( !isPrint)
			return path + "start.vm";
		else
			return path + "offer_letter.vm";
	}

}
