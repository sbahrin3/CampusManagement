package educate.studentaffair.module;

import java.util.List;

import educate.db.DbPersistence;
import educate.studentaffair.entity.StudentClubPosition;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class StudentClubPositionModule extends LebahModule {
	
	private String path = "studentaffair/student_club_position";
	private DbPersistence db = new DbPersistence();
	
	@Override
	public String start() {
		List<StudentClubPosition> positions = db.list("select p from StudentClubPosition p order by p.sequence");
		context.put("positions", positions);
		return path + "/start.vm";
	}	
	
	@Command("addPosition")
	public String addPosition() throws Exception {
		
		return path + "/position.vm";
	}
	
	@Command("editPosition")
	public String editPosition() throws Exception {
		String positionId = getParam("positionId");
		StudentClubPosition position = db.find(StudentClubPosition.class, positionId);
		context.put("position", position);
		return path + "/position.vm";
	}
	
	@Command("savePosition")
	public String savePosition() throws Exception {
		String positionId = getParam("positionId");
		StudentClubPosition position = null;
		int seq = 0;
		if ( !"".equals(positionId)) {
			position = db.find(StudentClubPosition.class, positionId);
		}
		else {
			position = new StudentClubPosition();

			Integer iSeq = (Integer) db.get("select max(p.sequence) from StudentClubPosition p");
			seq = iSeq != null ? iSeq.intValue() + 1 : 1;

			position.setSequence(seq);
		}
		
		position.setName(getParam("position_name"));
		
		db.begin();
		if ( "".equals(positionId)) db.persist(position);
		db.commit();
		
		List<StudentClubPosition> positions = db.list("select p from StudentClubPosition p order by p.sequence");
		context.put("positions", positions);
		return path + "/positions.vm";
	}

	@Command("deletePosition")
	public String deletePosition() throws Exception {
		String positionId = getParam("positionId");
		StudentClubPosition position = db.find(StudentClubPosition.class, positionId);
		
		Long counter = (Long) db.get("select count(c) from StudentClub c where c.position.id = '" + positionId + "'");
		int count = (int) counter.longValue();
		
		if ( count == 0 ) {
			db.begin();
			db.remove(position);
			db.commit();
		}
		
		List<StudentClubPosition> positions = db.list("select p from StudentClubPosition p order by p.sequence");
		context.put("positions", positions);
		return path + "/positions.vm";
		
	}

}
