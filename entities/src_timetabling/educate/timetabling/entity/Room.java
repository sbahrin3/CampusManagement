package educate.timetabling.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name="ttb_room")
public class Room {
	@Id
	private String id;
	private String code;
	private String name;
	private int capacity;
	private int examCapacity;
	private int epuCapacity;
	private int sequence;
	private int combined;
	private int grouped;
	private int zoneNumber;
	private int availability;
	private String description;
	
	@Temporal(TemporalType.DATE)
	private Date dateNotAvailableFrom;
	@Temporal(TemporalType.DATE)
	private Date dateNotAvailableTo;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateTimeNotAvailableFrom;
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateTimeNotAvailableTo;
	
	private String notAvailableRemarks;
	
	private int imported;
	
	@OneToMany(cascade=CascadeType.PERSIST)
	private List<LearningActivityType> activityTypes;

	
	@ManyToOne @JoinColumn(name="floor_id")
	private Floor floor;
	
	@ManyToOne @JoinColumn(name="department_id")
	private Department department;
	
	//@ManyToOne @JoinColumn(name="sub_cat_type_id")
	//private SubCategoryType subCategoryType;
	
	@ManyToOne @JoinColumn(name="category_id")
	private Category category;
	
	@ManyToOne @JoinColumn(name="subcattypedtl_id")
	private SubCategoryTypeDetail subCategoryTypeDetail;
	
	@OneToMany
	private List<Room> combinedRooms;
	
	@Temporal(TemporalType.DATE)
	private Date combinedRoomsDateFrom;
	@Temporal(TemporalType.DATE)
	private Date combinedRoomsDateTo;
	
	private int allowDelete;
	
	public Room() {
		setId(lebah.db.UniqueID.getUID());
		this.availability = 1;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getCode() {
		if ( code == null || "".equals(code)) {
			code = getId();
		}
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		if ( name == null || "".equals(name)) {
			name = getCode();
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public boolean getCombined() {
		return combined == 1;
	}

	public void setCombined(boolean combined) {
		this.combined = combined ? 1 : 0;
	}

	public List<Room> getCombinedRooms() {
		if ( combinedRooms == null ) combinedRooms = new ArrayList<Room>();
		return combinedRooms;
	}

	public void setCombinedRooms(List<Room> combinedRooms) {
		this.combinedRooms = combinedRooms;
	}

	public boolean getGrouped() {
		return grouped == 1;
	}

	public void setGrouped(boolean grouped) {
		this.grouped = grouped ? 1 : 0;
	}

	public int getZoneNumber() {
		return zoneNumber;
	}

	public void setZoneNumber(int zoneNumber) {
		this.zoneNumber = zoneNumber;
	}

	public boolean getAvailability() {
		return availability == 1;
	}

	public void setAvailability(boolean availability) {
		this.availability = availability ? 1 : 0;
	}

	public String getDescription() {
		/*
		if ( description == null || "".equals(description)) {
			description = getName();
		}
		*/
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Floor getFloor() {
		return floor;
	}

	public void setFloor(Floor floor) {
		this.floor = floor;
	}

	public int getExamCapacity() {
		return examCapacity;
	}

	public void setExamCapacity(int examCapacity) {
		this.examCapacity = examCapacity;
	}

	public List<LearningActivityType> getActivityTypes() {
		if ( activityTypes == null ) activityTypes = new ArrayList<LearningActivityType>();
		return activityTypes;
	}

	public void setActivityTypes(List<LearningActivityType> activityTypes) {
		this.activityTypes = activityTypes;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	/*
	public SubCategoryType getSubCategoryType() {
		return subCategoryType;
	}

	public void setSubCategoryType(SubCategoryType subCategoryType) {
		this.subCategoryType = subCategoryType;
	}
	*/

	public boolean getImported() {
		return imported == 1;
	}

	public void setImported(boolean imported) {
		this.imported = imported ? 1 : 0;
	}

	public Date getDateNotAvailableFrom() {
		return dateNotAvailableFrom;
	}

	public void setDateNotAvailableFrom(Date dateNotAvailableFrom) {
		this.dateNotAvailableFrom = dateNotAvailableFrom;
	}

	public Date getDateNotAvailableTo() {
		return dateNotAvailableTo;
	}

	public void setDateNotAvailableTo(Date dateNotAvailableTo) {
		this.dateNotAvailableTo = dateNotAvailableTo;
	}

	public String getNotAvailableRemarks() {
		return notAvailableRemarks;
	}

	public void setNotAvailableRemarks(String notAvailableRemarks) {
		this.notAvailableRemarks = notAvailableRemarks;
	}

	public Date getDateTimeNotAvailableFrom() {
		return dateTimeNotAvailableFrom;
	}

	public void setDateTimeNotAvailableFrom(Date dateTimeNotAvailableFrom) {
		this.dateTimeNotAvailableFrom = dateTimeNotAvailableFrom;
	}

	public Date getDateTimeNotAvailableTo() {
		return dateTimeNotAvailableTo;
	}

	public void setDateTimeNotAvailableTo(Date dateTimeNotAvailableTo) {
		this.dateTimeNotAvailableTo = dateTimeNotAvailableTo;
	}

	public int getEpuCapacity() {
		return epuCapacity;
	}

	public void setEpuCapacity(int epuCapacity) {
		this.epuCapacity = epuCapacity;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public SubCategoryTypeDetail getSubCategoryTypeDetail() {
		return subCategoryTypeDetail;
	}

	public void setSubCategoryTypeDetail(SubCategoryTypeDetail subCategoryTypeDetail) {
		this.subCategoryTypeDetail = subCategoryTypeDetail;
	}

	public Date getCombinedRoomsDateFrom() {
		return combinedRoomsDateFrom;
	}

	public void setCombinedRoomsDateFrom(Date combinedRoomsDateFrom) {
		this.combinedRoomsDateFrom = combinedRoomsDateFrom;
	}

	public Date getCombinedRoomsDateTo() {
		return combinedRoomsDateTo;
	}

	public void setCombinedRoomsDateTo(Date combinedRoomsDateTo) {
		this.combinedRoomsDateTo = combinedRoomsDateTo;
	}

	public boolean getAllowDelete() {
		return allowDelete == 1;
	}

	public void setAllowDelete(boolean allowDelete) {
		this.allowDelete = allowDelete ? 1 : 0;
	}


	
	
}
