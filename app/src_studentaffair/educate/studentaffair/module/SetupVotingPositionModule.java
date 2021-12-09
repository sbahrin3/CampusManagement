package educate.studentaffair.module;

import java.util.List;

import educate.db.DbPersistence;
import educate.studentaffair.entity.VotingPosition;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class SetupVotingPositionModule extends LebahModule {

	private String path = "studentaffair/voting_position";
	private DbPersistence db = new DbPersistence();	
	
	@Override
	public String start() {
		List<VotingPosition> positions = db.list("select c from VotingPosition c order by c.sequence");
		context.put("positions", positions);
		return path + "/start.vm";
	}
	
	@Command("getVotingPosition")
	public String getVotingPosition() throws Exception {
		String positionId = getParam("positionId");
		VotingPosition position = null;
		if ( !"".equals(positionId)) {
			position = db.find(VotingPosition.class, positionId);
			context.put("position", position);
		} else context.remove("position");
		return path + "/getVotingPosition.vm";
	}
	
	@Command("saveVotingPosition")
	public String saveVotingPosition() throws Exception {
		String positionId = getParam("positionId");
		VotingPosition position = null;
		int seq = 1;
		if ( !"".equals(positionId)) {
			position = db.find(VotingPosition.class, positionId);
		} else {
			position = new VotingPosition();
			//to get the sequence
			Integer sequence = (Integer) db.get("select max(p.sequence) from VotingPosition p");
			if ( sequence != null ) {
				seq = sequence.intValue();
				seq++;
			}
		}
		
		db.begin();
		position.setName(getParam("name"));
		if ( "".equals(positionId)) {
			position.setSequence(seq);
			db.persist(position);
		}
		db.commit();
		
		List<VotingPosition> positions = db.list("select c from VotingPosition c order by c.sequence");
		context.put("positions", positions);
		return path + "/listVotingPositions.vm";
	}
	
	@Command("deleteVotingPosition")
	public String deleteVotingPosition() throws Exception {
		String positionId = getParam("positionId");
		
		if ( db.list("select c from VotingCandidate c where c.position.id = '" + positionId + "'").size() == 0 ) {
		
			VotingPosition position = db.find(VotingPosition.class, positionId);
			db.begin();
			db.remove(position);
			db.commit();
		
		}
		List<VotingPosition> positions = db.list("select c from VotingPosition c order by c.sequence");
		context.put("positions", positions);
		return path + "/listVotingPositions.vm";
	}
	
	public static void main(String[] args) throws Exception {
		DbPersistence db = new DbPersistence();
		Integer sequence = (Integer) db.get("select max(p.sequence) from VotingPosition p");
		int seq = sequence.intValue();
		System.out.println(seq);
	}

}
