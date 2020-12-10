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
package objectDialogs;

import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.JFrame;

import graphicalObjects_BasicShapes.RectangularGraphic;
import standardDialog.ObjectEditEvent;
import standardDialog.ObjectInputPanel;
import standardDialog.OnGridLayout;
import standardDialog.choices.ChoiceInputEvent;
import standardDialog.choices.ChoiceInputListener;
import standardDialog.choices.ChoiceInputPanel;
import standardDialog.colors.ColorComboboxPanel;
import standardDialog.numbers.NumberArrayInputPanel;
import standardDialog.numbers.NumberInputEvent;
import standardDialog.numbers.NumberInputListener;
import standardDialog.numbers.NumberInputPanel;
import utilityClassesForObjects.StrokedItem;

public class StrokeInputPanel extends ObjectInputPanel implements OnGridLayout, NumberInputListener, ChoiceInputListener{
	/**
	 * 
	 */
	
	
	private static final long serialVersionUID = 1L;
	static String[] strokecaps=new String[] {"Cap Blunt", "Cap Round", "Cap Square"};
	static String[] strokejoins=new String[] {"Jion Miter", "Jion Round", "Jion Bevel"};
	
	
	StrokedItem strokedItem;
	
	NumberArrayInputPanel dashinput=new NumberArrayInputPanel(6,0);{
		dashinput.setLabel("Dashes");
		
	}
	
	
	
	NumberInputPanel widthInput=new NumberInputPanel("Stroke Width ", 0);
	NumberInputPanel miterInput=new NumberInputPanel("Miterlimit ", 0);
	ColorComboboxPanel strokeColorInput=null;
	ChoiceInputPanel joinInput=new ChoiceInputPanel("Stroke Join", strokejoins, 0);
	ChoiceInputPanel capInput=new ChoiceInputPanel("Stroke Cap", strokecaps, 0);
	
	public void setUpFont(Font f) {
		dashinput.setItemFont(f);
		widthInput.setItemFont(f);
		miterInput.setItemFont(f);
		joinInput.setItemFont(f);
		capInput.setItemFont(f);
		if (strokeColorInput!=null)strokeColorInput.setItemFont(f);
	}
	
	public StrokeInputPanel(StrokedItem s) {
		this.strokedItem=s;
		dashinput.setArray(s.getDashes());
		
		
		widthInput.setNumber(s.getStrokeWidth());
		widthInput.originalStatus=s.getStrokeWidth();
		
		strokeColorInput=new ColorComboboxPanel("Stroke Color ", null, s.getStrokeColor());
		
		
		joinInput.getBox().setSelectedIndex(s.getStrokeJoin());
		joinInput.originalStatus=s.getStrokeJoin();
		capInput.getBox().setSelectedIndex(s.getStrokeCap());
		capInput.originalStatus=s.getStrokeCap();
		
		miterInput.setNumber(s.getMiterLimit());
		miterInput.originalStatus=s.getMiterLimit();
		
		this.setLayout(new GridBagLayout());
		this.placeItems(this, 0, 0);
		addListeners();
	}
	
	public void addListeners() {
		dashinput.addNumberInputListener(this);
		widthInput.addNumberInputListener(this);
		strokeColorInput.addChoiceInputListener(this);
		joinInput.addChoiceInputListener(this);
		capInput.addChoiceInputListener(this);
		miterInput.addNumberInputListener(this);
	}

	public void setStrokedItemToPanel(StrokedItem s) {
		if (s==null) return;
		s.setDashes(dashinput.getArray());
		s.setStrokeWidth((float)widthInput.getNumber());
		s.setStrokeColor(strokeColorInput.getSelectedColor());
		s.setStrokeJoin(joinInput.getSelectedIndex());
		s.setStrokeCap(capInput.getSelectedIndex());
		s.setMiterLimit(miterInput.getNumber());
	}
	
	@Override
	public void placeItems(Container jp, int x0, int y0) {
		widthInput.placeItems(jp, x0, y0);
		strokeColorInput.placeItems(jp, x0+2, y0);
		dashinput.placeItems(jp, x0, y0+1);
		joinInput.placeItems(jp, x0, y0+2);
		capInput.placeItems(jp, x0+2, y0+2);
		miterInput.placeItems(jp, x0, y0+3);
	}

	@Override
	public int gridHeight() {
		// TODO Auto-generated method stub
		return 4;
	}

	@Override
	public int gridWidth() {
		// TODO Auto-generated method stub
		return 5;
	}
	
	public static void main(String[] args) {
		JFrame jf = new JFrame();
		jf.add(new StrokeInputPanel(new RectangularGraphic()));
		jf.pack();jf.setVisible(true);
	}

	@Override
	public void numberChanged(ChoiceInputEvent ne) {
		this.notifyListeners(new ObjectEditEvent(strokedItem));
		//setStrokedItemToPanel(strokedItem);
		
	}

	@Override
	public void numberChanged(NumberInputEvent ne) {
		this.notifyListeners(new ObjectEditEvent(strokedItem));
		
		//setStrokedItemToPanel(strokedItem);
		
	}

	/**restores all panels to original values*/
	public void revert() {
		strokeColorInput.revert();
		widthInput.revert();
		miterInput.revert();
		joinInput.revert();
		capInput.revert();
		dashinput.revert();
		if (strokeColorInput!=null) strokeColorInput.revert();
	}

	public NumberArrayInputPanel getDashInput() {
		return dashinput;
	}
	
	

}
