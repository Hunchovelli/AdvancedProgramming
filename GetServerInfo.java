// This class makes the GUI where the users enter the IP, PORT, etc...
// This class is called for every new user

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GetServerInfo {
	
	String[] vals = new String[4];
	JTextField ip = new JTextField(5);
	JTextField port = new JTextField(5);
	JTextField my_ip = new JTextField(5);
	JTextField my_port = new JTextField(5);
	
	public String[] getServerInfo(JFrame frame)
	{
		  JPanel myPanel = new JPanel(new GridLayout(4, 1));
    	myPanel.add(new JLabel("Enter the server ip:"));
    	myPanel.add(ip);
    	myPanel.add(new JLabel("Enter the server port:"));
    	myPanel.add(port);
    	myPanel.add(new JLabel("Enter your ip address:"));
    	myPanel.add(my_ip);
    	myPanel.add(new JLabel("Enter your port(optional):"));
    	myPanel.add(my_port);
    	
    	
    	int result = JOptionPane.showConfirmDialog(frame, myPanel, 
	               "Please Enter Details", JOptionPane.OK_CANCEL_OPTION);
    	
    	if (result == JOptionPane.OK_OPTION) {
	         vals[0] = ip.getText();
	         vals[1] = port.getText();
	         vals[2] = my_ip.getText();
	      }
    	
    	String port_option = my_port.getText();
    	
    	if (port_option != null)
    	{
    		vals[3] = port_option;
    	}
    	   	
    	return vals;
	}

}
