package utilityClassesForObjects;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import infoStorage.BasicMetaDataHandler;
import logging.IssueLog;
import utilityClasses1.ArraySorter;

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
	
	//private HashMap<TextLineSegment, Rectangle2D> segmentdimensions=new HashMap<TextLineSegment, Rectangle2D>();
	
	public String getText() {
		String output = "";
		for(TextLineSegment t: this) {
			output+=t.getText();
		}
		return output;
	}
	public String toString() {return getText();}
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
public ArrayList<TextLineSegment> replaceSegmentsWithoutColor(ArrayList<TextLineSegment> origin) {
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

	public Font getFont() {
		
		return this.getParent().getFont();
	}
	
	
	public TextLine(TextParagraph parent) {
		this.setParent(parent);
	}
	
	public void moveSegForward(	TextLineSegment lin) {
		
		ArraySorter<TextLineSegment> as = new ArraySorter<TextLineSegment>();
		as.moveItemForward(lin, this);
	}
	
public void moveSegBackward(	TextLineSegment lin) {
		
		ArraySorter<TextLineSegment> as = new ArraySorter<TextLineSegment>();
		as.moveItemBackward(lin, this);
	}

	
	public TextLine(TextParagraph parent, String text, Color c) {
		this.setParent(parent);
		addSegment(text,c);
	}
	

	
	/**decodes a string and adds the label with properties described by that string*/
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
	
	public TextLineSegment addSegment(String text, Color color) {
		return addSegment(text,color, 0);
	}
	
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
	
	public void addSegment() {
		this.add(new TextLineSegment("", 0));
	}
	
	
	
	public void removeAllSegments() {
		this.clear();
	}
	
	
	public void removeAllEmptySegments() {
		ArrayList<TextLineSegment> segs=new ArrayList<TextLineSegment>();
		segs.addAll(this);
		for(TextLineSegment s: segs) {if (s.getText().equals("")) remove(s);}
		
	}
	public Point2D getSegmentBoundsLocation(TextLineSegment t) {
		Rectangle2D r = t.bounds;//segmentdimensions.get(t);
		if (r==null) return new Point();
		return new Point2D.Double(r.getX(), r.getY());
	}
	
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
	
	
	public boolean add(TextLineSegment t) {
		t.setParent(this);
		return super.add(t);
	}

	public TextParagraph  getParent() {
		return parent;
	}

	public void setParent(TextParagraph parent) {
		this.parent = parent;
	}
	
	
//	boolean testnew=true;
	
	/**when given the basline x and y, this computes the line bounds*/
public Rectangle2D computeLineDimensions(Graphics g, double x, double y) {
	
	
	lineWidth=0;
	setLineHeight(0);
		double xrect = x;
		
		
		
		 FontMetrics metricsi0=textPrecis().getInflatedMetrics(getFont(), g);
		double fontHeight = metricsi0.getHeight()/this.inflationFactor();
        double descent = metricsi0.getDescent()/this.inflationFactor();
        ybase=y;
        xbase=x;
		y=y-fontHeight+descent;//changes the y from baseline to corner
		
		
		for(TextLineSegment t: this) try {
			if (t==null) continue;
			t.setParent(this);
			
			if (true) {
				Rectangle2D r = t.computeLineDimensions(g, xrect, ybase);
				 xrect+=r.getWidth();
				 lineWidth+=r.getWidth();
				 if (r.getHeight()>getLineHeight())setLineHeight(r.getHeight());
				 continue;
			}
			/**
			FontMetrics metrics2 = g.getFontMetrics(t.getFont());
	        Rectangle2D r=metrics2.getStringBounds(t.getText(), g);
	        FontMetrics metricsi=textPrecis().getInflatedMetrics(t.getFont(), g);
	        if (r==null) {IssueLog.log("null rectangle as string boungs");}
	        double yrect = y;
	        
	        //FontMetrics metrics2 = g.getFontMetrics(t.getFont());
			double fontHeight2 = metricsi.getHeight()/inflationFactor();
	        double descent2 = metricsi.getDescent()/inflationFactor();
	        if (t.isSubOrSuperScript()==1) yrect-=t.getFont().getSize();
	       // -fontHeight2+descent2
	        r=new Rectangle2D.Double(xrect, yrect, metricsi.stringWidth(t.getText())/inflationFactor(), r.getHeight());//changed from a setrect method
	        xrect+=r.getWidth();
	      //  metrics2.
	       
	        t.baseLine=new Point2D.Double(r.getX(), r.getY()+fontHeight2-descent2);
	       
			t.baseLineend=new Point2D.Double(r.getX()+r.getWidth()*1.1, r.getY()+fontHeight2-descent2);
	        t.bounds=r;
	        lineWidth+=r.getWidth();
	   
	      //  segmentdimensions.put(t, r);
	      
	       if (r.getHeight()>getLineHeight())setLineHeight(r.getHeight());*/
		} catch (Throwable tt) {IssueLog.log("problem with segmetn of line 1");IssueLog.log(tt);}
		
		
		
       
       
        setLineBounds(new  Rectangle2D.Double(x,y-fontHeight+descent, lineWidth, getLineHeight()));
       
        return getLineBounds();
	
	}



private double inflationFactor() {
		return textPrecis().getInflationFactor();
	}

public double getLineHeight() {
	return lineHeight;
}

public void setLineHeight(double lineHeight) {
	this.lineHeight = lineHeight;
}

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



TextPrecision textPrecis() {
	  return TextPrecision.createPrecisForFont(getFont());
}
public void fuseSegments(TextLineSegment previousSeg, TextLineSegment thisSegment) {
	String textmoved = thisSegment.getText();
	remove(thisSegment);
	previousSeg .setText(previousSeg.getText()+textmoved);
	
}



}
