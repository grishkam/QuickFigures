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
package standardDialog.numbers;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import standardDialog.OnGridLayout;
import standardDialog.StandardDialog;
import standardDialog.StandardDialogListener;



public class NumberInputPanel extends JPanel implements KeyListener, AdjustmentListener, MouseMotionListener, OnGridLayout{

	/**
	 * 
	 */
	private boolean editable=true;
	
	
	 public static  JPanel getPanelForContents(Component... obs) {
		   JPanel out=new JPanel();
		   out.setLayout(new FlowLayout());
		for(Component c:obs) {out.add(c);}
		
		return out;
	   }
	 
	 
	
 ArrayList<NumberInputListener> listeners=new ArrayList<NumberInputListener>();
	
	public void notifyListeners(NumberInputEvent ni) {
		//NumberInputEvent ni = new NumberInputEvent(this, getNumberFromField() );
		for(NumberInputListener l :listeners) {
			if(l==null) continue;
			l.numberChanged(ni);
		}
	}
	
	public void setItemFont(Font f) {
		 this.setFont(f);
		 this.getField().setFont(f);
		 this.getLabel().setFont(f);
		 this.getSlider().setFont(f);
	 }
	
	
	public void addNumberInputListener(NumberInputListener ni) {
		listeners.add(ni);
	}
	public void removeNumberInputListener(NumberInputListener ni) {
		listeners.remove(ni);
	}
	public ArrayList<NumberInputListener> getNumberInputListeners() {
		return listeners;
	}

	private static final long serialVersionUID = 1L;
	{this.setLayout(new FlowLayout());}
	NumericTextField field=new NumericTextField(0);// {field.addKeyListener(this);}
	JLabel label=new JLabel("Text");
	JScrollBar slider=new JScrollBar(JScrollBar.HORIZONTAL);
	private int slidelength=150; {}
	{
		slider.addAdjustmentListener(this); slider.addMouseMotionListener(this);field.addKeyListener(this);
		setSliderToDefaultPrefferedSize() ;
	//slider.setPreferredSize(new Dimension(350, 25));
	}
	
	public void setSlideLength(int length) {
		this.slidelength=length;
		setSliderToDefaultPrefferedSize() ;
	}
	
	public void setSliderToDefaultPrefferedSize() {
		slider.setPreferredSize(new Dimension(slidelength, 30));
		if (slider.getOrientation()==JScrollBar.VERTICAL)
			slider.setPreferredSize(new Dimension(30, slidelength));
	}
	
	boolean includeSlider =true;
	boolean includeField =true;
	double number=0;
	int slidermin=0; 
	int slidermax=100;
	private String key;
	public double originalStatus;
	
	/**meant to be implicit constructor for subclasses*/
	public  NumberInputPanel() {
		this("",0, false, false,-100,100);
	}
	
	public  NumberInputPanel(String label, double number) {
		this(label, number, true, false,-100,100);
		this.originalStatus=number;
	}
	
	public  NumberInputPanel(String label, double number, int precis) {
		this(label, number, true, false,-100,100);
		field.setDecimalPlaces(precis);
		field.setNumber(number);
		this.originalStatus=number;
	}
	public void setDecimalPlaces(int precis) {
		field.setDecimalPlaces(precis);
		field.setNumber(getNumber());
	}
	
	public  NumberInputPanel(String label, double number, int min, int max) {
		this(label, number, true, true,min,max);
		this.originalStatus=number;
	}
	
	public  NumberInputPanel(String label, double number, boolean includeField, boolean includeSlider, int slidermin, int slidermax) {
		this.setNumber(number);
		this.label.setText(label);
		field.setNumber(number);
		this.includeField=includeField;
		this.includeSlider=includeSlider;
		setSliderRange(slidermin, slidermax);
		addItemsToContainer(this);
		this.originalStatus=number;
	}
	
	public void revert() {
		this.setNumber(originalStatus);
	}
	
	public Component[] getParts() {
		Component[] comp = new Component[] {label, field, slider};
		return comp;
	}
	
	public void setSliderRange(int min, int max) {
		slidermin=min;
		slidermax=max;
		slider.setValues((int)number,
	            (int) 0,
	             slidermin,
	             slidermax);
		
		//slider.setMinimum(slidermin);
		//slider.setMaximum(slidermax);
		slider.repaint();
	}
	
	public void setSliderOrientation(int ori) {
		 slider.setOrientation( ori);
		 setSliderToDefaultPrefferedSize() ;
	}

	public void addItemsToContainer(Container c) {
		c.add(this.label);
		if(this.includeField) c.add(field) ;
		if(this.includeSlider) c.add(slider) ;
	}
	
	public void setNumber(double d) {
		number=d;
		field.setNumber(d);
		slider.setValue((int)d);
	}
	public double getNumber() {
		return number;
	}
	
	/**Sets the number and notifies all the listeners*/
	public void setNumberAndNotify(double d) {
		this.setNumber(d);
		NumberInputEvent ne = new NumberInputEvent(this, field, number);
		ne.setKey(key);
		notifyListeners( ne);
	}
	
	@Override
	public void adjustmentValueChanged(AdjustmentEvent arg0) {
		if(arg0.getSource()==slider) {
			
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
	
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		if (!isEditable()) return;
		if(arg0.getSource()==field) {
			slider.setValue((int)field.getNumberFromField());
			number=field.getNumberFromField();
			NumberInputEvent ne = new NumberInputEvent(this, field, number);
			ne.setKey(key);
			notifyListeners( ne);
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
		NumberInputPanel sb = new NumberInputPanel("Select number", 9, -100,100);
		sb.setItemFont(sb.getField().getFont().deriveFont((float)10));

		ff.add(sb);
		ff.pack();
		
		ff.setVisible(true);
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		if (arg0.getSource()==slider) {
			this.setNumber(slider.getValue());
			NumberInputEvent ne = new NumberInputEvent(this, slider, number);
			ne.setKey(key);
			notifyListeners(ne );
		}
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
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
		if (this.includeSlider) {
			gc.insets=middleInsets;
			jp.add(slider, gc);
			gc.gridx++;
		}
		gc.insets=lastInsets;
		if (this.includeField)  jp.add(field, gc);
		
	}

	@Override
	public int gridHeight() {
		return 1;
	}

	@Override
	public int gridWidth() {
		return 3;
	}

	public Component getSlider() {
		// TODO Auto-generated method stub
		return slider;
	}

	public Component getField() {
		// TODO Auto-generated method stub
		return field;
	}

	public Component getLabel() {
		// TODO Auto-generated method stub
		return label;
	}

	public void setKey(String key) {
		this.key=key;
		
	}
	
	
	/**used for easy access*/
	public static double getNumber(String prompt, double startnumber, int precis, boolean slider, StandardDialogListener dialogListener) {
		StandardDialog sd = new StandardDialog();
		sd.setModal(true);
		NumberInputPanel np = new NumberInputPanel(prompt, startnumber);
		 np.setDecimalPlaces(precis);
		 sd.addDialogListener(dialogListener);
		 sd.add(prompt, np);
		 
		sd.setWindowCentered(true);
		 sd.showDialog();
		 
		 return sd.getNumber(prompt);
		
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
		field.setEditable(editable);
	}

	
}
