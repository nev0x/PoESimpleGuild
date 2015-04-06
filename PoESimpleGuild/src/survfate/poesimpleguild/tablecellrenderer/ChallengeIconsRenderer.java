package survfate.poesimpleguild.tablecellrenderer;

import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class ChallengeIconsRenderer extends DefaultTableCellRenderer {

	public ChallengeIconsRenderer() {
	}

	public void setValue(Object value) {
		setText(value != null ? (String) value : "");
	}
}