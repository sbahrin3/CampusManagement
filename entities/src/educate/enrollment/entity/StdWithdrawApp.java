package educate.enrollment.entity;
import javax.persistence.*;
import java.util.Date;
import java.util.GregorianCalendar;


@Entity
@Table(name="enrl_stdwithdrawapp")
public class StdWithdrawApp {

	@Id
	private String id;
	private String reason;
	private String otherR;
	private String status;
	
	@Temporal(TemporalType.DATE)
	private Date reqDate;
	
	@OneToOne
	private Student student;
	
	public StdWithdrawApp(){
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getOtherR() {
		return otherR;
	}

	public void setOtherR(String otherR) {
		this.otherR = otherR;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getReqDate() {
		return reqDate;
	}

	public void setReqDate(Object d) {
		if(d instanceof Date) reqDate = (Date) d;
		else if(d instanceof String){
			setReqDate((String) d);
		}
	}
	
	public void setReqDate(String date){
		if (date == null || "".equals(date)) return;
		String separator = "-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setReqDate(new GregorianCalendar(year, month, day).getTime());
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}
	
}
