package survfate.poesimpleguild.tablecellrenderer;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class DateRenderer extends DefaultTableCellRenderer {

	SimpleDateFormat sDateFormat;

	public DateRenderer() {
		sDateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
	}

	public void setValue(Object value) {
		setText(value != null ? sDateFormat.format(value) : "");
	}
}
