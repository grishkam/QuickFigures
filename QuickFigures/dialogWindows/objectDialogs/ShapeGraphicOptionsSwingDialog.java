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
 * Date Modified: Jan 15, 2021
 * Version: 2022.2
 */
package objectDialogs;

import java.util.ArrayList;
import java.util.HashMap;

import graphicalObjects_Shapes.ShapeGraphic;
import graphicalObjects_SpecialObjects.HasBackGroundShapeGraphic;
import standardDialog.booleans.BooleanInputPanel;
import standardDialog.colors.ColorComboboxPanel;
import standardDialog.numbers.AngleInputPanel;
import undo.Edit;
import utilityClasses1.ArraySorter;

/**A dialog for editing the color, line width and other properties of a shape*/
public class ShapeGraphicOptionsSwingDialog extends GraphicItemOptionsDialog {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	protected ShapeGraphic s;
	public boolean omitPart1=false;
	
	ArrayList<ShapeGraphic> array=new 	ArrayList<ShapeGraphic>();
	private boolean angleExists;

	public ShapeGraphicOptionsSwingDialog(ShapeGraphic s, boolean omit) {
		 this.s=s;	
		 addOptionsToDialog();
		
		 
		 omitPart1=omit;
		 super.undoableEdit=Edit.createGenericEditForItem(s);
		 angleExists=s.doesHaveRotationAngle();
		 if(s!=null)
			 this.setTitle("Edit "+s.getShapeName() +" Shape");
	}
	
	/**returns true if at least one shape is setup*/
	public boolean hasItems() {
		if (s!=null)
		return true;
		
		if(array.size()>0) return true;
		
		return false;
	}
	
	/**Constructs a dialog that changes the appearance of multiple items*/
	public ShapeGraphicOptionsSwingDialog(ArrayList<?> items, boolean backgroundShapes) {
		setArray(items);
		if (backgroundShapes) setArrayToTextBackGround(items);
		s=new ArraySorter<ShapeGraphic>().getFirstNonNull(array);
		if (s!=null)
		addOptionsToDialog();
		}
	
	
	/**Add multiple shapes to the dialog*/
	public void setArray(ArrayList<?> items) {
		array=new 	ArrayList<ShapeGraphic>();
		for(Object i: items) {
			if (i instanceof ShapeGraphic) {
				array.add((ShapeGraphic) i);
			}
		}
		super.undoableEdit=Edit.createGenericEdit(items);
	}
	
	/**Add multiple shapes to the dialog*/
	public void setArrayToTextBackGround(ArrayList<?> items) {
		array=new 	ArrayList<ShapeGraphic>();
		for(Object i: items) {
			addBackgroundShapeToDialog(i);
		}
		
	}
	
	/**If another shape is set as the background for the given object
	  adds the background shape to the list of shapes for the dialog
	 * @see HasBackGroundShapeGraphic*/
	public void addBackgroundShapeToDialog(Object i) {
		if (i instanceof HasBackGroundShapeGraphic) {
			if (!array.contains(i)) {
				HasBackGroundShapeGraphic i2 = (HasBackGroundShapeGraphic) i;
				i2.setFillBackGround(true);
				array.add(i2.getBackGroundShape());
			}
		}
	}
	
	protected void addOptionsToDialog() {
		if (!omitPart1) {addOptionsToDialogPart1();}
		addOptionsToDialogPart2();
	}
	
	/**Adds the name and fixed edge position to the dialog. these items do not affect the objects appearance*/
	protected void addOptionsToDialogPart1() {
		addNameField(s);
		this.addFixedEdgeToDialog(s);
	}
	
	/**Adds all other options to the dialog. these items affect the objects appearance*/
	protected void addOptionsToDialogPart2() {
		this.add("AntiA", new BooleanInputPanel("Antialize Appeareance", s.isAntialize()));
		this.addStrokePanelToDialog(s);
		ColorComboboxPanel filpanel = new ColorComboboxPanel("Fill Color", null, s.getFillColor());
		this.add("FillColor", filpanel);
		BooleanInputPanel fillpanel2 = new BooleanInputPanel("fill?", s.isFilled());
		moveGrid(2, -1);
		this.add("fill", fillpanel2 );
		moveGrid(-2, 0);
		if(angleExists) {
			AngleInputPanel aip = new AngleInputPanel("Angle", s.getAngle(), true);
			this.add("Angle", aip);
		}
		
		if (s.isHasCloseOption()) {
			BooleanInputPanel colsepanel2 = new BooleanInputPanel("Closed?", s.isClosedShape());
			add("Closed", colsepanel2);
		}
		if (s.getAttachmentPosition()!=null) this.addAttachmentPositionToDialog(s);
	}
	
	protected void setItemsToDiaog() {
		setItemsToDiaog(s);
		for(ShapeGraphic s: array) {
			setItemsToDiaog(s);
		}
}
	
	/***/
	protected void setItemsToDiaog(ShapeGraphic s) {
		if (s==null) return;
		
		if (!omitPart1) {
			this.setFixedEdgeToDialog(s);
			setNameFieldToDialog(s);
		}
		this.setStrokedItemtoPanel(s);
		s.setFillColor(this.getColor("FillColor"));
		
		s.setFilled(this.getBoolean("fill"));
		s.setAntialize(this.getBoolean("AntiA"));
		if(angleExists) s.setAngle(this.getNumber("Angle"));
		if (s.isHasCloseOption()) {
			s.setClosedShape(this.getBoolean("Closed"));
		}
		setObjectSnappingBehaviourToDialog(s);
}
	
	
	
	/**returns a map of every input panel. used by testing functions*/
	public HashMap<String, Object> getAllInputPanels() {
		HashMap<String, Object> output = super.getAllInputPanels();
		if (this.strokeInput!=null) {
			output.put("Stroke Width", strokeInput.widthInput);
			output.put("Stroke Join", strokeInput.joinInput);
			output.put("Stroke Cap", strokeInput.capInput);
			output.put("Stroke Miter Limit", strokeInput.miterInput);
			output.put("Stroke Color", strokeInput.strokeColorInput);
		}
		return output;
	}
}
