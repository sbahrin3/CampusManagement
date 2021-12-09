package educate.enrollment.report;

public class DashboardChartByIntake extends DashboardChartModule {
	
	@Override
	public String doAction() throws Exception {
		setChartType("intake");
		return path + "dashboard/chartByIntake.vm";
	}
	

}
