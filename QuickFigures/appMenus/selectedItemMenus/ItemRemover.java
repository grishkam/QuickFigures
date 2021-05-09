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
 * Version: 2021.1
 */
package selectedItemMenus;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Area;

import javax.swing.Icon;

import graphicalObjects.KnowsParentLayer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_Shapes.ArrowGraphic;
import graphicalObjects_Shapes.BasicShapeGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import locatedObject.Mortal;
import messages.ShowMessage;
import standardDialog.graphics.GraphicDisplayComponent;
import ultilInputOutput.FileChoiceUtil;
import undo.CombinedEdit;
import undo.UndoAbleEditForRemoveItem;

/**An operation that removes a selected set of items from the worksheet*/
public class ItemRemover extends BasicMultiSelectionOperator {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	transient CombinedEdit undoableEdit=new CombinedEdit();

	@Override
	public void run() {
		undoableEdit=new CombinedEdit();
		if(array.size()==0) {
			ShowMessage.showOptionalMessage("No item selected");
			return;
		}
		boolean b= FileChoiceUtil.yesOrNo("Are you sure you want to delete these "+array.size()+" items?"); if (b==false) return;
		
		for(ZoomableGraphic item: array) {
			if (item==null) continue;
			actioinOnSelected(item);
		}
		selector.getWorksheet().getUndoManager().addEdit(undoableEdit);

	}
	
	@Override
	public String getMenuCommand() {
		return "Remove";
	}
	
	/**removes the selected item, period. it the item is mortal,
	   it will call its kill() method which should let some listeners know
	   of its demise*/
	public void actioinOnSelected(ZoomableGraphic selectedItem) {
		
		if ( selectedItem instanceof KnowsParentLayer) {
			
			UndoAbleEditForRemoveItem undo = new UndoAbleEditForRemoveItem(null, (ZoomableGraphic) selectedItem);
			
			undoableEdit.addEditToList(undo);
		
			
		}
		
		selector.getWorksheet().getTopLevelLayer().remove(selectedItem);
		
		
		
		if (selectedItem instanceof Mortal) {
			Mortal m=(Mortal) selectedItem;
			m.kill();
		}
		
		
		
		
	}
	
	/**creates the shape of a red X meant for the icon*/
	static ShapeGraphic createCartoonX(boolean selected) {
		Point p1=new Point(5,0);
		Point p2=new Point(17,24);
		
			ArrowGraphic ag1 =ArrowGraphic.createDefaltOutlineArrow(Color.red.darker(), Color.black);
			ag1.setStrokeWidth(4);
			if (selected) ag1.getBackGroundShape().setFillColor(Color.red);
			ag1.setPoints(p1, p2);
			ag1.setNumerOfHeads(0);
			
			ArrowGraphic ag2 = ag1.copy();
			p1=new Point(17,0);
			p2=new Point(5,24);
			ag2.setPoints(p1, p2);
			
			Area s= ag2.getOutline();
			s.add(ag1.getOutline());
			BasicShapeGraphic output = new BasicShapeGraphic(s);
			output.copyAttributesFrom(ag2);
			output.setStrokeColor(Color.black);
			output.setFillColor(Color.red);
			output.setStrokeWidth(2);
			output.setFilled(true);
			output.setAntialize(true);
			return output;
	}
	
	/**returns the delete icon as a red X*/
	public GraphicDisplayComponent getDeleteIcon(boolean selected) {
		 GraphicDisplayComponent output = new GraphicDisplayComponent(createCartoonX( selected));
		 
		 output.setRelocatedForIcon(false);
		
		 return output;
	}
	
	
	public Icon getIcon() {
		return  getDeleteIcon(true);
	}

}
