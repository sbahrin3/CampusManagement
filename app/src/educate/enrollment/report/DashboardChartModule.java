package educate.enrollment.report;

public class DashboardChartModule extends StudentStatisticModule {
	
	@Override
	public String doAction() throws Exception {
		statByProgram();
		return path + "dashboard/chartByProgram.vm";
	}
	
	protected void setChartType(String chartType) throws Exception {
		if ( "program".equals(chartType)) statByProgram();
		else if ( "intake".equals(chartType)) statByIntake();
		else statByProgram();		
	}

}
