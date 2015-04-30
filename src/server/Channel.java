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
	
	public void addUser(User nick) {
		members.add(nick);
	}
	
	public void removeUser(User nick, String userMask) {
		members.remove(nick);
		for (User user : members) {
			user.sendMessage(":" + userMask + " PART " + name);
		}
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
	
	public void sendMessage(String message, String from) {
		for (User user : members) {
			if (!user.getNick().equals(from)) {
				user.sendMessage(message);
			}
		}
	}
}
