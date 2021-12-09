package educate.chat;

import java.util.Hashtable;
import java.util.Vector;

public class Room {
	
	private String name;
	private Vector<String> users = new Vector<String>();
	private Hashtable<String, Vector> messages = new Hashtable<String, Vector>();
	
	public Room() {
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Hashtable getTextMessages() {
		return messages;
	}

	public void setTextMessages(Hashtable textMessages) {
		this.messages = textMessages;
	}

	public Vector<String> getUsers() {
		return users;
	}

	public void setUsers(Vector users) {
		this.users = users;
	}
	
	public void addUser(String user) {
		users.addElement(user);
	}
	
	public void removeUser(String user) {
		users.remove(user);
	}
	
	public Vector<String> getUserMessages(String user) {
		return messages.get(user);
	}
	
	public void removeMessage(String user) {
		messages.remove(user);
	}
	
	public void putUserMessages(String message) {
		
	}

	@Override
	public boolean equals(Object o) {
		if ( o == null ) return false;
		Room r = (Room) o;
		if ( r.getName().equals(name)) return true;
		return false;
	}

	public void addMessage(String message) {
		
		System.out.println(message);
		
		String user = message.substring(0, message.indexOf("|"));
		String text = message.substring(message.indexOf("|")+1);
		
		for ( String u : users ) {
			Vector<String> v = messages.get(u);
			if ( v == null ) {
				v = new Vector<String>();
				messages.put(u, v);
			}
			v.addElement(user + "|" + text);			
		}
		

	}

}
