/**
 * 
 */
package educate.sis.registration;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class SetupAcademicSessionAdminModule extends SetupIntakeModule2 {
	
	@Override
	public String doAction() throws Exception {
		modeAdmin = true;
		return super.doAction();
		
	}

}
