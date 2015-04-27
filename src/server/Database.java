package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Database {

	public static void main(String args[]) {
		Connection c = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:users.db");
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Opened database successfully");
	}
}
