/**
 * Author: Greg Mazo
 * Date Modified: May 10, 2023
 * Copyright (C) 2023 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package dataTableDialogs;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import applicationAdapters.StartApplication;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import imageDisplayApp.ImageWindowAndDisplaySet;
import textObjectProperties.TextLine;

/**
 
 * 
 */
public class TextFormatList extends StartApplication{
	
public static void main(String[] args) {
	String p1 = "ATTGTGATGACCTAG";
	String p2 = "ATCGTGATGNCCTTG";
	
	ComplexTextGraphic ctg = new ComplexTextGraphic();
	ctg.setFontFamily("Lucida Sans Typewriter");
	ctg.setFontStyle(Font.BOLD);
	ctg.getParagraph().addLine("");
	ctg.moveLocation(10, 50);
	TextLine t1 = ctg.getParagraph().get(0);
	TextLine t2 = ctg.getParagraph().get(1);
	
	int lastCondition=0;
	String growingspan1 = "";
	String growingspan2 = "";
	boolean span_is0=true;
	
	for(int i=0; i<p1.length(); i++) {
		
		boolean match = p1.charAt(i)==p2.charAt(i);
		int currentCondition = match? 0:1;
		
		
		if(currentCondition==lastCondition||span_is0) {
			growingspan1+=p1.charAt(i);
			growingspan2+=p2.charAt(i);
			span_is0=false;
			
		} else {
			//Adds the segments and starts a new 
			if(growingspan1.length()>0)
				t1.addSegment(growingspan1, match? Color.GREEN: Color.black);
			if(growingspan1.length()>0)
				t2.addSegment(growingspan2, match? Color.RED: Color.black);
			growingspan1=p1.charAt(i)+"";
			growingspan2=p2.charAt(i)+"";
			
		}
		
		lastCondition= currentCondition;
	}
	startToolbars(true);
	
	ImageWindowAndDisplaySet i = ImageWindowAndDisplaySet.createAndShowNew("test", 500, 400);
	i.getImageAsWorksheet().addItemToImage(ctg);
}
	
	

}
