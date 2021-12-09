package educate.sis.module;

import java.util.Hashtable;
import java.util.List;

import educate.db.DbPersistence;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.RegistrationFeeTable;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class RegistrationFeeTableSetupModule extends LebahModule {
	
	String path = "apps/util/registration_fee_setup/";
	DbPersistence db = new DbPersistence();
	
	@Override
	public String start() {
		//list of programs
		List<Program> programs = db.list("select p from Program p order by p.name");
		context.put("programs", programs);
		return path + "start.vm";
	}
	
	@Command("get_fees")
	public String getPeriods() {
		String programId = request.getParameter("program_id");
		Program program = db.find(Program.class, programId);
		context.put("program", program);
		List<Period> periods = program.getPeriodScheme().getFunctionalPeriodList();
		context.put("periods", periods);
		
		List<RegistrationFeeTable> feeTuitions = db.list("select f from RegistrationFeeTable f where f.feeType = 'tuition' and f.program.id = '" + programId + "'");
		Hashtable tuitions = new Hashtable();
		context.put("tuitions", tuitions);
		double totalTuitionFee = 0.0d;
		for ( RegistrationFeeTable fee : feeTuitions ) {
			tuitions.put(fee.getPeriod().getId(), fee.getAmount());
			totalTuitionFee += fee.getAmount();
		}
		context.put("total_tuition_fee", totalTuitionFee);
		Hashtable accomodations = new Hashtable();
		double totalAccomodationFee = 0.0d;
		List<RegistrationFeeTable> feeAccomodations = db.list("select f from RegistrationFeeTable f where f.feeType = 'accomodation' and f.program.id = '" + programId + "'");
		for ( RegistrationFeeTable fee : feeAccomodations ) {
			accomodations.put(fee.getPeriod().getId(), fee.getAmount());
			totalAccomodationFee += fee.getAmount();
		}		
		context.put("accomodations", accomodations);
		context.put("total_accomodation_fee", totalAccomodationFee);
		return path + "fee_periods.vm";
	}
	
	@Command("save_fee")
	public String saveFee() throws Exception {
		String programId = request.getParameter("program_id");
		Program program = db.find(Program.class, programId);
		
		String periodId = request.getParameter("period_id");
		Period period = db.find(Period.class, periodId);
		
		String feeType = request.getParameter("fee_type");
		String _amount = request.getParameter("fee_" + feeType + "_" + periodId);
		double amount = 0.0d;
		try {
			amount = Double.parseDouble(_amount);
		} catch ( Exception e ) {}
		RegistrationFeeTable fee = (RegistrationFeeTable) db.get("select f from RegistrationFeeTable f where f.program.id = '" + programId + "' and f.period.id = '" + periodId + "' and f.feeType = '" + feeType + "'");
		if ( fee != null ) {
			db.begin();
			fee.setAmount(amount);
			db.commit();
		}
		else {
			db.begin();
			fee = new RegistrationFeeTable();
			fee.setFeeType(feeType);
			fee.setProgram(program);
			fee.setPeriod(period);
			fee.setAmount(amount);
			db.persist(fee);
			db.commit();
		}
		return path + "empty.vm";
	}

}
