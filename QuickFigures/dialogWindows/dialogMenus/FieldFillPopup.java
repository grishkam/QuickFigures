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
 * Version: 2023.1
 */
package dialogMenus;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JTextField;

import menuUtil.SmartPopupJMenu;

/**
 
 * 
 */
public class FieldFillPopup extends SmartPopupJMenu implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean empty=true;
	
	private FieldFillPopup(JTextField f, HashMap<String, String> choices, ActionListener actionListener) {
		for(String s: choices.keySet()) {
			empty=false;
			FieldFillMenuItem menuItem = new FieldFillMenuItem(s, s, f);
			add(menuItem);
		}
		f.addMouseListener(this);
	}
	
	public FieldFillPopup(JTextField f, String[] contantMapValues, ActionListener actionListener) {
		for(int i=1; i<contantMapValues.length; i+=2) {
			FieldFillMenuItem menuItem = new FieldFillMenuItem(contantMapValues[i-1], contantMapValues[i-1], f);
			add(menuItem);
			menuItem.addActionListener2(actionListener);
			empty=false;
		}
		f.addMouseListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getClickCount()<2)
			return;
		Object s = e.getSource();
		showForObject(s);
	}

	/**shows this popup menu for the object
	 * @param s
	 */
	public void showForObject(Object s) {
		if(empty)
			return;
		if(s instanceof Component) {
			Component s2 = (Component) s;
			this.show(s2, s2.getWidth(), 0);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public JButton getPressTrigger() {
		JButton jb=new JButton("?");
		jb.setPreferredSize(new Dimension(15,15));
		jb.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				showForObject(e.getSource());
				
			}});
		if(empty) {
			return null;
		}
		return jb;
	}

}
