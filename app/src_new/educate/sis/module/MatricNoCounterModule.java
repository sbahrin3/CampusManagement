/**
 * 
 */
package educate.sis.module;

import java.util.ArrayList;
import java.util.List;

import educate.db.DbPersistence;
import educate.sis.struct.entity.MatricNumPrefix;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class MatricNoCounterModule extends LebahModule {

	private DbPersistence db = new DbPersistence();
	private String path = "apps/matricNoPrefix";
	
	public void preProcess() {
		context.put("util", new lebah.util.Util());
	}
	
	@Override
	public String start() {
		try {
			createRefId(db);
		} catch (Exception e) {
			e.printStackTrace();
		}
		listPrefixes();
		return path + "/start.vm";
	}

	private void listPrefixes() {
		List<MatricNumPrefix> _matrics = new ArrayList<MatricNumPrefix>();
		List<MatricNumPrefix> matrics = db.list("select m from MatricNumPrefix m where m.id <> 'template' order by m.prefix");
		for ( MatricNumPrefix m : matrics ) {
			if ( !m.getPrefix().contains("&")) {
				_matrics.add(m);
			}
		}
		context.put("matrics", _matrics);
	}
	
	@Command("deletePrefix")
	public String deletePrefix() throws Exception {
		String refId = getParam("refId");
		MatricNumPrefix prefix = (MatricNumPrefix) db.get("select p from MatricNumPrefix p where p.refId = '" + refId + "'");
		db.begin();
		db.remove(prefix);
		db.commit();
		listPrefixes();
		return path + "/matrics.vm";
	}
	
	@Command("deletePrefixes")
	public String deletePrefixes() throws Exception {
		String[] refIds = request.getParameterValues("refIds");
		if ( refIds != null ) {
			List<MatricNumPrefix> deletes = new ArrayList<MatricNumPrefix>();
			for ( String refId : refIds ) {
				MatricNumPrefix prefix = (MatricNumPrefix) db.get("select p from MatricNumPrefix p where p.refId = '" + refId + "'");
				deletes.add(prefix);
			}
			db.begin();
			for ( MatricNumPrefix prefix : deletes ) 
				db.remove(prefix);
			db.commit();
		}
		listPrefixes();
		return path + "/matrics.vm";
	}
	@Command("updateCounter")
	public String updateCounter() throws Exception {
		
		String refId = getParam("refId");
		MatricNumPrefix prefix = (MatricNumPrefix) db.get("select p from MatricNumPrefix p where p.refId = '" + refId + "'");

		int counter = Integer.parseInt(getParam("counter_" + refId));
		
		db.begin();
		prefix.setCounter(counter);
		db.commit();
		return path + "/updateCounter.vm";
	}
	
	public static void main(String[] args) throws Exception {
		DbPersistence db = new DbPersistence();
		createRefId(db);
		
	}

	private static void createRefId(DbPersistence db) throws Exception {
		List<MatricNumPrefix> prefixes = db.list("select m from MatricNumPrefix m");
		for ( MatricNumPrefix p : prefixes ) {
			
			if ( p.getRefId() == null || "".equals(p.getRefId())) {
				db.begin();
				p.setRefId(lebah.db.UniqueID.getUID());
				db.commit();
			}
		}
	}

}
