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
 * Version: 2021.2
 */
package standardDialog.strings;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JTextField;

import standardDialog.InputPanel;
import standardDialog.OnGridLayout;

/**A JPanel containing a Label and a Text field for placement into a standard dialog with a grided panel*/
public class StringInputPanel extends InputPanel implements OnGridLayout, KeyListener{

	JLabel label=new JLabel();
	JTextField field=new JTextField(15); {field.addKeyListener(this);}
	ArrayList<StringInputListener> lis=new ArrayList<StringInputListener>();
	String lasts="";
	
	private String originalStatus;
	
	
	public StringInputPanel(String labeln, String contend) {
		
		label.setText(labeln);
		setContentText(contend);
		lasts=contend;
		this.originalStatus=contend;
	}
	
	public StringInputPanel(String labeln, String contend, int fieldLength) {
		
		label.setText(labeln);
		field.setText(contend);
		lasts=contend;
		field.setColumns(fieldLength);
		this.originalStatus=contend;
	}
	
	public void revert() {
		setContentText(originalStatus);
	}
	
	public void setContentText(String contend) {
		field.setText(contend);
	}
	
	

	
	
	public String getTextFromField() {
		return field.getText();
	}
	
	public void addStringInputListener(StringInputListener l) {
		lis.add(l);
	}
	public void removeStringInputListener(StringInputListener l) {
		lis.remove(l);
	}
	public void notifyLiseners(StringInputEvent e) {
		for(StringInputListener l:lis) {l.stringInput(e);}
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
		jp.add(getTextField(), gc);
		
		
	}
	
	protected Component getTextField() {
		return field;
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
	public void keyPressed(KeyEvent arg0) {
		
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		if (this.getTextFromField().equals(lasts)||arg0.getSource()!=field) return;
		
		StringInputEvent e = new StringInputEvent(this, this.field, this.getTextFromField());
		e.setKey(key);
		this.notifyLiseners(e);
		lasts=this.getTextFromField();
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}



	
	
	public void setToDimension(Rectangle contend) {
		String st= contend.width+" X "+contend.height;
		 setContentText(st);
	}


	
	
}
