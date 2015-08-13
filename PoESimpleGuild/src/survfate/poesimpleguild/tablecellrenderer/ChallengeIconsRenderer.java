package survfate.poesimpleguild.tablecellrenderer;

import java.awt.Component;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class ChallengeIconsRenderer extends DefaultTableCellRenderer {
	ImageIcon iconOne, iconTwo, iconThree, iconFour, iconFive, iconSix, iconSeven, iconEight;
	URL urlOne, urlTwo, urlThree, urlFour, urlFive, urlSix, urlSeven, urlEight;

	public ChallengeIconsRenderer() {
		// Load the resources here since they are all small, will move to a
		// seperate resources loader when the amount of resources is rising.
		try {
			urlOne = new URL("https://p7p4m6s5.ssl.hwcdn.net/image/icons/achievements/1.png");
			urlTwo = new URL("https://p7p4m6s5.ssl.hwcdn.net/image/icons/achievements/2.png");
			urlThree = new URL("https://p7p4m6s5.ssl.hwcdn.net/image/icons/achievements/3.png");
			urlFour = new URL("https://p7p4m6s5.ssl.hwcdn.net/image/icons/achievements/4.png");
			urlFive = new URL("https://p7p4m6s5.ssl.hwcdn.net/image/icons/achievements/5.png");
			urlSix = new URL("https://p7p4m6s5.ssl.hwcdn.net/image/icons/achievements/6.png");
			urlSeven = new URL("https://p7p4m6s5.ssl.hwcdn.net/image/icons/achievements/7.png");
			urlEight = new URL("https://p7p4m6s5.ssl.hwcdn.net/image/icons/achievements/8.png");

			iconOne = new ImageIcon(urlOne);
			iconTwo = new ImageIcon(urlTwo);
			iconThree = new ImageIcon(urlThree);
			iconFour = new ImageIcon(urlFour);
			iconFive = new ImageIcon(urlFive);
			iconSix = new ImageIcon(urlSix);
			iconSeven = new ImageIcon(urlSeven);
			iconEight = new ImageIcon(urlEight);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if (value != null) {
			// if (value.toString().equals("Completed 1 Challenge"))
			// setIcon(iconOne);

			switch (value.toString()) {
			case "Completed 1 Challenge":
				setIcon(iconOne);
				break;
			case "Completed 2 Challenges":
				setIcon(iconTwo);
				break;
			case "Completed 3 Challenges":
				setIcon(iconThree);
				break;
			case "Completed 4 Challenges":
				setIcon(iconFour);
				break;
			case "Completed 5 Challenges":
				setIcon(iconFive);
				break;
			case "Completed 6 Challenges":
				setIcon(iconSix);
				break;
			case "Completed 7 Challenges":
				setIcon(iconSeven);
				break;
			case "Completed 8 Challenges":
				setIcon(iconEight);
				break;
			}

		} else
			setIcon(null);

		// setText(...);
		// setIcon(...);
		// setToolTipText(...);
		return this;
	}
}