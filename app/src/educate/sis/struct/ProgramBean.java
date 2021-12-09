package educate.sis.struct;

import java.util.List;
import java.util.Vector;

import educate.sis.struct.entity.Program;
import lebah.db.PersistenceManager;

public class ProgramBean {
	private PersistenceManager pm;
	private Program program;
	
	public void add(Program prog) throws Exception {
		pm = new PersistenceManager();
		PersistenceManager.add(prog);
	}
	public void update(String id,Program prog)throws Exception{
		pm = new PersistenceManager();
		program = (Program)pm.find(Program.class).whereId(id).forUpdate();
		program.setCode(prog.getCode());
		program.setName(prog.getName());
		program.setAbbrev(prog.getAbbrev());
		program.setRegisterNo(prog.getRegisterNo());
		program.setPeriodScheme(prog.getPeriodScheme());
		program.setCourse(prog.getCourse());
		program.setLevel(prog.getLevel());
		pm.update();
		
	}
	public Vector<Program> getProgramCourse(String courseId) throws Exception{
		pm = new PersistenceManager();
		List<Program> l = pm.list("SELECT a FROM Program a WHERE a.course.id='"+courseId+"'");	
		Vector<Program> v = new Vector<Program>();
		v.addAll(l);
		return v;
	}
	
	public Program getProgram(String id) throws Exception{
		pm = new PersistenceManager();
		program = (Program)pm.find(Program.class,id);
		return program;
	}
	
	public void delete(String id)throws Exception{
		pm = new PersistenceManager();
		program = (Program)pm.find(Program.class,id);
		pm.delete(program);
	}
	
	public Vector<Program> getUnderGradsProgram() throws Exception{
		pm = new PersistenceManager();
		List<Program> l = pm.list("SELECT a FROM Program a WHERE a.level.code='1'");
		Vector<Program> v = new Vector<Program>();
		v.addAll(l);
		return v;
	}
	public Vector<Program> getPostGradsProgram() throws Exception{
		pm = new PersistenceManager();
		List<Program> l = pm.list("SELECT a FROM Program a WHERE a.level.code='2'");
		Vector<Program> v = new Vector<Program>();
		v.addAll(l);
		return v;
	}
	public Program getProgramByCode(String code)throws Exception{
		pm = new PersistenceManager();
		List<Program> l = pm.list("SELECT a FROM Program a WHERE a.code='"+code+"'");
		if(l.size()>0){
			program = l.get(0);
		}
		return program;
	}
}
