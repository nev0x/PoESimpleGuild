package survfate.poesimpleguild;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.table.*;
import javax.swing.text.DefaultCaret;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@SuppressWarnings("serial")
class DateRenderer extends DefaultTableCellRenderer {

	SimpleDateFormat sDateFormat;

	public DateRenderer() {
		sDateFormat = new SimpleDateFormat("MMMM dd, yyyy");
	}

	public void setValue(Object value) {
		setText(value != null ? sDateFormat.format(value) : "");
	}
}

@SuppressWarnings("serial")
class TimeRenderer extends DefaultTableCellRenderer {

	SimpleDateFormat sTimeFormat;

	public TimeRenderer() {
		sTimeFormat = new SimpleDateFormat("MMMM dd, yyyy hh:mm a zzz");
	}

	public void setValue(Object value) {
		setText(value != null ? sTimeFormat.format(value) : "");
	}
}

@SuppressWarnings("serial")
public class GUI extends JPanel implements ActionListener {

	private JTable table;
	private DefaultTableModel tableModel;
	private JPanel panel;
	private JPanel p1;
	// private JPanel p2;
	// private JPanel p3;
	private JButton buttonGet;
	private JTextField textFieldID;
	private JTextArea output;

	public GUI() {
		setLayout(new BorderLayout());
		tableModel = new DefaultTableModel(new String[] { "Account Name",
				"Member Type", "Challenge(s) Done", "Joined Date",
				"Last Visted Date", "Last Ladder Online" }, 0) {

			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int column) {
				if (column == 3 || column == 4 || column == 5) {
					return Date.class;
				} else {
					return Object.class;
				}
			}
		};

		table = new JTable(tableModel);
		table.setPreferredScrollableViewportSize(new Dimension(700, 300));
		table.setFillsViewportHeight(true);
		table.setAutoCreateRowSorter(true);
		table.getColumnModel().getColumn(3).setCellRenderer(new DateRenderer());
		table.getColumnModel().getColumn(4).setCellRenderer(new DateRenderer());
		table.getColumnModel().getColumn(5).setCellRenderer(new TimeRenderer());
		add(new JScrollPane(table), "Center");

		panel = new JPanel();
		panel.setLayout(new BorderLayout(5, 5));
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		add(panel, "North");

		p1 = new JPanel();
		p1.setLayout(new FlowLayout());
		panel.add(p1, "Center");
		p1.add(new JLabel("Guild ID:"));
		textFieldID = new JTextField(6);
		p1.add(textFieldID);
		buttonGet = new JButton("Get Guild Members Data");
		buttonGet.addActionListener(this);
		p1.add(buttonGet);

		output = new JTextArea(5, 40);
		DefaultCaret ouputCaret = (DefaultCaret) output.getCaret();
		ouputCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		output.setEditable(false);
		add(new JScrollPane(output), "South");
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == buttonGet) {
			LoadData loadData = new LoadData();
			loadData.execute();
		}
	}

	private static void createAndShowGUI() {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// If Nimbus is not available, you can set the GUI to another look
			// and feel.
		}

		// UIManager.put("swing.boldMetal", Boolean.FALSE);
		JFrame frame = new JFrame("PoE Simple Guild v0.1");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		GUI newContentPane = new GUI();
		newContentPane.setOpaque(true);
		frame.setContentPane(newContentPane);

		frame.pack();
		frame.setVisible(true);
	}

	class LoadData extends SwingWorker<Void, Void> {
		protected Void doInBackground() {
			try {
				long tStart = System.currentTimeMillis();

				buttonGet.setEnabled(false);
				textFieldID.setEnabled(false);

				tableModel.getDataVector().removeAllElements();
				tableModel.fireTableDataChanged();

				Document jsoupDoc = Jsoup
						.connect(
								(new StringBuilder(
										"http://www.pathofexile.com/guild/profile/"))
										.append(textFieldID.getText())
										.toString()).timeout(5000).get();
				Element detailsContent = jsoupDoc.getElementsByClass(
						"details-content").first();
				int i = 0;
				int guildSize = detailsContent.getElementsByClass("member")
						.size();

				output.setText("");
				output.append("Guild Name: "
						+ detailsContent.getElementsByClass("name").first()
								.text() + "\n");
				try {
					output.append("Guild Tag: "
							+ detailsContent.getElementsByClass("guild-tag")
									.first().text() + "\n");
					output.append("Guild Status: "
							+ detailsContent.getElementsByClass("guild-status")
									.first().text() + "\n");
				} catch (Exception e) {
					// Some Guild don't have these two
				}
				output.append("Created: "
						+ detailsContent.child(1).childNode(3).toString()
						+ "\n");
				output.append("Total Members: " + guildSize + "\n\n");

				for (Element member : detailsContent
						.getElementsByClass("member")) {
					i++;
					String accountName = member.child(0).text().trim();
					String memberType = member.child(1).text().trim();
					String title = null;
					if (member.child(0).child(0).childNodeSize() == 2) {
						title = member.child(0).child(0).child(0).attr("title");
					}

					Account account = new Account(accountName);
					Date joinedDate = account.getJoinedDate();
					Date lastVisitedDate = account.getLastVisitedDate();
					Date lastLadderOnline = account.getLastLadderOnline();
					if (member.child(0).child(0).childNodeSize() == 2) {
						tableModel.addRow(new Object[] { accountName,
								memberType, title, joinedDate, lastVisitedDate,
								lastLadderOnline });
						output.append((new StringBuilder("Status: Loaded "))
								.append(i).append("/").append(guildSize)
								.append(" Members\n").toString());
					} else {
						tableModel.addRow(new Object[] { accountName,
								memberType, title, joinedDate, lastVisitedDate,
								lastLadderOnline });
						output.append((new StringBuilder("Status: Loaded "))
								.append(i).append("/").append(guildSize)
								.append(" Members\n").toString());
					}

				}
				long tEnd = System.currentTimeMillis();
				long tDelta = tEnd - tStart;
				double elapsedSeconds = tDelta / 1000.0;
				output.append("\nProcess completed, elapsed time: "
						+ elapsedSeconds + " second(s)");
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,
						"Invalid Guild ID! Please try again.", "Error", 0);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void done() {
			tableModel.fireTableDataChanged();
			buttonGet.setEnabled(true);
			textFieldID.setEnabled(true);
		}
	}

	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GUI.createAndShowGUI();
			}
		});
	}

}
