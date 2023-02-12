import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

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

//    String serverAddress;
    Scanner in;
    PrintWriter out;
    JFrame frame = new JFrame("Chatter");
    JTextField textField = new JTextField(50);
    JTextArea messageArea = new JTextArea(16, 50);
    DisplayClients user_panel = new DisplayClients();

    /**
     * Constructs the client by laying out the GUI and registering a listener with the
     * textfield so that pressing Return in the listener sends the textfield contents
     * to the server. Note however that the textfield is initially NOT editable, and
     * only becomes editable AFTER the client receives the NAMEACCEPTED message from
     * the server.
     */
    public NewChatClient() {

        textField.setEditable(false);
        messageArea.setEditable(false);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        		new JScrollPane(messageArea), user_panel.getPanel());
        splitPane.setResizeWeight(0.5);
        frame.getContentPane().add(splitPane, BorderLayout.CENTER);
        frame.getContentPane().add(textField, BorderLayout.SOUTH);
        frame.pack();

        // Send on enter then clear to prepare for next message
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                textField.setText("");
            }
        });
    }

    private String getName() {
        return JOptionPane.showInputDialog(
            frame,
            "Enter a 6 digit ID:",
            "User ID selection",
            JOptionPane.PLAIN_MESSAGE
        );
    }
    
    private void run() throws IOException {
        try {
        	String[] server = new GetServerInfo().getServerInfo(frame);
        	String ip = server[0];
        	int port = Integer.parseInt(server[1]);
            Socket socket = new Socket(ip, port);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
            String id = "";

            while (in.hasNextLine()) {
                String line = in.nextLine();
                if (line.startsWith("SUBMITNAME")) {
                	id = getName();
                	String group = id + " " + ip;
                    out.println(group);
                } else if (line.startsWith("NAMEACCEPTED")) {
                    this.frame.setTitle("Chatter - " + line.substring(13));
                    textField.setEditable(true);
                    user_panel.displayUsers();
                } else if (line.startsWith("MESSAGE")) {
                    messageArea.append(line.substring(8) + "\n");
                }
            }
        } finally {
            frame.setVisible(false);
            frame.dispose();
        }
    }

    public static void main(String[] args) throws Exception {

        NewChatClient client = new NewChatClient();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }
}

