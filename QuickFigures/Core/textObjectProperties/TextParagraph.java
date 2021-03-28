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
 * Date Modified: Mar 28, 2021
 * Version: 2021.1
 */
package textObjectProperties;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

import logging.IssueLog;
import utilityClasses1.ArraySorter;

/**stores a list of text lines that are displayed in a text graphic*/
public class TextParagraph extends ArrayList<TextLine> {

	/**
	 * 
	 */
	public static final int JUSTIFY_LEFT=0, JUSTIFY_CENTER=1, JUSTIFY_RIGHT=2;
	private static final long serialVersionUID = 1L;
	private int justification=JUSTIFY_LEFT;
	private TextItem parent=null;
	
	
	
	private HashMap<TextLine, Rectangle2D> linedims=new HashMap<TextLine, Rectangle2D>();
	Rectangle2D bounds=new Rectangle2D.Double();
	
	/**creates a new paragraph for the given text item*/
	public  TextParagraph(TextItem parent) {
		setParent(parent);
	}

	/**Returns the last line of the paragraph*/
	public TextLine getLastLine() {
		return this.get(size()-1);
	}
	
	/**adds text to the last line of the paragraph*/
	public TextLineSegment addText(String s) {
		return getLastLine().addSegment(s, null, 0);
	}
	
	
	/**adds a new line of text to the paragraph*/
	@Override
	public boolean add(TextLine t) {
		t.setParent(this);
		return super.add(t); }
	
	/**creates a copy of the text*/
	public TextParagraph copy() {
		TextParagraph outpu = new TextParagraph(this.getParent());
		for(TextLine lin: this) {
			outpu.add(lin.copy());
		}
		outpu.setJustification(this.getJustification());
		
		return outpu ;
	}
	
	/**returns the text paragraph as a string*/
	public String getText() {
		String output = "";
		boolean first=true;
		for(TextLine t: this) {
			if(!first) {output+='\n';} else first=false;
			output+=t.getText();
		}
		return output;
	}
	
	/**adds a new line of text to the end of paragraph and returns it*/
	public TextLine addLine() {
	TextLine lin = new TextLine(this);
		lin.addSegment();
		this.add(lin);
		return lin;
	}
	
	/**adds a new line of text to the end of paragraph and returns it*/
	public TextLine addLine(String st) {
		TextLine lin = addLine();
		lin.getFirstSegment().setText(st);
		return lin;
	}
	
	/**adds a new line of text to the paragraph at index i and returns it*/
	public TextLine addLine(int i) {
		TextLine lin = new TextLine(this);
			lin.addSegment();
			this.add(i, lin);
			return lin;
		}
	
	
	/**based on the instructions within the string, creates a new line of text in the paragraph*/
	public TextLine addLineFromCodeString(String st,Color c) {
		String[] sts=st.split("///");
		TextLine lin=null;
		for(String st1:sts) {
			lin = new TextLine(this);
			lin.removeAllSegments();
			lin.addFromCodeString(st1, c);
			this.add(lin);
		}
		return lin;
		
		}
	 
	/**replaces the text with the given text. Information about spliting the text into fragments of different style
	  may be included*/
	public TextLine setAllLinesToCodeString(String st,Color c) {
		this.removeAllLines();
		
		return addLineFromCodeString(st,c);
	}
	
	/**empties the paragraph*/
	public void removeAllLines() {
		super.clear();
	}

	/**switches the line of text with the one below it*/
	public void moveLineForward(	TextLine lin) {
		ArraySorter<TextLine> as = new ArraySorter<TextLine>();
		as.moveItemForward(lin, this);
	}
	
	/**switches the line of text with the one above it*/
	public void moveLineBackward(	TextLine lin) {
			
		ArraySorter<TextLine> as = new ArraySorter<TextLine>();
		as.moveItemBackward(lin, this);
	}

/**changes the locations of each line to comply with either right or center justification
  assumes that the baseline locations are set to their default (left justification)*/
	public void moveLinesFromLeftJustification() {
		for(TextLine t: this) {
			double movx=0;
			if (getJustification()==JUSTIFY_RIGHT) {
				movx=bounds.getWidth()-t.getLineBounds().getWidth();
			}
			
			if (getJustification()==JUSTIFY_CENTER) {
				movx=bounds.getWidth()-t.getLineBounds().getWidth();
				movx/=2;
			}
			t.move(movx, 0);
			
		}
	}

/**returns the text item that contains this paragraph*/
	public TextItem getParent() {
		if (parent==null) IssueLog.log("text paragraphic lacks parent");
		return parent;
	}
	/**sets the text item that contains the paragraph*/
	public void setParent(TextItem parent) {
		this.parent = parent;
	}

	/**returns the upper left hand corner of the text line*/
	public Point2D getLocationForLine(TextLine t) {
		Rectangle2D r = linedims.get(t);
		return new Point2D.Double(r.getX(), r.getY());
	}


	/**returns a bounding box that contains all of the lines of text*/
	public Rectangle2D getDimensionsForAllLines(Graphics g, double x, double y) {
		ArrayList<Rectangle2D> eachLinedim=new ArrayList<Rectangle2D>();
		double dy=y;
		
		for(TextLine line1: this) {
			if (line1==null) continue;
			Rectangle2D rect = line1.computeLineDimensions(g, x, dy);//getLineDimensions(line1,g,x,y);
			if (rect==null) continue;
			linedims.put(line1, rect);
			eachLinedim.add(rect);
			dy+=rect.getHeight();
		}
		
		
		double[] metrics =TextPrecision.getFontHeightAndDescent(getParent().getFont(), g);
		double fontHeight = metrics[0];
       double descent = metrics[1];
        
		bounds= getDimensionsOfStackofRects(eachLinedim);
		if (bounds==null) IssueLog.log("paragraph bounrs should never be null: check getdimensions of rects method ");
	    bounds = new  Rectangle2D.Double(x,y-fontHeight+descent, bounds.getWidth(), bounds.getHeight());
	    moveLinesFromLeftJustification();
		return bounds;
	}
	
	
	
/**when given a list of rectangles, adds up the heights and uses the maziumun with to creates new bounding box */
	private static Rectangle2D getDimensionsOfStackofRects(ArrayList<Rectangle2D> list) {
		if (list.size()==1) return list.get(0);
		
		double x=Double.MAX_VALUE;
		double y=Double.MAX_VALUE;
		double maxWidth=0;
		double totalHeight=0;
		for (Rectangle2D rect: list) {
			if (rect.getWidth()>maxWidth) maxWidth=rect.getWidth();
			totalHeight+=rect.getHeight();
			if (rect.getX()<x)x=rect.getX();
			if (rect.getX()<y)y=rect.getY();
		}
		return new Rectangle2D.Double(x, y, maxWidth, totalHeight);
	}

	
	public int getJustification() {
		return justification;
	}

	public void setJustification(int justification) {
		this.justification = justification;
	}

	public Font getFont() {
		return getParent().getFont();
	}

	public Color getTextColor() {
		return getParent().getTextColor();
	}
	
	/**returns an array containing all segments of text in the paragraph*/
	public ArrayList<TextLineSegment> getAllSegments() {
		ArrayList<TextLineSegment> output = new ArrayList<TextLineSegment>();
		for(TextLine l: this) {
			if(l!=null)
			for(TextLineSegment t: l) {
				if(t!=null)output.add(t);
			}
		}
		
		return output;
	}
	
	/**returns all the segments from one to the other*/
	public ArrayList<TextLineSegment> getAllSegmentsInRange(TextLineSegment p1, TextLineSegment p2) {
		ArrayList<TextLineSegment> output = new ArrayList<TextLineSegment>();
		ArrayList<TextLineSegment> fullList = getAllSegments();
		
		int index1=fullList.indexOf(p1);
		int index2=fullList.indexOf(p2);
		if(index1<0||index2<0) return output;//fixes a bug that occurs if  one is missing
		for(int i=Math.min(index1, index2); i<=Math.max(index1, index2); i++) {
			output.add(fullList.get(i));
		}
		
		
		
		return output;
	}
	
	/**returns the line that contains the given segment of text*/
	public TextLine getLineWithSegment(TextLineSegment t) {
		for(TextLine l: this) {
			if(l.contains(t))return l;
		}
		return null;
	}
	
	/**Split the line so that all segments after ths position are on the next line.
	  Precondition, the segment is in this line*/
	public TextLine[] splitLine(TextLine thisLine, int position) {
			int index = this.indexOf(thisLine);//the segmentIndex
			this.addLine(index+1);
			TextLine lin2 = this.get(index+1);
			for(int i=thisLine.size()-1; i>position; i--) {
				TextLineSegment seg = thisLine.get(i);
				thisLine.remove(seg);
				lin2.add(0,seg);
			}
			return new TextLine[]{thisLine, lin2};
	}

	
	TextPrecision textPrecis() {
		  return TextPrecision.createPrecisForFont(getFont());
	}
}
