package survfate.poesimpleguild.tablecellrenderer;

import java.awt.Component;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import survfate.poesimpleguild.resources.ResourcesLoader;

@SuppressWarnings("serial")
public class ChallengeIconsRenderer extends DefaultTableCellRenderer {
	ImageIcon[] challengeIcon = ResourcesLoader.challengeIcon;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if (value != null) {
			StringTokenizer stringTokenizer = new StringTokenizer(value.toString());
			int i = Integer.parseInt(stringTokenizer.nextToken("Completed").trim());
			setIcon(challengeIcon[i]);
		} else
			setIcon(null);

		// setText(...);
		// setIcon(...);
		// setToolTipText(...);
		return this;
	}
}