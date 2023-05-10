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
 * Date Modified: Nov 28, 2021
 * Version: 2023.2
 */
package standardDialog.numbers;

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
import javax.swing.JPanel;

/**A number input panel for more than one number 
 * A panel that contains many fields that can be used to input numbers*/
public class NumberArrayInputPanel extends NumberInputPanel implements KeyListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ArrayList<NumericTextField> fields=new ArrayList<NumericTextField>();
	private ArrayList<Double> numbers=new ArrayList<Double>();
	private ArrayList<Double> numbersOriginal=new ArrayList<Double>();//first innitialization of the numbers
	int precision=1;
	
	public NumberArrayInputPanel(int nNumbers, int precis) {
		precision=precis;
		for(int i=0; i<nNumbers; i++) {
			addFieldAndNumber(0) ;
		}
	
	}
	
	/**Sets the font of each field*/
	public void setItemFont(Font f) {
		super.setItemFont(f);
		for(NumericTextField f2:fields) {
			f2.setFont(f);
		}
	 }
	
	
	/**Adds another numerif field with the given starting number*/
	public void addFieldAndNumber(double number) {
		NumericTextField f = new NumericTextField(number, precision);
		f.addKeyListener(this);
		fields.add(f);
		numbers.add((double) 0);
		numbersOriginal.add((double) 0);
		this.add(f);
		
	}
	
	/**sets the number of decimal places shown in the text field*/
	public void setDecimalPlaces(int precis) {
		super.setDecimalPlaces(precis);
		for(NumericTextField f:fields) {f.setDecimalPlaces(precis);}
	}
	
	/**Sets the label for the series of fields*/
	public void setLabel(String st) {
		label.setText(st);
	}

	/**Sets the number within a specific text field*/
	public void setNumber(int index, Double value) {
		if (numbers==null) return;
		while(index>=numbers.size()) addFieldAndNumber(0);
		if (value!=null)numbers.set(index, value); else numbers.set(index, null);
		if (value!=null)fields.get(index).setNumber(value);
		else fields.get(index).setText("");
	}
	
	/**returns an array with each number. Blank fields are skipped*/
	public float[] getArray() {
		ArrayList<Float> oo=new ArrayList<Float>();
		for(NumericTextField f: fields) {
			if (!f.isBlank()) oo.add((float) f.getNumberFromField());
			
		}
		float[] output = new float[oo.size()];
		for(int i=0; i<oo.size(); i++) {output[i]=oo.get(i);}
		
		
		return output;
		
	}
	
	/**Sets the numbers for each numeric field in this panel*/
	public void setArray(float[] f) {
		if (f==null) return;
		for(int i=0; i<fields.size(); i++) {
			if (i<f.length) fields.get(i).setNumber(f[i]);
			else fields.get(i).setText("");
		
			
			if (i<f.length)  numbersOriginal.set(i, (double)f[i]);
			else numbersOriginal.set(i,null);
		}
	}
	
	/**Restores the panel to its original state*/
	public void revert() {
		for(int i=0; i<fields.size(); i++) {
			Double numberI = numbersOriginal.get(i);
			this.setNumber(i, numberI );
		}
	}
	

	@Override
	public void keyPressed(KeyEvent arg0) {
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		Object source = arg0.getSource();
		if(source instanceof NumericTextField) {
			double d=((NumericTextField) source).getNumberFromField();
			int i = fields.indexOf(source);
			numbers.set(i, d);
		NumberInputEvent nai = new NumberInputEvent(this, (Component)arg0.getSource(), d, getArray());
		super.notifyListeners(nai);
		}
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public static void main(String[] args) {
		JFrame ff = new JFrame("frame");
		ff.setLayout(new FlowLayout());
		ff.add(new JButton("button"));
		NumberArrayInputPanel sb = new NumberArrayInputPanel(3, 1);
		sb.setNumber(5, 3.12888888);
		sb.setLabel("input array of numbers");
		ff.add(sb);
		ff.pack();
		
		ff.setVisible(true);
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
	
	/**returns a JPanel with each numeric field inside of it*/
	JPanel fieldPanel() {
		JPanel fieldPanel=new JPanel();
		fieldPanel.setLayout(new FlowLayout());
		for(NumericTextField f: fields) {
			
			fieldPanel.add(f);
			}
		return fieldPanel;
	}
	


}
