/*******************************************************************************
 * Copyright (c) 2026 Gregory Mazo
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
 * Date Created: Jan 21, 2026
 * Date Modified: Jan 21, 2026
 * Version: 2026.1
 */
package standardDialog.choices;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.JScrollPane;

import logging.IssueLog;
import standardDialog.StandardDialog;
import storedValueDialog.StoredValueDilaog;

/**
 
 * 
 */
public class JListInputPanel<Type> extends ChoiceInputPanel {

	
	
	private JList<Type> box3;
	private JScrollPane thepane;
	
	
	/**
	 * @param labeln
	 * @param choices
	 * @param startingindex
	 * @param type
	 * @param m
	 */
	public JListInputPanel(String labeln, ArrayList<Type> choices, int startingindex, Class<?> type, Method m) {
		super(labeln, choices, startingindex, type, m);
		Vector<Type> v1 = new Vector<Type>() ;
		 ArrayList<Type> listx = new ArrayList<Type>();
		for(Type s:choices) {v1.add(s); listx.add(s);}
		
		 box3=new JList<Type>(v1);
		
		thepane=new JScrollPane(box3);
		
		thepane.setPreferredSize(new Dimension(100,200));
		
		
		box3.setSelectedIndex(startingindex);
		originalStatus=box3.getSelectedIndex();
		IssueLog.log(""+listx.size());
	}
	
	/**switches the functtionto a jlist*/
	public void changeToJListVersion(String[] choices, int startingindex,  boolean listen) {
		
		
		
	}
	;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	

	/**returns the selected index*/
	public int getSelectedIndex() {
		
			return box3.getSelectedIndex();
		
	}
	
	/**
	 * @return
	 */
	@Override
	Component getChoiceGUIObject() {
		return thepane;
	}
	
	/**
	 Sets the choice index
	 */
	public void setValue(int value) {
		box3 .setSelectedIndex(value);
	}
	
	@Override
	public void itemStateChanged(ItemEvent arg0) {
		if (arg0.getSource()==box3) {
			ChoiceInputEvent ni = new ChoiceInputEvent(this, box3, box3.getSelectedIndex(), box3.getSelectedValue()) ;
			ni.setKey(key);
			this.notifyListeners(ni);
		}
		
	}
	
	public static String  getChoiceFromUser(String st, ArrayList<String> possible_vales) {
		StandardDialog sd = new StandardDialog(st);
		sd.setModal(true);
		sd.setWindowCentered(true);
		
		int startingval=1;
		ChoiceInputPanel interpolationChoice = new JListInputPanel<String>(st, possible_vales, startingval, null, null);
		
			sd.add(st, interpolationChoice);
		
		
		sd.showDialog();
		
		if(sd.wasOKed()) {
			return possible_vales.get(interpolationChoice.getSelectedIndex());
					
		}
		return possible_vales.get(startingval);
	}
	
	public static void main(String[] args) {
		ArrayList<String> item = new ArrayList<String>();
		item.add("C1");
		item.add("C5");
		item.add("C8");
		
		String selected=getChoiceFromUser("pick a value", item);
		System.out.println(selected);
	}

}
