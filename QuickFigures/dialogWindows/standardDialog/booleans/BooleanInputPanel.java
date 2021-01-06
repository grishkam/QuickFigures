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
/**
 * Author: Greg Mazo
 * Date Modified: Dec 6, 2020
 * Version: 2021.1
 */
package standardDialog.booleans;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import standardDialog.OnGridLayout;

/**A JPanel containing a Label and a checkbox for placement into a standard dialog with a grided panel*/
public class BooleanInputPanel extends JPanel implements OnGridLayout, ItemListener{

	JLabel label=new JLabel();
	JCheckBox checkBox=new JCheckBox("", false); {checkBox.addItemListener(this);}
	boolean originAlStatus=false;
	
	ArrayList<BooleanInputListener> lis =new 	ArrayList<BooleanInputListener>();
	private String key;
	public void addBooleanInputListener(BooleanInputListener l) {
		lis.add(l);
	}
	public void removeBooleanInputListener(BooleanInputListener l) {
		lis.remove(l);
	}
	
	
	public BooleanInputPanel(String labeln, boolean b) {
		label.setText(labeln);
		setChecked(b);
		 originAlStatus=b;
	}
	
	public BooleanInputPanel(String labeln, boolean b, JCheckBox field2) {
		checkBox=field2;
		checkBox.addItemListener(this);
		label.setText(labeln);
		setChecked(b);
		 originAlStatus=b;
	}
	/**
	sets the box to selected
	 */
	public void setChecked(boolean b) {
		checkBox.setSelected(b);
	}
	
	public String getTextFromField() {
		return checkBox.getText();
	}
	
	public boolean isChecked() {
		return checkBox.isSelected();
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void placeItems(Container jp, int x0, int y0) {
		GridBagConstraints gc = new GridBagConstraints();
		
		gc.insets=firstInsets;
		gc.gridx=x0;
		gc.gridy=y0;
		gc.anchor = GridBagConstraints.EAST;
		jp.add(label, gc);
		gc.gridx++;
		gc.insets=lastInsets;
		gc.anchor = GridBagConstraints.WEST;
		jp.add(checkBox, gc);
		
		
	}

	
	@Override
	public int gridHeight() {
		return 1;
	}

	@Override
	public int gridWidth() {
		return 2;
	}
	@Override
	public void itemStateChanged(ItemEvent arg0) {
		if (arg0.getSource()==checkBox) {
			notigyListeners();
		}
		
	}
	/**
	notifies the listeners of a change
	 */
	private void notigyListeners() {
		BooleanInputEvent bi = new BooleanInputEvent(this, checkBox, checkBox.isSelected());
		bi.setKey(key);
		this.notifyListeners(bi);
	}
	void notifyListeners(BooleanInputEvent bi) {
		for(BooleanInputListener l:lis) {
			l.booleanInput(bi);
		}
	}
	public void setKey(String key) {
		this.key=key;
		
	}
	
	/**Changes the status of the item to its original*/
	public void revert() {
		checkBox.setSelected(originAlStatus);
	}

	
	
}