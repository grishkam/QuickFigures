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
 * Date Created: Dec 12, 2021
 * Date Modified: Dec 12, 2021
 * Version: 2022.1
 */
package standardDialog.strings;

import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import standardDialog.InputPanel;
import standardDialog.numbers.NumberInputPanel;

/**A number input panel for more than one number 
 * A panel that contains many fields that can be used to input numbers*/
public class CombindedInputPanel extends InputPanel implements KeyListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JLabel label=new JLabel();
	private ArrayList<StringInputPanel> fields=new ArrayList<StringInputPanel>();
	
	public CombindedInputPanel(String label, StringInputPanel... inputs) {
		this.setLabel(label);
		for(int i=0; i<inputs.length; i++) {
			getFieldList().add(inputs[i]);
		}
	
	}
	
	/**Sets the font of each field*/
	public void setItemFont(Font f) {
	
		for(StringInputPanel f2:getFieldList()) {
			f2.setFont(f);
		}
	 }
	
	

	
	
	
	/**Sets the label for the series of fields*/
	public void setLabel(String st) {
		label.setText(st);
	}


	
	/**Restores the panel to its original state*/
	public void revert() {
		for(int i=0; i<getFieldList().size(); i++) {
			getFieldList().get(i).revert();
		}
	}
	

	@Override
	public void keyPressed(KeyEvent arg0) {
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public void placeItems(Container jp, int x0, int y0) {
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx=x0;
		gc.gridy=y0;
		gc.insets=firstInsets;
		gc.anchor = GridBagConstraints.EAST;
		jp.add(label, gc);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridx++;
		gc.gridwidth=3;
			jp.add( fieldPanel() , gc);
		
		
		
		
	}
	
	/**returns a JPanel with each string field inside of it*/
	JPanel fieldPanel() {
		JPanel fieldPanel=new JPanel();
		fieldPanel.setLayout(new FlowLayout());
		for(StringInputPanel f: getFieldList()) {
			
			fieldPanel.add(f.getTextField());
			}
		return fieldPanel;
	}

	public ArrayList<StringInputPanel> getFieldList() {
		return fields;
	}
	


}
