import java.awt.BorderLayout;
import java.awt.Font;
import java.io.PrintWriter;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClientGUI extends JFrame {
	
	private JTextField textField;
    private JTextArea messageArea;
    private JTabbedPane tabbedChats;
    private JLabel clients;
    private JPanel messagePanel;
    private JSplitPane msgSplit;
    private JSplitPane splitPane;
    
    public ClientGUI(PrintWriter out)
    {
    	this.setTitle("Chatter");
    	textField = new JTextField(50);
    	messageArea = new JTextArea(16, 30);
    	tabbedChats = new JTabbedPane();
    	clients = new JLabel();
    	
    	textField.setEditable(false);
        messageArea.setEditable(false);
        messageArea.setFont(new Font("Tahoma", Font.PLAIN, 15)); // set font size to 15
             
        clients.setVerticalAlignment(JLabel.TOP);
        clients.setHorizontalAlignment(JLabel.CENTER);
        
        messagePanel = new JPanel(new BorderLayout());
    	
    	msgSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
    	        new JScrollPane(messageArea), tabbedChats);
    	
    	msgSplit.setResizeWeight(0.2);
    	
    	messagePanel.add(msgSplit);
        
        // Split the window into two halves, where the left side has the chat area and the
        // right side has the client list
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        		messagePanel, clients);
        
        // Split the frame evenly between the two parts
        splitPane.setResizeWeight(0.6);
        
        this.getContentPane().add(splitPane, BorderLayout.CENTER);
        this.getContentPane().add(textField, BorderLayout.SOUTH);
        this.pack();
        
        this.setSize(1100, 350);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        
    }
    
    public JFrame getFrame()
    {
    	return this;
    }
    
    
    public JTextField getTextField()
    {
    	return textField;
    }
    
    
    public JLabel getLabel()
    {
    	return clients;
    }
    
    
    public JTextArea getMessageArea()
    {
    	return messageArea;
    }
    
    
    public JTabbedPane getTabbedChats()
    {
    	return tabbedChats;
    }
    
    // Create a new tab for each private chat 
    public void createTab(String user)
    {
    	JTextArea private_chat = new JTextArea(16, 30);
		private_chat.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		tabbedChats.addTab(user, private_chat);
    }
    
    public int getTabIndex(String user)
    {
    	return tabbedChats.indexOfTab(user);
    }
    
    
    public void appendToTab(int index, String msg, String time)
    {
    	JTextArea private_chat = (JTextArea)tabbedChats.getComponentAt(index);
    	private_chat.append("[" + time + "] " + msg + "\n");
    }
    
    
    public void appendToMsg(String msg, String time)
    {
    	messageArea.append("[" + time + "] " + msg + "\n");
    }
    
    
    public void appendGame(String msg)
    {
    	messageArea.append(msg + "\n");
    }
    
    
    public void setLabelText(String msg)
    {
    	clients.setText(msg);
    }
    
    
    public String getTextFieldText()
    {
    	return textField.getText();
    }
    
    
    public void setTextFieldText()
    {
    	textField.setText("");
    }
    
    
    public String getName(JFrame frame)
    {
    	return JOptionPane.showInputDialog(
                frame,
                "Enter a 4 digit ID:",
                "User ID selection",
                JOptionPane.PLAIN_MESSAGE
            );
    }
}
