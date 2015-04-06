package survfate.poesimpleguild.tablecellrenderer;

import java.text.SimpleDateFormat;

import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class TimeRenderer extends DefaultTableCellRenderer {

	SimpleDateFormat sTimeFormat;

	public TimeRenderer() {
		sTimeFormat = new SimpleDateFormat("MMMM dd, yyyy hh:mm a zzz");
	}

	public void setValue(Object value) {
		setText(value != null ? sTimeFormat.format(value) : "");
	}
}