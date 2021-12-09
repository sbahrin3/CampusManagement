package educate.questionnare;

import educate.db.DbPersistence;
import educate.questionnare.entity.TEPart;
import educate.questionnare.entity.TEPartQuestion;

public class Testing {
	
	public static void main(String[] args) throws Exception {
		DbPersistence db = new DbPersistence();
		String partId = "1392377455681";
		
		TEPart part = db.find(TEPart.class, partId);
		System.out.println(part.getTitle());
		
		for ( TEPartQuestion q : part.getQuestions() ) {
			System.out.println(q.getRefNo() + q.getPart().getId());
		}
	}

}
