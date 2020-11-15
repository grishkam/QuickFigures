package utilityClassesForObjects;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.font.TextAttribute;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Map;

import logging.IssueLog;

public class TextLineSegment implements  Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int parentColorAlways=1, uniqueIfGiven=0, alwaysUnique=2;
	public static final int NORMAL_SCRIPT=0, Super_Script=1;
	public final double id=Math.random();
	String text="";
	TextLine parent=null;
	static Font defaultFont=new Font("Arial", 1, 20);
	static Color defaultColor=Color.white;
	private int script=NORMAL_SCRIPT;
	boolean hasUniqueColor=false;
	Color uniqueColor=new Color(0,0,0,0);
	private int lines=0;
	
	
	private int colorHandling=0;
	public Double copyOf=null;
	
	TextLineSegment copy() {
		TextLineSegment lin = colorLessCopy();
		
		lin.uniqueColor=uniqueColor;
		lin.copyOf=id;
		
	return lin;
		
	}

	public TextLineSegment colorLessCopy() {
		TextLineSegment lin = new TextLineSegment(text, uniqueColor);
		
		lin.script=script;
		lin.colorHandling=colorHandling;
		lin.hasUniqueColor=hasUniqueColor;
		lin.uniqueStyle=uniqueStyle;
		lin.lines=lines;
		return lin;
	}
	
	/**returns true if the color, font and other properties are the same 
	 * as this segment*/
	public boolean isSimilarStyle(TextLineSegment lin) {
		if 	(lin.script!=script) return false;
		if (lin.colorHandling!=colorHandling) return false;
		if (lin.hasUniqueColor!=hasUniqueColor) return false;
		if (lin.uniqueColor!=uniqueColor) return false;
		if (lin.uniqueStyle!=uniqueStyle) return false;
		if (	lin.lines!=lines) return false;
		
		return true;
	}
	
	/**the location of the baseline start of the segment*/
	public Point2D.Double baseLine;
	public Point2D.Double baseLineend;
	
	/**the bounds of the segment*/
	public Rectangle2D bounds=null;
	
	public Point2D transformedBaseLineStart;
	public Point2D transformedBaseLineEnd;
	
	/**The position of the bounding box after rotation operation*/
	public Shape transformedBounds;
	
	private double lineWidth;
	private double lineHeight;
	private double ybase;
	private double xbase;
	
	private Rectangle2D LineBounds;
	private int uniqueStyle;
	private int cursorposition;
	private int highlightPosition;
	
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

	public static Font deriveUnderlinedFont(Font f) {
		Map attributes = f.getAttributes();
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		return f.deriveFont( attributes);
	}
	
	public static Font deriveStrikedFont(Font f) {
		Map attributes = f.getAttributes();
		attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
		return f.deriveFont( attributes);
	}
	
	/**Whether or not there is a unique font stype. 0 if not uniue.
	 1 if plain, 2 bold 3 itallic, 4 both*/
	private int getUniqueStyle() {
		// TODO Auto-generated method stub
		return uniqueStyle;
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
	
	public static final int SUPER_SCRIPT=1;
	
	
	public boolean isSubscript() {return script==2;}
	public boolean isSuperscript() {return script==SUPER_SCRIPT;}
	public boolean isNormalscript() {return script==0;}
	public void makeSuperScript() {script=1;}
	public void makeSubScript() {script=2;}
	public void makeNormalScript() {script=0;}
	
	
	
	/**when given the basline x and y, this computes the line bounds*/
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
			  if (isSubOrSuperScript()==1) yrect-=getFont().getSize();
			 
			  double newwidth = metricsi.stringWidth(text)/inflationfactor();
			  
			  
			  r=new Rectangle2D.Double(xrect, yrect, newwidth, r.getHeight());
			  
			   baseLine=new Point2D.Double(r.getX(), r.getY()+fontHeight-descent);
			   
			   //multiplication by 1.25 and +4 is to make sure that the baseline extends beyond the text. there is a bug somewhere that makes this only apply to lines beyond the first 
			   baseLineend=new Point2D.Double(r.getX()+r.getWidth()*1.25+4, r.getY()+fontHeight-descent);
			  
			   bounds=r;
			   lineWidth=r.getWidth();
			   lineHeight=r.getHeight();
		return r;
		}

	private double inflationfactor() {
		return textPrecis().getInflationFactor();
	}

	protected double getLineHeight() {
		// TODO Auto-generated method stub
		return lineHeight;
	}

	

	private Rectangle2D getLineBounds() {
		return LineBounds;
	}

	public int getColorHandling() {
		return colorHandling;
	}

	public void setColorHandling(int colorHandling) {
		this.colorHandling = colorHandling;
	}

	public Color getUniqueTextColor() {
		// TODO Auto-generated method stub
		return  uniqueColor;
	}

	/**used to set if this item has a unique font style. 0 if none.
	 * 1 if plain, 2 bold est.*/
	public void setUniqueStyle(int uniqueStyle) {
		this.uniqueStyle = uniqueStyle;
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

	public void setHighlightPosition(int p) {
		this.highlightPosition=p;
		
	}

	public int getHightLightPosition() {
		return highlightPosition;
	}

	public int getLines() {
		return lines;
	}

	public void setLines(int lines) {
		this.lines = lines;
	}

	public boolean isUnderlined() {
		return lines==1;
	}
	public boolean isStrikeThrough() {
		return lines==2;
	}

	public void setStrikeThough(boolean b) {
		if (b) lines=2;
		else lines=0;
	}
	
	public void setUnderlined(boolean b) {
		if (b) lines=1;
		else lines=0;
		
	}
	
	
	
}
