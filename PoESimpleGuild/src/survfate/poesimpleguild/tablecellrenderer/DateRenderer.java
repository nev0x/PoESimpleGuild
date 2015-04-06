package survfate.poesimpleguild.tablecellrenderer;

import java.text.SimpleDateFormat;

import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class DateRenderer extends DefaultTableCellRenderer {

	SimpleDateFormat sDateFormat;

	public DateRenderer() {
		sDateFormat = new SimpleDateFormat("MMMM dd, yyyy");
	}

	public void setValue(Object value) {
		setText(value != null ? sDateFormat.format(value) : "");
	}
}
