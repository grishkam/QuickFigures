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
package objectDialogs;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.util.ArrayList;

import graphicActionToolbar.CurrentFigureSet;
import graphicalObjects_Shapes.BasicShapeGraphic;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import imageDisplayApp.CanvasOptions;
import standardDialog.booleans.BooleanInputPanel;
import standardDialog.choices.ChoiceInputPanel;
import standardDialog.colors.ColorComboboxPanel;
import standardDialog.colors.ColorDimmingBox;
import standardDialog.fonts.FontChooser;
import standardDialog.graphics.GraphicSampleComponent;
import standardDialog.numbers.AngleInputPanel;
import standardDialog.strings.StringInputPanel;
import undo.Edit;

/**A dialog for text graphics*/
public class TextGraphicSwingDialog extends GraphicItemOptionsDialog{

	public static final String[] JUSTIFICATION_CHOICES = new String[] {"Left", "Center", "Right"};

	/**
	 * 
	 */
	
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
		this.add("Text", new StringInputPanel("Text", textItem.getText()));
		this.addFixedEdgeToDialog(textItem);
		 addFontAngleToDialog();
		ColorComboboxPanel cbp = new ColorComboboxPanel("Color", null, textItem.getTextColor());
		this.add("tColor", cbp);
		addDimmingToDialog();
		addBackgroundOptionsToDialog();
		addSnappingBehviourToDialog(textItem);
		
	}
	
	void addInsetsTab() {
		
		TextInsetsDialog id = new TextInsetsDialog(textItem);
		this.addSubordinateDialog("Insets", id);
		
	}
	
	protected void addBackgroundOptionsToDialog() {
		this.add("backGround", new BooleanInputPanel("Use background", textItem.isFillBackGround()));
		addInsetsTab();
		BasicShapeGraphic bgShape = textItem.getBackGroundShape();
		bgDialog=new ShapeGraphicOptionsSwingDialog(bgShape, true);
		
		this.addSubordinateDialog("Background", bgDialog);
	
		//this.getOptionDisplayTabs().addTab("Background", new ShapeGraphicOptionsSwingDialog(bgShape).removeOptionsTab());
		/**this.moveGrid(2, -1);
		ColorComboboxPanel cbp = new ColorComboboxPanel("Color Background", null, textItem.getBackGroundColor());
		this.add("ColorBG", cbp);
		this.moveGrid(-2, 0);*/
	}
	
	protected void setBackgroundOptionsToDialog(TextGraphic t) {
		t.setFillBackGround(this.getBoolean("backGround"));
	//	t.setBackGroundColor(this.getColor("ColorBG"));
	}
	
	protected void addDimmingToDialog() {
		ChoiceInputPanel cp=new ChoiceInputPanel("Color Dims ",  new ColorDimmingBox(textItem.getDimming()));
		this.add("dim", cp);
		this.getMainPanel().moveGrid(2, -1);
		this.add("dim?", new BooleanInputPanel("Dim Color?", textItem.isDimColor()));
		this.getMainPanel().moveGrid(-2, 0);
	}
	
	protected void addFontAngleToDialog() {
		FontChooser sb = new FontChooser(textItem.getFont());
		sb.setUIFontSize(10);
		add("font", sb);
	
		AngleInputPanel pai2 = new AngleInputPanel("Angle ", textItem.getAngle(), true);
		
		add("angle", pai2);
	
	}
	

	
	
	protected void setItemsToDiaog() {
				setItemsToDiaog(textItem);
	}
	
	protected void setItemsToDiaog(TextGraphic textItem) {
		super.setNameFieldToDialog(textItem);
		
		textItem.setText(this.getString("Text"));
		textItem.setTextColor(this.getColor("tColor"));
		textItem.setDimColor(this.getBoolean("dim?"));
		
		//textItem.setInsets(this.getInsetsPanelFromDialog());
		setAtrributesToDialog(textItem);
	}
	
	public void setAtrributesToDialog(TextGraphic textItem) {
		this.setFixedEdgeToDialog(textItem);
		textItem.setFont(this.getFont("font"));
		textItem.setAngle(this.getNumber("angle"));
		textItem.setDimming(this.getChoiceIndex("dim"));
		setBackgroundOptionsToDialog(textItem);
		setObjectSnappingBehaviourToDialog(textItem);
		if (CanvasOptions.current.resizeCanvasAfterEdit)
			CurrentFigureSet.canvasResize();
	}
	
	
	public static void main(String[] args) { 
	TextGraphic t = new TextGraphic();
	t.setFont(new Font("Arial", Font.BOLD, 30));
	TextGraphicSwingDialog dia = new TextGraphicSwingDialog(t);
	dia.sam = new GraphicSampleComponent(t);
	//t.setSnappingBehaviour(SnappingBehaviour.defaultInternal());
	//SnappingPanel spanel = new SnappingPanel(t.getSnappingBehaviour());
	//dia.place(spanel);
	dia.add(dia.sam, new GridBagConstraints());
	dia.showDialog();
	
	}
	
	
	public void addJustificationToDialog(TextGraphic tg) {
		if (tg instanceof ComplexTextGraphic) {
			ComplexTextGraphic ct=(ComplexTextGraphic) tg;
			ChoiceInputPanel st = new ChoiceInputPanel("Justification", JUSTIFICATION_CHOICES, ct.getParagraph().getJustification());
			this.add("Justification", st);
		}
	}
	public void setComplexProperteisToDialog(ComplexTextGraphic ct) {
		int jus = this.getChoiceIndex("Justification");
		ct.getParagraph().setJustification(jus);
	}
	
	public ArrayList<TextGraphic> getAllEditedItems() {
		return array;
	}
	
}
