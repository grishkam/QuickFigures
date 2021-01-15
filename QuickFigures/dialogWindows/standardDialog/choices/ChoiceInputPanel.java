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
 * Version: 2021.1
 */
package standardDialog.choices;

import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import standardDialog.InputPanel;
import standardDialog.OnGridLayout;

/**A JPanel containing a combo box for placement into a standard dialog with a grided panel*/
public class ChoiceInputPanel extends InputPanel implements OnGridLayout, ItemListener{

	protected JLabel label=new JLabel();
	protected JComboBox<? extends Object> box=new JComboBox<String>();
	protected AbstractButton  button;
	
	boolean omitLabel=false;
	
	public JComboBox<? extends Object> getBox() {return box;}
	ArrayList<ChoiceInputListener> listeners=new ArrayList<ChoiceInputListener>();
	
	
	public int originalStatus;
	private ArrayList<Integer> originalValues=new ArrayList<Integer>();
	
	
	
	
	
	public ChoiceInputPanel(String labeln, String[] choices, int startingindex) {
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
		originalValues.add(startingindex);
		
	}
	

	
	public ChoiceInputPanel(String labeln, JComboBox<? extends Object> box) {
		label.setText(labeln);
		
		this.box=box;
		{box.addItemListener(this);}
		this.originalStatus=box.getSelectedIndex();
	}
	
	public void setButton(AbstractButton b) {button=b;}
	
	public void setItemFont(Font f) {
		box.setFont(f);
		label.setFont(f);
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
		
		if (button!=null) {
			gc.anchor = GridBagConstraints.EAST;
			gc.insets=lastInsets;
			gc.gridx++;
			gc.gridy=y0;
			jp.add(button, gc);
			
		}
		
	}

	

	@Override
	public int gridHeight() {
		return 1;
	}

	@Override
	public int gridWidth() {
		if (button!=null) return 3;
		return 2;
	}

	/**returns the selected index*/
	public int getSelectedIndex() {
		return getBox() .getSelectedIndex();
	}
	
	/**returns the selected index*/
	public ArrayList<Integer> getSelectedIndices() {
		 ArrayList<Integer> output=new  ArrayList<Integer>();
		output.add(getSelectedIndex());
		return output;
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
		
		for(ChoiceInputListener l :listeners) {
			if(l==null) continue;
			l.valueChanged(ni);
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

	
	
	/**the number of choices available*/
	public int getNChoices() {return box.getModel().getSize();}
	
	public int howManyMayBeSelected() {return 1;}
	
	/**returns the names of the enums*/
	public static String[] enumNames(Enum<?>[] en) {
		String[] output = new String[en.length];
		for(int i=0; i<output.length; i++) {
			output[i]=en[i].name().toLowerCase();
			output[i]=output[i].replaceAll("_", " ");
		}
		
		return output;
		
	}
	
	/**Builds a choice input panel for enums*/
	public static ChoiceInputPanel buildForEnum(String label, Enum<?>[] en, Enum<?> start) {
		String[] names = enumNames(en);
		
		int startIndex = start.ordinal();
		return new ChoiceInputPanel(label, names, startIndex);
	}

	
}
