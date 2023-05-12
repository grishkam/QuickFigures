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
 * Date Created: May 10, 2023
 * Date Modified: May 11, 2023
 * Version: 2023.2
 */
package dataTableDialogs;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import applicationAdapters.DisplayedImage;
import basicMenusForApp.BasicMenuItemForObj;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import layout.RetrievableOption;
import standardDialog.StandardDialog;
import textObjectProperties.TextLine;
import textObjectProperties.TextLineSegment;
import textObjectProperties.TextParagraph;
import ultilInputOutput.FileChoiceUtil;

/**
 Creates a formatted textthat reflects the match/mismatch between two strings
 */
public class TextMismatchDisplay extends BasicMenuItemForObj {
	

 Font font =null;
 
	/**the names of the samples that will be seen in the spreadsheet */
	@RetrievableOption(key = "permutate", label="Make combinations of mutations?")
	public boolean make_permutations=false;

/**Performs a task specific to the menu item. */
@Override
public void performActionDisplayedImageWrapper(DisplayedImage diw) {
	createMismatchDisplay();
}


/**
 * 
 */
public  void createMismatchDisplay() {
	String p1 = "ATTGTGATGACCTAG";
	String p2 = "ATCGTGATGNCCTTG";
	
	String[] su = StandardDialog.getStringArrayFromUser("input", p1+"\n"+p2, 2, this);
	p1=su[0];
	p2=su[1];
	
	ComplexTextGraphic ctg = createMismatchDisplay(p1, p2);
	
	
	
	
	try {
		ExcelTableReader etr = new ExcelTableReader();
		TextParagraph p = ctg.getParagraph();
		if(make_permutations)
			p=makePermutations(p);
		ctg.setParagraph(p);
		int count = 0;
		
		etr.setValueAt("sequence_colored", 0, 1);
		for(TextLine lin:p) {
			etr.setRichText(lin, count+1, 1);
			count++;
			}
		etr.saveTable(true, FileChoiceUtil.getSaveFile("two sequences.xlsx").getAbsolutePath());
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

/**
 * @param p1
 * @param p2
 * @return
 */
public  ComplexTextGraphic createMismatchDisplay(String p1, String p2) {
	ComplexTextGraphic ctg = new ComplexTextGraphic();
	ctg.setFontFamily("Lucida Sans Typewriter");
	//'Liberation Mono', "Bodoni MT", 'Bodoni MT Black', 'Lucida Sans Typewriter' and 'Courer New' are also monospaced fonts'
	ctg.setFontStyle(Font.BOLD);
	font=ctg.getFont();
	ctg.getParagraph().addLine("");
	ctg.moveLocation(10, 50);
	TextLine t1 = ctg.getParagraph().get(0);
	TextLine t2 = ctg.getParagraph().get(1);
	
	int lastCondition=0;
	String growingspan1 = "";
	String growingspan2 = "";
	boolean span_is0=true;
	boolean match =false;
	boolean seqMisMatch=false;
	Color green = Color.GREEN.darker();
	Color red = Color.RED.darker();
	for(int i=0; i<p1.length(); i++) {
		
		match = p1.charAt(i)==p2.charAt(i);
		int currentCondition = match? 0:1;
		
		
		if(currentCondition==lastCondition||span_is0) {
			growingspan1+=p1.charAt(i);
			growingspan2+=p2.charAt(i);
			span_is0=false;
			
		} else {
			//Adds the segments and starts a new 
			seqMisMatch=!growingspan1.contentEquals(growingspan2);
			if(growingspan1.length()>0)
				t1.addSegment(growingspan1, seqMisMatch? green: Color.black);
			if(growingspan1.length()>0)
				t2.addSegment(growingspan2, seqMisMatch? red: Color.black);
			growingspan1=p1.charAt(i)+"";
			growingspan2=p2.charAt(i)+"";
			
		}
		
		lastCondition= currentCondition;
	}
	
	seqMisMatch=!growingspan1.contentEquals(growingspan2);
	if(growingspan1.length()>0)
		t1.addSegment(growingspan1, seqMisMatch? green: Color.black);
	if(growingspan1.length()>0)
		t2.addSegment(growingspan2, seqMisMatch? red: Color.black);
	return ctg;
}
	
	public  TextParagraph makePermutations(TextParagraph p) {
		TextLine l1 = p.get(0);
		TextLine l2 = p.get(1);
		
		p=new ComplexTextGraphic().copyParagraph();
		
		for(int i=0; i<l1.size(); i++) {
			String t1 = l1.get(i).getText();
			String t2 = l2.get(i).getText();
			if(t1.contentEquals(t2)) {
				addTextToEachLine(p, t1,l2.get(i).getTextColor() );
			} else {
				TextParagraph p1 = p.copy();
				TextParagraph p2 = p.copy();
				addTextToEachLine(p1, t1,l1.get(i).getTextColor() );
				addTextToEachLine(p2, t2,l2.get(i).getTextColor() );
				p=merge(p1, p2);
			}
		}
		
		return p;
	}
	
	public  void addTextToEachLine(TextParagraph p, String text, Color c) {
		for(TextLine l: p) {
			TextLineSegment seg = l.addSegment(text, c);
			seg.setFont(font);
		}
	}
	
	/**returns a merged version*/
	public  TextParagraph merge(TextParagraph p1, TextParagraph p2) {
		ComplexTextGraphic ctg = new ComplexTextGraphic();
		ctg.setFont(font);
		TextParagraph p = ctg.getParagraph();
		p.removeAllLines();
		p.addAll(p1);
		p.addAll(p2);
		return p;
	}

	@Override
	public String getNameText() {
		return "Color mismatched base pairs";
	}

	@Override
	public String getMenuPath() {
		return "Tables";
	}

}
