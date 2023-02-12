import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class DisplayClients {
	
	JLabel heading;
	JLabel users;
	JPanel panel;
	private static ActiveClients active = ActiveClients.getInstance();
	
	public DisplayClients()
	{
		Border border = BorderFactory.createLineBorder(Color.BLACK, 1, true);
		heading = new JLabel("Active Users");
		heading.setBorder(border);
		
		Font font = heading.getFont();
		Font boldItalicFont = font.deriveFont(Font.BOLD | Font.ITALIC, 20f);
		heading.setFont(boldItalicFont);
		
		users = new JLabel();
		
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.add(heading);
		panel.add(Box.createVerticalStrut(10));
		panel.add(users);
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
	
	public void displayUsers()
	{
		 String list = active.getLabelText();
		 System.out.println("This is the text: " + list);
		 users.setText(list);
	}

}
