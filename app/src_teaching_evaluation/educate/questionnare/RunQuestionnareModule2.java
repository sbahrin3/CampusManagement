/**
 * 
 */
package educate.questionnare;

import java.text.SimpleDateFormat;
import java.util.List;

import educate.db.DbPersistence;
import educate.questionnare.entity.TEPart;
import educate.questionnare.entity.TEPartQuestion;
import educate.questionnare.entity.TESet;
import lebah.portal.action.LebahModule;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class RunQuestionnareModule2 extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "questionnare/run2";
	
	public void preProcess() {
		context.put("util", new lebah.util.Util());
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
	}

	@Override
	public String start() {
		
		String setId = "1412219166285";
		TESet set = db.find(TESet.class, setId);
		context.put("set", set);
		
		List<TEPart> parts = set.getParts();
		context.put("parts", parts);
		
		return path + "/start.vm";
	}
	
	public static void main(String[] args) throws Exception {
		DbPersistence db = new DbPersistence();
		String setId = "1412219166285";
		TESet set = db.find(TESet.class, setId);
		
		List<TEPart> parts = set.getParts();
		List<TEPartQuestion> partQuestions = parts.get(0).getQuestions();
		int i = 0;
		for ( TEPartQuestion pq : partQuestions ) {
			i++;
			String questionText = pq.getQuestion().getQuestionText();
			System.out.println(i + ") " + questionText);
			pq.getQuestion().getType();
		}
		
	}

}
