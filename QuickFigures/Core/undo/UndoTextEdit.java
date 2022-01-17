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
 * Date Modified: Dec 4, 2021
 * Version: 2022.0
 */
package undo;

import java.awt.Color;
import java.awt.Font;

import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import locatedObject.ColorDimmer;
import textObjectProperties.TextParagraph;

/**An undo for edits to text items*/
public class UndoTextEdit extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TextGraphic textItem;
	private TextParagraph innitial;
	private String startingText;
	private String endingText;
	private TextParagraph finalParagraph;
	private int ihighlight;
	private int iCursor;
	private int fHighlight;
	private int fCursor;
	private Font iFont;
	private Font fFont;
	private UndoAttachmentPositionChange isnap;
	private Color iColor;
	private Color fColor;
	private ColorDimmer iDim;
	private ColorDimmer fDim;

	public UndoTextEdit(TextGraphic t) {
		
		 setTextItem(t);
		startingText = t.getText();
		if (t instanceof ComplexTextGraphic) {
			ComplexTextGraphic comp = (ComplexTextGraphic) t;
			innitial=comp.copyParagraph();
		}
		
		ihighlight=t.getHighlightPosition();
		iCursor=t.getCursorPosition();
		
		iFont=t.getFont().deriveFont(t.getFont().getStyle());
		isnap=new UndoAttachmentPositionChange(t);
		iColor=t.getTextColor();
		iDim=t.getDimming();
		
		setUpFinalState();//TODO: determine why button for changing merge label does not redo (undo still works)
	}
	
	public void setUpFinalState() {
		endingText = getTextItem().getText();
		if (getTextItem() instanceof ComplexTextGraphic) {
			ComplexTextGraphic comp = (ComplexTextGraphic) getTextItem();
			finalParagraph=comp.copyParagraph();
		}
		
		fHighlight= getTextItem().getHighlightPosition();
		fCursor= getTextItem().getCursorPosition();
		fFont=getTextItem().getFont().deriveFont(getTextItem().getFont().getStyle());
		fColor=getTextItem().getTextColor();
		fDim=getTextItem().getDimming();
	}
	

	public void redo() {
		if (getTextItem() instanceof ComplexTextGraphic) {
			((ComplexTextGraphic) getTextItem()).setParagraph(this.finalParagraph);
		} else getTextItem().setText(endingText);
		getTextItem().setSelectedRange(fHighlight, fCursor);
		getTextItem().setFont(fFont);
		getTextItem().setTextColor(fColor);
		getTextItem().setDimming(fDim);
		isnap.redo();
	}
	
	public void undo() {
		if (getTextItem() instanceof ComplexTextGraphic) {
			((ComplexTextGraphic) getTextItem()).setParagraph(innitial);
		} else getTextItem().setText(startingText);
		getTextItem().setSelectedRange(ihighlight, iCursor);
		getTextItem().setFont(iFont);
		getTextItem().setTextColor(iColor);
		getTextItem().setDimming(iDim);
		isnap.undo();
	}

	public TextGraphic getTextItem() {
		return textItem;
	}

	public void setTextItem(TextGraphic textItem) {
		this.textItem = textItem;
	}

}
