package educate.hostel.entity;

import java.util.*;

import javax.persistence.*;

@Entity
@Table(name="hostel_building")
public class Building implements Comparable {
	
	@Id
	private String id;
	private String code;
	private String name;
	@ManyToOne @JoinColumn(name="hostel_id")
	private Hostel hostel;
	@OneToMany(cascade=CascadeType.ALL, mappedBy="building")
	private List<Block> blocks;
	
	private int sequence;
	
	public Building() {
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Hostel getHostel() {
		return hostel;
	}

	public void setHostel(Hostel hostel) {
		this.hostel = hostel;
	}

	public List<Block> getBlocks() {
		return blocks;
	}

	public void setBlocks(List<Block> blocks) {
		this.blocks = blocks;
	}
	
	public List<Block> getBlockList() {
		List<Block> list = new ArrayList<Block>();
		list.addAll(blocks);
		Collections.sort(list);
		return list;
	}

	public void addBlock(Block block) {
		if ( blocks == null ) blocks = new ArrayList<Block>();
		block.setBuilding(this);
		blocks.add(block);
	}
	
	public void removeBlock(Block block) {
		blocks.remove(block);
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	@Override
	public int compareTo(Object o) {
		Building r = (Building) o;
		if ( this.getSequence() > r.getSequence() ) return 1;
		else if ( this.getSequence() < r.getSequence() ) return -1;
		return 0;
	}
	
}
