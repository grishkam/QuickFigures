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
 * Date Modified: Nov 25, 2021
 * Version: 2022.1
 */
package graphicalObjects_SpecialObjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.Icon;
import popupMenusForComplexObjects.BarGraphicMenu;
import popupMenusForComplexObjects.TextGraphicMenu;
import standardDialog.StandardDialog;
import standardDialog.graphics.GraphicDisplayComponent;
import undo.AbstractUndoableEdit2;
import undo.ProvidesDialogUndoableEdit;
import undo.UndoScaleBarEdit;
import utilityClasses1.NumberUse;
import applicationAdapters.CanvasMouseEvent;
import export.pptx.BarGraphicToOffice;
import export.pptx.OfficeObjectConvertable;
import export.pptx.OfficeObjectMaker;
import export.pptx.TextGraphicImmitator;
import graphicTools.AttachedItemTool2;
import graphicalObjects.CordinateConverter;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicHolder;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_Shapes.PathGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import handles.HasSmartHandles;
import handles.ItemGlueSmartHandle;
import handles.AttachmentPositionHandle;
import handles.SmartHandle;
import handles.SmartHandleList;
import handles.SmartHandleForText;
import handles.miniToolbars.ActionButtonHandleList;
import handles.miniToolbars.ScaleBarActionHandleList;
import icons.IconSet;
import illustratorScripts.ArtLayerRef;
import layersGUI.HasTreeLeafIcon;
import locatedObject.AttachmentPosition;
import locatedObject.LocatedObject2D;
import locatedObject.PathPointList;
import locatedObject.RectangleEdgePositions;
import locatedObject.RectangleEdges;
import locatedObject.ScaleInfo;
import locatedObject.ScalededItem;
import locatedObject.Scales;
import logging.IssueLog;
import menuUtil.PopupMenuSupplier;
import menuUtil.HasUniquePopupMenu;
import objectDialogs.BarSwingGraphicDialog;

/**A graphical object that represents a scale bars
 * Each scale bar may be attached to an image panel
 * @see ImagePanelGraphic 
 * */
public class BarGraphic extends ShapeGraphic implements Scales,ScalededItem,RectangleEdgePositions, HasTreeLeafIcon, HasUniquePopupMenu, OfficeObjectConvertable,GraphicHolder, HasSmartHandles, ProvidesDialogUndoableEdit{
	

	/**
	 * 
	 */
	{name="Scale Bar"; dash=new float[]{100000000,1};super.setStrokeWidth(0); super.setFilled(true); {setFillColor(Color.black);}}
	
	private static final long serialVersionUID = 1L;

	public static final double[] reccomendedBarLengths=new double[] {0.5, 1,2,5,10, 20, 50, 100};
	public static final double[] shortBarLengths=new double[] {0.5, 1,2,5,10, 20, 50, 100};
	
	
	 ScaleInfo info=new ScaleInfo();
	 {super.setAttachmentPosition(AttachmentPosition.defaultScaleBar());}
	 private ScalededItem scaleProvider=null;
	 
	 /**transient values that are stored prior to drawing the shape*/
	 transient Rectangle2D mainBarRect=null;
	 transient Rectangle2D leftBarRect=null;
	 transient Rectangle2D rightBarRect=null;
	 transient Shape BarShape=null;
	 transient Point2D.Double oppositeEnd=null;
	 
	 private int projectionType=2;
	public static String[] projTypes=new String[] {"normal", "1 direction", "no projection"};
	public static final int ORGINARY_PROJECTIONS=0,  NO_PROJECTIONS=2,HALF_PROJECTION = 1;

	private static final int ROTATION_HANDLE = 0, TEXT_LOCATION_HANDLE2 = 14, TEXT_LOCATION_HANDLE = 12, BAR_THICKNESS_HANDLE=2, LENGTH_HANDLE=3;
	
	
	
	 private double barStroke=3;
	 private double lengthInUnits=4;
	 private double lengthInpix=0;
	 private double lengthProjection=8;
	 private boolean showText=true;
	 private boolean snapBarText=true;
	 BarTextGraphic barText=null;

	private boolean considerAngle;
	 
	public BarGraphic() {}
	 
	public BarGraphic(Color c) {
		this.setFillColor(c);
		this.setStrokeColor(c);
	}
	 
	 public String unitlengthString() {
		 String suffix=" "+ this.getScaleInfo().getUnits();
		 if (this.getScaleInfo().getUnits().equals("")) suffix="";
		 
		 String label=this.getLengthInUnits()+suffix;
			if (getLengthInUnits()%1==0) label=((int)getLengthInUnits())+suffix;
			return label;
	 }
	 
	 public BarTextGraphic getBarText() {
		 if (barText==null){
			 		barText=new BarTextGraphic();
			 		barText.setAngle(0);
			 		barText.setTextColor(getFillColor());
			 		barText.setLocationType(LOWER_LEFT);
		 			barText.setLocation(x, y);
		 			barText.setAttachmentPosition(AttachmentPosition.defaultExternal());
		 			barText.setUserEditable(false);
		 			snapTextToBar();
		 			}
		 barText.setUserEditable(false);
		 return barText;
	 }
	 
	 /**returns the pixel length based on the unit length*/
	 public double getBarWidthBasedOnUnits() {
		double cos = Math.cos(getAngle());
		double sin = Math.sin(getAngle());
		
		double pixelWidth = getDisplayScaleInfo().getPixelWidth();
		double pixelHeight = getDisplayScaleInfo().getPixelHeight();
		if(!considerAngle) {
			return getLengthInUnits()/pixelWidth;
		}
		
		double lw = getLengthInUnits()*cos/pixelWidth;
	
		double lh= getLengthInUnits()*sin/pixelHeight;
		double outputlength=Math.sqrt(lw*lw+lh*lh);
		
		return outputlength;
	 }
	 
	 /**Initializes the pixel width based on the units*/
	 public void setupPixWidth() {
		 this.setLengthInpixInnitial(getBarWidthBasedOnUnits());
	 }
	 
	
	 
	 /**returns the rectangle of unit length and width defined by the strokewidth*/
	 public Rectangle2D getBarRectangle() {
		if (mainBarRect==null) setUpBarRects();
		 return mainBarRect;
	 }
	 
	 public void setUpBarRects() {
		 double barWidth=getLengthInpix();
		 
		 /**the dimensions of the projection*/
		 double projectionwidth=getBarStroke();
		 double projectionDown=getProjectionLength();
		 
		 mainBarRect= new Rectangle2D.Double(0, 0-this.getBarStroke()/2, barWidth, getBarStroke());
		 
		 
		 double y2Projection = mainBarRect.getMinY()-getProjectionLength();
		
		
		 double projectionH = projectionDown+getBarStroke();
		 if(this.getProjectionType()==ORGINARY_PROJECTIONS) projectionH+=projectionDown;
		 
		leftBarRect= new Rectangle2D.Double(0-projectionwidth, y2Projection, projectionwidth, projectionH);
		 rightBarRect= new Rectangle2D.Double(0+barWidth,    y2Projection, projectionwidth, projectionH);
		
		
		 
		 Area a=new Area(mainBarRect);
		 if (usesProjections()) {
		 a.add(new Area( leftBarRect));
		 a.add(new Area( rightBarRect));
		 }
		 
		 AffineTransform af = AffineTransform.getRotateInstance(-this.getAngle());
		 BarShape=AffineTransform.getTranslateInstance(x, y).createTransformedShape(af.createTransformedShape(a));
		 oppositeEnd = new Point2D.Double();
		 Double oppositeEnd2 = new Point2D.Double();
		 af.transform(new Point2D.Double(barWidth, 0), oppositeEnd2);
		 
		 AffineTransform.getTranslateInstance(x, y).transform(oppositeEnd2, oppositeEnd);
	 }

	protected boolean usesProjections() {
		return this.getProjectionType()!=NO_PROJECTIONS&&getProjectionLength()>0;
	}
	 
	 public double getProjectionLength() {
		 return  lengthProjection;
	 }
	 

	public void setLengthProjection(double d) {
		this.lengthProjection = d;
		setUpBarRects() ;
	}



	@Override
	public BarGraphic copy() {
	
		BarGraphic bg = new BarGraphic();
		bg.copyAttributesFrom(this);
		bg.copyLocationFrom(this);
		bg.setStrokeColor(getStrokeColor());
		bg.setFillColor(getFillColor());
		//if (getSnappingBehaviour()!=null) bg.setSnappingBehaviour(this.getSnappingBehaviour().copy());
		return bg;
	}
	
	public void copyAttributesFrom(BarGraphic b) {
		copyAttributesButNotScale(b);
		
		this.setScaleInfo(b.getScaleInfo());
		this.setScaleProvider(b.getScaleProvider());
		if (b.getAttachmentPosition()!=null)this.setAttachmentPosition(b.getAttachmentPosition().copy());
		
		
		
		
	}
	
	public void copyAttributesButNotScale(BarGraphic b) {
		
		super.copyAttributesFrom(b);
		
		
		this.setShowText(b.isShowText());
		this.setProjectionType(b.getProjectionType());
		
		copySizeAngleFrom(b);
		
		this.setBarText(b.getBarText());
		
	}
	
	public void copySizeAngleFrom(BarGraphic b) {
		this.setBarStroke(b.getBarStroke());
		this.setLengthInUnits(b.getLengthInUnits());
		this.setLengthProjection(b.getProjectionLength());
		this.setAngle(b.getAngle());
	}
	
	public void copyLocationFrom(BarGraphic b) {
		this.setLocationType(b.getLocationType());
		this.setLocation(b.getLocation());
	}

	protected BarTextGraphic createAnother() {
		return new BarTextGraphic();
	}
	

	private void setBarText(BarTextGraphic copy) {
		copy.giveTraitsTo(getBarText());
		
	}

	@Override
	public Shape getOutline() {
		PathIterator pi = getShape().getPathIterator(new AffineTransform());
		double[] d=new double[6];
		Polygon poly = new Polygon();
		
		while (!pi.isDone()) {
			pi.currentSegment(d);
			//if (d[0]==0&& d[1]==0) {} else
			poly.addPoint((int)d[0], (int)d[1]);
			
			pi.next();
		}
		Area aa = new Area(poly);
		Area more = new Area( new BasicStroke(4).createStrokedShape(aa));
		aa.add(more);
		
		if (!showText)
		return aa;
		
		
		
		aa.add(new Area(getBarText().getOutline()));
		return aa;
	}
	

	@Override
	public Rectangle getBounds() {
		
		return  getShape().getBounds();
	}
	
	/**returns a box containing the entire shape and its bounds*/
	@Override
	public Rectangle getExtendedBounds() {
		Area a=new Area(this.getBounds());
		a.add(new Area(this.getBarText().getBounds()));
		return a.getBounds();
	}



	public double getAngleOfDragPoint(Point p2) {
		return -getAngleBetweenPoints((int)x, (int) y, p2.x, p2.y);
	}
	
	@Override
	public void moveLocation(double xmov, double ymov) {
		//if (snapBarText)this.getBarText().moveLocation(xmov, ymov);
		x=x+xmov;
		y=y+ymov;
		this.setUpBarRects();
		notifyListenersOfMoveMent();
	}
	

	@Override
	public void handleMouseEvent(CanvasMouseEvent me, int handlenum, int button, int clickcount, int type,
			int... other) {
		if (clickcount<2) return;
		if(clickcount>2) return;
		if (handlenum==1) getBarText().showOptionsDialog();
		else if (handlenum==NO_HANDLE_)showOptionsDialog();

	}

	
	public void dropColor(Color c, Point p) {
		if (this.getBarText().getOutline().contains(p)) {
			getBarText().dropColor(c, p);
		}
		else {
			this.setFillColor(c);
		}
		
	}
	
	@Override
	public void showOptionsDialog() {
		new BarSwingGraphicDialog(this).showDialog();;

	}

	@Override
	public Shape getShape() {
		if (BarShape==null) this.setUpBarRects();
		return BarShape;
	}
	
	@Override
	public ScaleInfo getScaleInfo() {
		if (getScaleProvider() !=null) return getScaleProvider().getDisplayScaleInfo();
		return info;
	}

	@Override
	public void setScaleInfo(ScaleInfo s) {
		info=s;
	}

	@Override
	public ScaleInfo getDisplayScaleInfo() {
		return getScaleInfo();
	}
	
	
	public void snapTextToBar() {
		if (getBarText().getAttachmentPosition()!=null) {
			getBarText().getAttachmentPosition().snapLocatedObjects(getBarText(), this);
		}
	}
	
	public void setupBarText() {
		this.getBarText().setText(this.unitlengthString());
	}
	
	public void draw(Graphics2D g, CordinateConverter cords) {
		
		;
		setLengthInUnits();
		if (this.isShowText()) {
			setupBarText();
			if (snapBarText)snapTextToBar();
			setupBarText();
			this.getBarText().draw(g, cords);
		}
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		//setUpBarRects();
		Shape r=this.getShape();
		 r= cords.getAffineTransform().createTransformedShape(r);
		
		 g.setColor(getFillColor());  
			g.fill(r);
			
		  drawHandesSelection(g, cords);
		  
		 
		   }
	
	public void drawHandesSelection(Graphics2D g2d, CordinateConverter cords) {
		if (this.isSelected())this.getSmartHandleList().draw(g2d, cords);
	}
	
	

	private Point2D getOppositeTipEndLocation() {
		if (oppositeEnd==null)setUpBarRects();
		if (oppositeEnd==null) IssueLog.log("Point not being set up");
		return oppositeEnd;
		
	}

	private Point2D getTipLocation() {
		return new Point2D.Double(x,y);
	}
	
	private Point2D getOppositeTipLocation() {
		 if( usesProjections()) 
			 return new Point2D.Double(x+getLengthInpix()+getBarStroke()*2,y);
			 else
				 return new Point2D.Double(x+getLengthInpix(),y);
	}

	public double getLengthInUnits() {
		return lengthInUnits;
	}


	private void setLengthInUnits() {
		setLengthInUnits(this.getLengthInUnits());
	}
	
	public void setLengthInUnits(double lengthInUnits) {
		if (lengthInUnits==0) return;
		this.lengthInUnits = lengthInUnits;
		this.setLengthInpix(this.getBarWidthBasedOnUnits());
	}



	public double getBarStroke() {
		return barStroke;
	}



	public void setBarStroke(double barStroke) {
		if(barStroke==0) return;
		this.barStroke = barStroke;
		setUpBarRects() ;
	}



	public ScalededItem getScaleProvider() {
		return scaleProvider;
	}



	public void setScaleProvider(ScalededItem scaleProvider) {
		if (scaleProvider instanceof ZoomableGraphic)
		this.scaleProvider = scaleProvider;
	}
	
	
	@Override
	public void setLocation(double x, double y) {
		//IssueLog.log("Setting bar location for "+this);
		Rectangle r=this.getBounds();
		Point2D pn = RectangleEdges.getLocation(getLocationType(), r);
		moveLocation((x-pn.getX()), (y-pn.getY()));
	//	setUpBarRects();
		//super.notifyListenersOfMoveMent();
	}
	
	public void setLocation( Point2D p) {
		this.setLocation(p.getX(), p.getY());
	}
	
	@Override
	public void setLocationUpperLeft(double x, double y) {
		Point2D pn = RectangleEdges.getLocation( RectangleEdges.UPPER_LEFT, getBounds());
		moveLocation((x-pn.getX()), (y-pn.getY()));
		//super.setLocationUpperLeft(x, (int)(y+this.getProjectionLength()/2));
	}
	
	
	@Override
	public Point2D getLocation() {
		Point2D out = RectangleEdges.getLocation(getLocationType(), getBounds());
		return out;
	}



	public boolean isShowText() {
		return showText;
	}



	public void setShowText(boolean showText) {
		this.showText = showText;
	}

	public boolean isSnapBarText() {
		return snapBarText;
	}

	public void setSnapBarText(boolean snapBarText) {
		this.snapBarText = snapBarText;
	}

	private double getLengthInpix() {
		if (lengthInpix==0) {setupPixWidth() ;}
		return lengthInpix;
	}

	private void setLengthInpix(double lengthInpix) {
		if (lengthInpix==this.getLengthInpix()) return;
		Point2D p = this.getLocation();
		this.lengthInpix = lengthInpix;
		this.setUpBarRects();
		this.setLocation(p.getX(), p.getY());
	}
	
	/**sets the length in pixels without keeping the edge fixed*/
	private void setLengthInpixInnitial(double lengthInpix) {
		this.lengthInpix = lengthInpix;
		
	}
	
	public Object toIllustrator(ArtLayerRef aref) {
		setStrokeWidth(0);//bug fix, the stroke was being set to 72 for some reason. this seemed to only affect the illustrator. As the bars stroke is initialized at 0, there must be a change made elseware that is the ultimate cause of the bug
		if (this.isShowText()) {this.getBarText().toIllustrator(aref);}
		Object ob = super.toIllustrator(aref);
		return ob;
	}
	
	transient static IconSet i;//=new IconSet("icons2/TextIcon.jpg");

	@Override
	public Icon getTreeIcon() {
		 return new GraphicDisplayComponent(createBarForIcon() );
	}
	
	public BarGraphic createBarForIcon() {
		BarGraphic bg = new BarGraphic();
		bg.copyAttributesButNotScale(this);
		bg.copyColorsFrom(this);
		bg.setLengthProjection(4);
		bg.setBarStroke(2);
		bg.setLocation(-1,2);
		bg.setLengthInUnits(10);
		ScaleInfo si = new ScaleInfo();
		si.setUnits("");
		si.scaleXY(0.8);
		//bg.setFillColor(getFillColor().darker());
		//bg.getBarText().setTextColor(this.getBarText().getTextColor().darker());
		bg.setScaleInfo(si);
		bg.getBarText().setFont(bg.getBarText().getFont().deriveFont((float)5));
		
		//bg.setShowText(false);
		bg.setupBarText();
		bg.snapTextToBar();
	if(super.isIconTooWhite()) bg.setStrokeColor(whiteIcon);
	bg.setFillColor(whiteIcon);
		return bg;
	}
	


	public static Icon createImageIcon() {
		if (i==null) i=new IconSet("iconsTree/ScaleGraphicTreeIcon.png");
		return i.getIcon(0);//new ImageIcon(i.getIcon(0));
	}

	public int getProjectionType() {
		return projectionType;
	}

	public void setProjectionType(int projectionType) {
		this.projectionType = projectionType;
	}
	
	public PopupMenuSupplier getMenuSupplier() {
		return new BarGraphicMenu(this);
	}

	@Override
	public void scaleAbout(Point2D p, double mag) {
		
		Point2D p2 = this.getLocation();
		p2=scalePointAbout(p2, p,mag,mag);
		this.setBarStroke(this.getBarStroke()*mag);
		this.setLengthProjection(this.getProjectionLength()*mag);
		ScaleInfo s2 = this.getScaleInfo().getScaledCopyXY(mag);
		this.setScaleInfo(s2);
		//if (snapBarText)snapTextToBar();
		//this.getBarText().scaleAbout(p, mag);
		boolean oSnap = this.snapBarText;
		this.snapBarText=false;
		this.setLocation(p2);
		this.snapBarText=oSnap;
		this.getAttachmentPosition().scaleAbout(p, mag);
	
	}

	@Override
	public OfficeObjectMaker getObjectMaker() {
		// TODO Auto-generated method stub
		return new BarGraphicToOffice(this);
	}

	/**returns null*/
	public double[] getDimensionsInUnits() {
		return null;
}
	
	/**decomposes this object into a text item and a shape*/
	public GraphicLayerPane getBreakdownGroup() {
		GraphicLayerPane output = new GraphicLayerPane(this.getName());
		
		output.add( this.createPathCopy());
		//output.add(this.getP);
		if (this.isShowText()) output.add(this.getBarText().copy());
		return output;
	}
	
	/**returns a pathGraphic that looks just like this */
	public PathGraphic createPathCopy() {
		PathPointList list = PathPointList.createFromIterator(getShape().getPathIterator(new AffineTransform()));
		PathGraphic oo = new PathGraphic(list);
		oo.copyColorsFrom(this);
		oo.copyAttributesFrom(this);
		oo.setName(getName());
		oo.setClosedShape(true);
		oo.setUseFilledShapeAsOutline(true);
		
		return oo;
	}
	
	static double[] barsizes=new double[] {0.5, 1, 2, 5, 10, 20};
public static void optimizeBarLengths(BarGraphic newbar, LocatedObject2D panel) {
	/**a loop to optimize the bar length. */
	
		
		for(int i2=barsizes.length-1; i2<=barsizes.length&&i2>-1;i2--) {
			newbar.setLengthInUnits(barsizes[i2]);
			if (newbar.getBounds().getWidth()<panel.getBounds().getWidth()/2) {
				break;
			}
		}
	
}

public static void optimizeBarFont(BarGraphic newbar, LocatedObject2D panel) {
	/**a loop to optimize the bar length. */
	
	double fontsize=100;
	while(fontsize>panel.getBounds().getHeight()/3)  {
		fontsize=newbar.getBarText().getFont().getSize();
		float newfontSize = (float) fontsize-2;
		if(newfontSize<2)newfontSize=2; 
		newbar.getBarText().setFont(newbar.getBarText().getFont().deriveFont(newfontSize));
		if(fontsize-2<2) break;//no point in going to a 0 font
	} 
	
}

/**When given a panel and a scale bar, alters the bars thickness so that it looks
  reasonable within the panel*/
public static void optimizeBarThickness(BarGraphic newbar, LocatedObject2D panel) {
	/**a loop to optimize the bar length. */
	
	double width=newbar.getBarStroke();
	while(width>panel.getBounds().getHeight()/8) {
		width=newbar.getBarStroke();
		newbar.setBarStroke(width-0.5);
	}
	
}

/**makes the bar smaller to fit inside of the located object b*/
public static void optimizeBar(BarGraphic bg, LocatedObject2D b) {
	BarGraphic.optimizeBarLengths(bg, b);
	BarGraphic.optimizeBarFont(bg, b);
	BarGraphic.optimizeBarThickness(bg, b);
}

/**Returns the bars' text. This method is required for the user to be able to click on the scale bar
  which is not inside in any layer */
public ArrayList<ZoomableGraphic> getAllHeldGraphics() {
	ArrayList<ZoomableGraphic> output = new ArrayList<ZoomableGraphic>();
	if (isShowText())output.add(getBarText());
	return output;
}

/**changes the length (the units) of this scale bar fits nicely into the panel*/
public void changeBarLengthToFit(ImagePanelGraphic panel) {
	if(panel==null) return;
	this.setLengthInUnits( getStandardBarLengthFor(panel));
}
/**When given an image panel, returns a scale bar length that can fit comfortable in the panel*/
public static double getStandardBarLengthFor(ImagePanelGraphic panel) {
	double[] dims = panel.getScaleInfo().convertPixelsToUnits(panel.getDimensions());
	double num = NumberUse.findNearest(dims[0]/3, BarGraphic.reccomendedBarLengths);
	return num;
}

public Rectangle getBarBounds() {
	return getBounds();
}

/**Special subclass of text item for scale bar labels*/
public class BarTextGraphic extends TextGraphic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public void setHidden(boolean hide) {
		showText=!hide;
		
	}
	
	/**does not create an object for writing into office, the object maker for the bar will create a group
	  for this instead*/
	@Override
	public OfficeObjectMaker getObjectMaker() {
		return null;
	}


	public OfficeObjectMaker getBarTextObjectMaker() {
		return new TextGraphicImmitator(this);
	}
	
	@Override
	public boolean isHidden() {
		return false;
	}
	
	@Override
	public TextGraphic copy() {
		BarTextGraphic tg = createAnother();
		giveTraitsTo(tg);
		return tg;
	}
	protected BarTextGraphic createAnother() {
		return new BarTextGraphic();
	}
	
	@Override
	public void scaleAbout(Point2D p, double mag) {
		super.scaleAbout(p, mag);
		snapTextToBar();
	}
	
	
	public PopupMenuSupplier getMenuSupplier() {
		TextGraphicMenu out = new TextGraphicMenu(this);
		out.add(new AttachmentPositionHandle(null, this, 10).createAdjustPositionMenuItem());
		return out;
	}
	
	public SmartHandleList getStandardHandles() {
		if (smartList==null) {
			smartList=new SmartHandleList ();
			smartList.add(new SmartHandleForText(this, SmartHandleForText.ROTATION_HANDLE));
			smartList.add(new SmartHandleForText(this, SmartHandleForText.TEXT_FONT_SIZE_HANDLE));
			smartList.add( createTextLocationHandle());
			smartList.add( createTextLocationHandle2());
			
		}
	return smartList;
	}
	
	public SmartHandle getLocationHandleForBarText() {
		return createTextLocationHandle2();
	}

	public boolean locationAutoMatic() {
		return isSnapBarText();
	}
	public ItemGlueSmartHandle getGlueHandle() {
		return null;
	}
	
}

class BarSmartHandle extends SmartHandle {

	private BarGraphic bar;
	private UndoScaleBarEdit undo;
	private boolean undoAdded;

	public BarSmartHandle(BarGraphic bar1, int x, int y) {
		
		this.bar=bar1;
		// TODO Auto-generated constructor stub
	}

	public BarSmartHandle(BarGraphic bar1,int rHandle) {
		this(bar1, 0,0);
		this.setHandleNumber(rHandle);
		
		if (rHandle==ROTATION_HANDLE) {
			this.setHandleColor(Color.blue);
		}
		if (rHandle==TEXT_LOCATION_HANDLE) {
			this.setHandleColor(new Color(255,000,000));
			
		}
		
		
		if (rHandle==TEXT_LOCATION_HANDLE2) {
			this.setHandleColor(new Color(200, 200, 200, 50));
			
			this.handlesize=10;
		}
	}
	
	
	public void handlePress(CanvasMouseEvent l) { 
		undo=new UndoScaleBarEdit(bar);
		undoAdded=false;
		
		if(l.clickCount()==2&&this.isBarThicknessHandle()) {
			setBarStroke(StandardDialog.getNumberFromUser("Bar Thickness", getBarStroke()));
			addUndo(l);
		}
		if(l.clickCount()==2&&this.isBarLengthHandle()) {
			setLengthInUnits(StandardDialog.getNumberFromUser("Bar Length in "+getScaleInfo().getUnits(), getLengthInUnits()));
			addUndo(l);
		}
	}
	
	void addUndo(CanvasMouseEvent lastDragOrRelMouseEvent) {
		if(undo==null) return;
		undo.establishFinalState();
		if(!undoAdded) {
			lastDragOrRelMouseEvent.addUndo(undo);
			undoAdded=true;
		}
	}
	
	public void handleDrag(CanvasMouseEvent lastDragOrRelMouseEvent) {
		
		Point p2 = lastDragOrRelMouseEvent.getCoordinatePoint();
		if (this.getHandleNumber() ==ROTATION_HANDLE) {
			double angle=getAngleOfDragPoint(p2);
			setAngle(angle);
			
		}
		if (this.getHandleNumber()==TEXT_LOCATION_HANDLE) {
	
		
			AttachedItemTool2.adjustPositionForBar((int)p2.getX(), (int)p2.getY(), getBarBounds(), getBarText());
			getBarText().setLocation(p2.x, p2.y);
			
		}
		
		if (this.getHandleNumber()==TEXT_LOCATION_HANDLE2) {
			getBarText().getAttachmentPosition().setToNearestSnap(getBarText().getBounds(), getBarBounds(), p2 );
			getBarText().setLocation(p2.x, p2.y);
			
		}
		
		if (isBarThicknessHandle()) {
			Point2D tipLocation = getOppositeTipLocation();
			int dir=getAttachmentPosition().getOffSetPolarities()[0];
			if(dir>0)tipLocation = getTipLocation();
			
			double newthick = tipLocation.distance(p2);
			
			double dx = p2.getX()-tipLocation.getX();
			double dy = p2.getY()-tipLocation.getY();
			if(newthick==getBarStroke()) return;
			setBarStroke(Math.abs(dy*Math.cos(getAngle()))+   Math.abs(dx*Math.sin(getAngle())));
			
		}
		
		if (isBarLengthHandle()) {
			int dir=getAttachmentPosition().getOffSetPolarities()[0];
			double oldLengthInUnits = getLengthInUnits();
			double oneUnit=getBarWidthBasedOnUnits()/oldLengthInUnits;
			int change=(int)(this.getCordinateLocation().distance(p2)/oneUnit);
			if(this.getCordinateLocation().getX()<p2.getX() &&dir<0) change=-change;
			if(this.getCordinateLocation().getX()>p2.getX() &&dir>0) change=-change;
			
			
			
			double newLengthInUnits = oldLengthInUnits+change;
			
			/**set of operation that jumps to round numbers if the scale bar size is large
			 * For example,user drag would set the length to 40 rather than 38 or 100 rather than 101*/
			if(change<oldLengthInUnits/200&&oldLengthInUnits>200) {
				newLengthInUnits = Math.round(newLengthInUnits/50)*50;
			}else
				if(change<oldLengthInUnits/50&&oldLengthInUnits>50) {
				newLengthInUnits = Math.round(newLengthInUnits/10)*10;
			}else
			if(change<oldLengthInUnits/20&&oldLengthInUnits>20) {
				newLengthInUnits = Math.round(newLengthInUnits/5)*5;
			}else
			if(change<oldLengthInUnits/10&&oldLengthInUnits>10) {
				newLengthInUnits = Math.round(newLengthInUnits/2.5)*2.5;
			}
			
			
			if (newLengthInUnits==0)newLengthInUnits=0.5;
			if (newLengthInUnits>0.5) newLengthInUnits=(int)newLengthInUnits;
			
			if (change<=20 && newLengthInUnits>0)
				{
				if(newLengthInUnits==oldLengthInUnits) return; else
				setLengthInUnits(newLengthInUnits);
				}
			
		}
		
		addUndo(lastDragOrRelMouseEvent);
	}

	protected boolean isBarLengthHandle() {
		return this.getHandleNumber()==LENGTH_HANDLE;
	}

	protected boolean isBarThicknessHandle() {
		return this.getHandleNumber()==BAR_THICKNESS_HANDLE;
	}

	@Override
	public boolean isHidden() {
		if (this.getHandleNumber()==TEXT_LOCATION_HANDLE &&!isShowText()) {return true;}
		if (this.getHandleNumber()==TEXT_LOCATION_HANDLE2 &&!isShowText()) {return true;}
		if (angle==0&&getHandleNumber()==ROTATION_HANDLE) {
			return true;
		}
		
		return hidden;
	}
	
	public Point2D getCordinateLocation() {
		if (this.getHandleNumber()==TEXT_LOCATION_HANDLE) {
			  Point2D textLocation =RectangleEdges.getLocation(RectangleEdges.CENTER, getBarText().getBounds());
				 return textLocation;
			
		}
		
		if (this.getHandleNumber()==TEXT_LOCATION_HANDLE2) {
			  Point2D textLocation =RectangleEdges.getLocation(RectangleEdges.CENTER, getBarText().getBounds());
			  
				 return textLocation;
			
		}
		
		if (this.getHandleNumber()==ROTATION_HANDLE) {
			 	 return getOppositeTipEndLocation();
			
		}
		
		if (isBarThicknessHandle()) {
		 	 return getBathThicknessHandleLocation();
		
	}
		if (isBarLengthHandle()) {
			this.setHandleColor(Color.GREEN.darker());
			int dir=getAttachmentPosition().getOffSetPolarities()[0];
			Point2D p = getOppositeTipEndLocation();
			if(dir<0) {
				p = getTipLocation();
			}
			
			return new Point2D.Double(p.getX()+4*dir, p.getY());
			
		}
		
		return super.getCordinateLocation();
		
	}

	protected Point2D getBathThicknessHandleLocation() {
		Point2D location = getBarBounds().getLocation();
		int dir=getAttachmentPosition().getOffSetPolarities()[0];
		if(dir>0) {
			 if( usesProjections()) {
					location =new Point2D.Double(location.getX()+getBarStroke(), location.getY()+getProjectionLength());
				 } else 
					location =new Point2D.Double(location.getX(), location.getY());
		}
		else {
		
			 if( usesProjections()) {
				location =new Point2D.Double(location.getX()+getLengthInpix()+getBarStroke()*2, location.getY()+getProjectionLength());
			 } else 
				location =new Point2D.Double(location.getX()+getLengthInpix(), location.getY());
		}
		return location;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public boolean handlesOwnUndo() {
		return true;
	}
}

transient SmartHandleList smartList=null;
transient ActionButtonHandleList aList=null;

@Override
public SmartHandleList getSmartHandleList() {
		if (smartList==null) {
			smartList=new SmartHandleList ();
			smartList.add(new BarSmartHandle(this, ROTATION_HANDLE));
			//smartList.add(createTextLocationHandle());
			//smartList.add(createTextLocationHandle2());
			
			
			smartList.add(new BarSmartHandle(this, BAR_THICKNESS_HANDLE));
			smartList.add(new BarSmartHandle(this, LENGTH_HANDLE));
		}
		return SmartHandleList.combindLists(smartList, getActionList());
	//return smartList;
}



protected BarSmartHandle createTextLocationHandle() {
	return new BarSmartHandle(this, TEXT_LOCATION_HANDLE);
}

protected BarSmartHandle createTextLocationHandle2() {
	return new BarSmartHandle(this,TEXT_LOCATION_HANDLE2);
}

@Override
public int handleNumber(double x, double y) {
	return getSmartHandleList().handleNumberForClickPoint(x, y);
}

public ActionButtonHandleList getActionList() {
	if(aList==null) {
		aList=createActionHandleList();
	}
	aList.updateLocation();
	return aList;
}

/**
Creates an action handle list for the object that looks and works like a mini toolbar
 */
public ActionButtonHandleList createActionHandleList()  {
	return new ScaleBarActionHandleList(this);
}

@Override
public AbstractUndoableEdit2 provideUndoForDialog() {
	return new UndoScaleBarEdit(this);
}

@Override
public String getShapeName() {
	return "Scale Bar";
}


/**Overrides the superclass method
 * @return
 */
public ShapeGraphic createFilledStrokeCopy() {
	return this;
}



}
