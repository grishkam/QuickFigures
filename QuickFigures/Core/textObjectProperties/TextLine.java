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
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import infoStorage.BasicMetaDataHandler;
import logging.IssueLog;
import utilityClasses1.ArraySorter;

/**stores information about a line of text that is part of a textGraphic object*/
public class TextLine extends ArrayList<TextLineSegment>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	double lineWidth=0;
	private double lineHeight=0;
	private Rectangle2D linedim=new Rectangle2D.Double();
	private TextParagraph parent=null;
	public final double id=Math.random();
	public Double copyOf=null;
	double xbase=0;
	double ybase=0;
	
	/**creates a line of text in the given paragraph*/
	public TextLine(TextParagraph parent) {
		this.setParent(parent);
	}
	/**creates a line of text in the given paragraph with the given conent and text color*/
	public TextLine(TextParagraph parent, String text, Color c) {
		this.setParent(parent);
		addSegment(text,c);
	}
	
	/**returns the constituent text as a string*/
	public String getText() {
		String output = "";
		for(TextLineSegment t: this) {
			output+=t.getText();
		}
		return output;
	}
	public String toString() {return getText();}
	
	/**creates a copy of this line*/
	public TextLine copy() {
		TextLine t=new TextLine(this.getParent());
		t.copySegmentsFrom(this);
		t.copyOf=id;
		return t;
	}
	
	/**adds the given segments to this line.*/
	public ArrayList<TextLineSegment> copySegmentsFrom(ArrayList<TextLineSegment> origin) {
		ArrayList<TextLineSegment> output=new ArrayList<TextLineSegment>();
		for(TextLineSegment seg: origin) {
			if (seg==null) continue;
			TextLineSegment copy = seg.copy();
			output.add(copy);
			add(copy);
		}
		
		return output;
	}
	
	/**replaces the segments of this line with the argument.*/
public ArrayList<TextLineSegment> replaceSegmentsWith(ArrayList<TextLineSegment> origin) {
		this.clear();
		return copySegmentsFrom(origin);
	}

/**replaces the segments of this line with the argument. However does not copy the segment colors*/
//TODO: determine if the matchWithoutColor method that I added on 11/17 to fix some of the consistency issues between dialog and labels causes any bugs. will write test cases
public ArrayList<TextLineSegment> replaceSegmentsWithoutColor(ArrayList<TextLineSegment> origin) {
	if(origin.size()==size()) {
		matchWithoutColor(origin);
		return this;
	}
	
	this.clear();
	ArrayList<TextLineSegment> output=new ArrayList<TextLineSegment>();
	for(TextLineSegment seg: origin) {
		if (seg==null) continue;
		TextLineSegment copy = seg.copy();
		output.add(copy);
		copy.setTextColor(null);
		add(copy);
	}
	return output;
}

/**replaces the segments of this line with the argument. However does not copy the segment colors*/
public void matchWithoutColor(ArrayList<TextLineSegment> origin) {
	for(int i=0;i<this.size(); i++) {
		TextLineSegment seg1=origin.get(0);
		TextLineSegment localSeg1=get(0);
		seg1.makeSimilar(localSeg1);
		localSeg1.text=seg1.text;
		localSeg1.setTextColor(null);
	}
}

/**returns the font*/
	public Font getFont() {
		
		return this.getParent().getFont();
	}
	

	/**moves the fragment of text one index forward*/
	public void moveSegForward(	TextLineSegment lin) {
		
		ArraySorter<TextLineSegment> as = new ArraySorter<TextLineSegment>();
		as.moveItemForward(lin, this);
	}
	
	/**moves the fragment of text one index backward*/
	public void moveSegBackward(	TextLineSegment lin) {
		ArraySorter<TextLineSegment> as = new ArraySorter<TextLineSegment>();
		as.moveItemBackward(lin, this);
	}

	

	

	
	/**decodes certain information regarding color and subscript/superscript from a string and adds the test with properties described by that string to the text line*/
	public void addFromCodeString(String st, Color c) {
		String[] sts=new String[] {st};
		
		if (st.contains(".new")) {
			sts=st.split(".new");
		}
		
		
		for(String label: sts) {
			if (label.contains(";")) label=label.split(";")[0];
			int script=0;
			Color color=c;
			if (label.contains("Color[")) {
				 color = BasicMetaDataHandler.getColor(label);
				label=BasicMetaDataHandler.removeImpliedMethod(label, "Color");
				}
			if (label.contains("Super[]") ) { 
				label=label.replace("Super[]", "");
				script=1;
			}
			addSegment(label, color, script) ;
		
		}
		
	}
	
	/**Adds a new segment of text to the end of this line and returns it*/
	public TextLineSegment addSegment(String text, Color color) {
		return addSegment(text,color, 0);
	}
	
	/**Adds a new segment of text to the end of this line and returns it.*/
	public TextLineSegment addText(String s) {
		return addSegment(s, null, 0);
	}
	
	/**creates a next segment with a given color,
	  */
	public TextLineSegment addSegment(String text, Color color, int script) {
		TextLineSegment added = new TextLineSegment(text, color);
		added.setScript(script);
		added.setParent(this);
		added.baseLine=new Point2D.Double(xbase+linedim.getWidth(), ybase);
		this.add(added);
		return added;
	}
	
	/**Adds a new segment of text to the end of this line and returns it.*/
	public TextLineSegment addSegment() {
		TextLineSegment newSegment = new TextLineSegment("", 0);
		this.add(newSegment);
		return newSegment;
	}
	
	/**empties this line of text*/
	public void removeAllSegments() {
		this.clear();
	}
	
	/**removes any segments of text that contain an emty string*/
	public void removeAllEmptySegments() {
		ArrayList<TextLineSegment> segs=new ArrayList<TextLineSegment>();
		segs.addAll(this);
		for(TextLineSegment s: segs) {if (s.getText().equals("")) remove(s);}
		
	}
	
	/**returns the location of the given segment of text (based on the bounding box)*/
	public static Point2D getSegmentBoundsLocation(TextLineSegment t) {
		Rectangle2D r = t.bounds;//segmentdimensions.get(t);
		if (r==null) return new Point();
		return new Point2D.Double(r.getX(), r.getY());
	}
	
	/**moves the location of this text line*/
	public void move(double x, double y) {
		for(TextLineSegment t: this) {
			t.move(x, y);
		}
		xbase+=x;
		ybase+=y;
		if (linedim!=null) {
			linedim.setRect(linedim.getX()+x, linedim.getY()+y, linedim.getWidth(), linedim.getHeight());
		}
	}
	
	/**adds the segment of text to this line*/
	public boolean add(TextLineSegment t) {
		t.setParent(this);
		return super.add(t);
	}

	/**returns the parent paragraph for this line*/
	public TextParagraph  getParent() {
		return parent;
	}
	/**sets the parent paragraph for this line*/
	public void setParent(TextParagraph parent) {
		this.parent = parent;
	}
	

	
	/**when given the basline x and y, this returns the bounding box for 
	  this line of text as if it were drawn at that x and y*/
	public Rectangle2D computeLineDimensions(Graphics g, double x, double y) {
	
	
		lineWidth=0;
		setLineHeight(0);
		double xrect = x;
		/**in order to obtain double precision values for the fontheight and descent, I used a relatively inefficient method*/
		double[] both = TextPrecision.getFontHeightAndDescent(getFont(), g);
		double fontHeight = both[0];
        double descent = both[1];
        ybase=y;
        xbase=x;
		y=y-fontHeight+descent;//changes the y from baseline of the text to the corner
		
		/**adds all of the segments of text to the linWidth*/
		for(TextLineSegment t: this) try {
			if (t==null) continue;
			t.setParent(this);
				Rectangle2D r = t.computeLineDimensions(g, xrect, ybase);
				 xrect+=r.getWidth();
				 lineWidth+=r.getWidth();
				 if (r.getHeight()>getLineHeight())setLineHeight(r.getHeight());//the tallest segment determines the line height
				 continue;
		
		} catch (Throwable tt) {IssueLog.log("problem with segment of line 1");IssueLog.logT(tt);}
		
        setLineBounds(new  Rectangle2D.Double(x,y-fontHeight+descent, lineWidth, getLineHeight()));
       
        return getLineBounds();
	}



/**returns the stored line height of this line*/
public double getLineHeight() {
	return lineHeight;
}

public void setLineHeight(double lineHeight) {
	this.lineHeight = lineHeight;
}

/**returns the stored bounds of this line*/
public Rectangle2D getLineBounds() {
	return linedim;
}

private void setLineBounds(Rectangle2D linedim) {
	this.linedim = linedim;
}


public Color getTextColor() {
	return this.getParent().getTextColor();
}

public TextLineSegment getLastSegment() {
	return this.get(size()-1);
}

public TextLineSegment getFirstSegment() {
	return this.get(0);
}

/**Split the segment in this line in two at the position.
  Precondition, the segment is in this line*/
public TextLineSegment[] splitSegment(TextLineSegment thisSegment, int position) {
		String t1 = thisSegment.getText().substring(0,position);
		String t2 = thisSegment.getText().substring(position);
		thisSegment.setText(t1);
		TextLineSegment seg2 = new TextLineSegment(t2, thisSegment.getTextColor());
		add(indexOf(thisSegment)+1, seg2);
		return new TextLineSegment[] {thisSegment, seg2};
}

/**When given two adjacent segments of the line. removes on and adds its text to the 
 * one before it. Used to reverse the splitSegment method above*/
public void fuseSegments(TextLineSegment previousSeg, TextLineSegment thisSegment) {
	String textmoved = thisSegment.getText();
	remove(thisSegment);
	previousSeg .setText(previousSeg.getText()+textmoved);
	
}

/**returns the length of the line that the text is drawn on top of
 * particularly important for illustrator export*/
public double getAllBaselineLengths() {
	double output=0;
	for(TextLineSegment seg: this) {
		output+=seg.baseLineDistance();
	}
	return output;
}




}
