package project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class AirlineReservation {
	
	static final String DB_URL = "jdbc:mysql://localhost/";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "[insert password]";
	private static Connection conn = null;
	private static Statement statement = null;

	public static void main(String[] args) throws SQLException {
		try {
			// Call all methods here
			createDatabase();
			createTable();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (statement != null)
					statement.close();
			} catch (SQLException se2) {
			}

			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		System.out.println("Goodbye!");
	}

	private static void createDatabase() throws SQLException {

		// Open a connection
		System.out.println("Connecting to database...");
		conn = DriverManager.getConnection(DB_URL + "?serverTimezone=UTC", USER, PASS);

		String queryDrop = "DROP DATABASE IF EXISTS airline";
		Statement stmtDrop = conn.createStatement();
		stmtDrop.execute(queryDrop);

		// Create a database named airline
		System.out.println("Creating database...");
		statement = conn.createStatement();

		String sql = "CREATE DATABASE airline";
		statement.executeUpdate(sql);
		System.out.println("Database created successfully...");
	}

	private static void createTable() throws SQLException {
		// Open a connection and select the database named CS

		System.out.println("Connecting to database...");
		conn = DriverManager.getConnection(DB_URL + "airline?serverTimezone=UTC", USER, PASS);
		statement = conn.createStatement();

		String queryDrop = "DROP TABLE IF EXISTS Passenger";
		Statement stmtDrop = conn.createStatement();
		stmtDrop.execute(queryDrop);

		String createTablePassenger = "CREATE TABLE Passenger(" 
				+ "uID int primary key AUTO_INCREMENT, " 
				+ "name VARCHAR(30), "
				+ "numBags int, " 
				+ "totalBagWeight float)";
		statement.execute(createTablePassenger);
		System.out.println("Table called Passenger created successfully...");
		
		String createTableFlights = "CREATE TABLE Flights(" 
				+ "fID int primary key AUTO_INCREMENT, " 
				+ "planeID int references Planes(planeID), "
				+ "startID int references Location(locationID), " 
				+ "destID int references Location(locationID), "
				+ "time timestamp)";
		statement.execute(createTableFlights);
		System.out.println("Table called Flights created successfully...");

	}
}
