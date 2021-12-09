package educate.facilities.entity;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import educate.sis.struct.entity.LearningCentre;

@Entity @Table(name="fac_building")
public class FBuilding {

	private String id;
	private String code;
	private String name;
	
}
