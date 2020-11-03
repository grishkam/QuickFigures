package utilityClassesForObjects;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

import logging.IssueLog;
import utilityClasses1.ArraySorter;


public class TextParagraph extends ArrayList<TextLine> {

	/**
	 * 
	 */
	public static final int Justify_Left=0, Justify_Center=1, Justify_Right=2;
	private static final long serialVersionUID = 1L;
	private int justification=0;
	private TextItem parent=null;
	
	
	private HashMap<TextLine, Rectangle2D> linedims=new HashMap<TextLine, Rectangle2D>();
	Rectangle2D bounds=new Rectangle2D.Double();

	public TextLine getLastLine() {
		return this.get(size()-1);
	}
	
	public TextLineSegment addText(String s) {
		return getLastLine().addSegment(s, null, 0);
	}
	
	public  TextParagraph(TextItem parent) {
		setParent(parent);
	}
	
	@Override
	public boolean add(TextLine t) {
		t.setParent(this);
		return super.add(t); }
	
	public TextParagraph copy() {
		TextParagraph outpu = new TextParagraph(this.getParent());
		for(TextLine lin: this) {
			outpu.add(lin.copy());
		}
		outpu.setJustification(this.getJustification());
		
		return outpu ;
	}
	
	public String getText() {
		String output = "";
		boolean first=true;
		for(TextLine t: this) {
			if(!first) {output+='\n';} else first=false;
			output+=t.getText();
		}
		return output;
	}
	
	public TextLine addLine() {
	TextLine lin = new TextLine(this);
		lin.addSegment();
		this.add(lin);
		return lin;
	}
	
	public TextLine addLine(int i) {
		TextLine lin = new TextLine(this);
			lin.addSegment();
			this.add(i, lin);
			return lin;
		}
	
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
	 
	public TextLine setAllLinesToCodeString(String st,Color c) {
		this.removeAllLines();
		
		return addLineFromCodeString(st,c);
	}
	
	public void removeAllLines() {
		super.clear();
	}

	public void moveLineForward(	TextLine lin) {
		
		ArraySorter<TextLine> as = new ArraySorter<TextLine>();
		as.moveItemForward(lin, this);
	}
	
public void moveLineBackward(	TextLine lin) {
		
		ArraySorter<TextLine> as = new ArraySorter<TextLine>();
		as.moveItemBackward(lin, this);
	}


	public void moveLinesFromLeftJustification() {
		for(TextLine t: this) {
			double movx=0;
			if (getJustification()==Justify_Right) {
				movx=bounds.getWidth()-t.getLineBounds().getWidth();
			}
			
			if (getJustification()==Justify_Center) {
				movx=bounds.getWidth()-t.getLineBounds().getWidth();
				movx/=2;
			}
			t.move(movx, 0);
			
		}
	}


	public TextItem getParent() {
		if (parent==null) IssueLog.log("text paragraphic lacks parent");
		return parent;
	}

public Point2D getLocationForLine(TextLine t) {
	Rectangle2D r = linedims.get(t);
	return new Point2D.Double(r.getX(), r.getY());
}


	public void setParent(TextItem parent) {
		this.parent = parent;
	}
	
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
		
		
		FontMetrics metrics =textPrecis().getInflatedMetrics(getParent().getFont(), g);
		double fontHeight = metrics.getHeight()/inflation();
       double descent = metrics.getDescent()/inflation();
        
		bounds= getDimensionsOfStackofRects(eachLinedim);
		if (bounds==null) IssueLog.log("paragraph bounrs should never be null: check getdimensions of rects method ");
	    bounds = new  Rectangle2D.Double(x,y-fontHeight+descent, bounds.getWidth(), bounds.getHeight());
	    moveLinesFromLeftJustification();
		return bounds;
	}
	
	double inflation() {
		return textPrecis().getInflationFactor();
	}
	

	public static Rectangle2D getDimensionsOfStackofRects(ArrayList<Rectangle2D> list) {
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
		// TODO Auto-generated method stub
		return getParent().getFont();
	}

	public Color getTextColor() {
		// TODO Auto-generated method stub
		return getParent().getTextColor();
	}
	
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
		
		for(int i=Math.min(index1, index2); i<=Math.max(index1, index2); i++) {
			output.add(fullList.get(i));
		}
		
		
		
		return output;
	}
	
	
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
