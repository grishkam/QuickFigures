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
 * Version: 2021.2
 */
package handles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;
import javax.swing.JPopupMenu;

import applicationAdapters.CanvasMouseEvent;
import graphicActionToolbar.CurrentFigureSet;
import graphicalObjects.CordinateConverter;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import locatedObject.Hideable;
import locatedObject.RainbowPaintProvider;
import locatedObject.RectangleEdgePositions;
import locatedObject.Selectable;
import undo.UndoManagerPlus;

/**This class defines a handle that the user can drag to freely edit objects.
   most handles that the user sees are instances of this class. contains the basic methods
   that subclasses may variously override. also contains methods for creating many shapes
   that the handles can appear as*/
public class SmartHandle implements Selectable, Hideable, ZoomableGraphic, RectangleEdgePositions{
	
	public static final int NORMAL_FILL=0, CROSS_FILL=5, RAINBOW_FILL=6, PLUS_FILL=7, CHECK_MARK=8;
	
	public int handlesize=3;
	private int specialFill=NORMAL_FILL;
	
	protected Color messageColor = Color.BLACK;
	protected Color handleStrokeColor=Color.black;
	protected Color decorationColor=Color.LIGHT_GRAY;
	private Color handleColor=Color.white;
	
	private SmartHandle lineto;
	
	private Point2D cordinateLocation=new Point();
	protected Icon icon=null;
	
	
	private int handleNumber=1;
	
	protected transient Shape lastDrawShape=null;
	transient protected Shape specialShape=null;
	protected String message=null;

	protected CordinateConverter lastDrawnConverter;

	private boolean selected;

	protected boolean hidden;

	public transient Area underDecorationShape;
	public transient Area overDecorationShape;

	private boolean ellipse;
	
	public boolean absent() {return false;}

	/**Creates a smart handle*/
	public SmartHandle() {
		
	}
	
	/**Sets a the handle's location. not all subclasses use the location that is set with this method*/
	public void setCordinateLocation(Point2D pt) {
		cordinateLocation=pt;
	}
	/**location of the handle. this determines where in the figure the handle will actually appear
	   overwritten in many subclasses*/
	public Point2D getCordinateLocation() {
		return cordinateLocation;
	}
	
	/**The stroke for drawing the outline of the handle*/
	protected Stroke getHandleStroke() {
		BasicStroke bs = new BasicStroke();
		return bs;
	}

	private static final long serialVersionUID = 1L;
	
	@Override
	public void draw(Graphics2D graphics, CordinateConverter cords) {
	
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			
		lastDrawnConverter=cords;
		Point2D pt = cords.transformP(getCordinateLocation());
		
	
		
		Shape s = createStandardHandleShape(pt);
		
		if (hasSpecialShape()) {
			s=createSpecialShape(pt, cords);
		}
		if(this.getUnderdecorationShape()!=null) {
			drawOnDecorationShape(graphics, createDecorationShape( pt, cords, getUnderdecorationShape()));
		}
		
		if(lineto!=null) {drawLineTo(graphics, cords, lineto);}
		
		drawHandleShape(graphics, s);
		
		if(this.getOverdecorationShape()!=null) {
			drawOnDecorationShape(graphics, createDecorationShape( pt, cords, getOverdecorationShape()));
		}
		
		drawIcon(graphics, pt);
		
		drawMessage(graphics, s);
			
	}

	/**draws the icon at the given point*/
	public void drawIcon(Graphics2D graphics, Point2D pt) {
		if (getIcon()!=null) {
			getIcon().paintIcon(null, graphics, (int)(pt.getX()-this.getIcon().getIconWidth()*0.5), (int)(pt.getY()-getIcon().getIconHeight()*0.5));
			
		};
	}

	protected Area getUnderdecorationShape() {
		return underDecorationShape;
	}

	protected Area getOverdecorationShape() {
		return overDecorationShape;
	}

	/**Draws the handle message next to the given shape*/
	protected void drawMessage(Graphics2D graphics, Shape s) {
		if(message!=null) {
			graphics.setColor(messageColor);
			graphics.setFont(getMessageFont());
			graphics.drawString(message, (int)s.getBounds().getMaxX()+1, (int)(s.getBounds().getCenterY()/2+s.getBounds().getMaxY()/2));
		}
	}

	/**returns true if the handle shape is set to something unique rather than the default rectangle*/
	protected boolean hasSpecialShape() {
		return specialShape!=null;
	}

	/**creates a transformed version of the 'special shape' that can be drawn at the scale of the coordinate converter
	 * and at the given point*/
	protected Shape createSpecialShape(Point2D pt, CordinateConverter cords) {
		AffineTransform g = AffineTransform.getTranslateInstance(pt.getX(), pt.getY());
		return	g.createTransformedShape(specialShape);
	}
	
	/**creates a transformed version of the given shape that can be drawn at the scale of the coordinate converter
	 * and at the given point*/
	protected Shape createDecorationShape(Point2D pt, CordinateConverter cords, Shape shape) {
		AffineTransform g = AffineTransform.getTranslateInstance(pt.getX(), pt.getY());
		return	g.createTransformedShape(shape);
	}

	/**returns the font of the handle message*/
	protected Font getMessageFont() {
		return new Font("Arial", Font.BOLD, 12);
	}
	
	/**Draws the main Shape. mouse clicks inside this shape will result in calls
	  to methods within this class*/
	protected void drawHandleShape(Graphics2D graphics, Shape s) {
		lastDrawShape=s;
		
		graphics.setColor(getHandleColor());
	
		graphics.fill(s);
		
		if (specialFill==CROSS_FILL &&s instanceof Rectangle2D) {
			Rectangle b = s.getBounds();
			graphics.setColor(decorationColor);
			graphics.setStroke(getHandleStroke());
			graphics.drawLine(b.x, b.y, b.x+b.width, b.y+b.height);
			graphics.drawLine(b.x, b.y+b.height, b.x+b.width, b.y);
		}
		if (specialFill==PLUS_FILL &&s instanceof Rectangle2D) {
			Rectangle b = s.getBounds();
			graphics.setColor(decorationColor);
			BasicStroke bs = new BasicStroke((float) (s.getBounds2D().getWidth()/3));
			graphics.setStroke(bs);
			int centerX = (int)b.getCenterX();
			graphics.drawLine(centerX, b.y, centerX, b.y+b.height);
			int centerY = (int)b.getCenterY();
			graphics.drawLine(b.x, centerY, b.x+b.width, centerY);
		}
		if (specialFill==CHECK_MARK ) {
			Rectangle b = s.getBounds();
			graphics.setColor(decorationColor);
			BasicStroke bs = new BasicStroke((float) (s.getBounds2D().getWidth()/3));
			graphics.setStroke(bs);
			int centerX = (int)b.getCenterX();
			int centerY = (int)b.getCenterY();
			graphics.drawLine(centerX, centerY,centerX -5, centerY-5);
			
			graphics.drawLine(centerX, centerY,centerX+ 15, centerY-15);
			
		}
		
		if (specialFill==RAINBOW_FILL) 
			{
			Rectangle b = s.getBounds();
			Paint paint1 = RainbowPaintProvider.getRaindowGradient(new Point(b.x, b.y) ,new Point( b.x+b.width, b.y+b.height));
			graphics.setPaint(paint1);
			graphics.fill(s);
			}
		
		graphics.setStroke(getHandleStroke());
		graphics.setColor(handleStrokeColor);
		graphics.draw(s);
	}
	
	/**draws an additional shape besides the main handle shape*/
	protected void drawOnDecorationShape(Graphics2D graphics, Shape s) {
		graphics.setColor(this.decorationColor);
	
		graphics.fill(s);
		
		graphics.setStroke(getHandleStroke());
		graphics.draw(s);
	}

	/**returns the handle size*/
	public double handleSize() {
		return handlesize;
	}
	
	/**returns the standard shape for the handle. Rectangle or Ellipse*/
	protected Shape createStandardHandleShape(Point2D pt) {
		double xr = pt.getX()-handleSize();
		double yr = pt.getY()-handleSize();
		double widthr =getDrawnHandleWidth();
		double heightr = getDrawnHandleHeight();
		if(isEllipseShape())return new Ellipse2D.Double(xr,yr, widthr, heightr);
		return new Rectangle2D.Double(xr,yr, widthr, heightr);
	}

	/**Returns the width of the handle used if the handle is defined by the standard shape*/
	public double getDrawnHandleWidth() {
		return handleSize()*2;
	}
	
	/**Returns the height of the handle used if the handle is defined by the standard shape*/
	public double getDrawnHandleHeight() {
		return getDrawnHandleWidth();
	}

	/**returns the ID number of the given handle. only handles with an id number will work properly*/
	public int getHandleNumber() {
		return handleNumber;
	}

	/**sets the ID number of the given handle. only handles with an id number will work properly
	  no two handles within the same object should have the same handle number unless they function identically*/
	public void setHandleNumber(int handleNumber) {
		this.handleNumber = handleNumber;
	}
	
	/**Called when a handle is moved from point p1 to p2*/
	public void handleMove(Point2D p1, Point2D p2) {
		
	}

	@Override
	public void select() {
		selected=true;
	}

	@Override
	public void deselect() {
		selected=false;
		
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public boolean isHidden() {
		return hidden;
	}

	@Override
	public void setHidden(boolean b) {
		hidden=b;
		
	}
	
	/**returns the popup menu for this handle. some subclasses return menus while others do not*/
	public JPopupMenu getJPopup() {
		
		return null;
	}

	/**Called when a handle is pressed*/
	public void handlePress(CanvasMouseEvent canvasMouseEventWrapper) {
		
		
	}
	
	/**Called when a handle is released*/
public void handleRelease(CanvasMouseEvent canvasMouseEventWrapper) {
	
		
	}
	
	/***/
	public void nudgeHandle(double dx, double dy) {}

/**returns true if the mouse event location is within the last drawn shape*/
public boolean containsClickPoint(Point2D p) {
	return getClickableArea().contains(p);
}



/**returns true if the given mouse event is inside this handle*/
public boolean containsClickPoint(CanvasMouseEvent canvasMouseEventWrapper) {
	return getClickableArea().contains(canvasMouseEventWrapper.getClickedXScreen(),canvasMouseEventWrapper. getClickedYScreen());
}

/**returns the shape within which the mouse is considered inside the handle*/
public Shape getClickableArea() {return lastDrawShape;}

	/**If the handle has an icon, returns it*/
	public Icon getIcon() {
		return icon;
	}
	/**sets an icon for the handle to dra*/
	public void setIcon(Icon icon) {
		this.icon = icon;
	}
	
	
	/**returns the undo manager to use if no other one is found*/
	public UndoManagerPlus getUndoManager() {
		return new CurrentFigureSet().getCurrentlyActiveDisplay().getUndoManager();
	}

	/**called when a user drags a handle */
	public void handleDrag(CanvasMouseEvent lastDragOrRelMouseEvent) {
		// TODO Auto-generated method stub
		
	}
	
	/**returns a shape that consists of arrows pointing in different directions
	 * @param handlesize the size of the arrows
	 * @param lenArr the length of the arrow
	 * @param whether to remove the very middle of the arrow (splitting it into parts)*/
	protected Area getAllDirectionArrows(int handlesize, int lenArr, boolean middleout) {
		Area a = createLeftRightArrow(handlesize, lenArr);
		a.add(new Area(getUpDownArrowShape(handlesize, lenArr)));
		lenArr=lenArr*2;
		if (middleout)a.subtract(new Area(new Rectangle(-lenArr,-lenArr,lenArr*2,lenArr*2)));
		return a;
	}

	/**returns a shape of two arros pointing in different directions*/
	protected Shape getUpDownArrowShape(int handlesize, int lenArr) {
		return AffineTransform.getRotateInstance(Math.PI/2).createTransformedShape(createLeftRightArrow(handlesize, lenArr));
	}

	/**returns a shape that looks like an arrow pointing in a particular direction*/
	protected Shape getDirectionPointer(boolean left) {
		Area a=new Area();
		handlesize=4;
		
		int plusSize = 4;
		a.add(new Area(new Rectangle(0, 0, plusSize*3, plusSize)));
		 Area t = new Area(new Rectangle(0, 0, plusSize, plusSize*3));
		 a.add(t);
		Shape t2 = AffineTransform.getRotateInstance(3*Math.PI/4).createTransformedShape(a);
		if (left) t2 = AffineTransform.getRotateInstance(3*Math.PI/4-Math.PI).createTransformedShape(a);
		return t2;
	}
	
	/**returns the shape of an arrow*/
	protected Shape getArrowPointer(int handlesize, int lenArr, boolean left) {
		
		Area a = createRightArrow(handlesize, lenArr);

		
		if (left) return AffineTransform.getRotateInstance(-Math.PI).createTransformedShape(a);
		return a ;
	}

	/**returns the shape of an arrow*/
	private Area createRightArrow(int handlesize, int lenArr ) {
		Area a=new Area();
		int lineHieght = handlesize/2;
		
		Polygon point = new Polygon(new int[] {-handlesize, -handlesize, handlesize}, new int[] {handlesize,-handlesize,   0}, 3);
		Rectangle line = new Rectangle(-lenArr*handlesize, -lineHieght, lenArr*handlesize, 2*lineHieght );
		
		a.add(new Area(point));
		a.add(new Area(line));
		return a;
	}
	
	/**returns the shape of an arrow that points up or down*/
	protected Shape createUpDownArrow(int handlesize, int lenArr) {
		return AffineTransform.getRotateInstance(Math.PI/2).createTransformedShape(createLeftRightArrow(handlesize, lenArr));
	}
	
	/**returns the shape of an arrow that points left or right*/
	protected Area createLeftRightArrow(int arrowSize, int lenArr) {
		Area arrow1 = createRightArrow(arrowSize, lenArr);
		Shape arrow2 = AffineTransform.getTranslateInstance(-arrow1.getBounds().getMinX(),0).createTransformedShape(arrow1);
		Shape arrow3 = AffineTransform.getRotateInstance(-Math.PI).createTransformedShape(arrow2);
		Area output=new Area(arrow2); output.add(new Area(arrow3));
		return output;
	}
	
	/**Creates an arrow pointing away from the 0,0 location
	 * @param direction which way the arrow points*/
	protected Shape createDirectionArrow(int handlesize, int lenArr, int direction) {
		Shape output = createRightArrow(handlesize, lenArr);
		double angle=0;
		
		switch(direction) {
			case LEFT: {angle=180;break;}
			case RIGHT: {break;}
			case TOP: {angle=270;break;}
			case BOTTOM: {angle=90;break;}
			case UPPER_RIGHT: {angle=315;break;}
			case LOWER_RIGHT: {angle=45;break;}
			case LOWER_LEFT: {angle=135;break;}
			case UPPER_LEFT: {angle=225;break;}
		}
		output =AffineTransform.getTranslateInstance(-output.getBounds().getMinX(), 0).createTransformedShape(output);
		output = AffineTransform.getRotateInstance(angle*Math.PI/180).createTransformedShape(output);
		
		return output;
	}

	
	@Override
	public boolean makePrimarySelectedItem(boolean isFirst) {
		return false;
	}

	/**Sets a special fill type for the arros besides the default solid color*/
	public void setSpecialFill(int sFill) {
		specialFill=sFill;
		
	}
	
	/**when given the coordinate location of points, draws them on the canvas*/
	public  void drawLineBetweenPoints(Graphics2D g, CordinateConverter cords, Point2D point, Point2D point2) {
		int x1=(int)cords.transformX( point.getX());
		int y1=(int)cords.transformY( point.getY());
		int x2=(int)cords.transformX( point2.getX());
		int y2=(int)cords.transformY( point2.getY());
		g.drawLine(x1, y1, x2, y2);
	}
	
	/**draws a line between this point and the other point*/
	private void drawLineTo(Graphics2D graphics, CordinateConverter cords, SmartHandle otherPoint) {
		graphics.setColor(getHandleColor());
		graphics.setStroke(getHandleStroke());
		drawLineBetweenPoints(graphics, cords, this.getCordinateLocation(), otherPoint.getCordinateLocation());
		
	}
	

	/**Subclasses may returns true if the method calls with the handle add an undo to the undomanager.
	  if not, an undo that simply tries to move a handle back to its origin location is used (those are often not complex enough to undo properly)*/
	public boolean handlesOwnUndo() {
		return false;
	}

	/**Some handles are linked to each other by a line. This method sets a handle that a line will be drawn from*/
	public void setLineConnectionHandle(SmartHandle s) {
		this.lineto=s;
}
	/**Some handles are linked to each other by a line. This method gets a handle that a line will be drawn from*/
	public SmartHandle getLineConnectionHandle() {
		return lineto;
	}

	/**returns true  if the standard shape is ellipse. rectangle otherwise*/
	public boolean isEllipseShape() {
		return ellipse;
	}
	/**set to true if the standard shape is ellipse. rectangle otherwise*/
	public void setEllipseShape(boolean ellipse) {
		this.ellipse = ellipse;
	}
	
	/**creates a shape that is a plus or minus sign*/
	protected Area addOrSubtractSymbol(int plusSize, boolean subtract) {
		Area a=new Area();
		a.add(new Area(new Rectangle(-plusSize, 0, plusSize*3, plusSize)));
		 if(!subtract) a.add(new Area(new Rectangle(0, -plusSize, plusSize, plusSize*3)));
		return a;
	}

	public Color getHandleColor() {
		return handleColor;
	}

	public void setHandleColor(Color handleColor) {
		this.handleColor = handleColor;
	}

	@Override
	public GraphicLayer getParentLayer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParentLayer(GraphicLayer parent) {
		// TODO Auto-generated method stub
		
	}

	/** called when a mouse is moved over the handle
	 * @param lastClickMouseEvent
	 */
	public void mouseMovedOver(CanvasMouseEvent lastClickMouseEvent) {
		
	}

	/**called when a mouse enters the handle
	 * @param lastMouseEvent
	 */
	public void mouseEnterHandle(CanvasMouseEvent lastMouseEvent) {
		// TODO Auto-generated method stub
		
	}

	/**called when a mouse exits the handle
	 * @param lastMouseEvent
	 */
	public void mouseExitHandle(CanvasMouseEvent lastMouseEvent) {
		// TODO Auto-generated method stub
		
	}
	
}
