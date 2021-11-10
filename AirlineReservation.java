package project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AirlineReservation {

	static final String DB_URL = "jdbc:mysql://localhost/";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "[insert password]";
	private static Connection conn = null;
	private static Statement statement = null;

	public static void main(String[] args) throws SQLException 
	{
		// Connect to database. Print message when successful.
		try 
		{
			System.out.println("Beginning connection to database...");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/airline?serverTimezone=UTC", USER, PASS);
			conn.setAutoCommit(true);

		} catch (SQLException e) 
		{
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		}

		if (conn != null) 
		{
			System.out.println("Successfully connected to database!");
			sop("");
		} else {
			System.out.println("Failed to make connection!");
			return;
		}
		
		// User requests
		try
		{
			registerPassenger(100, "Mary", 2, 50);
			noReservations();
			updateWeight(100, 100);
		}
		catch (SQLException x)
		{
			x.getMessage();
		}
	}

	/**
	 * Insert into Passenger table
	 * 
	 * @param uID primary key passenger ID
	 * @param name 
	 * @param numBags
	 * @param totalBagWeight
	 * @throws SQLException
	 */
	private static void registerPassenger(int uID, String name, int numBags, float totalBagWeight) throws SQLException 
	{
		sop("Processing request to register passenger: " + name + "(uID: " + uID + ")");
		String registerPassenger = "insert into passenger (uID, name, numBags, totalBagWeight) values (?, ?, ?, ?)";
		
		sop("Preparing statement...");
		PreparedStatement ps = conn.prepareStatement(registerPassenger);
		
		sop("\n" + "Initial state of Passenger table:");
		sop("uID\tname\tnumBags\ttotalBagWeight");
		ResultSet rs = ps.executeQuery("select * from passenger");
		while (rs.next())
		{
			sop(rs.getString("uID") + "\t" + rs.getString("name") + "\t" + rs.getString("numBags") + "\t" + rs.getString("totalBagWeight"));
		}
		sop("");
		
		// set all parameters before executing ps
		sop("Preparing to set arguments...");
		ps.setInt(1, uID);
		ps.setString(2, name);
		ps.setInt(3, numBags);
		ps.setFloat(4, totalBagWeight);
		
		sop("Preparing to execute statement...");
		ps.executeUpdate();
		
		sop ("Request has been submitted");
		sop("\n" + "Current state of Passenger table:");
		rs = ps.executeQuery("select * from passenger");
		while (rs.next())
		{
			sop(rs.getString("uID") + "\t" + rs.getString("name") + "\t" + rs.getString("numBags") + "\t" + rs.getString("totalBagWeight"));
		}
		sop("");
	}
	
	/**
	 * Lists all passengers who are not booked on any flights
	 * 
	 * @throws SQLException
	 */
	private static void noReservations() throws SQLException
	{
		sop("Processing request to view all passengers not booked on any flights:");

		String noReservations = "select uid from passenger natural left outer join reservations where fid is null";
		Statement s = conn.createStatement();
		
		sop("");
		sop("Initial list of passengers and their reservations (if any):");
		ResultSet rs = s.executeQuery("select * from passenger natural left outer join reservations");
		while (rs.next())
		{
			sop(rs.getString("uID") + "\t" + rs.getString("name") + "\t" + rs.getString("numBags") + "\t" + rs.getString("totalBagWeight") + "\t" 
					+ rs.getString("rID") + "\t" + rs.getString("fID") + "\t" + rs.getString("ticketType") + "\t" + rs.getString("ticketCost"));
		}
		sop("");
		
		rs = s.executeQuery(noReservations);
		sop ("Request has been submitted");
		
		sop("\n" + "List of passengers (by ID number) not booked on any flights:");
		while (rs.next())
		{
			sop(rs.getString("uID"));
		}
		sop("");
		
	}
	
	/**
	 * Update baggage weight for passenger
	 * 
	 * @param uID
	 * @param totalBagWeight
	 * @throws SQLException
	 */
	private static void updateWeight(int uID, float totalBagWeight) throws SQLException
	{
		sop("Processing request to update baggage weight for passenger with uID: " + uID + " and new baggage weight: " + totalBagWeight);
		String updateWeight = "update Passenger set totalBagWeight = ? where uID = ?";
		
		sop("Preparing statement...");
		PreparedStatement ps = conn.prepareStatement(updateWeight);
		ps.setInt(2, uID);
		ps.setFloat(1, totalBagWeight);
		
		sop("\n" + "Initial state of Passenger table:");
		sop("uID\tname\tnumBags\ttotalBagWeight");
		ResultSet rs = ps.executeQuery("select * from passenger");
		while (rs.next())
		{
			sop(rs.getString("uID") + "\t" + rs.getString("name") + "\t" + rs.getString("numBags") + "\t" + rs.getString("totalBagWeight"));
		}
		sop("");
		
		ps.executeUpdate();
		sop ("Request has been submitted");
		
		
		sop("\n" + "Current state of Passenger table:");
		sop("uID\tname\tnumBags\ttotalBagWeight");
		rs = ps.executeQuery("select * from passenger");
		while (rs.next())
		{
			sop(rs.getString("uID") + "\t" + rs.getString("name") + "\t" + rs.getString("numBags") + "\t" + rs.getString("totalBagWeight"));
		}
		sop("");
	}
	
	private static void sop(Object x)
	{
		System.out.println(x);
	}
}
