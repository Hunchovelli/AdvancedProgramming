import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.net.Socket;


// This class is responsible for maintaining the list of all active clients that have
// connected or left the server. This is a singleton class, meaning that all client
// threads will share one instance of this class.

public class ActiveClients {
	private Set<String> ids;
	private Set<PrintWriter> writers;
	private Map<String, String[]> pairs;
	private Map<String, PrintWriter> link;
	private static ActiveClients instance = null;
	private Map<String, Socket> idSocket;

	// Coordinator Queue
	private Queue<String> coordinator;
	
	private ActiveClients() 
	{
		
		// IDs of active users
		ids = new HashSet<>();
		
		// Output streams to connected clients
		writers = new HashSet<>();
		
		// Mapping of each id to their ip and port
		pairs = new HashMap<>();
		
		// Mapping of each user to their specific output stream
		link = new HashMap<>();
		
		// Maintain priority of coordinator
		coordinator = new LinkedList<>();

		// Mapping of each id to their socket object
		idSocket = new HashMap<>();


	}
	
	// Returns active instance of the class (creates one if it hasn't already)
	public static synchronized ActiveClients getInstance()
	{
		if (instance == null)
		{
			instance = new ActiveClients();
		}
		return instance;
	}
	
	// Add id of a client to ids set
	public synchronized void addID(String id)
	{
		ids.add(id);
		this.getIDSize();
	}
	
	// Get the list of ids
	public synchronized Set<String> getIds()
	{
		return this.ids;
	}
	
	// Add output stream of a client to writers set
	public synchronized void addWriter(PrintWriter writer)
	{
		writers.add(writer);
//		this.getWriterSize();
	}
	
	// Check if an id is already present in the ids set
	public synchronized boolean checkID(String id)
	{
		if (ids.contains(id))
		{
			return true;
		}
		
		else
		{
			return false;
		}
	}
	
	// get the size of the ids set
	public synchronized int idsSize()
	{
		return ids.size();
	}
	
	// get the size of the writers set
	public synchronized Set<PrintWriter> getWriters()
	{
		return writers;
	}
	
	// remove a client id when they disconnect
	public synchronized void removeSetID(String id)
	{
		ids.remove(id);
	}
	
	// remove the writer of the client when they disconnect
	public synchronized void removeWriter(PrintWriter writer)
	{
		writers.remove(writer);
	}
	
	// Add new client details to a map.
	// id will be the key and the value is a list of the client ip and port
	public synchronized void appendToMap(String id, String ip, String port)
	{
		String[] details = {ip, port};
		pairs.put(id, details);
	}

	// Add new clients id and socket object to a map
	// Id will be the key and the value is the socket object for the client
	public synchronized void appendToIdSocket(String id, Socket socket)
	{
		idSocket.put(id, socket);
	}
	
	// Remove an entry from the map
	public synchronized void removeMapID(String id)
	{
		pairs.remove(id);
	}
	
	// Generate text from the Map which is used to display all the active users along with their details
	public synchronized String getLabelText()
	{	
		// Used an HTML statement here to style the text and leave two new lines after it
		String users = "<font size=5><strong><u>Active Clients</u></strong></font><br><br>";
	    for (Map.Entry<String, String[]> entry : pairs.entrySet())
	    {
	    	String[] details = entry.getValue();
	    	String ip = details[0];
	    	String port = details[1];
	    	String id = entry.getKey();
	    	
	    	// Check if a user has provided a port or not
	    	if (port.equals("None"))
	    	{
	        users += "User " + id + " (" + "<em>" + "IP:" + "<font color=green>" + ip + "</font>" +  ", " + "Port:" + "<font color=red>" + port + "</font>" + "</em>" + ")" + "<br>";
	    	}
	    	
	    	else users += "User " + id + " (" + "<em>" + "IP:" + "<font color=green>" + ip + "</font>" +  ", " + "Port:" + "<font color=green>" + port + "</font>" + "</em>" + ")" + "<br>";
	    }
	    
	    // Format the entire string as an HTML formatted string
	    return "<html>" + users + "</html>";
	}
	
	// Add an id to the dictionary 'link' and its value will be the relevant PrintWriter used to communicate with it
	public synchronized void appendLink(String id, PrintWriter writer)
	{
		link.put(id, writer);
	}
	
	// Get the PrintWriter of a specific client
	public synchronized PrintWriter getSpecificWriter(String id)
	{
		return link.get(id);
	}
	
	// Add each new client to the Coordinator Queue
	public synchronized void addToQueue(String id)
	{
		coordinator.add(id);
	}
	
	// Check if the id is the coordinator in the queue
	public synchronized boolean checkFirst(String id)
	{
		if (id.equals(coordinator.peek()))
		{
			return true;
		}
		return false;
	}
	
	// Get the active coordinator
	public synchronized String getCoordinator()
	{
		return coordinator.peek();
	}
	
	// Remove the active coordinator
	public synchronized void removeCoordinator()
	{
		coordinator.remove();
	}
	
	public synchronized void getIDSize()
	{
		System.out.println(ids.size());
	}
	
	
	public synchronized void getWriterSize()
	{
		System.out.println(writers.size());
	}
	
	
	public synchronized void getPairsSize()
	{
		System.out.println(pairs.size());
	}

	//getter method that return the socket object for each client in ther server by passing the user's id in the parameter
	public synchronized Socket getIdSocket(String id)
	{
		return idSocket.get(id);
	}


}

