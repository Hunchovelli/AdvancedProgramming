import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.HashSet;
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
	
	private static ActiveClients active = ActiveClients.getInstance();
	
	public static synchronized ActiveClients getInstance()
	{
		return active;
	}

    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running...");
//        ActiveClients active = ActiveClients.getInstance();
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
                    String details = in.nextLine();
                    String[] parts = details.split(" ");
                    
                    id = parts[0];
                    if (id == null) {
                        return;
                    }
                    synchronized (active) {
                    	
                        if (!id.isEmpty() && active.checkID(id)==false) {
                            active.addID(id);
                            active.appendDetails(id, parts[1]);
                            break;
                        }
                    }
                }

                // Now that a successful name has been chosen, add the socket's print writer
                // to the set of all writers so this client can receive broadcast messages.
                // But BEFORE THAT, let everyone else know that the new person has joined!
                out.println("NAMEACCEPTED " + "User " + id);
                
                if (active.idsSize() == 1)
                {
                	out.println("MESSAGE You have been assigned as the coordinator of this session");
                }
                
                for (PrintWriter writer : active.getWriters()) {
                    writer.println("MESSAGE " + "User " + id + " has joined");
                }
                
                active.addWriter(out);

                // Accept messages from this client and broadcast them.
                while (true) {
                    String input = in.nextLine();
                    if (input.toLowerCase().startsWith("/quit")) {
                        return;
                    }
                    for (PrintWriter writer : active.getWriters()) {
                        writer.println("MESSAGE " + "User " + id + " : " + input);
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
                        writer.println("MESSAGE " + "User " + id + " has left");
                    }
                }
                try { socket.close(); } catch (IOException e) {}
            }
        }
    }
}
