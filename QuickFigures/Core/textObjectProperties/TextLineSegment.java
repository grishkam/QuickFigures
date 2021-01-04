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
 * Version: 2021.1
 */
package textObjectProperties;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.font.TextAttribute;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.HashMap;

import logging.IssueLog;

/**stores the properties of a fragment of text that is part of a
 TextGraphic object*/
public class TextLineSegment implements  Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int parentColorAlways=1, uniqueIfGiven=0, alwaysUnique=2;
	public static final int NORMAL_SCRIPT=0, SUPER_SCRIPT=1, SUB_SCRIPT=2;
	public static final int NO_LINE=0, UNDERLINE=1, STRIKETHROUGH=2;
	public final double id=Math.random();
	String text="";
	TextLine parent=null;
	static Font defaultFont=new Font("Arial", 1, 20);
	static Color defaultColor=Color.white;
	private int script=NORMAL_SCRIPT;
	boolean hasUniqueColor=false;
	Color uniqueColor=new Color(0,0,0,0);
	private int lines=NO_LINE;
	
	
	public Double copyOf=null;
	
	
	/**the location of the baseline start of the segment*/
	public Point2D.Double baseLine;
	public Point2D.Double baseLineend;
	
	/**the bounds of the segment*/
	public Rectangle2D bounds=null;
	
	/**the location of a rotated baseline start of the segment*/
	public Point2D transformedBaseLineStart;
	public Point2D transformedBaseLineEnd;
	
	/**The position of the bounding box after rotation operation*/
	public Shape transformedBounds;
	
	double lineWidth;
	double lineHeight;
	double ybase;
	double xbase;
	
	Rectangle2D LineBounds;
	private int uniqueStyle;
	private int cursorposition;
	private int highlightPosition;
	
	/**returns a copy. and stores an indication that the new segment is s copy */
	TextLineSegment copy() {
		TextLineSegment lin = simpleCopy();
		
		lin.uniqueColor=uniqueColor;
		lin.copyOf=id;
		
	return lin;
		
	}

	/**returns a copy.  */
	private TextLineSegment simpleCopy() {
		TextLineSegment lin = new TextLineSegment(text, uniqueColor);
		makeSimilar(lin);
		return lin;
	}

	/**
	 edits the target to make it similar to this
	 */
	public void makeSimilar(TextLineSegment lin) {
		lin.text=text;
		lin.script=script;
		
		lin.hasUniqueColor=hasUniqueColor;
		lin.uniqueStyle=uniqueStyle;
		lin.lines=lines;
	}
	
	/**returns true if the color, font and other properties are the same 
	 * as this segment*/
	public boolean isSimilarStyle(TextLineSegment lin) {
		if 	(lin.script!=script) return false;
		
		if (lin.hasUniqueColor!=hasUniqueColor) return false;
		if (lin.uniqueColor!=uniqueColor) return false;
		if (lin.uniqueStyle!=uniqueStyle) return false;
		if (	lin.lines!=lines) return false;
		
		return true;
	}
	

	
	public void boldUnbold() {
		if (getFont().isPlain()) {
			this.setUniqueStyle(Font.BOLD);
			} else if (getFont().isItalic()) {
				this.setUniqueStyle(Font.BOLD+Font.ITALIC);
			} else {this.setUniqueStyle(Font.PLAIN);}
	}
	
	public void move(double x, double y) {
		if (baseLine!=null) {
			baseLine.setLocation(baseLine.x+x, baseLine.y+y);
		}
		
		if (bounds!=null) {
			bounds.setRect(
					new Rectangle2D.Double(bounds.getX()+x, bounds.getY()+y, bounds.getWidth(), bounds.getHeight())
					);
		}
		
		
		
	}
	
	public TextLineSegment(String text, int script) {
		this.text=text;
		this.setScript(script);
	}
	
	public TextLineSegment(String text, Color c) {
		this.text=text;
		this.setTextColor(c);
	}
	
	
	public String getText() {
		return text;
	}

	
	public void setText(String st) {
		text=st;
	}

	
	public Font getFont() {
		Font output=defaultFont;
		if (parent==null) return output;
		output=parent.getFont();
		if (getScript()>0) output= parent.getFont().deriveFont((float) (parent.getFont().getSize()/2));
		if (this.getUniqueStyle()>0) output=output.deriveFont(getUniqueStyle()-1);
		if (isUnderlined()) {
			output=deriveUnderlinedFont(output);
		}
		if (isStrikeThrough()) {
			output= deriveStrikedFont(output);
		}
		
		return output;
	}

	/**derives an underlined version of the font*/
	public static Font deriveUnderlinedFont(Font f) {
		HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		return f.deriveFont( attributes);
	}
	
	/**derives a strike-through version of the font*/
	public static Font deriveStrikedFont(Font f) {
		HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
		attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
		return f.deriveFont( attributes);
	}
	
	/**Whether or not there is a unique font stype. 0 if not unique.
	 1 if plain, 2 bold 3 italic, 4 both*/
	private int getUniqueStyle() {
		return uniqueStyle;
	}
	/**used to set if this item has a unique font style. 0 if none.
	 * 1 if plain, 2 bold, 3 if both.*/
	public void setUniqueStyle(int uniqueStyle) {
		this.uniqueStyle = uniqueStyle;
	}

	public void setFont(Font font) {
			this.setUniqueStyle(font.getStyle()+1);
	}

	
	public Color getTextColor() {
		if (usesUniqueColor()) return uniqueColor;
		if (parent!=null) return parent.getTextColor();
		return defaultColor;
	}

	
	public void setTextColor(Color c) {
		if (c==null||c.getAlpha()==0) {
			
			hasUniqueColor=false;
			uniqueColor=c;
			return;
		}
		
		if (getParent()!=null &&c.equals(this.getParent().getTextColor())) {
			hasUniqueColor=false;
			uniqueColor=c;
			return;
		}
		
		hasUniqueColor=true;
		uniqueColor=c;		
	}
	
	public Boolean usesUniqueColor() {
		if (uniqueColor!=null&&hasUniqueColor&&uniqueColor.getAlpha()!=0) return true;
		return false;
	}

	
	public void setParent(TextLine ti) {
		parent=ti;
		
	}

	
	public TextLine getParent() {
		return parent;
	}

	
	public int isSubOrSuperScript() {
		return getScript();
	}
	

	public int getScript() {
		return script;
	}

	public void setScript(int script) {
		this.script = script;
	}
	
	
	
	public boolean isSubscript() {return script==SUB_SCRIPT;}
	public boolean isSuperscript() {return script==SUPER_SCRIPT;}
	public boolean isNormalscript() {return script==NORMAL_SCRIPT;}
	public void makeSuperScript() {script=SUPER_SCRIPT;}
	public void makeSubScript() {script=SUB_SCRIPT;}
	public void makeNormalScript() {script=NORMAL_SCRIPT;}
	
	
	
	/**when given the basline x and y, this computes the line  bounds*/
	public Rectangle2D computeLineDimensions(Graphics g, double x, double y) {	
		lineWidth=0;
		lineHeight=0;
			double xrect = x;
			 double yrect = y;
			 ybase=y;
		     xbase=x;
		     String text=getText();
		     if (text==null) {
		    	 IssueLog.log("null text");
		    	 text="";
		     }
			 FontMetrics metricsi=textPrecis().getInflatedMetrics(getFont(), g);
			FontMetrics metrics = g.getFontMetrics(getFont());
			 Rectangle2D r=metrics.getStringBounds(text, g);
			double fontHeight = metricsi.getHeight()/this.inflationfactor();
	        double descent = metricsi.getDescent()/this.inflationfactor();
	       
			yrect=y-fontHeight+descent;//changes the y from baseline to corner
			  if (this.isSuperscript()) //TODO: check the 
				  yrect-=getFont().getSize();
			 
			  double newwidth = metricsi.stringWidth(text)/inflationfactor();
			  
			  
			  r=new Rectangle2D.Double(xrect, yrect, newwidth, r.getHeight());
			  
			   baseLine=new Point2D.Double(r.getX(), r.getY()+fontHeight-descent);
			   
			   //multiplication by 1.25 and +4 is done to make sure that the baseline extends beyond the text. TODO: fix bug. there is a bug somewhere that makes this only apply to lines beyond the first 
			   baseLineend=new Point2D.Double(r.getX()+r.getWidth()*1.25+4, r.getY()+fontHeight-descent);
			  
			   bounds=r;
			   lineWidth=r.getWidth();
			   lineHeight=r.getHeight();
		return r;
		}



	protected double getLineHeight() {
		return lineHeight;
	}

	public Color getUniqueTextColor() {
		return  uniqueColor;
	}

	

	public void setCursorPosition(int p) {
		setCursorposition(p);
		
	}

	public int getCursorposition() {
		return cursorposition;
	}

	public void setCursorposition(int cursorposition) {
		this.cursorposition = cursorposition;
	}


	TextPrecision textPrecis() {
		  return TextPrecision.createPrecisForFont(getFont());
	}
	private double inflationfactor() {
		return textPrecis().getInflationFactor();
	}

	public void setHighlightPosition(int p) {
		this.highlightPosition=p;
		
	}

	public int getHightLightPosition() {
		return highlightPosition;
	}

	/**returns an int  that indicates whether the text has an underline or a strike through*/
	public int getLines() {
		return lines;
	}


	/**sets a stored value that indicates whether the text has an underline or a strike through*/
	public void setLines(int lines) {
		this.lines = lines;
	}

	public boolean isUnderlined() {
		return lines==UNDERLINE;
	}
	public boolean isStrikeThrough() {
		return lines==STRIKETHROUGH;
	}

	public void setStrikeThough(boolean b) {
		if (b) lines=STRIKETHROUGH;
		else lines=NO_LINE;
	}
	
	public void setUnderlined(boolean b) {
		if (b) lines=UNDERLINE;
		else lines=NO_LINE;
		
	}
	
	
	
}
