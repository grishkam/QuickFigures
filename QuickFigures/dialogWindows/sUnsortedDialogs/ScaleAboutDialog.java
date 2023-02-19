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
 * Version: 2023.1
 */
package sUnsortedDialogs;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.undo.UndoManager;

import locatedObject.Scales;
import standardDialog.StandardDialog;
import standardDialog.numbers.NumberInputPanel;
import standardDialog.numbers.PointInputPanel;
import undo.CombinedEdit;
import undo.UndoScalingAndRotation;

public class ScaleAboutDialog extends StandardDialog{
	
	ArrayList<Scales > items=new ArrayList<Scales >();
	private double scaleLevel=1;
	double x=0;
	double y=0;
	private UndoManager undoManager;
	
	
	public ScaleAboutDialog(double factor, double x, double y) {
		super();
		this.scaleLevel = factor;
		this.x = x;
		this.y = y;
		this.setModal(true);
		this.setWindowCentered(true);
		this.add("xy", new PointInputPanel("x y", new Point2D.Double(x,y)));
		this.add("m", new NumberInputPanel("scale",  factor));
	}
	
	public ScaleAboutDialog() {
		this(1,0,0);
	}
	public ScaleAboutDialog(UndoManager undoManager) {
		this(1,0,0);
		this.undoManager=undoManager;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public void addItem(Scales i) {
		items.add(i);
		
	}
	
	public void addItems(Collection<Scales> i) {
		items.addAll(i);
		
	}
	public void addItemsScalable(Collection<?> i) {
		for(Object f: i) {
			if (f instanceof Scales) {
				addItem((Scales) f);
			}
		}
		
	}
	
	@Override
	public
	void showDialog() {
		super.showDialog();
		this.x=this.getPoint("xy").getX();
		this.y=this.getPoint("xy").getY();
		this.scaleLevel=this.getNumber("m");
		if (this.wasOKed())
			scaleItemstoDialog();
	}
	
	void scaleItemstoDialog() {
		CombinedEdit undo = new CombinedEdit();
		for(Scales m:items ){
			UndoScalingAndRotation edit = new UndoScalingAndRotation(m);
			
			m.scaleAbout(getAbout(), this.scaleLevel);//does the scaling
			
			edit.establishFinalState();
			undo.addEditToList(edit);
		}
		this.undo=undo;
		if (undoManager!=null) undoManager.addEdit(undo);
	}
	
	public Point2D getAbout() {return new Point2D.Double(x, y);}

	public double getScaleLevel() {
		return scaleLevel;
	}

	public void setScaleLevel(double scaleLevel) {
		this.scaleLevel = scaleLevel;
	}

}
