package educate.chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lebah.servlets.IServlet;

public class ChatServlet implements IServlet {
	
	static Hashtable<String, Room> rooms = new Hashtable<String, Room>();
	
    public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	HttpSession session = request.getSession();
    	PrintWriter out = response.getWriter();
    	
    	String action = request.getParameter("action");
    	if ( "doMessage".equals(action)) {
    		doChatMessage(out, request);
    	}
    	else if ( "joinRoom".equals(action)) {
    		joinRoom(out, request);
    	}
    	else if ( "leaveRoom".equals(action)) {
    		leaveRoom(out, request);
    	}
    	else if ( "listRoom".equals(action)) {
    		listRoom(out, request);
    	}
    	else if ( "listUser".equals(action)) {
    		listUser(out, request);
    	}

    }
    
    private void listRoom(PrintWriter out, HttpServletRequest request) {
    	String json = "{\"rooms\": [";
    	int c = 0;
    	List<String> list = Collections.list(rooms.keys());
    	Collections.sort(list);
    	for ( String r : list ) {
    		c++;
			json += "\"" + r + "\"";
			if ( c < rooms.size() ) json += ", ";
    	}
		json += "]}";
		out.print(json);
	}
    
    private void listUser(PrintWriter out, HttpServletRequest request) {
    	String roomName = request.getParameter("room");
    	Room room = rooms.get(roomName);
    	if ( room != null) {
	    	String json = "{\"users\": [";
	    	int c = 0;
	    	Vector<String> users = room.getUsers();
	    	Collections.sort(users);
			for ( String u :  users ) {
				c++;
				json += "\"" + u + "\"";
				if ( c < users.size() ) json += ", ";
			}
			json += "]}";
			out.print(json);
    	}
    	else {
	    	String json = "{\"users\": []}";
			out.print(json);
    	}
	}

	private void joinRoom(PrintWriter out, HttpServletRequest request) {
    	String roomName = request.getParameter("room");
    	String user = request.getParameter("user");
    	Room room = rooms.get(roomName);
    	if ( room == null ) {
    		room = new Room();
    		rooms.put(roomName, room);
    	}
    	//is this user already in the room
    	Vector users = room.getUsers();
    	if ( !users.contains(user) && !"info".equalsIgnoreCase(user)) {
    		room.addUser(user);
        	String msgStr = "INFO|" + user + " join in. "; // + roomName;
        	room.addMessage(msgStr);
        	out.print("true");    		
    	}
    	else {
    		out.print("false");
    	}
    	

    }
    
    private void leaveRoom(PrintWriter out, HttpServletRequest request) {
    	String roomName = request.getParameter("room");
    	String user = request.getParameter("user");
    	Room room = rooms.get(roomName);
    	if ( room == null ) {
    		room = new Room();
    		rooms.put(roomName, room);
    	}
    	//is this user already in the room
    	Vector users = room.getUsers();
    	if ( users.contains(user)) {
    		room.removeUser(user);
    	}
    	String msgStr = "INFO|" + user + " left. "; // + roomName;
    	room.addMessage(msgStr);
   	
    	out.print("true");
    }

	private void doChatMessage(PrintWriter out, HttpServletRequest request) {
		String roomName = request.getParameter("room");
    	String user = request.getParameter("user");
    	String message = request.getParameter("message");
    	

    	Room room = rooms.get(roomName);
    	/*
    	if ( room == null ) {
    		room = new Room();
    		rooms.put(roomName, room);
    	}
    	*/
    	String json = "";
    	if ( room != null ) {
	    	//is this user already in the room
	    	Vector users = room.getUsers();
	    	if ( !users.contains(user)) {
	    		//room.addUser(user);
	        	json = "{\"messages\": {";
	        	json += "\"message\":["; 
		    	json += "]";
		    	json += "}}";   
	    	}
	    	else {
		    	//put messages into room
		    	String msgStr = user + "|" + message;
		    	room.addMessage(msgStr);
		    	
		    	//extract messages for this user
		    	Vector<String> messages = room.getUserMessages(user);
		    	json = "{\"messages\": {";
		    	json += "\"message\":[";
		    	int len = messages.size();
		    	int cnt = 0;
		    	for ( String str : messages ) {
		    		String x = str.substring(0, str.indexOf("|"));
		    		String y = str.substring(str.indexOf("|")+1);
			    	boolean empty = false;
			    	if ( x == null || "".equals(x)) empty = true;
			    	cnt ++;
			    	if ( !empty ) {
			    		
			    		json += "{";
			    		json += "\"user\" : \"" + x +"\", ";
			    		json += "\"text\" : \"" + y +"\" ";
			    		json += "}";
			    	}
			    	if ( cnt < len ) json += ", ";
		    	}
		    	json += "]";
		    	json += "}}";
		    	room.removeMessage(user);
	    	}

    	}
    	else {
        	json = "{\"messages\": {";
        	json += "\"message\":["; 
	    	json += "]";
	    	json += "}}";   
    	}
    	System.out.println(json);
    	out.print(json);
	}

}
