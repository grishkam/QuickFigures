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
 * Date Created: Mar 1, 2023
 * Date Modified: Mar 1, 2023
 * Version: 2023.2
 */
package dialogMenus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

import menuUtil.BasicSmartMenuItem;

/**
 
 * 
 */
public class FieldFillMenuItem extends BasicSmartMenuItem{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**The value to set*/
	private String value;
	private JTextField field;

	private ActionListener als2;

	public FieldFillMenuItem(String menuName, String vale, JTextField field) {
		this.setText(menuName);
		this.value=vale;
		this.field=field;
	}
	
	/**Called when this menu item is pressed*/
	public void actionPerformed(ActionEvent e) {
		field.setText(value);
		if(als2!=null) {
			als2.actionPerformed(e);
		}
	}

	/**
	 * @param actionListener
	 */
	public void addActionListener2(ActionListener actionListener) {
		als2=actionListener;
		
	}
	
}
