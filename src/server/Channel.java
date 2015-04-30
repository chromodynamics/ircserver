package server;

import java.util.ArrayList;
import java.util.List;

public class Channel {

	private ArrayList<User> members = new ArrayList<>();
	private String name;
	private String topic;
	
	public Channel(String name, String topic) {
		this.name = name;
		this.topic = topic;
	}
	
	public Channel(String name) {
		this(name, "");
	}
	
	public void addUser(User name) {
		members.add(name);
	}
	
	public void removeUser(String name) {
		members.remove(name);
	}
	
	public String getTopic() {
		return topic;
	}
	
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	public String getName() {
		return name;
	}
	
	public List<User> getMembers() {
		return members;
	}
	
	public void msg(String msg) {
		
	}
}
