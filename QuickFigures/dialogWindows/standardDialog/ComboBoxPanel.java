/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package standardDialog;

import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ComboBoxPanel extends JPanel implements OnGridLayout, ItemListener{

	JLabel label=new JLabel();
	JComboBox<? extends Object> box=new JComboBox<String>();
	boolean omitLabel=false;
	
	public JComboBox<? extends Object> getBox() {return box;}
	ArrayList<ChoiceInputListener> listeners=new ArrayList<ChoiceInputListener>();
	private String key;
	public int originalStatus;
	
	public void setItemFont(Font f) {
		box.setFont(f);
		label.setFont(f);
	}
	
	
	public ComboBoxPanel(String labeln, String[] choices, int startingindex) {
		JComboBox<String> box2 = new JComboBox<String>();
		box=box2;
		label.setText(labeln);
		for(String c: choices) {
			box2.addItem(c);
		}
		if (startingindex>=box2.getItemCount()) startingindex=0;
		box2.setSelectedIndex(startingindex);
		{box2.addItemListener(this);}
		this.originalStatus=startingindex;
	}
	
	public ComboBoxPanel(String labeln, JComboBox<? extends Object> box) {
		label.setText(labeln);
		
		this.box=box;
		{box.addItemListener(this);}
		this.originalStatus=box.getSelectedIndex();
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void placeItems(Container jp, int x0, int y0) {
		GridBagConstraints gc = new GridBagConstraints();
		
		if (!omitLabel)	{
			gc.anchor = GridBagConstraints.EAST;
			gc.insets=firstInsets;
			gc.gridx=x0;
			gc.gridy=y0;
			jp.add(label, gc);
			gc.gridx++;
			}
		
		gc.anchor = GridBagConstraints.WEST;
		
		gc.insets=lastInsets;
		jp.add(box, gc);
		
		
	}

	

	@Override
	public int gridHeight() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public int gridWidth() {
		// TODO Auto-generated method stub
		return 2;
	}

	/**returns the selected index*/
	public int getSelectedIndex() {
		return getBox() .getSelectedIndex();
	}

/**returns the choice to its original value */
public void revert() {
	setValue(originalStatus);
}


/**
 Sets the choice index
 */
public void setValue(int value) {
	getBox() .setSelectedIndex(value);
}


	
	public void notifyListeners(ChoiceInputEvent ni) {
		//NumberInputEvent ni = new NumberInputEvent(this, getNumberFromField() );
		for(ChoiceInputListener l :listeners) {
			if(l==null) continue;
			l.numberChanged(ni);
		}
	}
	
	public void addChoiceInputListener(ChoiceInputListener ni) {
		listeners.add(ni);
	}
	public void removeChoiceInputListener(ChoiceInputListener ni) {
		listeners.remove(ni);
	}
	public ArrayList<ChoiceInputListener> getChoiceInputListeners() {
		return listeners;
	}



	@Override
	public void itemStateChanged(ItemEvent arg0) {
		if (arg0.getSource()==box) {
			ChoiceInputEvent ni = new ChoiceInputEvent(this, box, box.getSelectedIndex(), box.getSelectedItem()) ;
			ni.setKey(key);
			this.notifyListeners(ni);
		}
		
	}

	public void setKey(String key) {
		this.key=key;
		
	}
	
	/**the number of choices*/
	public int getNChoices() {return box.getModel().getSize();}
	
}
