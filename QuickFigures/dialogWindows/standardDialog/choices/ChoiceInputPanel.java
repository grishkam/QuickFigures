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
 * Date Modified: April 24, 2021
 * Version: 2023.2
 */
package standardDialog.choices;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;

import channelMerging.ChannelEntry;
import logging.IssueLog;
import standardDialog.InputPanel;
import standardDialog.OnGridLayout;

/**A JPanel containing a combo box for placement into a standard dialog with a grided panel*/
public class ChoiceInputPanel extends InputPanel implements OnGridLayout, ItemListener{

	protected JLabel label=new JLabel();
	protected JComboBox<? extends Object> box=new JComboBox<String>();
	private JComboBox<String> box2;
	
	protected AbstractButton  button;
	
	boolean omitLabel=false;
	
	public JComboBox<? extends Object> getBox() {return box;}
	ArrayList<ChoiceInputListener> listeners=new ArrayList<ChoiceInputListener>();
	
	
	public int originalStatus;
	private ArrayList<Integer> originalValues=new ArrayList<Integer>();
	
	/**only applicable when this choice input panel refers to a list of objects*/
	private ArrayList<?> objectList;

	
	
	
	
	
	/**Constructor for the input panel*/
	public ChoiceInputPanel(String labeln, String[] choices, int startingindex) {
		
		label.setText(labeln);
		
		box2 = new JComboBox<String>();
		box=box2;
		changeOptions(choices, startingindex, true);
		
	}
	
	
	
	
	
	
	public ChoiceInputPanel(String labeln, ArrayList<ChannelEntry> choices, int startingindex) {
		
		this(labeln, updateOptions(choices), startingindex);
		
	}
	
public ChoiceInputPanel(String labeln, ArrayList<?> choices, int startingindex, Class<?> type, Method m) {
		
		this(labeln, setChoicesToStrings(choices, m), startingindex);
		this.objectList=choices;
	}

	/**When given a list of channel entries as choices*/
	public static String[] updateOptions(ArrayList<ChannelEntry> ce) {
		String[] sa = new String[ce.size()];
		for(int i=0; i<ce.size(); i++) {
			sa[i]=ce.get(i).getLabel();
		}
		return sa;
	}
	
	/**Sets the choice to the to strings*/
	public static String[] setChoicesToStrings(ArrayList<?> ce, Method m) {
		String[] sa = new String[ce.size()];
		for(int i=0; i<ce.size(); i++) {
			sa[i]=ce.get(i).toString();
			if(m!=null) try {
				sa[i]=""+m.invoke(ce.get(i));
			} catch (Throwable t) {
				IssueLog.logT(t);
			}
		}
		return sa;
	}

	/**
	 * @param choices
	 * @param startingindex
	 * @param box2
	 */
	public void changeOptions(String[] choices, int startingindex,  boolean listen) {
		
		box2.removeAllItems();
		for(String c: choices) {
			box2.addItem(c);
		}
		
		
		if (startingindex>=box2.getItemCount()) 
			startingindex=0;
		if(box2.getItemCount()!=0)
			box2.setSelectedIndex(startingindex);
		if(listen)	{box2.addItemListener(this);}
			
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
		
		
			jp.add(getChoiceGUIObject(), gc);
		
		
		if (button!=null) {
			gc.anchor = GridBagConstraints.EAST;
			gc.insets=lastInsets;
			gc.gridx++;
			gc.gridy=y0;
			jp.add(button, gc);
			
		}
		
	}






	 Component getChoiceGUIObject() {
		return box;
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
	public Object getSelectedObject() {
		if(objectList!=null)
			return objectList.get(getSelectedIndex());
		return null;
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
			String name = en[i].name();
			output[i]=titleCase(name);
			output[i]=output[i].replace("  ", ". ");
		}
		
		return output;
		
	}
	
	/**Builds a choice input panel for enums*/
	public static ChoiceInputPanel buildForEnum(String label, Enum<?>[] en, Enum<?> start) {
		String[] names = enumNames(en);
		if(start==null) start=en[0];
		int startIndex = start.ordinal();
		return new ChoiceInputPanel(label, names, startIndex);
	}
	
	/**returns the name of the panel*/
	public String getName() {
		if(label==null)
			{	IssueLog.log("no label");
		
				return "no label present";
			}
		return 
				label.getText();
		}
	
	/**changes the text to title case*/
	public static String titleCase(String st) {
		StringBuilder sb = new StringBuilder();
		st=st.replace("_", " ");
		boolean capital=true;
		
		for(int i=0; i<st.length(); i++) {
			String next =""+ st.charAt(i);
			if(capital)
				sb.append(next.toUpperCase());
			else sb.append(next.toLowerCase());
			
			if(" ".equals(next))
				capital=true;
			else capital=false;
			
		}
		
		return sb.toString();
		
		
	}


	/**
	 * @param icons
	 */
	public void setIcons(Icon[] icons) {
		this.box.setRenderer(new IconListCellRenderer(icons));
	}

	
}
