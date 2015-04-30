package survfate.poesimpleguild;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import survfate.poesimpleguild.tablecellrenderer.DateRenderer;
import survfate.poesimpleguild.tablecellrenderer.TimeRenderer;

@SuppressWarnings("serial")
public class GUI extends JPanel implements ActionListener {

	private static String VERSION = "v0.1.2b";
	private JTable table;
	private DefaultTableModel tableModel;
	private JPanel panel;
	private JPanel p1;

	private JButton buttonGet;
	private JCheckBox checkBoxPoeTrade;
	private JTextField jTextField;
	private JTextArea output;

	public GUI() {
		setLayout(new BorderLayout());
		tableModel = new DefaultTableModel(new String[] { "Account Name",
				"Member Type", "Challenge(s) Done", "Joined Date",
				"Last Visted Date", "Last Ladder Online", "Poe.Trade Online" },
				0) {

			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int column) {
				if (column == 3 || column == 4 || column == 5)
					return Date.class;
				else if (column == 6)
					return Boolean.class;
				else
					return Object.class;

			}

			public boolean isCellEditable(int row, int column) {
				return false;
				// This causes all cells to be not editable
			}
		};

		table = new JTable(tableModel) {
			public boolean getScrollableTracksViewportWidth() {
				return getPreferredSize().width < getParent().getWidth();
			}
		};

		table.setPreferredScrollableViewportSize(new Dimension(700, 300));
		table.setFillsViewportHeight(true);
		table.setAutoCreateRowSorter(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setRowHeight(25);

		// table.getColumnModel().getColumn(2)
		// .setCellRenderer(new ChallengesIconRenderer());
		table.getColumnModel().getColumn(3).setCellRenderer(new DateRenderer());
		table.getColumnModel().getColumn(4).setCellRenderer(new DateRenderer());
		table.getColumnModel().getColumn(5).setCellRenderer(new TimeRenderer());

		add(new JScrollPane(table), BorderLayout.CENTER);

		panel = new JPanel();
		panel.setLayout(new BorderLayout(5, 5));
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		add(panel, BorderLayout.NORTH);

		p1 = new JPanel();
		p1.setLayout(new FlowLayout());
		panel.add(p1, BorderLayout.CENTER);
		p1.add(new JLabel("Guild ID:"));
		jTextField = new JTextField(6);
		p1.add(jTextField);
		buttonGet = new JButton("Get Guild Members Data");
		buttonGet.addActionListener(this);
		p1.add(buttonGet);

		p1.add(new JLabel("Poe.Trade Online Check?"));
		checkBoxPoeTrade = new JCheckBox();
		checkBoxPoeTrade.addActionListener(this);
		p1.add(checkBoxPoeTrade);

		output = new JTextArea(5, 40);
		DefaultCaret ouputCaret = (DefaultCaret) output.getCaret();
		ouputCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		output.setEditable(false);
		add(new JScrollPane(output), BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == buttonGet) {
			LoadGuildData load = new LoadGuildData();
			load.execute();
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
		JFrame frame = new JFrame("PoE Simple Guild " + VERSION);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		GUI newContentPane = new GUI();
		newContentPane.setOpaque(true);
		frame.setContentPane(newContentPane);

		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GUI.createAndShowGUI();
			}
		});
	}

	class LoadGuildData extends SwingWorker<Void, Void> {
		protected Void doInBackground() {
			try {
				long tStart = System.currentTimeMillis();

				buttonGet.setEnabled(false);
				jTextField.setEnabled(false);
				checkBoxPoeTrade.setEnabled(false);

				tableModel.getDataVector().removeAllElements();
				tableModel.fireTableDataChanged();

				Document jsoupDoc = Jsoup
						.connect(
								"http://www.pathofexile.com/guild/profile/"
										+ jTextField.getText()).timeout(5000)
						.get();
				Element detailsContent = jsoupDoc.getElementsByClass(
						"details-content").first();
				int i = 0;
				int guildSize = detailsContent.getElementsByClass("member")
						.size();

				output.setText("");
				output.append("Guild Name: "
						+ detailsContent.getElementsByClass("name").first()
								.text() + "\t");
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
				output.append("Total Members: " + guildSize + "\n");

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
					boolean poeTradeOnline = false;
					if (checkBoxPoeTrade.isSelected() == true)
						poeTradeOnline = account.getPoeTradeOnlineStatus();
					// else {
					// table.getColumnModel().getColumn(6).setMinWidth(0);
					// table.getColumnModel().getColumn(6).setMaxWidth(0);
					// table.getColumnModel().getColumn(6).setWidth(0);
					// }

					if (member.child(0).child(0).childNodeSize() == 2) {
						tableModel.addRow(new Object[] { accountName,
								memberType, title, joinedDate, lastVisitedDate,
								lastLadderOnline, poeTradeOnline });
						output.append("Status: Loaded " + i + "/" + guildSize
								+ " Members\n");
					} else {
						tableModel.addRow(new Object[] { accountName,
								memberType, title, joinedDate, lastVisitedDate,
								lastLadderOnline, poeTradeOnline });
						output.append("Status: Loaded " + i + "/" + guildSize
								+ " Members\n");
					}

				}
				long tEnd = System.currentTimeMillis();
				long tDelta = tEnd - tStart;
				double elapsedSeconds = tDelta / 1000.0;
				output.append("Process completed, elapsed time: "
						+ elapsedSeconds + " second(s)");
			} catch (org.jsoup.HttpStatusException e) {
				JOptionPane.showMessageDialog(null,
						"Invalid Guild ID! Please try again.", "Error", 0);
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,
						"Connection timed out! Please try again.", "Error", 0);
				e.printStackTrace();
			}
			return null;
		}

		protected void done() {
			tableModel.fireTableDataChanged();
			buttonGet.setEnabled(true);
			jTextField.setEnabled(true);
			checkBoxPoeTrade.setEnabled(true);
		}
	}
}
