/*******************************************************************************
 * Copyright (c) 2026 Gregory Mazo
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
 * Date Created: Jan 26, 2026
 * Date Modified: Jan 26, 2026
 * Version: 2026.1
 */
package popupMenusForComplexObjects;

import java.awt.Color;
import java.awt.Point;

import graphicalObjects.KnowsParentLayer;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import locatedObject.AttachmentPosition;
import menuUtil.BasicSmartMenuItem;
import menuUtil.SmartJMenu;
import undo.AbstractUndoableEdit2;
import undo.CombinedEdit;
import undo.UndoAddItem;

/**A menu item for adding text to a variety of targets*/
public class AddTextToClickedLayer extends BasicSmartMenuItem {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GraphicLayer layer;
	protected ComplexTextGraphic addition;
	protected CombinedEdit the_undo;
	private AttachmentPosition attach;
	private KnowsParentLayer source;

	public AddTextToClickedLayer(String name, KnowsParentLayer l) {
		super(name);
		layer=l.getParentLayer();
		source=l;
		setIcon(ComplexTextGraphic.createImageIcon());
	}
	
	public AddTextToClickedLayer(String name, KnowsParentLayer l, AttachmentPosition a) {
		this(name, l);
		attach=a;
	}
	
	
	/**May be overwritten by subclasses. Adds a text item to the same layer as a target object*/
	public AbstractUndoableEdit2 performAction() {
		Point p = super.me.getCoordinatePoint();
		addition = createItem(p);
		
		UndoAddItem undo = new UndoAddItem(layer, addition);
		the_undo=new CombinedEdit(undo);
		layer.add(addition);
		
		if(source instanceof ShapeGraphic) {
			ShapeGraphic rect=(ShapeGraphic) source;
			
			if(rect.getStrokeWidth()>0) {
				addition.setTextColor(rect.getStrokeColor());
			}
			
			snapItems();
		}
		
		
		super.updateDisplay();
		
		
		afterAddition();
		return the_undo;
	}

	/**
	 * 
	 */
	private void snapItems() {
		if(attach!=null)
			attach.snapLocatedObjects(addition, (ShapeGraphic) source);
	}
	/**
	 * @param p
	 * @return
	 */
	private ComplexTextGraphic createItem(Point p) {
		ComplexTextGraphic t = new ComplexTextGraphic("Text");
		t.setLocation(p);
		t.setTextColor(Color.gray);
		t.select();
		return t;
	}
	
	public void afterAddition() {
		
	}
	
	public static SmartJMenu getAddTextMenu( String name, KnowsParentLayer l) {
		SmartJMenu output=new SmartJMenu(name);
		output.add(new AddTextToClickedLayer("Above", l, AttachmentPosition.defaultColLabel()));
		output.add(new AddTextToClickedLayer("Below", l, AttachmentPosition.defaultPlotBottomSide()));
		output.add(new AddTextToClickedLayer("Left", l, AttachmentPosition.defaultRowSide()));
		output.add(new AddTextToClickedLayer("Right", l, AttachmentPosition.rightOfRowSide()));
		
		return output;
	}
}