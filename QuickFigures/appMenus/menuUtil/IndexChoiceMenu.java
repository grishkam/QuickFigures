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
 * Date Modified: Jan 6, 2021
 * Version: 2022.1
 */
package menuUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import logging.IssueLog;

/**A menu that is used to choose a single item for a list of numbers
 * and perform an action on the i-th item from a list of objects*/
public class IndexChoiceMenu<Type> extends JMenu implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int startind=1;
	int endind=2;
	String name="";
	Type object;
	HashMap<JMenuItem, Integer> map=new HashMap<JMenuItem, Integer>();
	
	public IndexChoiceMenu(Type o, String name, int st, int en) {
		super(name);
		this.object=o;
		this.startind=st;
				this.endind=en;
				try {
					generateJMenuItems();
				} catch (Throwable e) {
					IssueLog.logT(e);
				}
	}
	
	public void generateJMenuItems() {
		int s=startind;
		while (s<=endind) {
			JMenuItem jm = new JMenuItem(""+s);
			jm.addActionListener(this);
			map.put(jm, s);
			this.add(jm);
			s++;
		}
	}
	
	
	public void performAction(Type t, int i) {
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() instanceof JMenuItem) {
			JMenuItem s=(JMenuItem) arg0.getSource() ;
			int i=map.get(s);
			performAction(object, i);	
		}
		
	}
	
	

}
