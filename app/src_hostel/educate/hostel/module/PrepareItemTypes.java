package educate.hostel.module;

import java.util.List;

import educate.db.DbPersistence;
import educate.hostel.entity.ItemType;

public class PrepareItemTypes {
	
	public static void main(String[] args) throws Exception {
		DbPersistence db = new DbPersistence();
		db.begin();
		db.executeUpdate("delete from ItemType t");
		db.commit();
		
		/*
		 * Types:
		 * home furniture HF
		 * office furniture  OF
		 * consumer electronic CE
		 * kitchen utensil KU
		 * bathroom utensil BU
		 * bedroom utensil RU
		 * household utensil HU
		 */
		
		db.begin();
		ItemType t = createItemType("home_furniture", "HF", "Home Furniture");
		db.persist(t);
		db.commit();
		
		db.begin();
		t = createItemType("office_furniture", "OF", "Office Furniture");
		db.persist(t);
		db.commit();
		
		db.begin();
		t = createItemType("consumer_electronic", "CE", "Consumer Electronic");
		db.persist(t);
		db.commit();		
		
		db.begin();
		t = createItemType("kitchen_utensil", "KU", "Kitchen Utensil");
		db.persist(t);
		db.commit();	
		
		db.begin();
		t = createItemType("bathroom_utensil", "BU", "Bathroom Utensil");
		db.persist(t);
		db.commit();		
		
		db.begin();
		t = createItemType("bedroom_utensil", "RU", "Bedroom Utensil");
		db.persist(t);
		db.commit();
		
		db.begin();
		t = createItemType("household_utensil", "KU", "Household Utensil");
		db.persist(t);
		db.commit();	
		
		
		List<ItemType> types = db.list("select t from ItemType t");
		for ( ItemType type : types ) {
			System.out.println(type.getCode() + ", " + type.getName());
		}

	}

	private static ItemType createItemType(String id, String code, String name) {
		ItemType t = new ItemType();
		t.setId(id);
		t.setCode(code);
		t.setName(name);
		return t;
	}

}
