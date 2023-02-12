import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ActiveClients {
	private Set<String> ids;
	private Set<PrintWriter> writers;
	private Map<String, String> pairs;
	private static ActiveClients instance = null;
	
	private ActiveClients() 
	{
		ids = new HashSet<>();
		writers = new HashSet<>();
		pairs = new HashMap<>();
	}
	
	public static synchronized ActiveClients getInstance()
	{
		if (instance == null)
		{
			instance = new ActiveClients();
		}
		return instance;
	}
	
	public synchronized void addID(String id)
	{
		ids.add(id);
		this.getIDSize();
	}
	
	public synchronized void addWriter(PrintWriter writer)
	{
		writers.add(writer);
		this.getWriterSize();
	}
	
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
	
	public synchronized int idsSize()
	{
		return ids.size();
	}
	
	public synchronized Set<PrintWriter> getWriters()
	{
		return writers;
	}
	
	public synchronized void removeSetID(String id)
	{
		ids.remove(id);
	}
	
	public synchronized void removeWriter(PrintWriter writer)
	{
		writers.remove(writer);
	}
	
	public synchronized void appendDetails(String id, String ip)
	{
		pairs.put(id, ip);
		this.getPairsSize();
	}
	
	public synchronized void removeMapID(String id)
	{
		pairs.remove(id);
	}
	
	public synchronized String getLabelText()
	{
		StringBuilder sb = new StringBuilder();
		
		for (Map.Entry<String, String> entry : pairs.entrySet())
		{
			String id = entry.getKey();
			String details = entry.getValue();
			sb.append(id).append("\n");
		}
		return sb.toString();
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
	
}

