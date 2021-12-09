package educate.sis.module;

public class SponsorPaymentModule extends SponsorInvoiceModule {
	
	@Override
	public String doAction() throws Exception {
		payment = true;
		return super.doAction();
	}

}
