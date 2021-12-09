/**
 * 
 */
package educate.timetabling.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import educate.sis.struct.entity.Faculty;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */

@Entity
@Table(name="ttb_room_allocation")
public class RoomAllocation {
	
	@Id @Column(length=50)
	private String id;
	private Faculty faculty;
	private ClassroomType classroomType;
	private List<Room> rooms;
	
	public RoomAllocation() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Faculty getFaculty() {
		return faculty;
	}
	public void setFaculty(Faculty faculty) {
		this.faculty = faculty;
	}
	public ClassroomType getClassroomType() {
		return classroomType;
	}
	public void setClassroomType(ClassroomType classroomType) {
		this.classroomType = classroomType;
	}
	public List<Room> getRooms() {
		return rooms;
	}
	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}

}
