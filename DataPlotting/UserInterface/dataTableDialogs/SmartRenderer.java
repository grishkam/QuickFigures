package dataTableDialogs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class SmartRenderer extends DefaultTableCellRenderer implements TableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color theColor=new Color(245, 180, 180);

	public SmartRenderer(SmartDataInputDialog smartDataInputDialog, int form, Color c) {
		theColor=c;
	}

	@Override
	public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3, int arg4,
			int arg5) {
		this.setBackground(theColor);
		if (arg4==0) {
			setBackground(Color.gray);
		}
		
		boolean excluded=arg1 instanceof String&& ((String) arg1).endsWith("*");
	
		/**Special font for excluded values*/
		if (excluded) {
			String st=(String) arg1;
			if (st.endsWith("*")) {
				setForeground(Color.blue.darker());
			} 
		}
		else {
			setForeground(Color.black);
		}
		
		
		Component output = super.getTableCellRendererComponent(arg0, arg1, arg2, arg3, arg4, arg5);
	
		/**Special font for excluded values*/
		if (excluded) {
			String st=(String) arg1;
			if (st.endsWith("*")) {
				output.setForeground(Color.blue.darker());
				Font f = output.getFont();
				f=f.deriveFont(Font.ITALIC);
				output.setFont(f);
			} 
		}
		else {
			output.setForeground(Color.black);
		}
		
		return output;
	}
	
	

}
