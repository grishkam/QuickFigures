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
 * Date Created: Jan 17, 2026
 * Date Modified: Jan 17, 2026
 * Version: 2023.2
 */
 
package standardDialog.choices;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/**
 A simnple modification to the list cell renderer that adds icons
 */
public class IconListCellRenderer extends BasicComboBoxRenderer implements ListCellRenderer<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Icon[] theIcons=new Icon[] {};
	
	public IconListCellRenderer(Icon[] theIcons) {
		this.theIcons= theIcons;
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index,
			boolean isSelected, boolean cellHasFocus) {
		
		IconListCellRenderer output = (IconListCellRenderer) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		try {
			if(index<theIcons.length & index!=-1)
				{output.setIcon(theIcons[index]);} else output.setIcon(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this;
	}
	
	public Dimension getPreferredSize() { 
		Dimension d = super.getPreferredSize();
		d.width+=25;
		return d;
	}

}
