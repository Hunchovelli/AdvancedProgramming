import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
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
	
	// the singleton class ActiveClients is instantiated here
	private static ActiveClients active = ActiveClients.getInstance();
	

    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running...");
        ExecutorService pool = Executors.newFixedThreadPool(500);
        try (ServerSocket listener = new ServerSocket(59001)) {
            while (true) {
            	
            	// the singleton instance of ActiveClients is passed here to be used for each client thread
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
        
        // Method used to check the result of the TIC TIC TAC game played between the server and a client
        public boolean checkServerWinner(TicTacToe game, String result)
        {
        	result = game.checkWinner();
        	
        	if (result.equals("tie"))
        	{
        		out.println("MESSAGE" + "@" + "The game has ended in a tie" + "@" + active.getLabelText());
        		out.println("MESSAGE" + "@" + "The board has been reset" + "@" + active.getLabelText());
        		game.resetBoard();
        		return true;
        	}
        	
        	else if (result.equals("player"))
        	{
        		out.println("MESSAGE" + "@" + "Congratulations, you have won!!" + "@" + active.getLabelText());
        		out.println("MESSAGE" + "@" + "The board has been reset" + "@" + active.getLabelText());
        		game.resetBoard();
        		return true;
        	}
        	
        	else if (result.equals("cpu"))
        	{
        		out.println("MESSAGE" + "@" + "The Server has won, unlucky" + "@" + active.getLabelText());
        		out.println("MESSAGE" + "@" + "The board has been reset" + "@" + active.getLabelText());
        		game.resetBoard();
        		return true;
        	}
        	
        	return false;
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
                    	
                    	// The client details are stored within data structures of the singleton class
                        if (!id.isEmpty() && active.checkID(id)==false) {
                            active.addID(id);
                            active.appendToMap(id, parts[1], parts[2]);
                            active.appendLink(id, out);
                            active.addToQueue(id);
                            active.appendToIdSocket(id, socket);
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
                	out.println("COORDINATOR" + "@" + "You have been assigned as the coordinator of this session"+ "@" + "Chatter - User " + id + " (Coordinator)");
                }
                
                for (PrintWriter writer : active.getWriters()) {
                    writer.println("MESSAGE" + "@" + username + " has joined" + "@" + active.getLabelText());
                }
                
                active.addWriter(out);
                
                TicTacToe game = new TicTacToe("player", "cpu");

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
                    	
//                    	out = active.getSpecificWriter(id);
                    	
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

                        			private_writer.println("PRIVATE" + "@" + username + "@" + "User " + id + " : " + result);

                        			out.println("PRIVATE" + "@" + recipient + "@" + "User " + id + " : " + result);
                        		}
                        	
                        		else 
                        		{
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
                    
                    
                    // This block handles the Tic Tac Toe game played between the server and a client
                    else if (input.toLowerCase().startsWith("/play"))
                    {
                    	String result = "";
                    	String[] splitter = input.split(" ");
                    	int chosen_num = Integer.parseInt(splitter[1]);
                    	
                    	if (chosen_num < 1 || chosen_num >9)
                    	{
                    		out.println("MESSAGE" + "@" + "The entered board position is invalid. Please try again" + "@" + active.getLabelText());
                    	}
                    	
                    	else {
                    	
                    		// Note that the player is symbol 'X' while the server is symbol 'O'
                    		game.placeSign(chosen_num, "player");
                           
                    	
                    		boolean resetGame = this.checkServerWinner(game, result);
                    	
                    	
                    		if (resetGame == false)
                    		{
                    			Random rand = new Random();
                    			int index = rand.nextInt(9) + 1;
                			
                    			boolean played = game.checkifPlayed(index);
                			
                    			while(played)
                    			{
                    				index = rand.nextInt(9) + 1;
                    				played = game.checkifPlayed(index);
                    			}
                    		
                    			game.placeSign(index, "cpu");
                    		
                    			this.checkServerWinner(game, result);
                        	
                    			out.println("GAME" + "@" + game.getBoardSection(0));
                    			out.println("GAME" + "@" + game.getBoardSection(1));
                    			out.println("GAME" + "@" + game.getBoardSection(2));
                    			out.println("GAME" + "@" + game.getBoardSection(3));
                    			out.println("GAME" + "@" + game.getBoardSection(4));
                    		}                	

                    	                   	
                    		out.println("GAME" + "@" + " ");
                    	
                    		
                    	}}
                    
                    //this block handles when the coordinator wants to broadcast a ping message to all clients in the server     
                    else if (input.toLowerCase().startsWith("/ping")) 
                    {   
                        //checks if the coordinator is inputting the /ping command
                        if (id == active.getCoordinator()){
                            // intialises the pong varaiable for the output stream of the ping message
                            PrintWriter pong;
                            //loops through the list of every id in the server
                            for (String id : active.getIds()){
                                //if block that executes only if id is not equal to the coordinators
                                if (!(id == active.getCoordinator())){
                                    //calls the active object to call the method getSpecificWriter from the ActiveClients class and passes the id as a parameter 
                                    pong = active.getSpecificWriter(id);
                                    //outputs the pong message to the specific client called in the previous line
                                    pong.println("PING" + "@" + "Please respond with PONG if active.");
                                }
                            }
                        //block executes if a client who isn't the coordinator tries to use the /ping function
                        }else{
                            //message displays to client who isn't coordinator that tries use the /ping function
                            out.println("PING" + "@" + "You don't have permission to send a PING.");
                        }  
                    }
                    //this block handles when the coordinator wants to kick a client out of the server    
                    else if (input.toLowerCase().startsWith("/kick")){
                        
                        //splits the user input by a whitespace into a String list 
                        String [] splitter = input.split(" ");
                        //as the user inputs a /ping "id", the list will contain two elements: /ping and the user's id. We store the user's id in this user variable
                        String user = splitter[1];
                        
                        //intialises the kickOut varaiable for the output stream of the kick message
                        PrintWriter kickOut;
                        //checks if the coordinator is inputting the /kick command
                        if (id == active.getCoordinator()){
                            //calls the active object to call the method getSpecificWriter from the ActiveClients class and passes coordinator id as a parameter
                            kickOut = active.getSpecificWriter(active.getCoordinator());
                            //outputs the kick message to the coordinatior on the confrimation of kicking out the user inputted by the coordinator
                            kickOut.println("KICK" + "@" + "- Kicking out user " + user);
                            //gets the users id socket from the getIdSocket in the ActiveClients class using the active object and closes the socket hence kicking him out the server.
                            active.getIdSocket(user).close();
                        //block executes if a client who isn't the coordinator tries to use the /kick function
                        } else{
                            //message displays to client who isn't coordinator that tries use the /kick function
                            out.println("KICK" + "@" + "You don't have permission to kick");
                        }
                    }              
                    else
                    {
                    	for (PrintWriter writer : active.getWriters()) {
                            writer.println("MESSAGE" + "@" + "User " + id + " : " + input + "@" + active.getLabelText());
                        }
                    }
                    	

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
                    
                    // This block is used to check if the coordinator has left the server, and if so, appoint a new coordinator
                    try {
                    
                    	if (active.checkFirst(id) == true)
                    	{
                    		while (true)
                    		{
                    			active.removeCoordinator();
                    			String newCoordinator = active.getCoordinator();
                    		
                    			if (active.checkID(newCoordinator) == true)
                    			{
                    				PrintWriter coordinator = active.getSpecificWriter(newCoordinator);
                    				coordinator.println("COORDINATOR" + "@" + "You are now the new coordinator of the group" + "@" + "Chatter - User " + newCoordinator + " (Coordinator)");
                    				break;
                    			}	
                    		}
                    	}
                    } catch (Exception e)
                    {
                    	System.out.println(e);
                    }
                try { socket.close(); } catch (IOException e) {}
            }
        }
    }
}}
