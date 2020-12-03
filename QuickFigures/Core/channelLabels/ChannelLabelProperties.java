package channelLabels;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import channelMerging.ChannelEntry;
import graphicalObjects_BasicShapes.TextGraphic;
import logging.IssueLog;
import utilityClassesForObjects.TextLine;
import utilityClassesForObjects.TextLineSegment;
import utilityClassesForObjects.TextParagraph;

/**This class stores information that pertains to all of the channel labels*/
public class ChannelLabelProperties implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**The possibles values for the standard merge text content option*/
	public static String[] mergeTexts=new String[] {"Merge", "merge", "Merged", "merged", "use custom", "Overlay", "overlay"};
	public static int USE_CUSTOM_MERGE_TEXT=4;
	
	/**these arrays are used when the rainbow style option is chosen, depending on the number of colors, the standard merge texts are split up */
	public static String[][] split2MergeTexts=new String[][] {new String[]{"Mer", "ge"},new String[]{"mer", "ge"} ,new String[]{"Mer", "ged"},new String[]{"mer", "ged"}, new String[]{"Mer", "ge"}, new String[]{"Over", "lay"}, new String[]{"over", "lay"}};
	public static String[][] split3MergeTexts=new String[][] {new String[]{"M","er", "ge"},new String[]{"m","er", "ge"} ,new String[]{"Me","rg", "ed"},new String[]{"me","rg", "ed"}, new String[]{"Me","rg", "e"}, new String[]{"Ov","er", "lay"}, new String[]{"ov", "er", "lay"}};
	public static String[][] split4MergeTexts=new String[][] {new String[]{"M","e","r", "ge"},new String[]{"m","e","r", "ge"} ,new String[]{"M","er","ge", "d"},new String[]{"m","er","ge", "d"},new String[]{"m","er","ge", "d"}, new String[]{"Ov","er", "la","y"}, new String[]{"ov", "er", "la","y"}};
	
	public static final int SPACE_SEPARATOR=1, NO_SEPARATOR=0, SLASH_SEPARATOR=2, CUSTOM_SEPATOR=3;
	public static Color emptyColor=new Color(255,255,255, 255);
	public static String[] separatorOptions=new String[] { "nothing", "a space", "/", "use custom"};
	public static String[] separatorTexts=new String[] {"", " ", "/", "?"};
	
	public static String[] mergeLabelOptions=new String[] {"'Merge' text",   "Multiline Channel Labels", "Single Line Channel Labels", "Soni Style", "Color Merge Style"};
	public static final int SIMPLY_lABEL_AS_MERGE=0,  MULTIPLE_LINES=1, ONE_LINE_WITH_ALL_CHANNEL_NAMES=2, RAINBOW_STYLE=3, OVERLAY_THE_COLORS=4;
	
	
	private String customMergeText="Merge";
	private String customSeparator="";
	private int separatorOption=NO_SEPARATOR;
	
	private int mergeTextContentOption=0;//the index of "Merge" in the mergeTexts arrays
	private int mergeLabelStyleOption=SIMPLY_lABEL_AS_MERGE;
	
	/**the text for each channel is stored here. Hashmap Links channel names to text lines. */
	private HashMap<String, TextLine> list=null;
	private HashMap<String, TextLine> getTextList() {
		if (list==null) {
			list=new HashMap<String, TextLine>();
		}
		
		return list;
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
	

	/**getter and setter method of the merge label style*/
	public int getMergeLabelStyle() {return mergeLabelStyleOption;}
	public void setMergeLabelStyle(int mergeLabelTypeOption) {
		this.mergeLabelStyleOption = mergeLabelTypeOption;
	}
	
	public int getMergeTextOption() {return mergeTextContentOption;}
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

	public String getCustomSeparator() {
		return customSeparator;
	}

	public void setCustomSeparator(String customSeparator) {
		this.customSeparator = customSeparator;
	}
	
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
		}
		if (lin.getText().length()>20) {
			IssueLog.log("Line for channel  name is is too long: "+lin.getText());
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
