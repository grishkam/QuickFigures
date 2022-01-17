/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
/**
 * Author: Greg Mazo
 * Date Modified: Jan 7, 2021
 * Version: 2022.0
 */
package dataTableDialogs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import fileread.PlotType;

/**A special table cell renderer for certain data tables
 * @see SmartDataInputDialog*/
public class SmartRenderer extends DefaultTableCellRenderer implements TableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color theColor=new Color(245, 180, 180);

	public SmartRenderer(SmartDataInputDialog smartDataInputDialog, PlotType form, Color c) {
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
