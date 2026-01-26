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
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import menuUtil.BasicSmartMenuItem;
import undo.AbstractUndoableEdit2;
import undo.CombinedEdit;
import undo.UndoAddItem;

/**A menu iten for adding text to a variety of targets*/
public class AddTextToClickedLayer extends BasicSmartMenuItem {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GraphicLayer layer;
	protected ComplexTextGraphic addition;
	protected CombinedEdit the_undo;

	public AddTextToClickedLayer(String name, KnowsParentLayer l) {
		super(name);
		layer=l.getParentLayer();
		setIcon(ComplexTextGraphic.createImageIcon());
	}
	/**May be overwritten by subclasses. Does some task and returns an undo*/
	public AbstractUndoableEdit2 performAction() {
		Point p = super.me.getCoordinatePoint();
		addition = createItem(p);
		
		UndoAddItem undo = new UndoAddItem(layer, addition);
		the_undo=new CombinedEdit(undo);
		layer.add(addition);
		super.updateDisplay();
		afterAddition();
		return the_undo;
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
}