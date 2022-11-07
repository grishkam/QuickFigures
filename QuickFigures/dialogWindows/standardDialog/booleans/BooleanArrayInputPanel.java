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
 * Version: 2022.2
 */
package standardDialog.booleans;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**An input panel for a series of boolean values*/
public class BooleanArrayInputPanel extends BooleanInputPanel {

	/**the checkboxes used to change the bool values*/
	ArrayList<JCheckBox> boxes=new ArrayList<JCheckBox>();
	
	public BooleanArrayInputPanel(String labeln, boolean[] b) {
		super(labeln, false);
		setArray(b);
		this.setLayout(new GridBagLayout());
		placeItems(this,0,0);
	}
	
	public BooleanArrayInputPanel(String labeln, boolean[] b, ArrayList<JCheckBox> check) {
		super(labeln, false);
		this.setBoxes(check);
		setArray(b);
		this.setLayout(new GridBagLayout());
		placeItems(this,0,0);
	}
	
	public void setBoxes(ArrayList<JCheckBox> check) {
		boxes=check;
		for(JCheckBox b: check) {b.addItemListener(this);};
	}
	
	public void setArray(boolean[] b) {
		for(int i=0; i<b.length; i++) {
			if (boxes.size()>i) boxes.get(i).setSelected(b[i]);
			else addBox( b[i]);
		}
	}
	
	public boolean[] getArray() {
		boolean[] b=new boolean[boxes.size()];
		for(int i=0; i<b.length; i++) {
			b[i]=boxes.get(i).isSelected();
		}
		return b;
	}
	
	public void addBox(boolean selected) {
		JCheckBox newbox = new JCheckBox("", selected);
		boxes.add( newbox);
		 newbox.addItemListener(this);
	}
	
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
		jp.add(getAllBoxPanel(), gc);
		
		
	}
	
	public JPanel getAllBoxPanel() {
		JPanel output = new JPanel();
		output.setLayout(new FlowLayout());
		for(JCheckBox j:boxes) output.add(j);
		return output;
	}

	
	private static final long serialVersionUID = 1L;
	
	public void itemStateChanged(ItemEvent arg0) {
		int index = boxes.indexOf(arg0.getSource());
		if (index>-1) {
			BooleanInputEvent bi = new BooleanInputEvent(this, boxes.get(index), boxes.get(index).isSelected());
			
			this.notifyListeners(bi);
		}
		
	}

}
