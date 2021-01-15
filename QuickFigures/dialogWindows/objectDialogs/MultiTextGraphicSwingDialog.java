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
package objectDialogs;

import java.util.ArrayList;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_SpecialObjects.BarGraphic;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import locatedObject.AttachmentPosition;
import locatedObject.LocatedObject2D;
import logging.IssueLog;
import standardDialog.booleans.BooleanInputPanel;
import standardDialog.choices.ChoiceInputPanel;
import standardDialog.colors.ColorDimmingBox;
import standardDialog.fonts.FontChooser;
import standardDialog.numbers.AngleInputPanel;
import undo.Edit;

/**A dialog for editing multiple text items at once*/
public class MultiTextGraphicSwingDialog extends TextGraphicSwingDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean unifyPosition=false;

	private boolean hasComplexGraphic;

	private boolean sameSnap;
	
	public MultiTextGraphicSwingDialog() {
		super();
	}
	
	public MultiTextGraphicSwingDialog(ArrayList<? extends ZoomableGraphic> zs, boolean sameSnap) {
		super();
		if (zs!=null)this.setGraphics(zs);
		if (array.size()==0) return;
		addOptionsToDialog();
		this.sameSnap=sameSnap;
	}
	
	public  ComplexTextGraphic getComplex(ArrayList<?> arr) {
		
		
		for( Object a:arr) {
			if (a instanceof ComplexTextGraphic) {
				return ( ComplexTextGraphic)a;
			}
			}
		return null;
	}
	
	public void setGraphics(ArrayList<? extends ZoomableGraphic> zs) {
		array=new ArrayList<TextGraphic>();
		addGraphicsToArray(array, zs);
		if (array.size()==0) {
			IssueLog.log("there are no items in new array");
			return;
		}
		
		ComplexTextGraphic comp = getComplex(array);
		if (comp!=null)  {this.hasComplexGraphic=true;
		super.textItem=comp;
		} else
		
			super.textItem	=array.get(0);
		super.undoableEdit=Edit.createGenericEdit(zs);
	}
	
	
	public void addGraphicsToArray(ArrayList<TextGraphic> array, ArrayList<? extends ZoomableGraphic> zs) {
		for(ZoomableGraphic z:zs) {
			if (z instanceof TextGraphic) {array.add((TextGraphic) z);}
			if (z instanceof BarGraphic) {array.add(((BarGraphic) z).getBarText() );}
			if (z instanceof GraphicLayer) {
				addGraphicsToArray(array,	((GraphicLayer) z).getAllGraphics());
			}
					}
	}
	
	protected void addOptionsToDialog() {
		this.addFixedEdgeToDialog(textItem);
		if (this.hasComplexGraphic) this.addJustificationToDialog(textItem);
		FontChooser sb = new FontChooser(textItem.getFont());
		add("font", sb);
		
		AngleInputPanel pai2 = new AngleInputPanel("Angle ", textItem.getAngle(), true);
		add("angle", pai2);
		ChoiceInputPanel cp = new ChoiceInputPanel("Color Dims ",  new ColorDimmingBox(textItem.getDimming()));
		this.add("dim", cp);
		addBackgroundOptionsToDialog();
	
		for(TextGraphic t: array) {
			super.bgDialog. addBackgroundShapeToDialog(t.getBackGroundShape());
		}
		
		addSnappingBehviourToDialog(textItem);
	}
	
	protected void addBackgroundOptionsToDialog() {
		this.add("backGround", new BooleanInputPanel("Use background", textItem.isFillBackGround()));
		addInsetsTab();
		
		
		ArrayList<Object> listBG=new ArrayList<Object>();
		for(TextGraphic t: array) {
			listBG.add(t.getBackGroundShape());
		}
		
		bgDialog=new ShapeGraphicOptionsSwingDialog(listBG, false);
		
		this.addSubordinateDialog("Background", bgDialog);
	
		
	}
	
	void addInsetsTab() {
		
		TextInsetsDialog id = new TextInsetsDialog(array, false);
		this.addSubordinateDialog("Insets", id);
		//this.getOptionDisplayTabs().addTab("Insets", id.removeOptionsTab());
		
	}
	
	public void setObjectSnappingBehaviourToDialog(LocatedObject2D l) {
		if (snappingPanel==null) return;
		l.setAttachmentPosition(snappingPanel.getSnappingBehaviour().copy());
		if (sameSnap)l.setAttachmentPosition(snappingPanel.getSnappingBehaviour());
	}

	

	protected void setItemsToDiaog() {
		AttachmentPosition snap = array.get(0).getAttachmentPosition();
		
		for(TextGraphic s: array) {
			setAtrributesToDialog(s);
			if (this.isUnifyPosition()) {
				s.setAttachmentPosition(snap);
			}
			if (s instanceof ComplexTextGraphic) {
				setComplexProperteisToDialog((ComplexTextGraphic) s);
			}
		}
}

	public boolean isUnifyPosition() {
		return unifyPosition;
	}

	public void setUnifyPosition(boolean unifyPosition) {
		this.unifyPosition = unifyPosition;
	}
	

	
	
}
