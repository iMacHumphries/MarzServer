import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;

import server.Constants;


public class ServerView extends JFrame implements ActionListener {
	private static final long serialVersionUID = -1857354783005906823L;

	private JTextArea statsArea;
	private JTextArea playersArea;
	private JTextArea logArea;
	private JTextField commandField;

	public ServerView() {
		super("Marz Server");
		
		statsArea = new JTextArea("");
		playersArea = new JTextArea("");
		logArea = new JTextArea("");
		commandField = new JTextField("");

		try {
			this.appendStringToLog("Starting server on " + InetAddress.getLocalHost().getHostAddress() + ":" + Constants.PORT_NUMBER);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		build();
		
		updateStats();
		Timer timer = new Timer(5_000, new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				updateStats();
			}
		});
		timer.start();
	}

	private void build() {

		statsArea.setEditable(false);
		statsArea.setPreferredSize(new Dimension(200, 300));
		statsArea.setBorder(new TitledBorder("Stats"));

		playersArea.setEditable(false);
		playersArea.setPreferredSize(new Dimension(200, 100));
		playersArea.setBorder(new TitledBorder("Players"));

		JPanel leftPanel = new JPanel(new GridLayout(2, 1));
		leftPanel.add(statsArea);
		leftPanel.add(new JScrollPane(playersArea));

		logArea.setEditable(false);
		logArea.setBorder(new TitledBorder("Log"));

		commandField.addActionListener(this);
		commandField.setPreferredSize(new Dimension(300, 25));

		JPanel main = new JPanel(new BorderLayout());
		JScrollPane js = new JScrollPane(logArea);
		main.add(leftPanel, BorderLayout.WEST);
		main.add(js, BorderLayout.CENTER);
		main.add(commandField, BorderLayout.SOUTH);
		getContentPane().add(main);

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(900, 500));
		pack();
		setVisible(true);
	}

	public void appendStringToLog(String msg) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				logArea.append("> " + msg + "\n");
			}
		});
	}
	
	public void displayPlayers(ArrayList<ServerPlayer> players) {
		String result = "";
		for (ServerPlayer p : players) 
			result += p.getUsername() + "\n";
		final String resCopy = result;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				playersArea.setText(resCopy);
			}
		});
	}

	private void updateStats() {
		Runtime runtime = Runtime.getRuntime();

		NumberFormat format = NumberFormat.getInstance();

		StringBuilder sb = new StringBuilder();
		long maxMemory = runtime.maxMemory();
		long allocatedMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();
		
		final long convertToMb = 1024 * 1024;

		sb.append("Free memory: " + format.format(freeMemory / convertToMb) + "mb\n");
		sb.append("Allocated memory: " + format.format(allocatedMemory / convertToMb) + "mb\n");
		sb.append("Max memory: " + format.format(maxMemory / convertToMb) + "mb\n");
		sb.append("Total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / convertToMb) + "mb\n");
		
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();

		int count = 0;
		for (Thread t : threadSet) {
			if (t.isAlive())
				count++;
		}
		
		sb.append("Threads running: " + count);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				statsArea.setText(sb.toString());
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == commandField) {
			commandField.setText("");
		}
	}

}
