package educate.questionnare;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.enrollment.entity.StudentSubject;
import educate.questionnare.entity.TEPart;
import educate.questionnare.entity.TEPartQuestion;
import educate.questionnare.entity.TEQuestionnare;
import educate.questionnare.entity.TESet;
import educate.questionnare.entity.TEUserLog;
import educate.questionnare.entity.TEUserPath;
import educate.sis.module.StudentStatusUtil;
import educate.sis.struct.entity.Subject;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class ListQuestionnareModule extends LebahModule {

	private DbPersistence db = new DbPersistence();
	private String path = "questionnare/list";

	public void preProcess() {
		System.out.println(command);
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("util", new lebah.util.Util());
	}

	@Override
	public String start() {
		try {
			listQuestionnareGroups();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path + "/start.vm";
	}

	private void listQuestionnareGroups() throws Exception {
		
		List<Subject> subjects = new ArrayList<Subject>();
		
		String role = (String) request.getSession().getAttribute("_portal_role");
		if ( "student".equals(role)) {
			String matric = (String) request.getSession().getAttribute("_portal_login");
			System.out.println("matric = " + matric);
			Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matric + "'");
			if ( student != null ) {
				StudentStatusUtil u = new StudentStatusUtil();
				StudentStatus currentStatus = u.getCurrentStudentStatus(student);
				Set<StudentSubject> studentSubjects = currentStatus.getStudentSubjects();
				for ( StudentSubject s : studentSubjects ) {
					subjects.add(s.getSubject());
				}
			}
		}
		
		Hashtable h = new Hashtable();
		h.put("date", new Date());

		List<TEQuestionnare> questionnares = db
				.list(
						"select q from TEQuestionnare q where :date between q.startDate and q.endDate order by q.set.id, q.subject.id, q.startDate desc",
						h);

		List<TEQuestionnare> groups = new ArrayList<TEQuestionnare>();
		context.put("questionnares", groups);
		String groupId = "";
		for (TEQuestionnare q : questionnares) {
			if (!groupId.equals(q.getSet().getId() + "-"
					+ (q.getSubject() != null ? q.getSubject().getId() : "")
					+ "-" + q.getStartDate())) {
				if ( q.getSubject() == null ) {
					groups.add(q);
				} else {
					if ( subjects.contains(q.getSubject())) {
						groups.add(q);
					}
				}
				groupId = q.getSet().getId()
						+ "-"
						+ (q.getSubject() != null ? q.getSubject().getId() : "")
						+ "-" + q.getStartDate();
			}
		}
	}

	@Command("listQuestionnares")
	public String listQuestionnares() throws Exception {

		String setId = getParam("setId");
		String subjectId = getParam("subjectId");
		String dateStart = getParam("dateStart");

		Date startDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateStart);

		Hashtable h = new Hashtable();
		h.put("startDate", startDate);
		h.put("date", new Date());

		List<TEQuestionnare> questionnares = null;

		if (!"".equals(subjectId))
			questionnares = db
					.list(
							"select q from TEQuestionnare q where q.set.id = '"
									+ setId
									+ "' and q.subject.id = '"
									+ subjectId
									+ "' and q.startDate = :startDate and :date between q.startDate and q.endDate order by q.startDate desc",
							h);
		else
			questionnares = db
					.list(
							"select q from TEQuestionnare q where q.set.id = '"
									+ setId
									+ "' and q.startDate = :startDate and :date between q.startDate and q.endDate order by q.startDate desc",
							h);

		context.put("questionnares", questionnares);

		TEQuestionnare que = questionnares.get(0);
		context.put("questionnare", que);
		TEPart part = que.getSet().getParts().get(0);
		String instruction = part.getInstructionText();
		context.put("instruction", instruction);

		String userId = (String) request.getSession().getAttribute(
				"_portal_login");
		List<TEUserLog> userLogs = db
				.list(
						"select u from TEUserLog u where u.questionnare.startDate = :startDate and :date between u.questionnare.startDate and u.questionnare.endDate and u.userId = '"
								+ userId + "'", h);
		Map<String, String> takeStatus = new HashMap<String, String>();
		context.put("takeStatus", takeStatus);
		for (TEUserLog u : userLogs) {
			takeStatus.put(u.getQuestionnare().getId(),
					u.getComplete() ? "complete" : "take");
		}
		return path + "/questionnares.vm";
	}

	@Command("listGroups")
	public String listGroups() {
		try {
			listQuestionnareGroups();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path + "/groups.vm";
	}

	@Command("testRun")
	public String testRun() throws Exception {
		String setId = getParam("setId");
		TESet set = db.find(TESet.class, setId);
		context.put("set", set);
		context.put("questionnareId", getParam("questionnareId"));
		context.put("userId", (String) request.getSession().getAttribute(
				"_portal_login"));
		return path + "/testRun.vm";
	}

	@Command("updateStatus")
	public String updateStatus() throws Exception {

		String userId = (String) request.getSession().getAttribute(
				"_portal_login");
		String userSession = userId + "." + getParam("questionnareId");
		TEUserLog userLog = (TEUserLog) db
				.get("select u from TEUserLog u where u.userSessionId = '"
						+ userSession + "'");

		// TEQuestionnare q = db.find(TEQuestionnare.class,
		// getParam("questionnareId"));
		// List<TEUserPath> userPaths = userLog.getUserPaths();
		// calculatePercentage(q, userPaths);

		// double percentage = ((double) totalAnswers/totalQuestions ) * 100;
		if (userLog.getComplete()) {
			context.put("status", "complete");
		} else
			context.put("status", "take");
		//		
		//		
		// context.put("percentage", new
		// DecimalFormat("##").format(percentage));

		return path + "/status.vm";
	}

	private void calculatePercentage(TEQuestionnare q,
			List<TEUserPath> userPaths) {
		List<TEPartQuestion> partQuestions = new ArrayList<TEPartQuestion>();
		for (TEPart p : q.getSet().getParts()) {
			for (TEPartQuestion qs : p.getQuestions()) {
				partQuestions.add(qs);
			}
		}

		int numTaken = userPaths.size();
		int totalCount = 0;
		if (numTaken > 0) {
			TEUserPath lastPath = userPaths.get(numTaken - 1);
			TEPartQuestion lastTakenQuestion = lastPath.getQuestion();
			int cnt = 0;
			for (TEPartQuestion qs : partQuestions) {
				cnt++;
				if (qs.getId().equals(lastTakenQuestion.getId())) {
					break;
				}
			}
			totalCount = numTaken + (partQuestions.size() - cnt);

			System.out.println("numTaken = " + numTaken);
			System.out.println("out of = " + totalCount);

		} else {

		}
	}

}
