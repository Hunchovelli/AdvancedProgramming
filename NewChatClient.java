import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.html.HTMLEditorKit;

/**
 * A simple Swing-based client for the chat server. Graphically it is a frame with a text
 * field for entering messages and a textarea to see the whole dialog.
 *
 * The client follows the following Chat Protocol. When the server sends "SUBMITNAME" the
 * client replies with the desired screen name. The server will keep sending "SUBMITNAME"
 * requests as long as the client submits screen names that are already in use. When the
 * server sends a line beginning with "NAMEACCEPTED" the client is now allowed to start
 * sending the server arbitrary strings to be broadcast to all chatters connected to the
 * server. When the server sends a line beginning with "MESSAGE" then all characters
 * following this string should be displayed in its message area.
 */
public class NewChatClient {

    Scanner in;
    PrintWriter out;    
    ClientGUI gui;
    JFrame frame;

    public NewChatClient() {

        gui = new ClientGUI(out);
        frame = gui.getFrame();
        
        gui.getTextField().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                out.println(gui.getTextField().getText());
                gui.getTextField().setText("");
            }
        });
   
    }

    private String getName() {
        return JOptionPane.showInputDialog(
            frame,
            "Enter a 4 digit ID:",
            "User ID selection",
            JOptionPane.PLAIN_MESSAGE
        );
    }
    
    private void run() throws IOException {
        try {
        	
        	// Get the details provided by the client
        	String[] server = new GetServerInfo().getServerInfo(frame);
        	String ip = server[0];
        	String user_ip = server[2];
        	String user_port = server[3];
        	System.out.println(user_port);
        	int port = Integer.parseInt(server[1]);
            Socket socket = new Socket(ip, port);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
            String id = "";

            // The input stream will be split into a list by "@"
            while (in.hasNextLine()) {
            	String[] line = in.nextLine().split("@");
            	
            	
            	System.out.println(Arrays.toString(line));
            	
                
                if (line[0].equals("SUBMITNAME")) {
                	id = getName();
                	String group = id + " " + user_ip + " " + user_port;
                    out.println(group);
                    
                }
                
                else if (line[0].equals("NAMEACCEPTED")) {
                    this.frame.setTitle("Chatter - " + line[1]);
                    gui.getTextField().setEditable(true);
                    gui.getLabel().setText(line[2]);
                } 
                
                else if (line[0].equals("MESSAGE")) {
                	
                    gui.getMessageArea().append(line[1] + "\n");     
                    gui.getLabel().setText(line[2]);
                }
                
                else if (line[0].equals("PRIVATE")) {
                	String user = line[1];
                	int tabIndex = gui.getTabbedChats().indexOfTab(user);
                	
                	if (tabIndex == -1)
                	{
                		JPanel chat = new JPanel();
                		JTextArea private_chat = new JTextArea(16, 30);
                		private_chat.setFont(new Font("Tahoma", Font.PLAIN, 13));
                		chat.add(new JScrollPane(private_chat));
                		
                		gui.getTabbedChats().addTab(line[1], private_chat);
                		tabIndex = gui.getTabbedChats().indexOfTab(user);
                	}
                	
                	JTextArea private_chat = (JTextArea)gui.getTabbedChats().getComponentAt(tabIndex);
                	private_chat.append(line[2] + "\n");
                	
                }
                	                
            }
            
        } finally {
            frame.setVisible(false);
            frame.dispose();
        }
    }

    public static void main(String[] args) throws Exception {

        NewChatClient client = new NewChatClient();

        client.run();
    }
}

