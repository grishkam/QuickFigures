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
 * Date Modified: Dec 7, 2020
 * Version: 2021.1
 */
package objectDialogs;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;

import standardDialog.ObjectEditEvent;
import standardDialog.ObjectInputPanel;
import standardDialog.OnGridLayout;
import standardDialog.choices.ChoiceInputEvent;
import standardDialog.choices.ChoiceInputListener;
import standardDialog.choices.ChoiceInputPanel;
import standardDialog.colors.ColorComboboxPanel;
import standardDialog.strings.StringInputEvent;
import standardDialog.strings.StringInputListener;
import standardDialog.strings.StringInputPanel;
import textObjectProperties.TextLineSegment;

/**A panel within a dialog that allows the user to edit to a specific fragment of text, determining if the text is underlined, superscripted and so on 
 * @see TextLine
 * @see ComplexTextGraphic
 * */
public class TextLineSegmentPanel extends  ObjectInputPanel implements StringInputListener, ChoiceInputListener, OnGridLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TextLineSegment segment;
	StringInputPanel textPanel;
	ColorComboboxPanel colorPanal;
	ChoiceInputPanel scriptType;
	ChoiceInputPanel scriptStyle;
	private ChoiceInputPanel scriptLine;
	Boolean includeColor=true;
	
	/**creates a panel for the given text line segment*/
	public TextLineSegmentPanel(TextLineSegment t) {
		setSegment(t);
		setupDialog(t);
	}

	/**
	adds all of the gui items to this dialog.
	 */
	protected void setupDialog(TextLineSegment t) {
		textPanel=new StringInputPanel("Text", t.getText(),15);
		
		if (includeColor)	colorPanal=new ColorComboboxPanel("Text Color", null, t.getUniqueTextColor());
		
		scriptType=new ChoiceInputPanel("Text is", new String[] {"normal", "superscript", "subscript"},t.isSubOrSuperScript());
		scriptStyle=new ChoiceInputPanel("Text style", new String[] {"normal","Plain", "Bold", "Italic", "Bold+Italic"},t.getFont().getStyle());
		scriptLine=new ChoiceInputPanel("Line Type", new String[] {"no line","Underline", "Strike Through"},t.getLines());
		
		addListeners();
		this.setLayout(new GridBagLayout());
		this.placeItems(this, 0, 0);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(350,200);
	}
	
	
	
	
	/**changes the segment of text to match the dialog*/
	public void setSegmentToPanels() {
		 getSegment().setText(textPanel.getTextFromField());
		 if (includeColor)	 getSegment().setTextColor(colorPanal.getSelectedColor());
		 getSegment(). setScript(scriptType.getSelectedIndex());
		 getSegment().setUniqueStyle(scriptStyle.getSelectedIndex());
		 getSegment().setLines(scriptLine.getSelectedIndex());
	}



	/**places all the the dialog items within the grid bag layout of the container given*/
	@Override
	public void placeItems(Container jp, int x0, int y0) {
		textPanel.placeItems(jp, x0, y0);
		colorPanal.placeItems(jp, x0, y0+1);
		scriptType.placeItems(jp, x0, y0+2);
		scriptStyle.placeItems(jp, x0, y0+3);
		scriptLine.placeItems(jp, x0, y0+4);
	}

	@Override
	public int gridHeight() {
		return 3;
	}

	@Override
	public int gridWidth() {
		return 2;
	}

	public TextLineSegment getSegment() {
		return segment;
	}

	public void setSegment(TextLineSegment segment) {
		this.segment = segment;
	}
	
	
	/**used to ensure that certain methods are called when a change is made to this dialog panel*/
	public void addListeners() {
		textPanel.addStringInputListener(this);
		scriptType.addChoiceInputListener(this);
		scriptStyle.addChoiceInputListener(this);
		scriptLine.addChoiceInputListener(this);
		if (includeColor)	colorPanal.addChoiceInputListener(this);
		
	}
	/**if dialog items are changed, this notifies teh listeners for object edit events*/
	@Override
	public void numberChanged(ChoiceInputEvent ne) {
		setSegmentToPanels();
		this.notifyListeners(new ObjectEditEvent(getSegment()));
	}
	/**if dialog items are changed, this notifies teh listeners for object edit events*/
	@Override
	public void StringInput(StringInputEvent sie) {
		setSegmentToPanels();
		this.notifyListeners(new ObjectEditEvent(getSegment()));
	}
}
