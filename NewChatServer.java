import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * A multithreaded chat room server. When a client connects the server requests a screen
 * name by sending the client the text "SUBMITNAME", and keeps requesting a name until
 * a unique one is received. After a client submits a unique name, the server acknowledges
 * with "NAMEACCEPTED". Then all messages from that client will be broadcast to all other
 * clients that have submitted a unique screen name. The broadcast messages are prefixed
 * with "MESSAGE".
 *
 * This is just a teaching example so it can be enhanced in many ways, e.g., better
 * logging. Another is to accept a lot of fun commands, like Slack.
 */
public class NewChatServer 
{

//    // All client names, so we can check for duplicates upon registration.
//    private static Set<String> ids = new HashSet<>();
//
//     // The set of all the print writers for all the clients, used for broadcast.
//    private static Set<PrintWriter> writers = new HashSet<>();
	
	// the singleton class ActiveClients is instantiated here and passed to each Thread Handler
	private static ActiveClients active = ActiveClients.getInstance();
	

    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running...");
        ExecutorService pool = Executors.newFixedThreadPool(500);
        try (ServerSocket listener = new ServerSocket(59001)) {
            while (true) {
                pool.execute(new Handler(listener.accept(), active));
            }
        }
    }

    /**
     * The client handler task.
     */
    private static class Handler implements Runnable {
        private String id;
        private Socket socket;
        private Scanner in;
        private PrintWriter out;
//        private ActiveClients single;
//        ActiveClients active = ActiveClients.getInstance();

        /**
         * Constructs a handler thread, squirreling away the socket. All the interesting
         * work is done in the run method. Remember the constructor is called from the
         * server's main method, so this has to be as short as possible.
         */
        public Handler(Socket socket, ActiveClients active) {
            this.socket = socket;
        }

        /**
         * Services this thread's client by repeatedly requesting a screen name until a
         * unique one has been submitted, then acknowledges the name and registers the
         * output stream for the client in a global set, then repeatedly gets inputs and
         * broadcasts them.
         */
        public void run() {
            try {
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);

                // Keep requesting a name until we get a unique one.
                while (true) {
                    out.println("SUBMITNAME");
                    
                    // Split the output sent by the client into an array
                    // parts[0] --> id
                    // parts[1] --> client ip address
                    // parts[2] --> client port
                    String details = in.nextLine();
                    String[] parts = details.split(" ");
                    
                    id = parts[0];
                    if (id == null) {
                        return;
                    }
                    synchronized (active) {
                    	
                        if (!id.isEmpty() && active.checkID(id)==false) {
                            active.addID(id);
                            active.appendToMap(id, parts[1], parts[2]);
                            active.appendLink(id, out);
                            break;
                        }
                    }
                }

                // Now that a successful name has been chosen, add the socket's print writer
                // to the set of all writers so this client can receive broadcast messages.
                // But BEFORE THAT, let everyone else know that the new person has joined!
                
                String username = "User " + id;
                
                out.println("NAMEACCEPTED" + "@" + username + "@" + active.getLabelText());
                
                out.println("MESSAGE" + "@" + "Use '/private id' if you wish to message a user privately" + "@" + active.getLabelText());
                
                // Checks if the client is the first person to join the server
                if (active.idsSize() == 1)
                {
                	out.println("MESSAGE" + "@" + "You have been assigned as the coordinator of this session" + "@" + active.getLabelText());
                }
                
                for (PrintWriter writer : active.getWriters()) {
                    writer.println("MESSAGE" + "@" + username + " has joined" + "@" + active.getLabelText());
                }
                
                active.addWriter(out);
                
                TicTacToe game = new TicTacToe();

                // Accept messages from this client and broadcast them.
                while (true) {
                    String input = in.nextLine();
                    if (input.toLowerCase().startsWith("/quit")) {
                        return;
                    }
                    
                    
                    // Block which handles the private messaging feature of the application
                    else if (input.toLowerCase().startsWith("/private"))
                    {
                    	
                    	String[] splitter = input.split(" ");
                    	
                    	String receiver = splitter[1];
                    	
                    	if (active.checkID(receiver) == false)
                    	{
                    		out.println("MESSAGE" + "@" + "Invalid id entered for private messaging" + "@" + active.getLabelText());
                    	}
                    	
                    	else
                    	{
                    		PrintWriter private_writer = active.getSpecificWriter(receiver);
                        	
                        	if (out == private_writer)
                        	{
                        		out.println("MESSAGE" + "@" + "Invalid id entered for private messaging" + "@" + active.getLabelText());
                        	}
                        	
                        	else {
                        	
                        		String recipient = "User " + receiver;
                        		String result = "";
                        	
                        		if(splitter.length > 2)
                        		{
                        			StringBuilder sb = new StringBuilder();
                        			for (int i=2; i<splitter.length; i++)
                        			{
                        				sb.append(splitter[i] + " ");
                        			}
                        		
                        			result = sb.toString();
//                        			private_writer.println("PRIVATE" + "@" + username + "@" + username + " : " + result);
                        			private_writer.println("PRIVATE" + "@" + username + "@" + "User " + id + " : " + result);
//                        			out.println("PRIVATE" + "@" + recipient + "@" + username + " : " + result);
                        			out.println("PRIVATE" + "@" + recipient + "@" + "User " + id + " : " + result);
                        		}
                        	
                        		else 
                        		{
//                        			private_writer.println("PRIVATE" + "@" + username + " : " + result);
//                        			out.println("PRIVATE" + "@" + recipient + "@" + username + " : " + result);
                        			private_writer.println("PRIVATE" + "@" + username + "@" + "User " + id + " : " + result);
                        			out.println("PRIVATE" + "@" + recipient + "@" + "User " + id + " : " + result);
                        		}
                    	}

                    	}}
                    
                    else if (input.toLowerCase().startsWith("/game"))
                    {

                    	out.println("GAME" + "@" + game.getBoardSection(0));
                    	out.println("GAME" + "@" + game.getBoardSection(1));
                    	out.println("GAME" + "@" + game.getBoardSection(2));
                    	out.println("GAME" + "@" + game.getBoardSection(3));
                    	out.println("GAME" + "@" + game.getBoardSection(4));
                    	
                    	out.println("GAME" + "@" + " ");
                    }
                    
                    
                    else if (input.toLowerCase().startsWith("/play"))
                    {
                    	String[] splitter = input.split(" ");
                    	int chosen_num = Integer.parseInt(splitter[1]);
                    	
                    	game.placeX(chosen_num, "player");
                    	
                    	game.placeX(chosen_num, "cpu");
                    	
                    	
                    	out.println("GAME" + "@" + game.getBoardSection(0));
                    	out.println("GAME" + "@" + game.getBoardSection(1));
                    	out.println("GAME" + "@" + game.getBoardSection(2));
                    	out.println("GAME" + "@" + game.getBoardSection(3));
                    	out.println("GAME" + "@" + game.getBoardSection(4));
                    	
                    	
                    	
                    	out.println("GAME" + "@" + " ");
                    	
                    	String result = game.checkWinner();
                    	
                    	if (result.equals("tie"))
                    	{
                    		out.println("MESSAGE" + "@" + "The game has ended in a tie" + "@" + active.getLabelText());
                    		out.println("MESSAGE" + "@" + "The board has been reset" + "@" + active.getLabelText());
                    		game.resetBoard();
                    	}
                    	
                    	else if (result.equals("player"))
                    	{
                    		out.println("MESSAGE" + "@" + "Congratulations, you have won!!" + "@" + active.getLabelText());
                    		out.println("MESSAGE" + "@" + "The board has been reset" + "@" + active.getLabelText());
                    		game.resetBoard();
                    	}
                    	
                    	else if (result.equals("cpu"))
                    	{
                    		out.println("MESSAGE" + "@" + "The Server has won, unlucky" + "@" + active.getLabelText());
                    		out.println("MESSAGE" + "@" + "The board has been reset" + "@" + active.getLabelText());
                    		game.resetBoard();
                    	}
                    		
                    }
                    
                    
                    else
                    {
                    	for (PrintWriter writer : active.getWriters()) {
                            writer.println("MESSAGE" + "@" + "User " + id + " : " + input + "@" + active.getLabelText());
                        }
                    }
                    	
//                    for (PrintWriter writer : active.getWriters()) {
//                        writer.println("MESSAGE" + "@" + username + " : " + input + "@" + active.getLabelText());
//                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                if (out != null) {
                    active.removeWriter(out);
                }
                if (id != null) {
                    System.out.println("User " + id + " is leaving");
                    active.removeSetID(id);
                    active.removeMapID(id);
                    for (PrintWriter writer : active.getWriters()) {
                        writer.println("MESSAGE" + "@" + "User " + id + " is leaving the server" + "@" + active.getLabelText());
                    }
                }
                try { socket.close(); } catch (IOException e) {}
            }
        }
    }
}
