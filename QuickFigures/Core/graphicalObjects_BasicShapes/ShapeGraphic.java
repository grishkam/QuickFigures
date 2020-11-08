package graphicalObjects_BasicShapes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.undo.AbstractUndoableEdit;

import officeConverter.OfficeObjectConvertable;
import officeConverter.OfficeObjectMaker;
import officeConverter.PathGraphicToOffice;
import popupMenusForComplexObjects.ShapeGraphicMenu;
import standardDialog.GraphicDisplayComponent;
import undo.ColorEditUndo;
import undo.CompoundEdit2;
import undo.ProvidesDialogUndoableEdit;
import undo.UndoScaleBarEdit;
import undo.UndoScaling;
import undo.UndoStrokeEdit;
import utilityClassesForObjects.BasicStrokedItem;
import utilityClassesForObjects.DefaultPaintProvider;
import utilityClassesForObjects.Fillable;
import utilityClassesForObjects.PaintProvider;
import utilityClassesForObjects.PathPointList;
import utilityClassesForObjects.RectangleEdges;
import utilityClassesForObjects.Rotatable;
import utilityClassesForObjects.ShowsOptionsDialog;
import utilityClassesForObjects.StrokedItem;
import animations.KeyFrameAnimation;
import fieldReaderWritter.SVGEXporterForShape;
import fieldReaderWritter.SVGExportable;
import fieldReaderWritter.SVGExporter;
import graphicalObjectHandles.ShapeActionButtonHandleList2;
import graphicalObjects.CordinateConverter;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.IllustratorObjectConvertable;
import illustratorScripts.PathItemRef;
import keyFrameAnimators.ShapeGraphicKeyFrameAnimator;
import layersGUI.HasTreeLeafIcon;
import logging.IssueLog;
import menuUtil.HasUniquePopupMenu;
import menuUtil.PopupMenuSupplier;
import objectDialogs.ShapeGraphicOptionsSwingDialog;

public abstract class ShapeGraphic extends BasicGraphicalObject implements GraphicalObject, StrokedItem, ShowsOptionsDialog,IllustratorObjectConvertable, Fillable, Rotatable, HasTreeLeafIcon , OfficeObjectConvertable, HasUniquePopupMenu, SVGExportable, ProvidesDialogUndoableEdit {
	/**
	 * 
	 */
	
	{name="Shape ";}
	private static final long serialVersionUID = 1L;
	private boolean antialize=true;
	protected static final Color whiteIcon = new Color(240,240,240);
	
	PaintProvider fillPaintProvider=null;
	PaintProvider strokePaintProvider=null;
	
	//transient GraphicUtil Gu=new GraphicUtil();
	public HashMap<String, Object> map=new HashMap<String, Object>();
	//Color strokeColor=Color.white;
//	Color fillColor=Color.white;
	
	boolean filled=true; {setFillColor(new Color(0,0,0,0));}
	
	protected float strokeWidth=1;
	float[] dash=new float[]{};
	
	int end=BasicStroke.CAP_BUTT;
	int join=BasicStroke.JOIN_ROUND;
	float miterLimit=8;
	private boolean hasCloseOption=false;
	
	
	public void setMiterLimit(double miter) {
		 miterLimit=(float)miter;
	}
	public double getMiterLimit() {
		return  miterLimit;
	}
	
	
	
	

	public float[] getDashes() {return dash;}
	public void setDashes(float[] dash) {this.dash=dash;}

	
	/***/
	public void copyAttributesFrom(ShapeGraphic source) {
		
		this.setAngle(source.getAngle());
		 copyStrokeFrom(source);
		
		this.setLocationType(source.getLocationType());
		this.setAntialize(source.isAntialize());
	
	}
	
	public void copyStrokeFrom(StrokedItem source) {
		this.setDashes(source.getDashes().clone());
		this.setStrokeWidth(source.getStrokeWidth());
		this.end=source.getStrokeCap();
		this.join=source.getStrokeJoin();
	}
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
		// TODO Auto-generated method stub
		return this.getFillPaintProvider().getColor();
	}
	
	@Override
	public Color getStrokeColor() {
		return this.getStrokePaintProvider().getColor();
	}
	
	@Override
	public boolean isFilled() {
		// TODO Auto-generated method stub
		return filled;
	}
	@Override
	public void setFilled(boolean fill) {
		filled=fill;
		
	}

	
	public void dropColor(Color c, Point p) {
		double closetocenter=p.distance(getCenter());
		
			if (closetocenter<getBounds().getWidth()/4)
				this.setFillColor(c);
			else
			this.setStrokeColor(c);
		
	}
	
	public void setStrokeWidth(float strokeWidth) {
		this.strokeWidth = strokeWidth;
	}
	
	
	public Point getCenter() {
		return new Point((int)getBounds().getCenterX(), (int)getBounds().getCenterY());
	}

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
	

	
	
	@Override
	public void draw(Graphics2D g, CordinateConverter<?> cords) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, isAntialize()?RenderingHints.VALUE_ANTIALIAS_ON: RenderingHints.VALUE_ANTIALIAS_OFF);
		
		 Shape r= cords.getAfflineTransform().createTransformedShape(getShape());
		   if (angle!=0) {
			  r=getRotationTransform().createTransformedShape(getShape());
			  r= cords.getAfflineTransform().createTransformedShape(r);
		   }
		 
		  if (filled) {
			//g.setColor(getFillColor());  
			getFillPaintProvider().fillShape(g, r);
			
		  }
		  
		  if (this.getStrokeWidth()>=0){
		  g.setStroke(cords.getScaledStroke(getStroke()));
		 
		  if (r!=null )getStrokePaintProvider().strokeShape(g, r);
		 
		  else IssueLog.log("Shape graphic has no shape "+this.getClass().getName()+" "+this.getName());
	}
		
		  drawHandesSelection(g, cords);
		  
		 
		   }
	
	public void drawHandesSelection(Graphics2D g2d, CordinateConverter<?> cords) {
		if (selected &&!handlesHidden) {

			   getGrahpicUtil().drawHandlesAtPoints(g2d, cords,  RectangleEdges.getLocationsForHandles(this.getBounds()));
			   handleBoxes=getGrahpicUtil().lastHandles;
			   drawLocationAnchorHandle(g2d,cords);
		}
		
	}
	
	public void drawLocationAnchorHandle(Graphics2D g2d, CordinateConverter<?> cords) {
		Point2D p = RectangleEdges.getLocation(getLocationType(), this.getBounds());//gets the point
		
		/**if shape has a rotation, there will need to be a correction*/
		AffineTransform aa = RectangleEdges.getRotationAboutCenter(this.getBounds(), -this.getAngle());
		aa.transform(p, p);
		
		getGrahpicUtil().setHandleFillColor(Color.red);//the handle color is different for the Anchor
		getGrahpicUtil().drawHandlesAtPoint(g2d, cords, p);
		getGrahpicUtil().setHandleFillColor(Color.white);
	}
	
	
	


	@Override
	public boolean isRandians() {
		return true;
	}

	@Override
	public boolean isDegrees() {
		return false;
	}
	
	
	/**returns the unrotated shape*/
	public abstract Shape getShape() ;
	
	private static boolean completeMoveToIlls=true;
	
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
	
	@Override
	public Object toIllustrator(ArtLayerRef aref) {
		PathItemRef pi = new PathItemRef();
		createShapeOnPathItem(aref, pi);
		setPathItemColorsToImmitate(pi);
		return pi;
	}
	
	
	
	private boolean closedShape=false;
	
	
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
	
	
	
	
	
	@Override
	public Rectangle getExtendedBounds() {
		return getBounds();
	}
	
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
		new ShapeGraphicOptionsSwingDialog(this).showDialog();
		//new TextGraphicDialog(this).showDialog();;
	}
	public boolean isAntialize() {
		return antialize;
	}
	public void setAntialize(boolean antialize) {
		this.antialize = antialize;
	}
	public boolean isCompleteMoveToIlls() {
		return completeMoveToIlls;
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
	
	@Override
	public Shape getOutline() {
		return getRotationTransformShape();
	}
	
	/**returns the shape after the rotation transform of its angle has been applied*/
	public Shape getRotationTransformShape() {
		AffineTransform at = AffineTransform.getRotateInstance(-getAngle(), this.getCenterOfRotation().getX(), getCenterOfRotation().getY());
		return at.createTransformedShape(getShape());
	} 
	
	public void makeDashLess() {
		setDashes(new float[]{100000,1});
	}
	
	@Override
	public Icon getTreeIcon() {
		return new GraphicDisplayComponent(createIcon() );
	}
	
	ShapeGraphic createIcon() {
		ShapeGraphic out = rectForIcon() ;//RectangularGraphic.blankRect(new Rectangle(0,0,14,12), Color.BLACK);//ArrowGraphic.createDefaltOutlineArrow(this.getFillColor(), this.getStrokeColor());
		out.setAntialize(true);
		out.setStrokeWidth(1);
		out.copyColorsFrom(this);
		out.makeDashLess();
		out.setClosedShape(this.isClosedShape());
		boolean isTooWhite=isIconTooWhite();
		if(isTooWhite) {
			out.setStrokeColor(whiteIcon);
			
		}
		
		return out;
	}
	
	protected boolean isIconTooWhite() {
		if (this.getStrokeColor().equals(Color.white) &&!isFilled()) return true;
		if (this.getStrokeColor().equals(Color.white)&&this.getFillColor()!=null &&this.getFillColor().equals(Color.white)) return true;
		if (this.getStrokeColor().equals(Color.white)&&this.getFillColor().getAlpha()==0) return true;
		
		return false;
	}
	ShapeGraphic rectForIcon() {
		return  PathGraphic.createExample();
	}
	
	public PaintProvider getFillPaintProvider() {
		if (fillPaintProvider==null) {fillPaintProvider=new DefaultPaintProvider(Color.white);}
		if (fillPaintProvider!=null) {
			
			return fillPaintProvider;
			}
		return fillPaintProvider;
	}
	
	public void setStrokePaintProvider(PaintProvider p) {
		this.strokePaintProvider=p;
	}
	
	public PaintProvider getStrokePaintProvider() {
		if (strokePaintProvider==null) {strokePaintProvider=new DefaultPaintProvider(Color.white);}
		if (strokePaintProvider!=null) {
			
			return strokePaintProvider;
			}
		return strokePaintProvider;
	}
	
	public void setFillPaintProvider(PaintProvider p) {
		this.fillPaintProvider=p;
	}
	
	/**sets the colors and the general attributes of shape out to those of this
	 * shape*/
	protected void copyColorAttributeTo(ShapeGraphic out) {
		out.copyAttributesFrom(this);
		out. copyColorsFrom(this);
	}
	
	@Override
	public OfficeObjectMaker getObjectMaker() {
		/**if (this.getShape() instanceof Line2D) {
			Shape shapeStroked = this.getStroke().createStrokedShape(getRotationTransformShape());
			return BasicShapeGraphic.createFilled(getStrokeColor(), shapeStroked).getObjectMaker();
		}*/
		return new PathGraphicToOffice(this);
	}
	
	
	@Override
	public SVGExporter getSVGEXporter() {
		// TODO Auto-generated method stub
		return new SVGEXporterForShape(this);
	}
	
	
	public  KeyFrameAnimation getOrCreateAnimation() {
		if (animation instanceof KeyFrameAnimation) return (KeyFrameAnimation) animation;
		animation=new ShapeGraphicKeyFrameAnimator(this);
		return (KeyFrameAnimation) animation;
	}
	
	
	public void scaleAbout(Point2D p, double mag) {
		Point2D p2 = this.getLocationUpperLeft();
		AffineTransform af = new AffineTransform();
		af.scale(mag, mag);
		p2=scaleAbout(p2, p,mag,mag);
		this.setLocationUpperLeft(p2);
		BasicStrokedItem.scaleStrokeProps(this, mag);
		
	}

	protected transient ShapeActionButtonHandleList2 buttonList;
	public ShapeActionButtonHandleList2 getButtonList() {
		if(buttonList==null) {
			buttonList=new ShapeActionButtonHandleList2(this);
			
		}
		buttonList.updateLocation();
		return buttonList;
	}
	public boolean isFillable() {
		return true;
	}
	public boolean doesJoins() {
		return true;
	}
	
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
	
	public static Point2D betweenPoint(Point2D p1, Point2D p2, double d) {
		double d2=1-d;
		double nx = (p1.getX()*d+p2.getX()*d2);
		double ny = (p1.getY()*d+p2.getY()*d2);
		return new Point2D.Double(nx, ny);
	}
	
	public Point2D[] calculatePointsOnStrokeBetween(Point2D location1, Point2D location2) {
		Point2D m = midPoint(location1, location2);
		double a = getAngleBetweenPoints(location1, location2)+Math.PI/2;
		double stroke=getStrokeWidth();
		double dx = m.getX()+Math.cos(a)*stroke/2; 
		double dy = m.getY()+Math.sin(a)*stroke/2; 
		return new Point2D[] {new Point2D.Double(dx, dy), m};
	}
	
	
	public PopupMenuSupplier getMenuSupplier() {
		return new ShapeGraphicMenu(this);
	}
	
	/**returns a pathGraphic that looks just like this arrow*/
	public PathGraphic createPathCopy() {
		PathPointList list = PathPointList.createFromIterator(this.getOutline().getPathIterator(new AffineTransform()));
		PathGraphic oo = new PathGraphic(list);
		oo.copyColorsFrom(this);
		oo.copyAttributesFrom(this);
		oo.setName(getName());
		oo.setClosedShape(true);
		if(this.isSelected())oo.select();
	//	oo.setUseFilledShapeAsOutline(this.isFilled());
		return oo;
	}
	
	
	@Override
	public AbstractUndoableEdit provideUndoForDialog() {
		return new CompoundEdit2(new UndoStrokeEdit(this), new UndoScaling(this), new ColorEditUndo(this));
	}
}