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
	
	// variables
	static final int MAX_PLANE_WEIGHT = 1000;
	static final int WEIGHT_PER_PASSENGER = 50;

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
			//registerPassenger(100, "Mary", 2, 50);
			//noReservations();
			//updateWeight(100, 100);
			//availableFlights();
			//scheduledFlights(1);
			//maxWeightPlanes();
			//changeDest(1, 3);
			//changeTicket(2, 1);
			//updateBags(1, 1);
			//updateCost(100, 1);
			//cancelFlight(2);
			//cancelReservation(2);
			//removePassenger(2);
			//reserveFlight(2, 1, 2, 2, 150);
			//addFlight(2, 5, 1, 2, "0000-00-00 00:00:00");
			//eitherFlight(1, 2);
			countPass(1);
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
	
	/**
	 * List flight IDs for all available flights (not at max capacity)
	 * @throws SQLException
	 */
	private static void availableFlights() throws SQLException
	{
		sop("Processing request to view all available flights:");

		String availableFlights = "select fid from flights f1 natural join planes where numPassengers > (select count(*) from reservations where fID=f1.fID)";
		Statement s = conn.createStatement();
		
		ResultSet rs = s.executeQuery(availableFlights);
		sop ("Request has been submitted");
		
		sop("\n" + "List of all available flights by flight ID:");
		while (rs.next())
		{
			sop(rs.getString("fID"));
		}
		sop("");
	}
	
	/**
	 * List all scheduled flights for specified passenger
	 * @param uID passenger ID
	 * @throws SQLException
	 */
	private static void scheduledFlights(int uID) throws SQLException
	{
		sop("Processing request to list scheduled flights for passenger with uID: ");
		String passengerFlights = "select fid from reservations where uid = ?";
		
		sop("Preparing statement..." + "\n");
		PreparedStatement ps = conn.prepareStatement(passengerFlights);
		ps.setInt(1, uID);
		
		ResultSet rs = ps.executeQuery();
		sop ("Request has been submitted" + "\n");
		
		sop("List of all flights by fID booked by passenger " + uID);
		while (rs.next())
		{
			sop(rs.getString("fID"));
		}
		sop("");
		
	}
	
	/**
	 * List all flights where max weight has been reached. 
	 * @throws SQLException
	 */
	private static void maxWeightPlanes() throws SQLException
	{
		sop("Processing request to list all flights at maximum weight capacity");
		String maxWeight = "select fid from reservations natural join passenger group by fid having sum(totalBagWeight) "
				+ "<= " + MAX_PLANE_WEIGHT + " and sum(totalBagWeight) >= " + (MAX_PLANE_WEIGHT - WEIGHT_PER_PASSENGER); 

		Statement s = conn.createStatement();
		ResultSet rs = s.executeQuery(maxWeight);
		sop("Request has been submitted" + "\n");

		sop("List of all flights by fID where maximum weight has been reached");
		while (rs.next())
		{
			sop(rs.getString("fID"));
		}

		sop("");
	}
	
	/**
	 * Change destination of flight
	 * @param fID
	 * @param destID
	 * @throws SQLException
	 */
	private static void changeDest(int fID, int destID) throws SQLException 
	{
		sop("Processing request to change destination of flight" + fID + " to location " + destID);
		String changeDest = "update flights set destID = ? where fid = ?";
		
		Statement s = conn.createStatement();
		
		ResultSet rs = s.executeQuery("select destID from flights where fID=" + fID);
		while (rs.next())
		{
			sop("Old destination ID: " + rs.getString("destID"));
		}
		
		sop("Preparing statement..." + "\n");
		PreparedStatement ps = conn.prepareStatement(changeDest);
		ps.setInt(1, destID);
		ps.setInt(2, fID);
		
		ps.executeUpdate();
		sop("Request has been submitted" + "\n");

		rs = s.executeQuery("select destID from flights where fID=" + fID);
		while (rs.next())
		{
			sop("New destination ID: " + rs.getString("destID"));
		}

		sop("");
	}
	
	/**
	 * Change ticket type of reservation
	 * First class standard price = 250
	 * Business class standard price = 150
	 * Economy class standard price = 50
	 * @param ticketType int between 0 and 2 where 0=first class, 1=business class, 2=economy
	 * @param rID
	 * @throws SQLException
	 */
	private static void changeTicket(int ticketType, int rID ) throws SQLException
	{
		sop("Processing request to change ticket type of reservation " + rID + " to type " + ticketType);
		String updateTicket = "update reservations set ticketType = ? where rid = ?";
		
		Statement s = conn.createStatement();
		
		ResultSet rs = s.executeQuery("select ticketType, ticketCost from reservations where rID=" + rID);
		while (rs.next())
		{
			sop("Old ticketType: " + rs.getString("ticketType"));
			sop("Old ticketCost: " + rs.getString("ticketCost"));
		}
		
		sop("Preparing statement..." + "\n");
		PreparedStatement ps = conn.prepareStatement(updateTicket);
		ps.setInt(1, ticketType);
		ps.setInt(2, rID);
		
		ps.executeUpdate();
		sop("Request has been submitted" + "\n");

		rs = s.executeQuery("select ticketType, ticketCost from reservations where rID=" + rID);
		while (rs.next())
		{
			sop("New ticketType: " + rs.getString("ticketType"));
			sop("New ticketCost: " + rs.getString("ticketCost"));
		}

		sop("");
	}
	
	/**
	 * Change passenger's number of bags
	 * @param numBags
	 * @param uID
	 * @throws SQLException
	 */
	private static void updateBags(int numBags, int uID) throws SQLException
	{
		sop("Processing request to change number of bags for passenger " + uID + " to " + numBags);
		String updateNumBags = "update passenger set numBags = ? where uID = ?";
		
		Statement s = conn.createStatement();
		
		ResultSet rs = s.executeQuery("select numBags from passenger where uid=" + uID);
		while (rs.next())
		{
			sop("Old number of bags: " + rs.getString("numBags"));
		}
		
		sop("Preparing statement..." + "\n");
		PreparedStatement ps = conn.prepareStatement(updateNumBags);
		ps.setInt(1, numBags);
		ps.setInt(2, uID);
		
		ps.executeUpdate();
		sop("Request has been submitted" + "\n");

		rs = s.executeQuery("select numBags from passenger where uid=" + uID);
		while (rs.next())
		{
			sop("New number of bags: " + rs.getString("numBags"));
		}

		sop("");
	}
	
	/**
	 * Change ticket cost of reservation
	 * @param ticketCost
	 * @param rID
	 * @throws SQLException
	 */
	private static void updateCost(int ticketCost, int rID) throws SQLException
	{
		sop("Processing request to change cost of ticket for reservation " + rID + " to " + ticketCost);
		String updateTicketCost = "update Reservations set ticketCost = ? where uID = ?";
		
		Statement s = conn.createStatement();
		
		ResultSet rs = s.executeQuery("select ticketCost from reservations where rid=" + rID);
		while (rs.next())
		{
			sop("Old ticket cost: " + rs.getString("ticketCost"));
		}
		
		sop("Preparing statement..." + "\n");
		PreparedStatement ps = conn.prepareStatement(updateTicketCost);
		ps.setInt(1, ticketCost);
		ps.setInt(2, rID);
		
		ps.executeUpdate();
		sop("Request has been submitted" + "\n");

		rs = s.executeQuery("select ticketCost from reservations where rid=" + rID);
		while (rs.next())
		{
			sop("New ticket cost: " + rs.getString("ticketCost"));
		}

		sop("");
	}
	
	/**
	 * Cancel a flight using the flight ID
	 * @param fID
	 * @throws SQLException
	 */
	private static void cancelFlight(int fID) throws SQLException 
	{
		sop("Processing request to cancel flight " + fID);
		String cancelFlight = "delete from flights where fid = ?";
		
		Statement s = conn.createStatement();
		
		ResultSet rs = s.executeQuery("select fid from flights");
		sop("Initial list of flights:");
		while (rs.next())
		{
			sop(rs.getString("fID"));
		}
		
		sop("Preparing statement..." + "\n");
		PreparedStatement ps = conn.prepareStatement(cancelFlight);
		ps.setInt(1, fID);
		
		ps.executeUpdate();
		sop("Request has been submitted" + "\n");

		rs = s.executeQuery("select fid from flights");
		sop("Current list of flights:");
		while (rs.next())
		{
			sop(rs.getString("fID"));
		}

		sop("");
	}
	
	/**
	 * Cancel a reservation using rid
	 * @param rID
	 * @throws SQLException
	 */
	private static void cancelReservation(int rID) throws SQLException 
	{
		sop("Processing request to cancel reservation " + rID);
		String cancelReservation = "delete from reservations where rid = ?";
		
		Statement s = conn.createStatement();
		
		ResultSet rs = s.executeQuery("select rid from reservations");
		sop("Initial list of reservations:");
		while (rs.next())
		{
			sop(rs.getString("rID"));
		}
		
		sop("Preparing statement..." + "\n");
		PreparedStatement ps = conn.prepareStatement(cancelReservation);
		ps.setInt(1, rID);
		
		ps.executeUpdate();
		sop("Request has been submitted" + "\n");

		rs = s.executeQuery("select rid from reservations");
		sop("Current list of reservations:");
		while (rs.next())
		{
			sop(rs.getString("rID"));
		}

		sop("");
	}
	
	/**
	 * Remove passenger using uid
	 * @param uID
	 * @throws SQLException
	 */
	private static void removePassenger(int uID) throws SQLException 
	{
		sop("Processing request to remove passenger " + uID);
		String removePassenger = "delete from passenger where uID = ?";
		
		Statement s = conn.createStatement();
		
		ResultSet rs = s.executeQuery("select uid from passenger");
		sop("Initial list of passengers:");
		while (rs.next())
		{
			sop(rs.getString("uID"));
		}
		
		sop("Preparing statement..." + "\n");
		PreparedStatement ps = conn.prepareStatement(removePassenger);
		ps.setInt(1, uID);
		
		ps.executeUpdate();
		sop("Request has been submitted" + "\n");

		rs = s.executeQuery("select uid from passenger");
		sop("Current list of passengers:");
		while (rs.next())
		{
			sop(rs.getString("uID"));
		}

		sop("");
	}
	
	/**
	 * Reserve a flight
	 * @param rID
	 * @param fID
	 * @param uID
	 * @param ticketType
	 * @param ticketCost
	 * @throws SQLException
	 */
	private static void reserveFlight(int rID, int fID, int uID, int ticketType, int ticketCost) throws SQLException
	{
		sop("Processing request to reserve flight for passenger " + uID + " with reservation " + rID + " and ticket type: " 
				+ ticketType + " for $" + ticketCost + " on flight" + fID);
		String reserveFlight = "insert into reservations (rid, fid, uid, ticketType, ticketcost) values (?, ?, ?, ?, ?)";

		Statement s = conn.createStatement();
		
		ResultSet rs = s.executeQuery("select * from reservations");
		sop("Initial list of reservations:");
		sop("rid\tfid\tuid\tticketType\tticketCost");
		while (rs.next())
		{
			sop(rs.getString("rID") + "\t" + rs.getString("fID") + "\t" + rs.getString("uID") 
			+ "\t" + rs.getString("ticketType") + "\t" + rs.getString("ticketCost"));
		}
		
		sop("\n" + "Preparing statement..." + "\n");
		PreparedStatement ps = conn.prepareStatement(reserveFlight);
		ps.setInt(1, uID);
		ps.setInt(2, fID);
		ps.setInt(3, uID);
		ps.setInt(4, ticketType);
		ps.setInt(5, ticketCost);
		
		ps.executeUpdate();
		sop("Request has been submitted" + "\n");

		rs = s.executeQuery("select * from reservations");
		sop("Current list of reservations:");
		sop("rid\tfid\tuid\tticketType\tticketCost");
		while (rs.next())
		{
			sop(rs.getString("rID") + "\t" + rs.getString("fID") + "\t" + rs.getString("uID") 
			+ "\t" + rs.getString("ticketType") + "\t" + rs.getString("ticketCost"));
		}

		sop("");
	}
	
	/**
	 * Create new flight
	 * @param fid
	 * @param planeid
	 * @param startid
	 * @param destid
	 * @param time
	 * @throws SQLException
	 */
	private static void addFlight(int fid, int planeid, int startid, int destid, String time) throws SQLException
	{
		sop("Processing request to create flight " + fid + " using plane " + planeid + " from " + startid + " to " 
				+ destid + " at time " + time);
		String createFlight = "insert into flights (fid, planeID, startID, destID, time) values (?, ?, ?, ?, ?)";

		Statement s = conn.createStatement();
		
		ResultSet rs = s.executeQuery("select * from flights");
		sop("Initial list of flights:");
		sop("fid\tplaneid\tstartid\tdestid\ttime");
		while (rs.next())
		{
			sop(rs.getString("fID") + "\t" + rs.getString("planeID") + "\t" + rs.getString("startID") 
			+ "\t" + rs.getString("destID") + "\t" + rs.getString("time"));
		}
		
		sop("\n" + "Preparing statement..." + "\n");
		PreparedStatement ps = conn.prepareStatement(createFlight);
		ps.setInt(1, fid);
		ps.setInt(2, planeid);
		ps.setInt(3, startid);
		ps.setInt(4, destid);
		ps.setString(5, time);
		
		ps.executeUpdate();
		sop("Request has been submitted" + "\n");

		rs = s.executeQuery("select * from flights");
		sop("Current list of flights:");
		sop("fid\tplaneid\tstartid\tdestid\ttime");
		while (rs.next())
		{
			sop(rs.getString("fID") + "\t" + rs.getString("planeID") + "\t" + rs.getString("startID") 
			+ "\t" + rs.getString("destID") + "\t" + rs.getString("time"));
		}

		sop("");
	}
	
	/**
	 * List all passengers booked on either of the specified flights
	 * @param fid1
	 * @param fid2
	 * @throws SQLException
	 */
	private static void eitherFlight(int fid1, int fid2) throws SQLException 
	{
		sop("Processing request to list all passengers booked on either flight " + fid1 + " or " + fid2);
		String eitherFlight = "(select uid from reservations where fid=?) union (select uid from reservations where fid=?)";

		Statement s = conn.createStatement();
		
		ResultSet rs = s.executeQuery("select * from reservations");
		sop("Current list of reservations:");
		sop("rid\tfid\tuid\tticketType\tticketCost");
		while (rs.next())
		{
			sop(rs.getString("rID") + "\t" + rs.getString("fID") + "\t" + rs.getString("uID") 
			+ "\t" + rs.getString("ticketType") + "\t" + rs.getString("ticketCost"));
		}
		
		sop("\n" + "Preparing statement..." + "\n");
		PreparedStatement ps = conn.prepareStatement(eitherFlight);
		ps.setInt(1, fid1);
		ps.setInt(2, fid2);
		
		rs = ps.executeQuery();
		sop("Request has been submitted" + "\n");

		sop("List of passengers on either flight " + fid1 + " or " + fid2);
		while (rs.next())
		{
			sop(rs.getString("uID"));
		}

		sop("");
	}
	
	/**
	 * Count the number of passengers booked for a flight
	 * @param fid
	 * @throws SQLException
	 */
	private static void countPass(int fid) throws SQLException 
	{
		sop("Processing request to list all passengers booked on flight " + fid);
		String numPass = "select count(*) as count from reservations where fid=?";
		
		Statement s = conn.createStatement();
		
		ResultSet rs = s.executeQuery("select * from reservations");
		sop("Current list of reservations:");
		sop("rid\tfid\tuid\tticketType\tticketCost");
		while (rs.next())
		{
			sop(rs.getString("rID") + "\t" + rs.getString("fID") + "\t" + rs.getString("uID") 
			+ "\t" + rs.getString("ticketType") + "\t" + rs.getString("ticketCost"));
		}
		
		PreparedStatement ps = conn.prepareStatement(numPass);
		ps.setInt(1, fid);
		rs = ps.executeQuery();
		sop("Request has been submitted" + "\n");

		sop("Number of passengers on flight " + fid);
		while (rs.next())
		{
			sop(rs.getString("count"));
		}

		sop("");
	}
	
	private static void sop(Object x)
	{
		System.out.println(x);
	}
}
