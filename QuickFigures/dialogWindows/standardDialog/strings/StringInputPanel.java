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
 * Version: 2022.0
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import standardDialog.InputPanel;
import standardDialog.OnGridLayout;

/**A JPanel containing a Label and a Text field for placement into a standard dialog with a grided panel*/
public class StringInputPanel extends InputPanel implements OnGridLayout, KeyListener{

	/**
	 * 
	 */
	private static final int STANDARD_NUMBER_OF_COLUMNS_IN_TEXT_FIELD = 15;
	public static final String lineseparator = ""+'\n';
	JLabel label=new JLabel();
	JTextComponent field=new JTextField(15);
	ArrayList<StringInputListener> lis=new ArrayList<StringInputListener>();
	String lasts="";
	
	private String originalStatus;
	
	
	public StringInputPanel(String labeln, String contend) {
		this(labeln, contend, STANDARD_NUMBER_OF_COLUMNS_IN_TEXT_FIELD);
	}
	
	public StringInputPanel(String labeln, String[] contents) {
		this(labeln, contents,  contents.length+1, 15);
	}
	
	/**Creates a sring input panel with a text field of given length */
	public StringInputPanel(String labeln, String contend, int fieldLength) {
		field=new JTextField(contend, fieldLength);
		setupInnitialText(labeln, contend);
		
	}

	/**
	 * @param labeln
	 * @param contend
	 */
	protected void setupInnitialText(String labeln, String contend) {
		label.setText(labeln);
		getTextComponent().setText(contend);
		lasts=contend;
		this.originalStatus=contend;
		{getTextComponent().addKeyListener(this);}
	}
	
	public StringInputPanel(String labeln, String contend, int rows, int cols) {
		field=new JTextArea(rows, cols);
		
		setupInnitialText(labeln, contend);
	}
	
	/**creates a string input panel for many strings that are separated by lines*/
	public StringInputPanel(String labeln, String[] contend, int rows, int cols) {
		field=new JTextArea(rows, cols);
		String starting="";
		for(String s: contend)starting+=s+lineseparator;//makes a string where eaxh strig is a different line
		setupInnitialText(labeln, starting);
	}
	
	public void revert() {
		setContentText(originalStatus);
	}
	
	public void setContentText(String contend) {
		getTextComponent().setText(contend);
	}
	
	

	
	
	public String getTextFromField() {
		return getTextComponent().getText();
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
		return getTextComponent();
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
		if (this.getTextFromField().equals(lasts)||arg0.getSource()!=getTextComponent()) return;
		
		StringInputEvent e = new StringInputEvent(this, this.getTextComponent(), this.getTextFromField());
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

	/**Returns the text component that the user types into*/
	public JTextComponent getTextComponent() {
		return field;
	}


	
	
}
