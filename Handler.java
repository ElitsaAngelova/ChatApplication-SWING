import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import javax.swing.border.EtchedBorder;
import javax.swing.JOptionPane;
import javax.swing.UIManager;


public class Handler extends JFrame
					implements Runnable{
	
	private static final long serialVersionUID = 1L;
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	Scanner scan = new Scanner(System.in);
	private Socket socket;
	private String nickname;
	private String email;
	private BufferedReader in;
	private PrintWriter out;
	private int id;
	private JFrame frmChatapp;
	private JTextField textField;
	private JTextArea textArea;
	private JList<String> list;
	private JButton btnNewButton_1;
	private JPanel panel;
	private JPanel panel_1;
	private JButton btnNewButton_2;
	
	public Handler(Socket socket, String nickname, BufferedReader in, PrintWriter out, int id) {
		this.socket = socket;
		this.nickname = nickname;
		this.in = in;
		this.out = out;
		this.id = id;	
		setEmail("java.project2021@gmail.com");
		
		DBManager.addUser(getId(), nickname, email);
		ChatServer.activeClients.add(this);
		ChatServer.getActiveClientsNames().add(this.getNickname());
		ChatServer.defListModel.addElement(this.getNickname());
		initialize();
	}

	private void initialize() {
		frmChatapp = new JFrame();
		frmChatapp.getContentPane().setFont(new Font("Bernard MT Condensed", Font.PLAIN, 11));
		frmChatapp.setTitle("Welcome to ChatAPP, " + this.getNickname());
		frmChatapp.setBounds(100, 100, 900, 600);
		frmChatapp.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frmChatapp.getContentPane().setLayout(null);
		
		panel = new JPanel();
		panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Chat", TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLUE));
		panel.setBounds(10, 21, 706, 529);
		frmChatapp.getContentPane().add(panel);
		panel.setLayout(null);
		
		textArea = new JTextArea();
		textArea.setBounds(10, 21, 686, 420);
		panel.add(textArea);
		textArea.setEditable(false);
		
		JButton btnNewButton = new JButton("SEND");
		btnNewButton.setBounds(601, 495, 95, 23);
		panel.add(btnNewButton);
		
		textField = new JTextField();
		textField.setBounds(10, 452, 578, 66);
		panel.add(textField);
		textField.setColumns(10);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String inputLine = textField.getText();
				textField.setText("");
				if (inputLine != null) {
					for (Handler clientHandler : ChatServer.getActiveClients()) {
						clientHandler.textArea.append("[" + LocalDateTime.now().format(formatter) + "] " + getNickname() + ": " + inputLine + "\n");
					}
				}
			}
		});
		
		panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Active users:", TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLUE));
		panel_1.setBounds(715, 21, 159, 529);
		frmChatapp.getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		btnNewButton_2 = new JButton("SEND DM");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String inputLine = textField.getText();
				textField.setText("");
				
				if (inputLine != null) {
					String recipient = inputLine.split("-", 2)[0];
					String message = inputLine.split("-", 2)[1];
					for (Handler clientHandler : ChatServer.getActiveClients()) {
						if (clientHandler.getNickname().equals(recipient)) {
							clientHandler.textArea.append("[" + LocalDateTime.now().format(formatter) + "] " 
									+ getNickname() + " send DM: " + message + "\n");
							JOptionPane.showMessageDialog(frmChatapp, "Your message to " + recipient + " was sent!");
						}
					}
					
				}
			}
		});
		btnNewButton_2.setBounds(14, 461, 135, 23);
		panel_1.add(btnNewButton_2);
		
			list = new JList<String>(ChatServer.defListModel);
			list.setBounds(14, 21, 121, 413);
			panel_1.add(list);
			list.setBackground(UIManager.getColor("Button.background"));
			list.setFont(new Font("Arial Black", Font.PLAIN, 14));
			
			btnNewButton_1 = new JButton("EXIT");
			btnNewButton_1.setBounds(14, 495, 135, 23);
			panel_1.add(btnNewButton_1);
			btnNewButton_1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ChatServer.getActiveClientsNames().remove(getNickname());
					int index = 0;
					for (Handler clientHandler : ChatServer.getActiveClients()) {
						if (clientHandler.getNickname().equals(getNickname())) {
							ChatServer.getActiveClients().remove(index);
						}
						else {
							index++;
						}
					}
					DBManager.removeUser(getId());
					ChatServer.defListModel.removeElement(getNickname());
					
					int option = JOptionPane.showConfirmDialog(frmChatapp, "Do you want a copy of this conversation?");
					if (option == 0) {
						Mail.sendEmail(getEmail(), ChatServer.serverMail, (getNickname() + ", Your convo: \n" + textArea.getText()));
						JOptionPane.showMessageDialog(frmChatapp, "Email sent!");
					}
				}
			});
	}
	
	@Override
	public void run() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				frmChatapp.setVisible(true);
			}
		});
		out.println(this.getNickname() + " logged.");

	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public BufferedReader getIn() {
		return in;
	}

	public void setIn(BufferedReader in) {
		this.in = in;
	}

	public PrintWriter getOut() {
		return out;
	}

	public void setOut(PrintWriter out) {
		this.out = out;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}


