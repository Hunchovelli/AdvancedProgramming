import java.io.*;
import java.net.*;
import java.util.*;

//class to handle each client in the server
public class Ping {
    //creates list for each active client object in server
    private static List<Socket> clients = new ArrayList<Socket>();

    public static void main(String[] args) throws Exception
    {
        //create server socket and bind it to port number 
        try (ServerSocket serverSocket = new ServerSocket(59001);)
        {
            while (true)
            {
                //returns a socket object when new client connects to server, representing a connection
                Socket clientSocket = serverSocket.accept();
                //adds clients socket object to the list
                clients.add(clientSocket);
            }
        }
    }
    
    // coordinator sending ping message
    public static void sendMessage() throws IOException
    {
    // iterate over list in client of ClientHandler objects
    for (Socket client : clients)
        {
        // output stream to each client in the server
        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        // broadcast ping message to each client in server
        out.println("ping");
        }

    }
    
    public static void receiveMessage() throws IOException
    {
    // iterate over list in client of ClientHandler objects
    for (Socket client : clients)
    {
        // input stream from each client to check for activity
        BufferedReader in = new BufferedReader(new InputStreamReader((client.getInputStream())));
        if

    }
    }

}






