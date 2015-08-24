package survfate.poesimpleguild.tablecellrenderer;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class TimeRenderer extends DefaultTableCellRenderer {

	SimpleDateFormat sTimeFormat;

	public TimeRenderer() {
		sTimeFormat = new SimpleDateFormat("MMMM dd, yyyy hh:mm a zzz", Locale.ENGLISH);
	}

	public void setValue(Object value) {
		setText(value != null ? sTimeFormat.format(value) : "");
	}
}