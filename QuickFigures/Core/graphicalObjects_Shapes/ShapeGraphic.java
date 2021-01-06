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
 * Date Modified: Jan 5, 2021
 * Version: 2021.1
 */
package graphicalObjects_Shapes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Icon;
import popupMenusForComplexObjects.ShapeGraphicMenu;
import standardDialog.graphics.GraphicDisplayComponent;
import undo.AbstractUndoableEdit2;
import undo.ColorEditUndo;
import undo.CombinedEdit;
import undo.ProvidesDialogUndoableEdit;
import undo.UndoScalingAndRotation;
import undo.UndoStrokeEdit;
import animations.KeyFrameAnimation;
import export.pptx.OfficeObjectConvertable;
import export.pptx.OfficeObjectMaker;
import export.pptx.PathGraphicToOffice;
import export.svg.SVGEXporterForShape;
import export.svg.SVGExportable;
import export.svg.SVGExporter;
import graphicalObjects.BasicGraphicalObject;
import graphicalObjects.CordinateConverter;
import handles.miniToolbars.ActionButtonHandleList;
import handles.miniToolbars.HasMiniToolBarHandles;
import handles.miniToolbars.ShapeActionButtonHandleList2;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.IllustratorObjectConvertable;
import illustratorScripts.PathItemRef;
import keyFrameAnimators.ShapeGraphicKeyFrameAnimator;
import layersGUI.HasTreeLeafIcon;
import locatedObject.BasicStrokedItem;
import locatedObject.DefaultPaintProvider;
import locatedObject.Fillable;
import locatedObject.PaintProvider;
import locatedObject.PathPointList;
import locatedObject.RectangleEdges;
import locatedObject.Rotatable;
import locatedObject.ShowsOptionsDialog;
import locatedObject.StrokedItem;
import logging.IssueLog;
import menuUtil.HasUniquePopupMenu;
import menuUtil.PopupMenuSupplier;
import objectDialogs.ShapeGraphicOptionsSwingDialog;

/**An abstract class for the shapes that are drawn and can be
 * maniputed by the user*/
public abstract class ShapeGraphic extends BasicGraphicalObject implements  StrokedItem, ShowsOptionsDialog,IllustratorObjectConvertable, Fillable, Rotatable, HasTreeLeafIcon , OfficeObjectConvertable, HasUniquePopupMenu, SVGExportable, ProvidesDialogUndoableEdit, HasMiniToolBarHandles {
	


	
	private static final int DEFAULT_STROKE_WIDTH = 1;

	/**
	 * 
	 */
	
	{name="Shape ";}
	
	protected static final float[] NEARLY_DASHLESS = new float[]{100000,1}, DASHLESS = new float[]{};
	protected static final int THICK_STROKE_4 = 4;
	private static final int DEFAULT_MITER_LIMIT = 8;
	protected static final Color whiteIcon = new Color(240,240,240),//a nearly but not 100% white that will be used in icons
				TRANSPARENT_FILL = new Color(0,0,0,0);

	
	private static final long serialVersionUID = 1L;
	
	

	
	
	/**Is the shape closed of open*/
	private boolean closedShape=false;
	/**set to true if antialias rendering hint used*/
	private boolean antialize=true;
	
	PaintProvider fillPaintProvider=null;
	PaintProvider strokePaintProvider=null;
	
	public HashMap<String, Object> map=new HashMap<String, Object>();

	
	boolean filled=true; {setFillColor(TRANSPARENT_FILL);}//starts with transparent fill color
	
	protected float strokeWidth=DEFAULT_STROKE_WIDTH;
	protected float[] dash=DASHLESS;
	
	int end=BasicStroke.CAP_BUTT;
	int join=BasicStroke.JOIN_ROUND;
	float miterLimit=DEFAULT_MITER_LIMIT;
	private boolean hasCloseOption=false;//set to true if the shape can be set to either open or close (used for 1 subclass)
	
	
	public void setMiterLimit(double miter) {
		 miterLimit=(float)miter;
	}
	public double getMiterLimit() {
		return  miterLimit;
	}

	/**getter and setter method for the dashes*/
	public float[] getDashes() {return dash;}
	public void setDashes(float[] dash) {this.dash=dash;}

	
	/**changes the angle and stroke properties of this object to 
	 * match the shape given*/
	public void copyAttributesFrom(ShapeGraphic source) {
		
		this.setAngle(source.getAngle());
		 copyStrokeFrom(source);
		
		this.setLocationType(source.getLocationType());
		this.setAntialize(source.isAntialize());
	
	}
	
	/**changes the stroke properties of this object to 
	 *match the shape given*/
	public void copyStrokeFrom(StrokedItem source) {
		this.setDashes(source.getDashes().clone());
		this.setStrokeWidth(source.getStrokeWidth());
		this.end=source.getStrokeCap();
		this.join=source.getStrokeJoin();
	}
	/**changes the colors of this object to 
	 *match the shape given*/
	public void copyColorsFrom(ShapeGraphic source) {
		this.setFillColor(source.getFillColor());
		this.setFilled(source.isFilled());
		this.setStrokeColor(source.getStrokeColor());
	}
	
	
	
	@Override
	public float getStrokeWidth() {
		return strokeWidth;
	}

	@Override
	public void setStrokeColor(Color c) {
		this.getStrokePaintProvider().setColor( c);
		
	}

	
	@Override
	public void setFillColor(Color c) {
		this.getFillPaintProvider().setColor(c);
		
	}
	@Override
	public Color getFillColor() {
		return this.getFillPaintProvider().getColor();
	}
	
	@Override
	public Color getStrokeColor() {
		return this.getStrokePaintProvider().getColor();
	}
	
	@Override
	public boolean isFilled() {
		return filled;
	}
	@Override
	public void setFilled(boolean fill) {
		filled=fill;
		
	}

	/**When given a color and a location, this methods sets either the fill or the stroke color
	  depending on how close to the center of the object the user clicked*/
	public void dropColor(Color c, Point p) {
		double closetocenter=p.distance(getCenter());
		
			if (closetocenter<getBounds().getWidth()/4)
				this.setFillColor(c);
			else
			this.setStrokeColor(c);
		
	}
	
	public void setStrokeWidth(float strokeWidth) {
		if(strokeWidth>250) {
			IssueLog.log("Attmpted to set a strange stroke width for a shape "+strokeWidth);
			return;
		}
		this.strokeWidth = strokeWidth;
	}
	
	/**Returns the center of the object*/
	public Point getCenter() {
		return new Point((int)getBounds().getCenterX(), (int)getBounds().getCenterY());
	}

	/**returns an instance of class BasicStroke used to draw the object*/
	@Override
	public BasicStroke getStroke() {
		float width = getStrokeWidth();
		float limit = (float) getMiterLimit();
		float[] d = this.getDashes();
		if (limit<1) limit=1;
		if (width<0) width=0;
		
		if (!isDashVaid())
			return new BasicStroke(width, end,  join, limit);
		
		return new BasicStroke(width, end, join, limit, d, 2);
	}
	
	/**Returns true if the stored array of dashes is usable for a dashed line*/
	boolean isDashVaid() {
		float[] d = this.getDashes();
		if (d==null) return false;
		if (d.length==0) return false;
		double sum=0;
		for(float i: d) {
			if (i<0) return false;
			sum+=Math.abs(i);
		}
		if (sum==0) return false;
		return true;
	}
	
	/**Sets the stroke properties of this object based on the argument*/
	@Override
	public void setStroke(BasicStroke stroke) {
		end=stroke.getEndCap();
		join=stroke.getLineJoin();
		dash=stroke.getDashArray();
		miterLimit=stroke.getMiterLimit();
		this.setStrokeWidth(stroke.getLineWidth());
	}
	


	@Override
	public Point2D getLocationUpperLeft() {
		return new Point2D.Double(x,y);
	}

	@Override
	public void setLocationUpperLeft(double x, double y) {
		this.x=x;
		this.y=y;
	}

	/**Draws the shape*/
	@Override
	public void draw(Graphics2D g, CordinateConverter cords) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, isAntialize()?RenderingHints.VALUE_ANTIALIAS_ON: RenderingHints.VALUE_ANTIALIAS_OFF);
		
		/**Sets up the shape that will be used to draw*/
		 Shape r= cords.getAffineTransform().createTransformedShape(getShape());
		   if (angle!=0) {
			  r=getRotationTransform().createTransformedShape(getShape());
			  r= cords.getAffineTransform().createTransformedShape(r);
		   }
		 
		   /**fills the shape*/
		  if (filled) {
			getFillPaintProvider().fillShape(g, r);
		  }
		  
		  /**draws the stroke*/
		  if (this.getStrokeWidth()>=0){
			  g.setStroke(cords.getScaledStroke(getStroke()));
			  if (r!=null )getStrokePaintProvider().strokeShape(g, r);
			  	else IssueLog.log("Shape graphic has no shape "+this.getClass().getName()+" "+this.getName());
	}
		
		  drawHandesSelection(g, cords);
		  
		 
		   }
	
	/**If the object is selected, draws the handles that the user may drag. 
	 * this should be be overwritten by most subclasses*/
	public void drawHandesSelection(Graphics2D g2d, CordinateConverter cords) {
		if (selected &&!handlesHidden) {

			handleBoxes= getGrahpicUtil().drawHandlesAtPoints(g2d, cords,  RectangleEdges.getLocationsForHandles(this.getBounds()));
			 
			   drawLocationAnchorHandle(g2d,cords);
		}
		
	}
	
	/**Draws the handle for the fixed edge of the shape*/
	private void drawLocationAnchorHandle(Graphics2D g2d, CordinateConverter cords) {
		Point2D p = RectangleEdges.getLocation(getLocationType(), this.getBounds());//gets the point
		
		/**if shape has a rotation, there will need to be a correction*/
		AffineTransform aa = RectangleEdges.getRotationAboutCenter(this.getBounds(), -this.getAngle());
		aa.transform(p, p);
		
		
		getGrahpicUtil().setHandleFillColor(Color.red);//the handle color is different for the Anchor
		ArrayList<Point2D> ps = new ArrayList<Point2D>();
		ps.add(p);
		getGrahpicUtil().drawHandlesAtPoints(g2d, cords, ps);
		getGrahpicUtil().setHandleFillColor(Color.white);
	}
	
	
	

	/**returns true if the units of the angle are radians*/
	@Override
	public boolean isRadians() {
		return true;
	}
	/**returns true if the units of the angle are degrees*/
	@Override
	public boolean isDegrees() {
		return false;
	}
	
	
	/**returns the unrotated shape*/
	public abstract Shape getShape() ;
	
	
	private static boolean completeMoveToIlls=true;
	/**Method used during production of illustrator scripts*/
	protected void setPathItemColorsToImmitate(	PathItemRef pi) {
		pi.setStrokeColor(getStrokeColor());
		pi.setStrokeDashes(dash);
		pi.setFilled(isFilled());
		
		if (isFilled()&&this.getFillColor().getAlpha()!=0)
				pi.setFillColor(this.getFillColor());
			else pi.setNoColorFill();
		
		/**sets the opacity of the fill*/
		int alpha = this.getFillColor().getAlpha();
		if (alpha<255&&alpha>0) {
			double opacity=100-getFillColor().getAlpha()*100.0/255;
			pi.setOpacity(opacity);
		}
		
		pi.setName(getName());
		pi.setStoke((int)getStrokeWidth());
		if (getAngle()!=0) {
			pi.rotate((getAngle()*(180/Math.PI)));
		}
		pi.setStrokeCap(end);
		pi.setStrokeJion(join);
		pi.setMiterLimit(getMiterLimit());
	}
	
	/**implementation of an interface required for generating adobe illustrator scripts*/
	@Override
	public Object toIllustrator(ArtLayerRef aref) {
		PathItemRef pi = new PathItemRef();
		createShapeOnPathItem(aref, pi);
		setPathItemColorsToImmitate(pi);
		return pi;
	}
	
	public void createShapeOnPathItem(ArtLayerRef aref, PathItemRef pi) {
		basicCreateShapeOnPathItem(	aref,pi);
	}
	protected void basicCreateShapeOnPathItem(ArtLayerRef aref, PathItemRef pi) {
		if (this.isCompleteMoveToIlls()) {
			pi.addPathWithCurves(aref, getShape().getPathIterator(new AffineTransform()), true, isDrawClosePoint());
			
			//IssueLog.log("trying experimental illustrator export on "+this);
		} else
			pi.createPathWithoutCurves(aref, getShape());
		
		
		 if (this.isClosedShape()) pi.setClosed(true);
		 pi.setName(this.getName());
		}
	public boolean isCompleteMoveToIlls() {
		return completeMoveToIlls;
	}
	
	
	
	
	@Override
	public Rectangle getExtendedBounds() {
		return getBounds();
	}
	
	/**basic geometry operation is used by many subclasses for several distinct purposes*/
	public static double getAngleBetweenPoints(double x, double y, double x2, double y2) {
		double angle=Math.atan(((double)(y2-y))/(x2-x));
		if (!java.lang.Double.isNaN(angle)) {
			if (x2-x<0) angle+=Math.PI;
			//this.setAngle(angle);
			}
		return angle;
	}
	
	
	
	
	
	public int getStrokeJoin() {
		return join;
	}
	public int getStrokeCap(){
		return end;
	}
	
	public void setStrokeJoin(int j) {
		join=j;
	}
	
	public void setStrokeJoin(String j) {
		if (j==null) return;
		String st=j.toLowerCase().trim();
		if (st.equals("bevel")) join=BasicStroke.JOIN_BEVEL;
		if (st.equals("miter")) join=BasicStroke.JOIN_MITER;
		if (st.equals("round")) join=BasicStroke.JOIN_ROUND;
		
	}
	
	public void setStrokeCap(int e){
		end=e;
	}
	public void setStrokeCap(String j){
		if (j==null) return;
		
		String st=j.toLowerCase().trim();
		
		if (st.equals("butt")) end=BasicStroke.CAP_BUTT;
		if (st.equals("round")) end=BasicStroke.CAP_ROUND;
		if (st.equals("square")) end=BasicStroke.CAP_SQUARE;
	}
	
	
	@Override
	public void showOptionsDialog() {
		getOptionsDialog().showDialog();
	}
	

	public ShapeGraphicOptionsSwingDialog getOptionsDialog() {
		return new ShapeGraphicOptionsSwingDialog(this, false);
	}
	
	public boolean isAntialize() {
		return antialize;
	}
	public void setAntialize(boolean antialize) {
		this.antialize = antialize;
	}
	
	public boolean isClosedShape() {
		return closedShape;
	}
	public void setClosedShape(boolean closedShape) {
		this.closedShape = closedShape;
	}
	public boolean isHasCloseOption() {
		return hasCloseOption;
	}
	public void setHasCloseOption(boolean hasCloseOption) {
		this.hasCloseOption = hasCloseOption;
	}
	public boolean isDrawClosePoint() {
		return true;
	}
	
	/**returns the outline that represents the area that a user may click on 
	   for this shape*/
	@Override
	public Shape getOutline() {
		return getRotationTransformShape();
	}
	
	/**returns the shape after the rotation transform of its angle has been applied*/
	public Shape getRotationTransformShape() {
		AffineTransform at = AffineTransform.getRotateInstance(-getAngle(), this.getCenterOfRotation().getX(), getCenterOfRotation().getY());
		return at.createTransformedShape(getShape());
	} 
	
	/**sets the dash array to a very simple form that appears similar to a dash free one*/
	public void makeNearlyDashLess() {
		setDashes(NEARLY_DASHLESS);
	}
	
	/**returns the icon*/
	@Override
	public Icon getTreeIcon() {
		return new GraphicDisplayComponent(createIcon() );
	}
	
	/**creates a small shape that resembles this graphic. that small shape functions as an icon*/
	ShapeGraphic createIcon() {
		ShapeGraphic out = shapeUsedForIcon() ;
		out.setAntialize(true);
		out.setStrokeWidth(1);
		out.copyColorsFrom(this);
		out.makeNearlyDashLess();
		out.setClosedShape(this.isClosedShape());
		boolean isTooWhite=isIconTooWhite();
		if(isTooWhite) {
			out.setStrokeColor(whiteIcon);
			
		}
		
		return out;
	}
	
	/**returns true if the icon is closer to pure white than is convenient for display */
	protected boolean isIconTooWhite() {
		if (this.getStrokeColor().equals(Color.white) &&!isFilled()) return true;
		if (this.getStrokeColor().equals(Color.white)&&this.getFillColor()!=null &&this.getFillColor().equals(Color.white)) return true;
		if (this.getStrokeColor().equals(Color.white)&&this.getFillColor().getAlpha()==0) return true;
		
		return false;
	}
	
	/**returns the icon*/
	ShapeGraphic shapeUsedForIcon() {
		return  PathGraphic.createExample();
	}
	
	
	/**returns the paint provider for the fill color*/
	public PaintProvider getFillPaintProvider() {
		if (fillPaintProvider==null) {fillPaintProvider=new DefaultPaintProvider(Color.white);}
		if (fillPaintProvider!=null) {
			
			return fillPaintProvider;
			}
		return fillPaintProvider;
	}
	
	/**Sets the paint provider for the fill color*/
	public void setFillPaintProvider(PaintProvider p) {
		this.fillPaintProvider=p;
	}
	/**Sets the paint provider for the stroke color*/
	public void setStrokePaintProvider(PaintProvider p) {
		this.strokePaintProvider=p;
	}
	/**returns the paint provider for the stroke color*/
	public PaintProvider getStrokePaintProvider() {
		if (strokePaintProvider==null) {strokePaintProvider=new DefaultPaintProvider(Color.white);}
		if (strokePaintProvider!=null) {
			
			return strokePaintProvider;
			}
		return strokePaintProvider;
	}
	
	
	
	/**sets the colors and the general attributes of aShape to those of this
	 * shape*/
	protected void copyColorAttributeTo(ShapeGraphic aShape) {
		aShape.copyAttributesFrom(this);
		aShape. copyColorsFrom(this);
	}
	
	/**Called when object is exported to powerpoint*/
	@Override
	public OfficeObjectMaker getObjectMaker() {
		//TODO: determine if any subclasses use a line2d as thier shape and delete the commented part
		/**if (this.getShape() instanceof Line2D) {
			Shape shapeStroked = this.getStroke().createStrokedShape(getRotationTransformShape());
			return BasicShapeGraphic.createFilled(getStrokeColor(), shapeStroked).getObjectMaker();
		}*/
		return new PathGraphicToOffice(this);
	}
	
	/**Called when the user exports to adobe illustrator*/
	@Override
	public SVGExporter getSVGEXporter() {
		return new SVGEXporterForShape(this);
	}
	
	
	public  KeyFrameAnimation getOrCreateAnimation() {
		if (animation instanceof KeyFrameAnimation) return (KeyFrameAnimation) animation;
		animation=new ShapeGraphicKeyFrameAnimator(this);
		return (KeyFrameAnimation) animation;
	}
	
	/**Scales the shape. this method is overwritten by some subclasses*/
	public void scaleAbout(Point2D p, double mag) {
		Point2D p2 = this.getLocationUpperLeft();
		AffineTransform af = new AffineTransform();
		af.scale(mag, mag);
		p2=scalePointAbout(p2, p,mag,mag);
		this.setLocationUpperLeft(p2);
		BasicStrokedItem.scaleStrokeProps(this, mag);
		
	}

	/**a list of handles that serve as a mini toolbar for this shape*/
	protected transient ActionButtonHandleList buttonList;
	
	/**returns the list of handles that take the role of buttons on a 'mini-toolbar' of sorts*/
	public ActionButtonHandleList getButtonList() {
		if(buttonList==null) {
			buttonList=createActionHandleList();
		}
		buttonList.updateLocation();
		return buttonList;
	}
	
	/**
	Creates an action handle list for the object
	 */
	public ActionButtonHandleList createActionHandleList()  {
		return new ShapeActionButtonHandleList2(this);
	}
	
	/**set to true if the shape is fillable*/
	public boolean isFillable() {
		return true;
	}
	/**returns true if the shape has joins (some subclasses dont)*/
	public boolean doesJoins() {
		return true;
	}
	
	/**returns the angle of a line between the two points*/
	public static double getAngleBetweenPoints(Point2D p1, Point2D p2) {
		double angle=Math.atan(((double)(p2.getY()-p1.getY()))/(p2.getX()-p1.getX()));
		if (!java.lang.Double.isNaN(angle)) {
			if (p2.getX()-p1.getX()<0) angle+=Math.PI;
			
			}
		return angle;
	}
	
	public static Point2D midPoint(Point2D p1, Point2D p2) {
		double nx = (p1.getX()+p2.getX())/2;
		double ny = (p1.getY()+p2.getY())/2;
		return new Point2D.Double(nx, ny);
	}
	
	/**returns a point along a line between p1 and p2. location of that point determined by d*/
	public static Point2D betweenPoint(Point2D p1, Point2D p2, double d) {
		double d2=1-d;
		double nx = (p1.getX()*d+p2.getX()*d2);
		double ny = (p1.getY()*d+p2.getY()*d2);
		return new Point2D.Double(nx, ny);
	}
	
	/**calculates two point positions that are crucial for determining how the stroke handles 
	  of the subclasses work*/
	public Point2D[] calculatePointsOnStrokeBetween(Point2D location1, Point2D location2) {
		Point2D m = midPoint(location1, location2);
		double a = getAngleBetweenPoints(location1, location2)+Math.PI/2;
		double stroke=getStrokeWidth();
		double dx = m.getX()+Math.cos(a)*stroke/2; 
		double dy = m.getY()+Math.sin(a)*stroke/2; 
		return new Point2D[] {new Point2D.Double(dx, dy), m};
	}
	
	/**returns an object for displaying a popup menu in response to a right click on the object*/
	public PopupMenuSupplier getMenuSupplier() {
		return new ShapeGraphicMenu(this);
	}
	
	/**returns a pathGraphic that looks just like this shape
	 * @see PathGraphic*/
	public PathGraphic createPathCopy() {
		PathPointList list = PathPointList.createFromIterator(this.getOutline().getPathIterator(new AffineTransform()));
		PathGraphic oo = new PathGraphic(list);
		oo.copyColorsFrom(this);
		oo.copyAttributesFrom(this);
		oo.setName(getName());
		oo.setClosedShape(true);
		if(this.isSelected())oo.select();
		return oo;
	}
	
	/**Provides an edit that can be used to undo changes to this item.
	 *  If the undo for a dialog is set to this edit, 
	 * undo is performed when the user clicks cancel on a dialog */
	@Override
	public AbstractUndoableEdit2 provideUndoForDialog() {
		return new CombinedEdit(new UndoStrokeEdit(this), new UndoScalingAndRotation(this), new ColorEditUndo(this));
	}
}
