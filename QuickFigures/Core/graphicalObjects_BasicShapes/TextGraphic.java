package graphicalObjects_BasicShapes;

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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.undo.AbstractUndoableEdit;

import officeConverter.OfficeObjectConvertable;
import officeConverter.OfficeObjectMaker;
import officeConverter.TextGraphicImmitator;
import popupMenusForComplexObjects.TextGraphicMenu;
import popupMenusForComplexObjects.TextSelectionMenu;
import standardDialog.StandardDialog;
import undo.AbstractUndoableEdit2;
import undo.ProvidesDialogUndoableEdit;
import undo.UndoTextEdit;
import utilityClassesForObjects.BasicStrokedItem;
import utilityClassesForObjects.ColorDimmer;
import utilityClassesForObjects.ColorDims;
import utilityClassesForObjects.PathPointList;
import utilityClassesForObjects.RectangleEdgePosisions;
import utilityClassesForObjects.RectangleEdges;
import utilityClassesForObjects.Rotatable;
import utilityClassesForObjects.Scales;
import utilityClassesForObjects.ShapesUtil;
import utilityClassesForObjects.Snap2Rectangle;
import utilityClassesForObjects.TextItem;
import utilityClassesForObjects.TextLineSegment;
import utilityClassesForObjects.TextPrecision;
import animations.KeyFrameAnimation;
import applicationAdapters.CanvasMouseEventWrapper;
import externalToolBar.IconSet;
import externalToolBar.textGraphicTreeIcon;
import fieldReaderWritter.SVGExportable;
import fieldReaderWritter.SVGExporter;
import fieldReaderWritter.TextSVGExporter;
import graphicalObjectHandles.TextActionButtonHandleList;
import graphicalObjectHandles.HandleRect;
import graphicalObjectHandles.HasSmartHandles;
import graphicalObjectHandles.SmartHandleList;
import graphicalObjectHandles.TextHandle;
import graphicalObjects.CordinateConverter;
import graphicalObjects.HasBackGroundShapeGraphic;
import graphicalObjects.HasTextInsets;
import illustratorScripts.*;
import keyFrameAnimators.TextGraphicKeyFrameAnimator;
import layersGUI.HasTreeLeafIcon;
import logging.IssueLog;
import menuUtil.PopupMenuSupplier;
import menuUtil.HasUniquePopupMenu;
import objectDialogs.TextGraphicSwingDialog;

public class TextGraphic extends BasicGraphicalObject implements HasSmartHandles, TextItem, Scales, HasTextInsets,HasBackGroundShapeGraphic,GraphicalObject, Rotatable, ColorDims,IllustratorObjectConvertable, RectangleEdgePosisions , HasTreeLeafIcon, HasUniquePopupMenu, OfficeObjectConvertable,  SVGExportable, ProvidesDialogUndoableEdit {
	/**
	 
	 */
	
	public static String lastCopy=null;

	private boolean fillBackGround=false;
	transient boolean boundsInnitial=false;
	private transient int cursorPosition=1000;
	
	private Insets insets=null;
	private BasicStrokedItem outlineStroke=null;
	protected BasicShapeGraphic backGroundShape=null;
	

	
	String name="graphic text";
	Color strokeColor=Color.black;

	{ locationType=9;}
	
	protected int colordimming=0;
	private boolean dimColor=true;
	
	transient double width=0;
	transient double height=0;
	
	
	public void setX(int x) {this.x=x;}
	public void setY(int y) {this.y=y;}

	Font font=new Font("Arial", Font.BOLD, 12);
	String theText="Hello";
	int justtification=0;
	
	transient boolean showRectCornerHandes=false;


	private static final long serialVersionUID = 1L;
	//transient ArrayList<Rectangle> handleBoxes=null;

	transient FontMetrics fontMetrics=defaultMetrics(getFont());
	transient  BufferedImage fmImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
	
	transient Rectangle2D textDimension=new Rectangle(0,0,0,0);
	transient Polygon rotatedBounds=new Polygon();
	transient Point2D.Double[] basLine=new Point2D.Double[] {new Point2D.Double(), new Point2D.Double()};
	transient Point2D.Double[] rotatedBoundsPrecise=new Point2D.Double[] { new Point2D.Double(), new Point2D.Double(), new Point2D.Double(), new Point2D.Double()};
	transient java.awt.geom.Rectangle2D.Double wholebBounds=new Rectangle2D.Double(5,5,5,5);
	private boolean strokeOutline;
	private int lInset;
	private int tInset;
	private int rInset;
	private int bInset;
	private java.awt.geom.Point2D.Double[] unrotatedTextBoundv;
	private int highlightPosition=-1;
	private boolean editMode=false;
	private boolean userEditable=true;
	
	
	
	
	public TextGraphic() {
		// TODO Auto-generated constructor stub
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
		return new Point2D.Double(x,y);
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
		  //	FontMetrics metrics = g.getFontMetrics(getFont());
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
	        java.awt.geom.Point2D.Double[] bend=new Point2D.Double[] {new Point2D.Double(x, y), new Point2D.Double(x+width*1.1, y)};
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
public void draw(Graphics2D g, CordinateConverter<?> cords) {
	setUpBounds(g);
	
	if (this.isFillBackGround()) {
		this.getBackGroundShape().setShape(GraphicUtil.shapeFromArray(rotatedBoundsPrecise, true));
		getBackGroundShape().draw(g, cords);
		
	}
	
	
	
   g.setColor(getDimmedColor());
   if (selected &&!super.handlesHidden) try {
 	  drawHandlesAndOutline(g, cords);
 	  
 } catch (Throwable t) {IssueLog.log(t);}
   g.setColor(getDimmedColor());
	drawRotatedText(g,cords);

      
     
      

}



public void drawRotatedText(Graphics2D g, CordinateConverter<?> cords) {
	   int sx1 = (int)cords.transformX(getCenterOfRotation().getX());
	   int sy1 = (int)cords.transformY(getCenterOfRotation().getY());

    setAntialiasedText(g, true);
	   Graphics2D g2d = (Graphics2D)g;
    
	   g2d.rotate(-angle, sx1, sy1);
	   drawText(g, cords);
	   g2d.rotate(angle, sx1, sy1);
      
	
}



 
/**
public Point2D getPositionOfCharNRelativeToBounds(int n) {
	
	return null;
}*/

/**draws the given text onto a graphics 2d object*/
public void drawText(Graphics2D g, CordinateConverter<?> cords) { 
    
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
protected void drawCursor(Graphics2D g, CordinateConverter<?> cords, double x, double y, String startText, int position, Font f) {

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
protected void drawHighlight(Graphics2D g, CordinateConverter<?> cords, double x, double y, String startText, int position, int position2, Font f) {

	    double sy = cords.transformY(y);
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

public void drawHandlesAndOutline(Graphics2D g2d, CordinateConverter<?> cords) {
	g2d.setColor(getTextColor());  
	handleBoxes=new ArrayList<HandleRect>();

	  g2d.setColor(getTextColor());
	  g2d.setStroke( getGrahpicUtil().getStroke());
	  g2d.setColor(getTextColor());
	  getGrahpicUtil().setHandleFillColor(Color.white);
	  if(this.isEditMode()) {getGrahpicUtil().setHandleFillColor(Color.red);  getGrahpicUtil().setHandleSize(2);} else

	  getGrahpicUtil().drawLine(g2d, cords, getBaseLineStart(), getBaseLineEnd(),false);
	  
	  
	  getGrahpicUtil().drawLine(g2d, cords, getBaseLineStart(), getUpperLeftCornerOfBounds(),false);
	  getSmartHandleList().draw(g2d, cords);
	  if (showRectCornerHandes) {
		  
		 // getGrahpicUtil().drawHandlesAtPoints(g2d, cords,  RectangleEdges.getLocationsForHandles(this.getBounds()));
		  //handleBoxes.addAll( getGrahpicUtil().lastHandles);
		  //getGrahpicUtil().setHandleSize(3);
	  }
	  try{
		
	 }
	catch (Throwable t) {
		IssueLog.log(t);
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
protected void giveTraitsTo(TextGraphic tg) {
	tg.theText=theText;
	tg.copyAttributesFrom(this);
	tg.copyBasicTraitsFrom(this);
    tg.setFillBackGround(isFillBackGround());
	
	tg.strokeColor=strokeColor;
	if (getSnappingBehaviour()!=null)
	tg.setSnappingBehaviour(getSnappingBehaviour().copy());
	tg.map= map;
	tg.backGroundShape=this.getBackGroundShape().copy();
}
protected TextGraphic createAnother() {
	return new TextGraphic();
}

public void copyBasicTraitsFrom(BasicGraphicalObject b) {
	this.setName(b.getName());
	//this.setLocationUpperLeft(b.getLocationUpperLeft());
	this.x=b.x;//may be a problem here. commented out without checking purpose
	this.y=b.y;
	
}

@Override
public int getX() {
	return (int)x;
}

@Override
public int getY() {
	return (int)y;
}

@Override
public int getTextWidth() {
	return (int)width;
}


/**
@Override
public int handleNumber(int x, int y) {
	if (handleBoxes==null||handleBoxes.size()==1) return -1;
	for(int i=0; i<handleBoxes.size(); i++) {
		if (handleBoxes.get(i).contains(x, y)) return i;
	}
	return -1;
}*/

@Override
public double getAngle() {
	// TODO Auto-generated method stub
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
public boolean isRandians() {
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
		Point2D p2 = RectangleEdges.getRelativeLocation( new Point2D.Double(x,y), locationType, getBounds());
		return new Point2D.Double(p2.getX(), p2.getY());
	}
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
	//IssueLog.log("in location move. currently at  "+x+", "+y) ;
	x=x+xmov;
	y=y+ymov;
	//IssueLog.log("in location move. just moved to "+x+", "+y) ;
	
}

@Override
public Point2D getLocationUpperLeft() {
	return new Point2D.Double(wholebBounds.x, wholebBounds.y);
}
@Override
public void setLocationUpperLeft(double x, double y) {
	//IssueLog.log("setting upper left will set upper left locaton "+x+ ", "+y);
	
	double dx = x-wholebBounds.x;
	double dy = y-wholebBounds.y;
	//IssueLog.log("setting upper left will move text "+dx+ ", "+dy);
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
public void handleMove(int handlenum, Point p1, Point p2) {

}


@Override
public int getDimming() {
	return colordimming;
}
@Override
public void setDimming(int i) {
	colordimming=i;
	
}
@Override
public Color getDimmedColor(Color c) {
	if (isDimColor())
	return ColorDimmer.modifyColor(c, colordimming, true);
	else return getTextColor();
}

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
	BackGroundToIllustrator(aref);
	TextFrame ti = new TextFrame();
	ti.createLinePathItem(aref, getBaseLineStart(), getBaseLineEnd());
	ti.createCharAttributesRef();
	ti.setContents2(getText());
	ti.getCharAttributesRef().setfont(getFont());
	ti.getCharAttributesRef().setFillColor(getDimmedColor());
	
	
	if (getAngle()!=0) {
		
		ti.rotate(angle);
		
	}
	
	return ti;
	//return null;
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
	// TODO Auto-generated method stub
	return getBounds();
}
transient static IconSet i;//=new IconSet("icons2/TextIcon.jpg");

@Override
public Icon getTreeIcon() {
	//output=
	//return null;
	return new textGraphicTreeIcon(this.getFont(), "a");
	//return createImageIcon();
}



public static Icon createImageIcon() {
	if (i==null) i=new IconSet("iconsTree/TextTreeIcon.png");
	return i.getIcon(0);
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

/**
public Color getBackGroundColor() {
	if (this.backGroundColor==null) {backGroundColor=Color.white;}
	return backGroundColor;
}
public void setBackGroundColor(Color backGroundColor) {
	this.backGroundColor = backGroundColor;
	
}*/

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
	p2=scaleAbout(p2, p,mag,mag);
	this.setFont(this.getFont().deriveFont((float) (this.getFont().getSize2D()*mag)));
	this.setLocation(p2);
	if (this.getSnappingBehaviour()!=null) this.getSnappingBehaviour().scaleAbout(p, mag);
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
	/**handleKeyTypedEvent(e);
	if (e.getKeyCode()==KeyEvent.VK_BACK_SPACE) {
		//String st = KeyOnString(e,this.getText(),-1);
		//this.setText(st);
	}*/
	
}

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


public boolean hasHighlightRegion() {
	if (this.getHighlightPosition()<0) return false;
	if (this.getHighlightPosition()>getCursorPosition()) return false;
	return this.getHighlightPosition()!=this.getCursorPosition();
}


public void handleKeyPressEvent(KeyEvent arg0) {
	
	if(this.handleNonLetterKey(arg0)) return;
	
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
public void handleMouseEvent(CanvasMouseEventWrapper me,int handlenum, int button, int clickcount, int type,
		int... other) {
	
	if (handlenum>0) return;
	if(clickcount==2 && !editMode)editMode=true;
	else
	if(clickcount==2) this.showOptionsDialog();//super.handleMouseEvent(me, handlenum, button, clickcount, type, other);
}


/**Determines if the user is allowed to edit this text or not*/
public void setUserEditable(boolean b) {
	userEditable=b;
}

public boolean isUserEditable() {
	return userEditable;
}

protected transient TextActionButtonHandleList aList;
transient SmartHandleList smartList=null;

@Override
public SmartHandleList getSmartHandleList() {
		if (this.isEditMode()||superSelected) {
			if (aList==null) {
				aList=new TextActionButtonHandleList(this);
			}
			aList.updateLocationBasedOnParentItem();
			return SmartHandleList.combindLists(aList, this.getStandardHandles());
			
		} else
			return getStandardHandles();
}

public SmartHandleList getStandardHandles() {
	if (smartList==null) {
		smartList=new SmartHandleList ();
		smartList.add(new TextHandle(this, TextHandle.ROTATION_HANDLE));
		smartList.add(new TextHandle(this, TextHandle.TEXT_FONT_SIZE_HANDLE));
		
	}
return smartList;
}

@Override
public int handleNumber(int x, int y) {
	return getSmartHandleList().handleNumberForClickPoint(x, y);
}
@Override
public AbstractUndoableEdit2 provideUndoForDialog() {
	return new UndoTextEdit(this);
}


	
}



