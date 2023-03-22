import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


//class to handle each client in the server
public class Ping {
    //creates list for each active client object in server
    private static List<Socket> clients = Collections.synchronizedList(new ArrayList<Socket>());
    //variable for waiting time for response in milliseconds 
    private static final int RESPONSE_TIMEOUT_MS = 15000;
    //
    private static ExecutorService executorService;
    private static CountDownLatch latch;

    //constructor to start 
    public Ping(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendMessage(clients);
            }
        }, 0, 60000);
        executorService = Executors.newCachedThreadPool();
    }

    //coordinator sending ping message
    public static void sendMessage(List<Socket> clients){

        //all clients are pinged before continuing to different thread       
        latch = new CountDownLatch(clients.size());

        //iterate over clients list 
        for (Socket client : clients){
            executorService.execute(() -> {
                try {
                    //output stream to each client in the server
                    PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                    //input stream of each client in the server
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    //broadcast ping message to each client in server
                    out.println("ping");
                    //wait for 15 seconds for a response   
                    client.setSoTimeout(RESPONSE_TIMEOUT_MS);
                    //reads the input from the client
                    String response = in.readLine();

                    if (response != null){
                        //client is active
                        System.out.println("Client " + client.getRemoteSocketAddress());
                    } else{
                        //Client is inactive
                        System.out.println("Client " + client.getRemoteSocketAddress());
                        client.close();
                    }
                } catch (IOException e){
                    // throws custom message when I/O error occurs
                    e.printStackTrace();;
                } finally {
                    //decrements the laych variable after each client has been checked for activity
                    latch.countDown();
                }
            });
        }

        try{
            //wait until all client have been checked for activity and latch variable = 0
            latch.await();
        } catch (InterruptedException e) {
             e.printStackTrace();
        } finally {
            //when complete, shut down executor service.
            executorService.shutdown();
        }
    }

    public static void main(String[] args) throws Exception{

        Ping ping = new Ping();
    
        
        //create server socket and bind it to port number 
        try (ServerSocket serverSocket = new ServerSocket(59001);){
            while (true){
            //returns a socket object when new client connects to server, representing a connection
                Socket clientSocket = serverSocket.accept();
                //adds clients socket object to the list
                clients.add(clientSocket);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}






