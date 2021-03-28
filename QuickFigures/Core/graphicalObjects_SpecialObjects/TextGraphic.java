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
package graphicalObjects_SpecialObjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.Icon;

import popupMenusForComplexObjects.TextGraphicMenu;
import popupMenusForComplexObjects.TextSelectionMenu;
import standardDialog.StandardDialog;
import textObjectProperties.DimsColor;
import textObjectProperties.TextItem;
import textObjectProperties.TextParagraph;
import textObjectProperties.TextPrecision;
import undo.AbstractUndoableEdit2;
import undo.ProvidesDialogUndoableEdit;
import undo.UndoTextEdit;
import animations.KeyFrameAnimation;
import applicationAdapters.CanvasMouseEvent;
import export.pptx.OfficeObjectConvertable;
import export.pptx.OfficeObjectMaker;
import export.pptx.TextGraphicImmitator;
import export.svg.SVGExportable;
import export.svg.SVGExporter;
import export.svg.TextSVGExporter;
import graphicalObjects.BasicGraphicalObject;
import graphicalObjects.CordinateConverter;
import graphicalObjects_Shapes.BasicShapeGraphic;
import graphicalObjects_Shapes.GraphicUtil;
import handles.HasSmartHandles;
import handles.SmartHandleList;
import handles.SmartHandleForText;
import handles.miniToolbars.HasMiniToolBarHandles;
import handles.miniToolbars.TextActionButtonHandleList;
import icons.IconSet;
import icons.TreeIconWithText;
import illustratorScripts.*;
import keyFrameAnimators.TextGraphicKeyFrameAnimator;
import layersGUI.HasTreeLeafIcon;
import locatedObject.BasicStrokedItem;
import locatedObject.ColorDimmer;
import locatedObject.ColorDims;
import locatedObject.PathPointList;
import locatedObject.RectangleEdgePositions;
import locatedObject.RectangleEdges;
import locatedObject.Rotatable;
import locatedObject.Scales;
import locatedObject.ShapesUtil;
import locatedObject.Snap2Rectangle;
import logging.IssueLog;
import menuUtil.PopupMenuSupplier;
import menuUtil.HasUniquePopupMenu;
import objectDialogs.TextGraphicSwingDialog;

/**A graphical object that consists of text. This one displays a piece of 
 * text with a single font and color. Used for the most simple labels*/
public class TextGraphic extends BasicGraphicalObject implements HasSmartHandles,HasMiniToolBarHandles, TextItem, Scales, HasTextInsets,HasBackGroundShapeGraphic, Rotatable, ColorDims,IllustratorObjectConvertable, RectangleEdgePositions , HasTreeLeafIcon, HasUniquePopupMenu, OfficeObjectConvertable,  SVGExportable, ProvidesDialogUndoableEdit, DimsColor {
	/**
	 
	 */
	
	public static String lastCopy=null;

	private boolean fillBackGround=false;
	transient boolean boundsInnitial=false;
	
	private transient int cursorPosition=Integer.MAX_VALUE-1000;//the cursor position starts at a number greater then the length of the text
	private int highlightPosition=-1;//highlight position starts at an invalid value that indicates there is not highlight
	
	/**A couple of text items that may be drawn behind the text*/
	private BasicStrokedItem outlineStroke=null;
	protected BasicShapeGraphic backGroundShape=null;
	

	
	String name="graphic text";
	Color strokeColor=Color.black;

	{ locationType=RectangleEdgePositions.MIDDLE;}
	
	protected ColorDimmer colordimming=ColorDimmer.FULL_BRIGTHNESS;
	private boolean dimColor=true;
	
	transient double width=0;
	transient double height=0;
	
	
	public void setX(int x) {this.x=x;}
	public void setY(int y) {this.y=y;}

	Font font=new Font("Arial", Font.BOLD, 12);
	String theText="Hello";
	int justtification=TextParagraph.JUSTIFY_LEFT;
	
	transient boolean showRectCornerHandes=false;


	private static final long serialVersionUID = 1L;

	transient FontMetrics fontMetrics=defaultMetrics(getFont());
	transient  BufferedImage fmImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
	
	transient Rectangle2D textDimension=new Rectangle2D.Double(0,0,0,0);
	transient Polygon rotatedBounds=new Polygon();
	transient Point2D.Double[] basLine=new Point2D.Double[] {new Point2D.Double(), new Point2D.Double()};
	transient Point2D.Double[] rotatedBoundsPrecise=new Point2D.Double[] { new Point2D.Double(), new Point2D.Double(), new Point2D.Double(), new Point2D.Double()};
	transient java.awt.geom.Rectangle2D.Double wholebBounds=new Rectangle2D.Double(5,5,5,5);
	private boolean strokeOutline;
	
	private Insets insets=null;//an object of class insets for the text
	private int lInset;
	private int tInset;
	private int rInset;
	private int bInset;
	
	private java.awt.geom.Point2D.Double[] unrotatedTextBoundv;
	private boolean editMode=false;
	private boolean userEditable=true;

	public TextGraphic() {
		
	}
	
	
	public Rectangle2D getBoundPriorToRotation() {
		return getRotatedBoundsPolygon(unrotatedTextBoundv).getBounds2D();
	}
	
	/**Returns the path around the rotated shape*/
	public static Shape getRotatedBoundsPolygon(Point2D.Double[] rotatedBoundsPrecise) {
		PathPointList pp = new PathPointList();
		for(java.awt.geom.Point2D.Double p: rotatedBoundsPrecise)  pp.addPoint(p);
		return pp.createPath(true);
	}
	
	
	
	public TextGraphic(String description) {
		this.setText(description);
	}
	
	public TextGraphic(String description, Color c) {
		this.setText(description);
		this.setTextColor(c);
	}
	
	public TextGraphic(String description, Font font, Point2D loc) {
		this.setText(description);
		this.setFont(font);
		this.setLocation(loc);
	}
	
	
	void innitializeArrays() {
		rotatedBoundsPrecise=new Point2D.Double[] { new Point2D.Double(), new Point2D.Double(), new Point2D.Double(), new Point2D.Double()};
		basLine=new Point2D.Double[] {new Point2D.Double(), new Point2D.Double()};
		 wholebBounds=new Rectangle2D.Double(5,5,5,5);// to ensure it is not null. will be set later
			
	}
	
	void ensureBounds() {
		if (!boundsInnitial) this.setUpBounds(null);
	}
	
	public Point2D getBaseLineStart() {
		ensureBounds();
		return new Point2D.Double(basLine[0].getX(),basLine[0].getY());
	}
	public Point2D getBaseLineEnd() {
		ensureBounds();
		return new Point2D.Double(basLine[1].getX(),basLine[1].getY()) ;
	}
	
	public Point2D getUpperLeftCornerOfBounds() {
		ensureBounds();
		return rotatedBoundsPrecise[0];
	}
	
	public Point2D getCenterOfRotation() {
		return getBaseLocation();
	}
	
	public void copyAttributesFrom(TextGraphic t) {
		if (t==null) return;
		this.setFont(t.getFont());
		this.setAngle(t.getAngle());
		this.setDimming(t.getDimming());
		this.setLocationType(t.getLocationType());
		
	}
	
	/**Sets the internally stored rectangles, shapes and fontmetrics needed
	  to keep stack of the boundry of this objects*/
	 void setupFontMetricsAndBounds(Graphics g) {
		 if (fmImage==null)
			 fmImage=new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		  if (g==null)
	          g = fmImage.getGraphics();
		  setUpFontMetrics(g);
		  setUpBounds(g);
       
  }
	  
	 void setUpFontMetrics(Graphics g) {
		 if (font==null)
	          font = new Font("SansSerif", Font.PLAIN, 12);
	          fontMetrics = g.getFontMetrics(getFont());
	  }

	  /**this method determines the height and width of the text,
	     it then calls another method to determine the polygon 
	     representing the bounds*/
	  void setUpBounds(Graphics g) {
		  	FontMetrics metricsi=textPrecis().getInflatedMetrics(getFont(), g);
	        Rectangle2D r=metricsi.getStringBounds(getText(), g);
	        width=r.getWidth()/textPrecis().getInflationFactor();//;
	        height=r.getHeight()/textPrecis().getInflationFactor();;
	    
	        
	        double fontHeight = metricsi.getHeight()/textPrecis().getInflationFactor();;
	       double descent = metricsi.getDescent()/textPrecis().getInflationFactor();;
	        textDimension =new  Rectangle2D.Double(x,y-fontHeight+descent, width, height);
	       textDimension=ShapesUtil.addInsetsToRectangle((Rectangle2D.Double)textDimension, getInsets());
	        setUpOutlineFromParagraphiBounds(textDimension);
	        
	        boundsInnitial=true;
	  }
	  
	  
	  
	  /**When given a bounding box of what would be horixontal text,
	   this sets the polygon outline for the rotated text, the baseline
	   and a bounding box of everything*/
	  public void setUpOutlineFromParagraphiBounds(Rectangle2D r2) {
		  /**stores the rotated points in a double array*/
		  if (r2==null) IssueLog.log("null rectangle for bounds outline in text");
		    innitializeArrays();
	         unrotatedTextBoundv = Snap2Rectangle.RectangleVertices(r2);
	        java.awt.geom.Point2D.Double[] bend=new Point2D.Double[] {getBaseLocation(), new Point2D.Double(x+width*1.1, y)};
	        if (unrotatedTextBoundv==null) IssueLog.log("null set of vertices");
	        if (rotatedBoundsPrecise==null) IssueLog.log("null set of 4 vertices");
	        
	        AffineTransform at = createAffline();
	        if (at==null) IssueLog.log("null affline");
	        at.transform(unrotatedTextBoundv, 0, rotatedBoundsPrecise, 0, 4); 
	        at.transform(bend,  0, basLine, 0, 2);
	       // if (Gu==null) IssueLog.log("no graphic util");
	        Polygon p=getGrahpicUtil().PolygonFromRect(r2.getBounds(), at);
	      
	        rotatedBounds=p;
	        setWholeBoundsBasedOnRotated();
	        
	       
	  }
	  
	  public Point2D.Double[] rotateRect(Rectangle2D rect) {
			Point2D.Double[] rotatedBoundsPrecise=new Point2D.Double[] { new Point2D.Double(), new Point2D.Double(), new Point2D.Double(), new Point2D.Double()};
			 java.awt.geom.Point2D.Double[] rv = Snap2Rectangle.RectangleVertices(rect);
			AffineTransform at = createAffline();
			at.transform(rv, 0, rotatedBoundsPrecise, 0, 4); 
			return rotatedBoundsPrecise;
	  }
	  
	  /**set the whole bounds field to account for the inset and the rotation*/
	 protected void setWholeBoundsBasedOnRotated() {
		 wholebBounds=new Rectangle2D.Double();
		 wholebBounds.setRect(rotatedBounds.getBounds2D());
	        wholebBounds.x-=lInset;
	        wholebBounds.y-=tInset;
	        wholebBounds.width+=lInset+rInset;
	        wholebBounds.height+=tInset+bInset;
	 }
	  

	@Override
	public String getText() {
		return theText;
	}

	@Override
	public void setText(String st) {
		if (st==null) st="";
		Point2D p = this.getLocation();
		theText=st;
		setupFontMetricsAndBounds(null);	
		this.setLocation(p.getX(), p.getY());
	}

	@Override
	public Font getFont() {
		return font;
	}


	@Override
	public void setFont(Font font) {
		Point2D p = this.getLocation();
		this.font=font;
		setupFontMetricsAndBounds(null);	
		this.setLocation(p.getX(), p.getY());
	}

	@Override
	public void storeFontMetrics(FontMetrics fontMetrics) {
		this.fontMetrics=fontMetrics;	
	}
	@Override
	public FontMetrics getStoredFontMetrics() {
		return fontMetrics;
	}

	@Override
	public void cleanUpText() {	}
	
	public FontMetrics defaultMetrics(Font font) {
		return	new java.awt.Canvas().getFontMetrics(font);
	}

	/**returns the affline transform associated with the text.
	   in other words the rotation transformation*/
	public AffineTransform createAffline() {
		return AffineTransform.getRotateInstance(-angle, x, y);
	}

@Override 
public Shape getOutline() {
	setupFontMetricsAndBounds(null);
	return GraphicUtil .shapeFromArray(rotatedBoundsPrecise, true);
	//return rotatedBounds;
}

public Rectangle getBounds() {
	setupFontMetricsAndBounds(null);
	if ( wholebBounds!=null) return  wholebBounds.getBounds();
	return null;
}

public Rectangle2D getBounds2D() {
	setupFontMetricsAndBounds(null);
	return  wholebBounds;
}

@Override
public void draw(Graphics2D g, CordinateConverter cords) {
	setUpBounds(g);
	
	if (this.isFillBackGround()) {
		this.getBackGroundShape().setShape(GraphicUtil.shapeFromArray(rotatedBoundsPrecise, true));
		getBackGroundShape().draw(g, cords);
		
	}
	
	
	
   g.setColor(getDimmedColor());
   if (selected &&!super.handlesHidden) try {
 	  drawHandlesAndOutline(g, cords);
 	  
 } catch (Throwable t) {IssueLog.logT(t);}
   g.setColor(getDimmedColor());
	drawRotatedText(g,cords);

      
     
      

}


/**Draws the text onto the graphics*/
public void drawRotatedText(Graphics2D g, CordinateConverter cords) {
	   int sx1 = (int)cords.transformX(getCenterOfRotation().getX());
	   int sy1 = (int)cords.transformY(getCenterOfRotation().getY());

    setAntialiasedText(g, true);
	   Graphics2D g2d = (Graphics2D)g;
    
	   g2d.rotate(-angle, sx1, sy1);
	   drawText(g, cords);
	   g2d.rotate(angle, sx1, sy1);
      
	
}

/**draws the given text onto a graphics 2d object*/
public void drawText(Graphics2D g, CordinateConverter cords) { 
    
    double sx = cords.transformX(x);
    double sy = cords.transformY(y);
    Font font = cords.getScaledFont(getFont());
    g.setFont(font);
    g.drawString(getText(), (int)sx, (int)sy);  
    
   if (this.isSelected()&&this.isEditMode()) 
   {
	   if (getHighlightPosition()>=0&&this.getHighlightPosition()!=this.getCursorPosition()) {
		   
		   drawHighlight(g,cords,x,y, this.getText(), this.getHighlightPosition(), this.getCursorPosition(), this.getFont());
	   }
	   drawCursor(g,cords,x,y, this.getText(), this.getCursorPosition(), this.getFont());
   }
   }


/**draws the cursor*/
protected void drawCursor(Graphics2D g, CordinateConverter cords, double x, double y, String startText, int position, Font f) {

	    double sy = cords.transformY(y);
	    if(startText.length()==0) return;
	  
	    String text=startText;
	    if( position>text.length()) {position=text.length();}
		   if( position>=text.length()+1) text=text+" "; else text=startText.substring(0,  position);
	    FontMetrics metricsi=textPrecis().getInflatedMetrics(f, g);
	    Rectangle2D r=metricsi.getStringBounds(text, g);
	     double w2 = r.getWidth()/textPrecis().getInflationFactor();
	  
	   w2=  cords.transformX(x+w2);
	   
	   int sy2 = (int)cords.transformY(y-f.getSize());
	   
	    int width=(int) (2*cords.getMagnification());
	    Color oldColor = g.getColor();
	    g.setStroke(new BasicStroke(4,0,0,2));
		   g.setColor(Color.white);
		   
		   g.drawLine((int)w2, (int)sy,(int) w2, (int)sy2);
		  
		   g.drawLine((int)w2-width, (int)sy2,(int) w2+width, (int)sy2);
		   g.drawLine((int)w2-width, (int)sy,(int) w2+width, (int)sy);
	    
	    
	   g.setStroke(new BasicStroke(3,0,0,2, new float[] {4,2}, 0));
	   g.setColor(this.getDimmedColor(oldColor.darker().darker()));
	   
	   g.drawLine((int)w2, (int)sy,(int) w2, (int)sy2);
	  
	   g.drawLine((int)w2-width, (int)sy2,(int) w2+width, (int)sy2);
	   g.drawLine((int)w2-width, (int)sy,(int) w2+width, (int)sy);
	   
	 
	   
	   
}

/**draws the cursor highlight*/
protected void drawHighlight(Graphics2D g, CordinateConverter cords, double x, double y, String startText, int position, int position2, Font f) {

	   
	    if(startText.length()==0) return;
	    Color oldColor = g.getColor();
	  
	    String text=startText;
	    if( position>text.length()) {position=text.length();}
		if( position>=text.length()+1) text=text+" "; else text=startText.substring(0,  position);
	   
		 String text2=startText;
		    if( position2>text2.length()) {position2=text2.length();}
			if( position2>=text2.length()+1) text2=text2+" "; else text2=startText.substring(0,  position2);
		   
		   
		   FontMetrics metricsi=textPrecis().getInflatedMetrics(f, g);
	    
				Rectangle2D r=metricsi.getStringBounds(text, g);
			    double w2 = r.getWidth()/textPrecis().getInflationFactor();
			    w2=  cords.transformX(x+w2);
	   
			    Rectangle2D rc2=metricsi.getStringBounds(text2, g);
			    double wc2 = rc2.getWidth()/textPrecis().getInflationFactor();
			    wc2=  cords.transformX(x+wc2);
	    
	   int sy2 = (int)cords.transformY(y-f.getSize());
	   
	    int width=(int) Math.abs(w2- wc2);//(int) (2*cords.getMagnification());
	    g.setColor(new Color(10,10, 200, 125));
	    
	    g.fillRect((int)w2, (int)sy2, width, (int)( f.getSize()*cords.getMagnification()));
	    g.setColor(oldColor);
	 
	   
	   
}

/**draws the outline*/
public void drawHandlesAndOutline(Graphics2D g2d, CordinateConverter cords) {
	g2d.setColor(getTextColor());  
	
	  g2d.setColor(getTextColor());
	  g2d.setStroke( getGrahpicUtil().getStroke());
	  g2d.setColor(getTextColor());
	  getGrahpicUtil().setHandleFillColor(Color.white);
	  if(this.isEditMode()) {getGrahpicUtil().setHandleFillColor(Color.red);  getGrahpicUtil().setHandleSize(2);} else

	  getGrahpicUtil().drawLine(g2d, cords, getBaseLineStart(), getBaseLineEnd(),false);
	  
	  
	  getGrahpicUtil().drawLine(g2d, cords, getBaseLineStart(), getUpperLeftCornerOfBounds(),false);
	  getSmartHandleList().draw(g2d, cords);

	  try{
		
	 }
	catch (Throwable t) {
		IssueLog.logT(t);
	}
}



public static void setAntialiasedText(Graphics g, boolean antialiasedText) {
    Graphics2D g2d = (Graphics2D)g;
    if (antialiasedText)
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    else
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
}


@Override
public TextGraphic copy() {
	TextGraphic tg = createAnother();
	giveTraitsTo(tg);
	return tg;
}

/***/
public void giveTraitsTo(TextGraphic tg) {
	tg.theText=theText;
	tg.copyAttributesFrom(this);
	tg.copyBasicTraitsFrom(this);
    tg.setFillBackGround(isFillBackGround());
	
	tg.strokeColor=strokeColor;
	if (getAttachmentPosition()!=null)
	tg.setAttachmentPosition(getAttachmentPosition().copy());
	tg.map= map;
	tg.backGroundShape=this.getBackGroundShape().copy();
}

/**Creates another object of this same class*/
protected TextGraphic createAnother() {
	return new TextGraphic();
}

public void copyBasicTraitsFrom(TextGraphic b) {
	this.setName(b.getName());
	//this.setLocationUpperLeft(b.getLocationUpperLeft());
	this.x=b.x;//may be a problem here. commented out without checking purpose
	this.y=b.y;
	
}

public double getX() {
	return x;
}


public double getY() {
	return y;
}

@Override
public int getTextWidth() {
	return (int)width;
}



@Override
public double getAngle() {
	return angle;
}
public double getAngleInDegrees() {
	return getAngle()*180/Math.PI;
}
public void setAngleInDegrees(double angle) {
	setAngle( angle*Math.PI/180);
}
@Override
public void setAngle(double angle) {	
	Point2D p = this.getLocation();
	this.angle=angle;
	setupFontMetricsAndBounds(null);	
	this.setLocation(p.getX(), p.getY());
}

@Override
public void rotate(double angle) {
	this.angle+=angle;
}

@Override
public boolean isRadians() {
	return false;
}

@Override
public boolean isDegrees() {
	return true;
}

@Override
public Point2D getLocation() {
	if (locationType<=CENTER) {
		Point2D p2 = RectangleEdges.getLocation(locationType, getBounds());
		return new Point2D.Double(p2.getX(), p2.getY());
	}
	if (locationType>CENTER) {
		Point2D p2 = RectangleEdges.getRelativeLocation( getBaseLocation(), locationType, getBounds());
		return new Point2D.Double(p2.getX(), p2.getY());
	}
	return getBaseLocation();
}
/**
 * @return
 */
public Double getBaseLocation() {
	return new Point2D.Double(x,y);
}

@Override
public void setLocation(double x, double y) {
	if (locationType<=CENTER) {
		Point2D p2 = RectangleEdges.getLocation(locationType, getBounds2D());
		this.moveLocation(x-p2.getX(), y-p2.getY());
		return;
	}
	
	if (locationType>CENTER&&locationType<9) {
		Point2D p2 = RectangleEdges.getRelativeLocation( new Point2D.Double(x,y), locationType, getBounds2D());
		this.moveLocation(x-p2.getX(), y-p2.getY());
		return;
	}
	
	this.x=x;
	this.y=y;
	
}

public void setLocation( Point2D p) {
	setLocation(p.getX(), p.getY());
}

@Override
public void moveLocation(double xmov, double ymov) {
	x=x+xmov;
	y=y+ymov;
	
}

@Override
public Point2D getLocationUpperLeft() {
	return new Point2D.Double(wholebBounds.x, wholebBounds.y);
}
@Override
public void setLocationUpperLeft(double x, double y) {
	
	double dx = x-wholebBounds.x;
	double dy = y-wholebBounds.y;
	this.moveLocation(dx, dy);
}

@Override
public boolean isInside(Rectangle2D rect) {
	return rect.contains(getBounds());
}

@Override
public boolean doesIntersect(Rectangle2D rect) {
	return getOutline().intersects(rect);
}



@Override
public void setTextColor(Color c) {
	strokeColor=c;
}

@Override
public Color getTextColor() {
	return strokeColor;
}





@Override
public ColorDimmer getDimming() {
	return colordimming;
}
@Override
public void setDimming(ColorDimmer i) {
	colordimming=i;
	
}

/**returns a dimmed version of the color*/
@Override
public Color getDimmedColor(Color c) {
	if (isDimColor())
	return ColorDimmer.modifyColor(c, colordimming, true);
	else return getTextColor();
}

/**returns a dimmed version of the color*/
public Color getDimmedColor() {
	return getDimmedColor(getTextColor());
}



@Override
public String toString() {
	return this.getText();
}
@Override
public void showOptionsDialog() {
	getOptionsDialog().showDialog();

}

public StandardDialog getOptionsDialog() {
	TextGraphicSwingDialog dd = new TextGraphicSwingDialog(this);
	return dd;
}




public void BackGroundToIllustrator(ArtLayerRef aref) {
	if (!this.isFillBackGround()) return;
	this.getBackGroundShape().toIllustrator(aref);
	/**pathItemRef pi = new IllustratorScripts.pathItemRef();
	this.getGrahpicUtil();
	pi.createPath(aref, GraphicUtil.shapeFromArray(this.rotatedBoundsPrecise));
	pi.setFillColor(getBackGroundColor());*/
	
}
@Override
public Object toIllustrator(ArtLayerRef aref) {
	this.updateDisplay();
	BackGroundToIllustrator(aref);
	TextFrame ti = new TextFrame();
	ti.createLinePathItem(aref, getBaseLineStart(), getBaseLineEnd());
	ti.createCharAttributesRef();
	ti.setContents2(getText());
	ti.getCharAttributesRef().setfont(getFont());
	ti.getCharAttributesRef().setFillColor(getDimmedColor());
	

	
	return ti;
}

@Override
public void dropColor(Color ob, Point p) {
	
	setTextColor(ob);
	return ;
}


public int isUserLocked() {
	return 0;
}
@Override
public Rectangle getExtendedBounds() {
	return getBounds();
}
transient static IconSet i;//=new IconSet("icons2/TextIcon.jpg");

@Override
public Icon getTreeIcon() {
	return new TreeIconWithText(this.getFont(), "ab");
}



public static Icon createImageIcon() {
	return new TreeIconWithText(new Font("Arial", Font.BOLD, 10) ,"ab");
	
}

public boolean isDimColor() {
	return dimColor;
}
public void setDimColor(boolean dimColor) {
	this.dimColor = dimColor;
}

public boolean isFillBackGround() {
	return fillBackGround;
}
public void setFillBackGround(boolean fillBackGround) {
	this.fillBackGround = fillBackGround;
}

public Insets getInsets() {
	return insets;
}
public void setInsets(Insets insets) {
	this.insets = insets;
}
public BasicStrokedItem getOutlineStroke() {
	return outlineStroke;
}
public void setOutlineStroke(BasicStrokedItem outlineStroke) {
	this.outlineStroke = outlineStroke;
}
public boolean isStrokeOutline() {
	return strokeOutline;
}
public void setStrokeOutline(boolean strokeOutline) {
	this.strokeOutline = strokeOutline;
}
public BasicShapeGraphic getBackGroundShape() {
	if (backGroundShape==null) {
		backGroundShape=new  BasicShapeGraphic(this.getOutline());
		backGroundShape.setStrokeWidth(-1);
	}
	return backGroundShape;
}

public PopupMenuSupplier getMenuSupplier() {
	if(this.isEditMode()) 
		return new TextSelectionMenu(this);
	
	return new TextGraphicMenu(this);
}

@Override
public void scaleAbout(Point2D p, double mag) {
	Point2D p2 = this.getLocation();
	p2=scalePointAbout(p2, p,mag,mag);
	this.setFont(this.getFont().deriveFont((float) (this.getFont().getSize2D()*mag)));
	this.setLocation(p2);
	if (this.getAttachmentPosition()!=null) this.getAttachmentPosition().scaleAbout(p, mag);
}
@Override
public OfficeObjectMaker getObjectMaker() {
	return new TextGraphicImmitator(this);
}
@Override
public SVGExporter getSVGEXporter() {
	return new TextSVGExporter(this);
}

public void handleKeyTypedEvent(KeyEvent e) {

	
}

/**Called in the event that a keyboard shortcut */
boolean handleNonLetterKey(KeyEvent arg0) {
	boolean meta = arg0.isMetaDown();
	if (IssueLog.isWindows()) {
		meta=arg0.isControlDown();
	}
	
	if (meta &&arg0.getKeyCode()==KeyEvent.VK_Z) return true;
	if (meta &&arg0.getKeyCode()==KeyEvent.VK_Y) return true;
	//if (meta &&arg0.getKeyCode()==KeyEvent.VK_Z) return true;
	//if (meta &&arg0.getKeyCode()==KeyEvent.VK_Z) return true;
	
	
	
	/**needed so software does not mistake other keyboard shortcuts for 
	   super and subscript*/
	if (!arg0.isShiftDown()) {
		if (meta&&arg0.getKeyCode()==KeyEvent.VK_B) {setFont(embolden(font, font.isBold())); return true;}
		if (meta&&arg0.getKeyCode()==KeyEvent.VK_I) {setFont(italicize(font, font.isItalic())); return true;}
	
		if (meta&&arg0.getKeyCode()==KeyEvent.VK_PLUS) return true;
		if (meta&&arg0.getKeyCode()==KeyEvent.VK_MINUS) return true;
		if (meta&&arg0.getKeyCode()==KeyEvent.VK_EQUALS) return true;
		if (meta&&arg0.getKeyCode()==KeyEvent.VK_UNDERSCORE) return true;
	}
	
	if(arg0.getKeyCode()==KeyEvent.VK_LEFT) {this.setCursorPosition(this.getCursorPosition() - 1); setHighlightPositionToCursor();return true;}
	if(arg0.getKeyCode()==KeyEvent.VK_RIGHT) {this.setCursorPosition(this.getCursorPosition() + 1);setHighlightPositionToCursor(); return true;}
	
	return false;
}

/**returns true if part of the text is highlighted*/
public boolean hasHighlightRegion() {
	if (this.getHighlightPosition()<0) return false;
	if (this.getHighlightPosition()>getCursorPosition()) return false;
	return this.getHighlightPosition()!=this.getCursorPosition();
}

/**Called when the text is in editmode and the user types*/
public void handleKeyPressEvent(KeyEvent arg0) {
	
	if(this.handleNonLetterKey(arg0)) return;//returns if the press is not a letter
	
	if (arg0.getKeyCode()==KeyEvent.VK_BACK_SPACE || (this.hasHighlightRegion()&&arg0.getKeyCode()==KeyEvent.VK_DELETE)) {
		handleBackspaceKeyStroke();
		return;
	}
	
	boolean deletesChar=arg0.getKeyCode()==KeyEvent.VK_DELETE&&this.getText().length()>0&&getCursorPosition()<getText().length();
	
	String st = KeyOnString(arg0,this.getText(),this.getCursorPosition());
	
	if (!deletesChar)
	setCursorPosition(getCursorPosition() + st.length()-getText().length());

	if(st.equals(this.getText())) return;
	this.setText(st);
	setHighlightPositionToCursor();
	
}

public void onBackspace() {
	handleBackspaceKeyStroke();
}

protected void handleBackspaceKeyStroke() {
	String newText=this.getText();
	if (!hasHighlightRegion()) {
		newText=handleBackSpaceForString(newText, getCursorPosition());
		this.setText(newText);
		if(this.getCursorPosition()>0) this.setCursorPosition(getCursorPosition()-1);
		setHighlightPositionToCursor();
		return ;
	} else {
		for(int i=this.getCursorPosition(); i>this.getHighlightPosition(); i--) {
			newText=handleBackSpaceForString(newText, i);
		}
		this.setHighlightPositionToCursor();
		this.setText(newText);
	}
	
}

protected static String handleBackSpaceForString(String st, int cursor) {
	String newST="";
	if(cursor==1&&st.length()==1) {newST="";} 
	else
	if (cursor<st.length())newST=st.substring(0, cursor-1)+st.substring(cursor);
	else {
		if(cursor>1)
		newST=st.substring(0, cursor-1);//if cursor is in the last position
	}
	return newST;
}

public void handlePaste(String st) {
	String newText=this.getText();
	if (!hasHighlightRegion()) {
		
		this.setText(newText+st);
		
		return ;
	} else {
		for(int i=this.getCursorPosition(); i>this.getHighlightPosition(); i--) {
			newText=handlePasteForString(newText, st, i);
		}
		this.setHighlightPositionToCursor();
		this.setText(newText);
	}
	
}

protected static String handlePasteForString(String st, String newText, int cursor) {
	String newST="";
	if(cursor==1&&st.length()==1) {newST="";} 
	else
	if (cursor<st.length())newST=st.substring(0, cursor-1)+newText+st.substring(cursor);
	else {
		if(cursor>1)
		newST=st.substring(0, cursor-1)+newText;//if cursor is in the last position
	}
	return newST;
}

/**adds a key to a string based on a key event*/
static String KeyOnString(KeyEvent e, String st, int cursor) {
	if (modifierKey(e)) {return st;}
	if (ArrowKey(e)) {return st;}
	if(cursor<0||cursor>st.length()) cursor=st.length();
	char enteredKey=e.getKeyChar();
	String newST=st.substring(0, cursor)+enteredKey+st.substring(cursor);
	if (e.getKeyCode()==KeyEvent.VK_BACK_SPACE&&st.length()>0) {
		
		return handleBackSpaceForString(st, cursor);
		
	} 
	if (e.getKeyCode()==KeyEvent.VK_DELETE&&st.length()>0&&cursor<st.length()) {
		if (cursor<st.length()) {newST=st.substring(0, cursor)+st.substring(cursor+1);}
		
	} 
	
	
	
	return newST;
	
}
/**returns true if it is not a character key*/
protected static boolean modifierKey(KeyEvent e) {
	switch (e.getKeyCode()) {
	
	case KeyEvent.VK_SHIFT: return true;
	case KeyEvent.VK_ALT: return true;
	case KeyEvent.VK_META: return true;
	case KeyEvent.VK_CONTROL: return true;
	}
	
	return false;
}

/**returns true if it is not a character key*/
protected static boolean ArrowKey(KeyEvent e) {
	switch (e.getKeyCode()) {
	
	case KeyEvent.VK_DOWN: return true;
	case KeyEvent.VK_UP: return true;
	case KeyEvent.VK_LEFT: return true;
	case KeyEvent.VK_RIGHT: return true;
	}
	
	return false;
}



/**Switches between bold and nonbold fonts*/
public static Font embolden(Font font, boolean reverse) {
	
	if (reverse) {
		if (font.getStyle()==Font.BOLD) {
		font=font.deriveFont(Font.PLAIN, font.getSize());
	} 
		else if (font.getStyle()==Font.BOLD+Font.ITALIC) {
			font=font.deriveFont(Font.ITALIC, font.getSize());
		}
		
	} else {
		if (font.getStyle()==Font.PLAIN) {
		font=font.deriveFont(Font.BOLD, font.getSize());}
		if (font.getStyle()==Font.ITALIC) {
		font=font.deriveFont(Font.BOLD+Font.ITALIC, font.getSize());
	} 
		
	}
	
	
	return font;
	
	
	} 
	
	
	
	


/**Switches between bold and nonbold fonts*/
public static Font italicize(Font font, boolean reverse) {

	if (reverse)  {
				if (font.getStyle()==Font.BOLD+Font.ITALIC) {
				font=font.deriveFont(Font.BOLD, font.getSize());
					} else
				if (font.getStyle()==Font.ITALIC) {
				font=font.deriveFont(Font.PLAIN, font.getSize());
					} 
			} else 
			{
		if (font.getStyle()==Font.PLAIN) {
			font=font.deriveFont(Font.ITALIC, font.getSize());
		} else
		
		if (font.getStyle()==Font.BOLD) {
			font=font.deriveFont(Font.BOLD+Font.ITALIC, font.getSize());
		} 
	}
	
	return font;
	
}

public void setFontStyle(int style) {
	font=font.deriveFont(style);
}

public void setFontFamily(String fam) {
	if(fam==null) return;
	try {
		Font font2 = new Font(fam, font.getStyle(),font.getSize() );
		font=font2;
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
}

public int getCursorPosition() {
	if (cursorPosition>maxCursorPosition())cursorPosition=maxCursorPosition();
	if (cursorPosition<0)cursorPosition=0;
	return cursorPosition;
	//return cursorPosition;
}

protected int maxCursorPosition() {
	return getText().length();
}


public  KeyFrameAnimation getOrCreateAnimation() {
	if (animation instanceof KeyFrameAnimation) return (KeyFrameAnimation) animation;
	animation=new TextGraphicKeyFrameAnimator(this);
	return (KeyFrameAnimation) animation;
}

TextPrecision textPrecis() {
	  return TextPrecision.createPrecisForFont(getFont());
}
public void setFontSize(int j) {
	font=font.deriveFont((float) j);
}
public void setCursorPosition(int cursorPosition) {
	this.cursorPosition = cursorPosition;
	if (cursorPosition<0) cursorPosition=0;
	
}

/**sets the highlight position and cursur position, the lower of the two numbers
  will always be the highlight*/
public void setSelectedRange(int highlightPosition, int newCursorPosition) {
	
	int p1 = Math.max(highlightPosition, newCursorPosition);
	int p2 = Math.min(highlightPosition, newCursorPosition);

	this.highlightPosition=p2;
	this.setCursorPosition(p1);
	
}
public int getHighlightPosition() {
	return highlightPosition;
}


public void setHighlightPositionToCursor() {
	highlightPosition=getCursorPosition();
}

public String getSelectedText() {
	if (!this.hasHighlightRegion()) return null;
	
	String st=this.theText;
	
	int p1 = Math.max(highlightPosition, this.cursorPosition);
	int p2 = Math.min(highlightPosition, this.cursorPosition);
	if (p1==p2) return null;
	return st.substring(p2, p1);
	
}


public boolean isEditMode() {
	if (!userEditable) return false;
	return editMode;
}
public void setEditMode(boolean editMode) {
	this.editMode = editMode;
}
@Override
public void deselect() {
	super.deselect();
	editMode=false;
	this.setCursorPosition(0);
	this.setHighlightPositionToCursor();
}

@Override
public void handleMouseEvent(CanvasMouseEvent me,int handlenum, int button, int clickcount, int type,
		int... other) {
	
	if (handlenum!=NO_HANDLE_) return;
	if(clickcount==2 && !editMode) {
		editMode=true;
		
	}
	else
	if(clickcount>2) this.showOptionsDialog();//super.handleMouseEvent(me, handlenum, button, clickcount, type, other);
}


/**Determines if the user is allowed to edit this text or not*/
public void setUserEditable(boolean b) {
	userEditable=b;
}

public boolean isUserEditable() {
	return userEditable;
}

protected transient TextActionButtonHandleList aList;
protected transient SmartHandleList smartList=null;


/**creates a handle list that appears similar to a mini-toolbar*/
@Override
public TextActionButtonHandleList createActionHandleList() {
	return new TextActionButtonHandleList(this);
}

@Override
public SmartHandleList getSmartHandleList() {
		if (this.isEditMode()||superSelected) {
			if (aList==null) {
				aList=createActionHandleList();
			}
			aList.updateLocationBasedOnParentItem();
			return SmartHandleList.combindLists(aList, this.getStandardHandles());
			
		} else
			return getStandardHandles();
}

public SmartHandleList getStandardHandles() {
	if (smartList==null) {
		smartList=new SmartHandleList ();
		smartList.add(new SmartHandleForText(this, SmartHandleForText.ROTATION_HANDLE));
		smartList.add(new SmartHandleForText(this, SmartHandleForText.TEXT_FONT_SIZE_HANDLE));
		
	}
return smartList;
}

@Override
public int handleNumber(double x, double y) {
	return getSmartHandleList().handleNumberForClickPoint(x, y);
}
@Override
public AbstractUndoableEdit2 provideUndoForDialog() {
	return new UndoTextEdit(this);
}



	
}



