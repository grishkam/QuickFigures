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
 * Date Modified: Jan 4, 2021
 * Version: 2023.1
 */
package channelLabels;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import channelMerging.ChannelEntry;
import graphicalObjects_SpecialObjects.TextGraphic;
import logging.IssueLog;
import textObjectProperties.TextLine;
import textObjectProperties.TextLineSegment;
import textObjectProperties.TextParagraph;

/**This class stores information that pertains to all of the channel labels*/
public class ChannelLabelProperties implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**The possibles values for the standard merge text content option*/
	public static String[] mergeTexts=new String[] {"Merge", "merge", "Merged", "merged", "use custom", "Overlay", "overlay"};
	public static final int USE_CUSTOM_MERGE_TEXT=4;
	
	/**these arrays are used when the rainbow style option is chosen, depending on the number of colors, the standard merge texts are split up */
	public static String[][] split2MergeTexts=new String[][] {new String[]{"Mer", "ge"},      new String[]{"mer", "ge"} ,      new String[]{"Mer", "ged"},       new String[]{"mer", "ged"}, new String[]{"Mer", "ge"}, new String[]{"Over", "lay"}, new String[]{"over", "lay"}};
	public static String[][] split3MergeTexts=new String[][] {new String[]{"M","er", "ge"},   new String[]{"m","er", "ge"} ,   new String[]{"Me","rg", "ed"},    new String[]{"me","rg", "ed"}, new String[]{"Me","rg", "e"}, new String[]{"Ov","er", "lay"}, new String[]{"ov", "er", "lay"}};
	public static String[][] split4MergeTexts=new String[][] {new String[]{"M","e","r", "ge"},new String[]{"m","e","r", "ge"} ,new String[]{"M","er","ge", "d"}, new String[]{"m","er","ge", "d"},new String[]{"m","er","ge", "d"}, new String[]{"Ov","er", "la","y"}, new String[]{"ov", "er", "la","y"}};
	
	public static final int SPACE_SEPARATOR=1, NO_SEPARATOR=0, SLASH_SEPARATOR=2, CUSTOM_SEPATOR=3;
	public static Color emptyColor=new Color(255,255,255, 255);
	public static String[] separatorOptions=new String[] { "nothing", "a space", "/", "use custom"};
	public static String[] separatorTexts=new String[] {"", " ", "/", "?"};
	
	public static String[] mergeLabelOptions=new String[] {"'Merge' text",   "Multiline Channel Labels", "Single Line Channel Labels", "Rainbow Text", "Color Merge Style"};
	
	
	private String customMergeText="Merge";
	private String customSeparator="";
	private int separatorOption=NO_SEPARATOR;
	
	private int mergeTextContentOption=0;//the index of "Merge" in the mergeTexts arrays
	private MergeLabelStyle mergeLabelStyleOption=MergeLabelStyle.SIMPLY_LABEL_AS_MERGE;
	
	/**the text for each channel is stored here. Hashmap Links channel names to text lines. */
	private HashMap<String, TextLine> eachChannelTextList=null;
	private HashMap<String, TextLine> getTextList() {
		if (eachChannelTextList==null) {
			eachChannelTextList=new HashMap<String, TextLine>();
		}
		
		return eachChannelTextList;
	}
	
	/**If a certain string is meant to separate single line channel labels, this returns it*/
	public String getSeparatorText() {
		
		if (this.getSeparatorOption()==CUSTOM_SEPATOR) return this.getCustomSeparator();
		String o = separatorTexts[separatorOption];
		return o;
	}
	
	
	public ChannelLabelProperties copy() {
		ChannelLabelProperties output = new ChannelLabelProperties();
		output.setCustomMergeText(getCustomMergeText());
		output.setCustomSeparator(getCustomSeparator());
		output.setMergeLabelStyle(getMergeLabelStyle());
		output.setMergeTextOption(getMergeTextOption());
		return output;
	}
	
	/**Returns the string used for merge labels. examples include 'merge', 'Merged'*/
	public String getMergeText(){
		String ot = mergeTexts[getMergeTextOption()];
		if (this.getMergeTextOption()==USE_CUSTOM_MERGE_TEXT) return this.getCustomMergeText();
		return ot;
		
	}
	

	/**getter method for the merge label style*/
	public MergeLabelStyle getMergeLabelStyle() {return mergeLabelStyleOption;}
	
	/** setter method of the merge label style*/
	public void setMergeLabelStyle(MergeLabelStyle mergeLabelTypeOption) {
		this.mergeLabelStyleOption = mergeLabelTypeOption;
	}
	
	/**return a code that determines what the content of the 'merge' label will be*/
	public int getMergeTextOption() {return mergeTextContentOption;}
	
	/**sets the content of the 'merge' label */
	public void setMergeTextOption(int mergeTextOption) {
			this.mergeTextContentOption = mergeTextOption;
		}
	

	
	/**returns what kind of separator is used if each channel's label share a single line*/
	public int getSeparatorOption() {
		return separatorOption;
	}
	/**determine what kind of separator is used if each channel's label share a single line*/
	public void setSaparatorOption(int saparatorOption) {
		this.separatorOption = saparatorOption;
	}

	public String getCustomMergeText() {
		return customMergeText;
	}

	public void setCustomMergeText(String customMergeText) {
		this.customMergeText = customMergeText;
	}

	/**Sets the merge text. if text matches one of the options, changes to that option, option is set to custom text otherwise*/
	public void setMergeText(String customMergeText) {
		for(int i=0; i<mergeTexts.length; i++) {
			String t=mergeTexts[i];
			if(t.equals(customMergeText)) 
				{mergeTextContentOption=i; 
					return;}
			
		}
		mergeTextContentOption=USE_CUSTOM_MERGE_TEXT;
		this.customMergeText=customMergeText;
	}

	/**returns the separator for single line channel labels*/
	public String getCustomSeparator() {
		return customSeparator;
	}

	/**Sets the separator for single line channel labels*/
	public void setCustomSeparator(String customSeparator) {
		this.customSeparator = customSeparator;
	}
	
	/**Sets the separator for single line channel labels*/
	public void setSeparatorText(String customSeparator) {
		for(int i=0; i<separatorTexts.length; i++) {
			String t=separatorTexts[i];
			if(t.equals(customSeparator)) 
				{this.separatorOption=i; 
					return;}
			
		}
		this.separatorOption=CUSTOM_SEPATOR;
		this.customSeparator=customSeparator;
	}
	
	
	

	/**Combines channel colors to create a third color that is an overlay of them all
	 * to display */
	public static Color fuseColors(ArrayList<ChannelEntry> chanEntries) {
		ArrayList<Color> arr = new ArrayList<Color> ();
		for(ChannelEntry en:chanEntries) {
			arr.add(en.getColor());
		}
		return fuseColors(arr);
	}
	
	/**Adds up the rgb compoments of the colors to create a merged color*/
	public static Color fuseColors(Iterable<Color> cs) {
		int r=0;
		int g=0;
		int b=0;
		int a=0;
		for(Color c: cs) {
			if (c==null) continue;
			r+=c.getRed();
			g+=c.getGreen();
			b+=c.getBlue();
			a+=c.getAlpha();
		}
		if (r>255) r=255;
		if (g>255) g=255;
		if (b>255) b=255;
		if (a>255) a=255;
		return new Color(r,g,b);
		
		
		
		
	}

	/**Sets these channel label properties to match the model*/
	public void copyOptionsFrom(ChannelLabelProperties theModel) {
		this.customMergeText=theModel.customMergeText;
		this.customSeparator=theModel.customSeparator;
		separatorOption=theModel.separatorOption;
		mergeTextContentOption=theModel.mergeTextContentOption;
		mergeLabelStyleOption=theModel.mergeLabelStyleOption;
	}

	/**Creates a line of text to match the channel of name label and stores it in a hashmap
	  as the channel label for that channel*/
	public void createTextLineForChannel(ChannelEntry c) {
		String label=c.getLabel();
		if (this.getTextLineForChannel(c.getLabel())!=null) return;
		
		TextLine lin = new TextLine( generateDisplayParaGraph());
		
		boolean standardLoci=isStandardModifiedLociLabel(label);//if the channel is in standard loci format
		
		/**if the label is in the formal 'c:#/#, this truncates the label*/
		if (standardLoci ) { 
			
					String label2 = label.substring(0, 5)+";"+label.substring(5, label.length());
					if (c.getRealChannelName()!=null) label2=c.getRealChannelName();
					
					lin.addFromCodeString(label2, c.getColor());
			} else
		lin.addFromCodeString(label, c.getColor());
		
		
		for(TextLineSegment seg:lin) {
			seg.setTextColor(new Color(0,0,0,0));//makes sure the color is transparent so it will not be copied 
			seg.setUniqueStyle(0);
		}
		if (lin.getText().length()>20) {
			IssueLog.log("Line for channel  name is is too long: "+lin.getText().length()+" characters");
			String newT = lin.getText();
			newT=newT.substring(newT.length()-20);
			lin.getFirstSegment().setText(newT);
		}
		getTextList().put(label, lin);
		
	}
	
	/**returns true, if the label text indicates that QuickFigures may have altered the channel labels
	  to match image metadata (images would have been opened using locitools)*/
	boolean isStandardModifiedLociLabel(String label) {
		if (!label.startsWith("c:")) return false;
		if (label.charAt(3)!='/') return false;
		return true;
	}
	
	/**creates a paragraph pane to be used by UI classes to edit the channel panels*/
	TextParagraph generateDisplayParaGraph() {
		TextParagraph tp = new TextParagraph(new TextGraphic(""));
		tp.getParent().setTextColor(new Color(0,0,0,0));
		return tp;
		
	}

	
/**Returns a Text line for the given channel entry.
 * The text lines for each channelare stored within this object
 *  always returns a Text line
   */
	public TextLine getTextLineForChannel(ChannelEntry label) {
		TextLine output = getTextList().get(label.getLabel());
		if (output==null) {
			createTextLineForChannel(label);
		}
		return getTextList().get(label.getLabel());
	}
	
	/**returns the text for a given channel name*/
	private TextLine getTextLineForChannel(String channelName) {
		return getTextList().get(channelName);
	}
	
	
	
	

}
