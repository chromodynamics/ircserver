package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseFacade {

	Connection conn;
	Statement statement;

	public DatabaseFacade() {
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:users.db");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		Connection c = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:users.db");

			Statement statement = c.createStatement();
			String sql = "INSERT INTO USER (NICK) VALUES ('eith');";
			statement.executeUpdate(sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void insertUser(String nick, String pass) {
		try {
			String sql = "INSERT INTO USER (NICK, PASS) VALUES ('" + nick + "', '" + pass + "');";
			statement = conn.createStatement();
			statement.executeUpdate(sql);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean userExists(String nick) {
		try {
			String sql = "SELECT NICK FROM USER WHERE NICK = '" + nick + "';";
			statement = conn.createStatement();
			ResultSet results = statement.executeQuery(sql);

			if (results.next()) {
				statement.close();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean checkPassword(String nick, String pass) {
		try {
			String sql = "SELECT NICK, PASS FROM USER WHERE NICK ='" + nick + "' AND PASS='" + pass + "';";
			statement = conn.createStatement();
			ResultSet results = statement.executeQuery(sql);
			
			if (results.next()) {
				statement.close();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void insertChannel() {

	}
}
