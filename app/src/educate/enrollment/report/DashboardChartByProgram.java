package educate.enrollment.report;

public class DashboardChartByProgram extends DashboardChartModule {
	
	@Override
	public String doAction() throws Exception {
		setChartType("program");
		return path + "dashboard/chartByProgram.vm";
	}
	
}
