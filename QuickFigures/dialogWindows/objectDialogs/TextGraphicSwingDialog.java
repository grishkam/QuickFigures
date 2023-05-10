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
 * Version: 2023.2
 */
package objectDialogs;

import java.util.ArrayList;

import graphicActionToolbar.CurrentFigureSet;
import graphicalObjects_Shapes.BasicShapeGraphic;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import imageDisplayApp.CanvasOptions;
import locatedObject.ColorDimmer;
import standardDialog.booleans.BooleanInputPanel;
import standardDialog.choices.ChoiceInputPanel;
import standardDialog.colors.ColorComboboxPanel;
import standardDialog.colors.ColorDimmingBox;
import standardDialog.fonts.FontChooser;
import standardDialog.numbers.AngleInputPanel;
import standardDialog.strings.StringInputPanel;
import undo.Edit;

/**A dialog for text items*/
public class TextGraphicSwingDialog extends GraphicItemOptionsDialog{

	/**
	 * 
	 */
	protected static final String NAME_OF_TEXT_INSET_TAB = "Inset the text";

	/**
	 string keys used to retrive certain values from the dialog 
	 */
	protected static final String ANGLE_KEY = "angle",  FONT_KEY = "font", JUSTIFICATION_KEY = "Justification",
			TEXT_CONTENT_KEY = "Text", TEXT_COLOR_KEY = "tColor",  DOES_DIM_KEY = "dim?", DIM_KEY = "dim";

	public static final String[] JUSTIFICATION_CHOICES = new String[] {"Left", "Center", "Right"};

	/**
	 * 
	 */
	
	/**A list of the text items that this dialog applied to*/
	protected ArrayList<TextGraphic> array=new ArrayList<TextGraphic>();//used by multitext graphic subclass
	
	private static final long serialVersionUID = 1L;
	TextGraphic textItem;

	protected ShapeGraphicOptionsSwingDialog bgDialog;
	
	public TextGraphicSwingDialog() {}
	
	public TextGraphicSwingDialog(TextGraphic t) {
		textItem=t;
		addOptionsToDialog();
		super.undoableEdit=Edit.createGenericEditForItem(t);
	}
	
	protected void addOptionsToDialog() {
		this.addNameField(textItem);
		this.add(TEXT_CONTENT_KEY, new StringInputPanel(TEXT_CONTENT_KEY, textItem.getText()));
		this.addFixedEdgeToDialog(textItem);
		 addFontAngleToDialog();
		ColorComboboxPanel cbp = new ColorComboboxPanel("Color", null, textItem.getTextColor());
		this.add(TEXT_COLOR_KEY, cbp);
		
		addDimmingToDialog();
		addBackgroundOptionsToDialog();
		addAttachmentPositionToDialog(textItem);
		
	}
	
	/**The text mey be draw a little bit inward compared to the bounding box, a tab is added with options related to this*/
	void addInsetsTab() {
		
		TextInsetsDialog id = new TextInsetsDialog(textItem);
		this.addSubordinateDialog(NAME_OF_TEXT_INSET_TAB, id);
		
	}
	
	/**text items may have a background*/
	protected void addBackgroundOptionsToDialog() {
		this.add("backGround", new BooleanInputPanel("Use background", textItem.isFillBackGround()));
		addInsetsTab();
		BasicShapeGraphic bgShape = textItem.getBackGroundShape();
		bgDialog=new ShapeGraphicOptionsSwingDialog(bgShape, true);
		
		this.addSubordinateDialog("Background", bgDialog);

	}
	
	protected void setBackgroundOptionsToDialog(TextGraphic t) {
		t.setFillBackGround(this.getBoolean("backGround"));
	
	}
	
	/***/
	protected void addDimmingToDialog() {
		ChoiceInputPanel cp=new ChoiceInputPanel("Color Dim Type ",  new ColorDimmingBox(textItem.getDimming().ordinal()));
		this.add(DIM_KEY, cp);
		this.getCurrentUsePanel().moveGrid(2, -1);
		this.add(DOES_DIM_KEY, new BooleanInputPanel("Dim Color?", textItem.isDimColor()));
		this.getCurrentUsePanel().moveGrid(-2, 0);
	}
	
	/**Adds the font and the angle fields to the dialog*/
	protected void addFontAngleToDialog() {
		
		FontChooser sb = new FontChooser(textItem.getFont(), FontChooser.LIMITED_FONT_LIST);
		sb.setUIFontSize(10);
		add(FONT_KEY, sb);
		
	
		AngleInputPanel pai2 = new AngleInputPanel("Angle ", textItem.getAngle(), true);
		
		add(ANGLE_KEY, pai2);
	
	}
	

	
	/**changes the text object's properties to match the fields in this dialog*/
	protected void setItemsToDiaog() {
				setItemsToDiaog(textItem);
	}
	
	protected void setItemsToDiaog(TextGraphic textItem) {
		super.setNameFieldToDialog(textItem);
		
		textItem.setText(this.getString(TEXT_CONTENT_KEY));
		textItem.setTextColor(this.getColor(TEXT_COLOR_KEY));
		textItem.setDimColor(this.getBoolean(DOES_DIM_KEY));
		
		setAtrributesToDialog(textItem);
	}
	
	public void setAtrributesToDialog(TextGraphic textItem) {
		this.setFixedEdgeToDialog(textItem);
		textItem.setFont(this.getFont(FONT_KEY));
		textItem.setAngle(this.getNumber(ANGLE_KEY));
		textItem.setDimming(ColorDimmer.values()[this.getChoiceIndex(DIM_KEY)]);
		setBackgroundOptionsToDialog(textItem);
		setObjectSnappingBehaviourToDialog(textItem);
		if (CanvasOptions.current.resizeCanvasAfterEdit)
			CurrentFigureSet.canvasResize();
	}
	

	
	/**Adds a justification field to the dialog*/
	public void addJustificationToDialog(TextGraphic tg) {
		if (tg instanceof ComplexTextGraphic) {
			ComplexTextGraphic ct=(ComplexTextGraphic) tg;
			ChoiceInputPanel st = new ChoiceInputPanel(JUSTIFICATION_KEY, JUSTIFICATION_CHOICES, ct.getParagraph().getJustification());
			this.add(JUSTIFICATION_KEY, st);
		}
	}
	public void setComplexProperteisToDialog(ComplexTextGraphic ct) {
		int jus = this.getChoiceIndex(JUSTIFICATION_KEY);
		ct.getParagraph().setJustification(jus);
	}
	
	public ArrayList<TextGraphic> getAllEditedItems() {
		return array;
	}
	
}
