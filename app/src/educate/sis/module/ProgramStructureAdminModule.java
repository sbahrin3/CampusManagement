/**
 * 
 */
package educate.sis.module;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class ProgramStructureAdminModule extends ProgramStructureModule2 {
	
	public String doAction() throws Exception {
		isAdmin = true;
		return super.doAction();
	}

}
