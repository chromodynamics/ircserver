package server;

import java.util.ArrayList;

public class Channel {

	private ArrayList<String> members;
	private String name;
	private String topic;
	
	public Channel(String name, String topic) {
		this.name = name;
		this.topic = topic;
	}
	
	public void addUser(String name) {
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
}
