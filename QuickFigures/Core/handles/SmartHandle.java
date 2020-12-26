/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
import logging.IssueLog;
import undo.UndoManagerPlus;
import utilityClassesForObjects.Hideable;
import utilityClassesForObjects.RainbowPaintProvider;
import utilityClassesForObjects.Selectable;

/**This class defines a handle that the user can drag to freely edit objects.
   most handles that the user sees are instances of this class. contains the basic methods
   that subclasses may variously override. also contains methods for creating many shapes
   that the handles can appear as*/
public class SmartHandle implements Selectable, Hideable, ZoomableGraphic{
	
	public static int CROSS_FILL=5, RAINBOW_FILL=6, PLUS_FILL=7, CHECK_MARK=8;
	
	public int handlesize=3;
	protected Color messageColor = Color.BLACK;
	protected Color handleStrokeColor=Color.black;
	protected Color decorationColor=Color.LIGHT_GRAY;
	private Color handleColor=Color.white;
	private int specialFill=0;
	private SmartHandle lineto;
	
	private Point2D cordinateLocation=new Point();
	protected Icon icon=null;
	
	
	private int handleNumber=1;
	
	protected transient Shape lastDrawShape=null;
	transient protected Shape specialShape=null;
	protected String message=null;

	protected CordinateConverter<?> lastDrawnConverter;

	private boolean selected;

	protected boolean hidden;

	public transient Area underDecorationShape;
	public transient Area overDecorationShape;

	private boolean ellipse;
	
	public boolean absent() {return false;}

	/**Creates a smart handle*/
	public SmartHandle() {
		
	}
	
	
	public void setCordinateLocation(Point2D pt) {
		cordinateLocation=pt;
	}
	
	protected Stroke getHandleStroke() {
		BasicStroke bs = new BasicStroke();
		return bs;
	}

	private static final long serialVersionUID = 1L;
	
	@Override
	public void draw(Graphics2D graphics, CordinateConverter<?> cords) {
	
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			
		lastDrawnConverter=cords;
		Point2D pt = cords.transformP(getCordinateLocation());
		
	
		
		Shape s = createDrawnRect(pt);
		
		if (hasSpecialShape()) {
			s=createSpecialShape(pt, cords);
		}
		if(this.getUnderdecorationShape()!=null) {
			drawOnDecorationShape(graphics, createDecorationShape( pt, cords, getUnderdecorationShape()));
		}
		
		if(lineto!=null) {drawLineTo(graphics, cords, lineto);}
		
		drawOnShape(graphics, s);
		
		if(this.getOverdecorationShape()!=null) {
			drawOnDecorationShape(graphics, createDecorationShape( pt, cords, getOverdecorationShape()));
		}
		
		drawIcon(graphics, pt);
		
		drawMessage(graphics, s);
			
	}



	public void drawIcon(Graphics2D graphics, Point2D pt) {
		if (getIcon()!=null) {
			getIcon().paintIcon(null, graphics, (int)pt.getX()-this.getIcon().getIconWidth()/2, (int)pt.getY()-getIcon().getIconHeight()/2);
			
		};
	}

	protected Area getUnderdecorationShape() {
		return underDecorationShape;
	}

	protected Area getOverdecorationShape() {
		return overDecorationShape;
	}

	protected void drawMessage(Graphics2D graphics, Shape s) {
		if(message!=null) {
			graphics.setColor(messageColor);
			graphics.setFont(getMessageFont());
			graphics.drawString(message, (int)s.getBounds().getMaxX()+1, (int)(s.getBounds().getCenterY()/2+s.getBounds().getMaxY()/2));
		}
	}

	protected boolean hasSpecialShape() {
		return specialShape!=null;
	}

	protected Shape createSpecialShape(Point2D pt, CordinateConverter<?> cords) {
		AffineTransform g = AffineTransform.getTranslateInstance(pt.getX(), pt.getY());
		return	g.createTransformedShape(specialShape);
	}
	
	protected Shape createDecorationShape(Point2D pt, CordinateConverter<?> cords, Shape shape) {
		AffineTransform g = AffineTransform.getTranslateInstance(pt.getX(), pt.getY());
		return	g.createTransformedShape(shape);
	}

	protected Font getMessageFont() {
		return new Font("Arial", 0, 12);
	}
	
	/**Draws the main Shape. mouse clicks inside this shape will result in calls
	  to methods within this class*/
	protected void drawOnShape(Graphics2D graphics, Shape s) {
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
	
	protected void drawOnDecorationShape(Graphics2D graphics, Shape s) {

		graphics.setColor(this.decorationColor);
	
		graphics.fill(s);
		
		graphics.setStroke(getHandleStroke());
		graphics.draw(s);
	}

	public int handleSize() {
		return handlesize;
	}
	
	/**returns the standard shape for the handle. Rectangle or Ellipse*/
	protected Shape createDrawnRect(Point2D pt) {
		double xr = pt.getX()-handleSize();
		double yr = pt.getY()-handleSize();
		double widthr =getDrawnHandleWidth();
		double heightr = getDrawnHandleHeight();
		if(isEllipseShape())return new Ellipse2D.Double(xr,yr, widthr, heightr);
		return new Rectangle2D.Double(xr,yr, widthr, heightr);
	}

	/**Returns the width of the handle used if the handle is defined by the standard shape*/
	public int getDrawnHandleWidth() {
		return handleSize()*2;
	}
	
	/**Returns the height of the handle used if the handle is defined by the standard shape*/
	public int getDrawnHandleHeight() {
		return getDrawnHandleWidth();
	}

	/**location of the handle in the figures' coordinates. this determines where the handle will actually appear*/
	public Point2D getCordinateLocation() {
		return cordinateLocation;
	}

	public int getHandleNumber() {
		return handleNumber;
	}

	public void setHandleNumber(int handleNumber) {
		this.handleNumber = handleNumber;
	}
	
	/**What to do when a handle is moved from point p1 to p2*/
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
	
	public JPopupMenu getJPopup() {
		
		return null;
	}

	public void handlePress(CanvasMouseEvent canvasMouseEventWrapper) {
		
		
	}
	
public void handleRelease(CanvasMouseEvent canvasMouseEventWrapper) {
	
		
	}
	
	public void nudgeHandle(double dx, double dy) {}

/**returns true if the mouse event location is within the last drawn shape*/
public boolean containsClickPoint(Point2D p) {
	return getClickableArea().contains(p);
}




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
	
	
	public UndoManagerPlus getUndoManager() {
		return new CurrentFigureSet().getCurrentlyActiveDisplay().getUndoManager();
	}

	public void handleDrag(CanvasMouseEvent lastDragOrRelMouseEvent) {
		// TODO Auto-generated method stub
		
	}
	
	
	protected Area getAllDirectionArrows(int handlesize, int lenArr, boolean middleout) {
		Area a = createLeftRightArrow(handlesize, lenArr);
		a.add(new Area(getUpDownArrowShape(handlesize, lenArr)));
		lenArr=lenArr*2;
		if (middleout)a.subtract(new Area(new Rectangle(-lenArr,-lenArr,lenArr*2,lenArr*2)));
		return a;
	}

	protected Shape getUpDownArrowShape(int handlesize, int lenArr) {
		return AffineTransform.getRotateInstance(Math.PI/2).createTransformedShape(createLeftRightArrow(handlesize, lenArr));
	}

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
	
	protected Shape getArrowPointer(int handlesize, int lenArr, boolean left) {
		
		Area a = createRightArrow(handlesize, lenArr);

		
		if (left) return AffineTransform.getRotateInstance(-Math.PI).createTransformedShape(a);
		return a ;
	}

	private Area createRightArrow(int handlesize, int lenArr ) {
		Area a=new Area();
		int lineHieght = handlesize/2;
		
		Polygon point = new Polygon(new int[] {-handlesize, -handlesize, handlesize}, new int[] {handlesize,-handlesize,   0}, 3);
		Rectangle line = new Rectangle(-lenArr*handlesize, -lineHieght, lenArr*handlesize, 2*lineHieght );
		
		a.add(new Area(point));
		a.add(new Area(line));
		return a;
	}
	
	protected Shape createUpDownArrow(int handlesize, int lenArr) {
		return AffineTransform.getRotateInstance(Math.PI/2).createTransformedShape(createLeftRightArrow(handlesize, lenArr));
	}
	
	protected Area createLeftRightArrow(int handlesize, int lenArr) {
		Area arrow1 = createRightArrow(handlesize, lenArr);
		Shape arrow2 = AffineTransform.getTranslateInstance(-arrow1.getBounds().getMinX(),0).createTransformedShape(arrow1);
		Shape arrow3 = AffineTransform.getRotateInstance(-Math.PI).createTransformedShape(arrow2);
		Area output=new Area(arrow2); output.add(new Area(arrow3));
		return output;
	}

	@Override
	public boolean makePrimarySelectedItem(boolean isFirst) {
		return false;
	}

	public void setSpecialFill(int sFill) {
		specialFill=sFill;
		
	}
	
	/**when given the coordinate location of points, draws them on the canvas*/
	public  void drawLineBetweenPoints(Graphics2D g, CordinateConverter<?> cords, Point2D point, Point2D point2) {
		int x1=(int)cords.transformX( point.getX());
		int y1=(int)cords.transformY( point.getY());
		int x2=(int)cords.transformX( point2.getX());
		int y2=(int)cords.transformY( point2.getY());
		g.drawLine(x1, y1, x2, y2);
	}
	
	/**draws a line between this point and the other point*/
	private void drawLineTo(Graphics2D graphics, CordinateConverter<?> cords, SmartHandle otherPoint) {
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
	protected Area addSubtractShape(int plusSize, boolean subtract) {
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

	/**
	 * @param lastClickMouseEvent
	 */
	public void mouseMovedOver(CanvasMouseEvent lastClickMouseEvent) {
		
	}

	/**
	 * @param lastMouseEvent
	 */
	public void mouseEnterHandle(CanvasMouseEvent lastMouseEvent) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param lastMouseEvent
	 */
	public void mouseExitHandle(CanvasMouseEvent lastMouseEvent) {
		// TODO Auto-generated method stub
		
	}
	
}
