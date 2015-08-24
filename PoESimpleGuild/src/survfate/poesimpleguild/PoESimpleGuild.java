package survfate.poesimpleguild;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.StringTokenizer;

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
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import survfate.poesimpleguild.resources.ResourcesLoader;
import survfate.poesimpleguild.resources.ResourcesLoaderDialog;
import survfate.poesimpleguild.tablecellrenderer.ChallengeIconsRenderer;
import survfate.poesimpleguild.tablecellrenderer.DateRenderer;
import survfate.poesimpleguild.tablecellrenderer.LeftRenderer;
import survfate.poesimpleguild.tablecellrenderer.SupporterTagsRenderer;
import survfate.poesimpleguild.tablecellrenderer.TimeRenderer;
import survfate.poesimpleguild.tablecellrenderer.URLRenderer;

@SuppressWarnings("serial")
public class PoESimpleGuild extends JPanel implements ActionListener {

	private static String VERSION = "v0.1.3";
	private JTable table;
	private TableColumnAdjuster adjuster;
	private DefaultTableModel tableModel;
	private JPanel panel;
	private JPanel p1;

	private JButton buttonGet;
	private JCheckBox checkBoxPoeTrade;
	private JTextField jTextField;
	private JTextArea output;

	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				PoESimpleGuild.loadResources();
				PoESimpleGuild.createAndShowGUI();
			}
		});
	}

	private static void loadResources() {
		Dialog dialog = new ResourcesLoaderDialog();
		SwingWorker<Void, Void> loaderWorker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				// Instantiate ResourcesLoader object
				new ResourcesLoader();
				return null;
			}

			public void done() {
				dialog.setVisible(false);
				dialog.dispose();
			}
		};
		loaderWorker.execute();
		dialog.setVisible(true);
	}

	private static void createAndShowGUI() {
		try {
			org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
			// UIManager.setLookAndFeel(org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.getBeautyEyeLNFCrossPlatform());

			UIManager.put("RootPane.setupButtonVisible", false);

			// UIManager.getDefaults().put("TextArea.font",
			// UIManager.getFont("TextField.font"));

			Font robotoFont = ResourcesLoader.robotoFont;

			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(robotoFont.deriveFont(13F));

			setUIFont(new FontUIResource(robotoFont.deriveFont(13F)));

		} catch (Exception e) {
			// If not available, you can set the GUI to another look
			// and feel.
		}
		// UIManager.put("swing.boldMetal", Boolean.FALSE);
		JFrame frame = new JFrame("PoE Simple Guild " + VERSION);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		PoESimpleGuild newContentPane = new PoESimpleGuild();
		newContentPane.setOpaque(true);
		frame.setContentPane(newContentPane);

		frame.pack();
		frame.setVisible(true);
	}

	public PoESimpleGuild() {
		setLayout(new BorderLayout());
		tableModel = new DefaultTableModel(new String[] { "Account Name", "Member Type", "Challenge(s) Done",
				"Total Forum Posts", "Joined Date", "Last Visted Date", "Last Ladder Online", "Supporter Tag(s)",
				"Poe.Trade Online", "Profile URL" }, 0) {

			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int column) {
				if (column == 3)
					return Integer.class;
				else if (column == 4 || column == 5 || column == 6)
					return Date.class;
				else if (column == 8)
					return Boolean.class;
				else if (column == 9)
					return URL.class;
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

		adjuster = new TableColumnAdjuster(table);
		adjuster.adjustColumns();

		table.getColumnModel().getColumn(2).setCellRenderer(new ChallengeIconsRenderer());
		table.getColumnModel().getColumn(3).setCellRenderer(new LeftRenderer());
		table.getColumnModel().getColumn(4).setCellRenderer(new DateRenderer());
		table.getColumnModel().getColumn(5).setCellRenderer(new DateRenderer());
		table.getColumnModel().getColumn(6).setCellRenderer(new TimeRenderer());
		table.getColumnModel().getColumn(7).setCellRenderer(new SupporterTagsRenderer());

		URLRenderer urlRenderer = new URLRenderer();
		table.getColumnModel().getColumn(9).setCellRenderer(urlRenderer);
		table.addMouseListener(urlRenderer);
		table.addMouseMotionListener(urlRenderer);

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

		p1.add(new JLabel("Poe.Trade Online Check? (Slow!)"));
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

	public static void setUIFont(javax.swing.plaf.FontUIResource f) {
		@SuppressWarnings("rawtypes")
		java.util.Enumeration keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value != null && value instanceof javax.swing.plaf.FontUIResource)
				UIManager.put(key, f);
		}
	}

	// SwingWorker for geting Guild information
	// Better as Class for easier calling in ActionEvent
	class LoadGuildData extends SwingWorker<Void, Void> {
		protected Void doInBackground() {
			try {
				long tStart = System.currentTimeMillis();

				buttonGet.setEnabled(false);
				jTextField.setEnabled(false);
				checkBoxPoeTrade.setEnabled(false);

				tableModel.getDataVector().removeAllElements();
				tableModel.fireTableDataChanged();

				Document jsoupDoc = Jsoup.connect("http://www.pathofexile.com/guild/profile/" + jTextField.getText())
						.timeout(5000).get();
				Element detailsContent = jsoupDoc.getElementsByClass("details-content").first();
				int i = 0;
				int guildSize = detailsContent.getElementsByClass("member").size();

				// Clear all texts and print out Guild details
				output.setText("");

				output.append("Guild Name: " + detailsContent.getElementsByClass("name").first().text() + "\t");
				try {
					output.append("Guild Tag: " + detailsContent.getElementsByClass("guild-tag").first().text() + "\n");
					output.append(
							"Guild Status: " + detailsContent.getElementsByClass("guild-status").first().text() + "\n");
				} catch (Exception e) {
					// Some Guild don't have these two
				}
				output.append("Created: " + detailsContent.child(1).childNode(3).toString() + "\n");
				output.append("Total Members: " + guildSize + "\n");

				// Run through each Guildmembers
				for (Element member : detailsContent.getElementsByClass("member")) {
					i++;
					String accountName = member.child(0).text().trim(); // 0
					String memberType = member.child(1).text().trim(); // 1
					String title = null;
					if (member.child(0).child(0).childNodeSize() == 2) {
						title = member.child(0).child(0).child(0).attr("title"); // 2
					}

					// Instantiate account object
					Account account = new Account(accountName);
					int forumPosts = account.getForumPosts(); // 3
					Date joinedDate = account.getJoinedDate(); // 4
					Date lastVisitedDate = account.getLastVisitedDate(); // 5
					Date lastLadderOnline = account.getLastLadderOnline(); // 6
					if (lastLadderOnline.equals(Date.from(Instant.ofEpochSecond(0))))
						lastLadderOnline = null;
					StringTokenizer tokenizer = new StringTokenizer(account.getSupporterTagKeys());
					// Technically the tagKeys is still a string but we can
					// still sort it since it only single digit, also it'll help
					// shorter the code
					String tagKeys = String.valueOf(tokenizer.countTokens()) + " " + account.getSupporterTagKeys(); // 7
					boolean poeTradeOnline = false;
					if (checkBoxPoeTrade.isSelected() == true)
						poeTradeOnline = account.getPoeTradeOnlineStatus(); // 8
					URL profileURL = account.getURL(); // 9

					tableModel.addRow(new Object[] { accountName, memberType + account.getStatus(), title, forumPosts,
							joinedDate, lastVisitedDate, lastLadderOnline, tagKeys, poeTradeOnline, profileURL });
					output.append("Status: Loaded " + i + "/" + guildSize + " Members\n");

				}
				adjuster.adjustColumns();
				long tEnd = System.currentTimeMillis();
				long tDelta = tEnd - tStart;
				double elapsedSeconds = tDelta / 1000.0;
				output.append("Process completed, elapsed time: " + elapsedSeconds + " second(s)");
			} catch (org.jsoup.HttpStatusException e) {
				JOptionPane.showMessageDialog(null, "Invalid Guild ID! Please try again.", "Error", 0);
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (java.net.SocketTimeoutException e) {
				JOptionPane.showMessageDialog(null, "Connection timed out after 10 try! Please try again.", "Error", 0);
				e.printStackTrace();
			} catch (IOException e) {
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
