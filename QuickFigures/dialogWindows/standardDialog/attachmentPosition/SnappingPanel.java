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
package standardDialog.attachmentPosition;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import standardDialog.ObjectEditEvent;
import standardDialog.ObjectInputPanel;
import standardDialog.OnGridLayout;
import standardDialog.numbers.NumberInputEvent;
import standardDialog.numbers.NumberInputListener;
import standardDialog.numbers.NumberInputPanel;
import utilityClassesForObjects.AttachmentPosition;

public class SnappingPanel extends  ObjectInputPanel  implements  OnGridLayout, NumberInputListener, ItemListener {
	
	/**
	 * 
	 */

	
	private static final long serialVersionUID = 1L;
	 private AttachmentPosition snappingBehaviour=AttachmentPosition.defaultInternal();
	JLabel label=new JLabel("Position");
	 private SnapBox SnapBox=new  SnapBox(snappingBehaviour);
	 JComboBox<?> jcom=new  JComboBox<String>(AttachmentPosition.getGridSpaceCodeNames());
	 NumberInputPanel xoffSet=new NumberInputPanel("xOffset", 0, true, true, 0,100);
	 NumberInputPanel yoffSet=new NumberInputPanel("yOffset", 0, true, true, 0,100); {
		 yoffSet.setSliderOrientation(JScrollBar.VERTICAL);
		 xoffSet.addNumberInputListener(this);
		 yoffSet.addNumberInputListener(this);
		 getSnapBox().addItemListener(this);
		 jcom.addItemListener(this);
	 }
	 
	 

	 
	
	 
	 
	 public SnappingPanel(AttachmentPosition s) {
		 this.setSnapping(s);
		 
	 }
	 
	 public SnappingPanel(AttachmentPosition s, String text) {
		this(s);
		label.setText(text);
		 
	 }
	 
	 private void setSnapping(AttachmentPosition s) {
		 this.snappingBehaviour=s;
		// IssueLog.log("inputted snapping behaviour is "+s);
		getSnapBox().setSnappingBehaviour(s.copyWOofffsets());
		xoffSet.setNumber(s.getHorizontalOffset());
		yoffSet.setNumber(s.getVerticalOffset());
		this.setLayout(new GridBagLayout());
		jcom.setSelectedIndex(s.getGridLayoutSnapType() );
		//IssueLog.log("snapping behaviour of panel set up");
		placeItems(this,0,0);
	}

	@Override
		public void placeItems(Container jp, int x0, int y0) {
			GridBagConstraints gc = new GridBagConstraints();
			gc.gridx=x0;
			gc.gridy=y0;
			gc.insets=firstInsets;
			gc.anchor = GridBagConstraints.CENTER;
			jp.add(label, gc);
			gc.anchor = GridBagConstraints.WEST;
			gc.gridy++;
			gc.gridwidth=1;
			jp.add(getSnapBox(), gc);
			gc.gridwidth=4;
			gc.gridy++;
			gc.gridy++;
			gc.gridx--;
			jp.add(generateSliderPanel() , gc);
			
			
		}
	
	public JPanel generateSliderPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gc2 = new GridBagConstraints();
			gc2.gridx=2;
			gc2.gridy=0;
			gc2.gridwidth=2;
			gc2.anchor=GridBagConstraints.NORTH;
			panel.add(xoffSet.getSlider(), gc2);
			
			gc2 = new GridBagConstraints();
			gc2.gridx=1;
			gc2.gridy=0;
			gc2.gridheight=3;
			gc2.anchor=GridBagConstraints.EAST;
			panel.add(yoffSet.getSlider(), gc2);
			
			gc2 = new GridBagConstraints();
			gc2.gridx=2;
			gc2.gridy=2;
			gc2.anchor=GridBagConstraints.NORTH;
			panel.add(yoffSet.getField(), gc2);;
			gc2.gridx++;
			panel.add(xoffSet.getField(), gc2);;
			
			gc2 = new GridBagConstraints();
			gc2.insets= new Insets(30, 4, 4, 4);
			gc2.gridx=2;
			gc2.gridy=1;
			gc2.anchor=GridBagConstraints.SOUTH;
			panel.add(yoffSet.getLabel(), gc2);;
			gc2.gridx++;
			gc2.anchor=GridBagConstraints.SOUTH;
			panel.add(xoffSet.getLabel(), gc2);;
			
			gc2.gridwidth=2;
			gc2.gridy+=1;
			gc2.gridx--;
			//gc2.gridx-=2;
			panel.add(jcom, gc2);
			
		return panel;
	}

	@Override
	public int gridHeight() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public int gridWidth() {
		// TODO Auto-generated method stub
		return 5;
	}

	@Override
	public void numberChanged(NumberInputEvent ne) {
		if (ne.getSourcePanel()==xoffSet) {
			getSnappingBehaviour().setHorizontalOffset((int)xoffSet.getNumber());
			
			notifyListeners(new ObjectEditEvent(this.getSnappingBehaviour()));
		}
		if (ne.getSourcePanel()==yoffSet) {
			getSnappingBehaviour().setVerticalOffset((int)yoffSet.getNumber());
			notifyListeners(new ObjectEditEvent(this.getSnappingBehaviour()));
		}
		
		this.repaint();
		
	}

	public AttachmentPosition getSnappingBehaviour() {
		return snappingBehaviour;
	}

	

	@Override
	public void itemStateChanged(ItemEvent arg0) {
		this.getSnappingBehaviour().copyPositionFrom(getSnapBox().getSnappingBehaviour());
		this.getSnappingBehaviour().setGridLayoutSnapType(jcom.getSelectedIndex());
		notifyListeners(new ObjectEditEvent(this.getSnappingBehaviour()));
	}
	
	
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.add(new SnappingPanel(AttachmentPosition.defaultInternal()));
		
		f.pack();
		f.setVisible(true);
	}

	public SnapBox getSnapBox() {
		return SnapBox;
	}

}
