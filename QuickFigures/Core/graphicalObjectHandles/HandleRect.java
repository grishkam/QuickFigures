package graphicalObjectHandles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import graphicalObjects.CordinateConverter;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;

public class HandleRect extends Rectangle2D.Double implements ZoomableGraphic {

	private static int defaulthandleSize = 3;
	protected Color handleStrokeColor = Color.black;
	/**
	 * 
	 */
	
	public int handlesize=defaulthandleSize;
	 private Color handleColor=Color.white;
	
	public HandleRect(int x, int y) {
		super(x, y, 0,0);
		this.x-=handlesize;
		this.y-=handlesize;
		this.width=handlesize*2;
		this.height=handlesize*2;
	}
	
	public HandleRect(int x, int y, int handleSize) {
		super(x, y, 0,0);
		this.handlesize=handleSize;
		this.x-=handlesize;
		this.y-=handlesize;
		this.width=handlesize*2;
		this.height=handlesize*2;
	}
	
	public HandleRect(Rectangle2D r) {
		super(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}
	
	public HandleRect(Rectangle2D r,CordinateConverter<?> cords, Color c ) {
		this(cords.getAffineTransform().createTransformedShape(r).getBounds2D());
		this.setHandleColor(c);
	}
	
	
	
	
	private static final long serialVersionUID = 1L;

	@Override
	public void draw(Graphics2D g, CordinateConverter<?> cords) {
			Rectangle2D r = this.getBounds2D();
		g.setStroke(new BasicStroke());
		g.setColor(getHandleColor());
		g.fillRect((int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
		
		g.setColor(handleStrokeColor);
		g.drawRect((int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
		

	}
	


	public Color getHandleColor() {
		return handleColor;
	}

	public void setHandleColor(Color handleColor) {
		this.handleColor = handleColor;
	}
	
	
	private transient GraphicLayer layer;
	@Override
	public GraphicLayer getParentLayer() {
		// TODO Auto-generated method stub
		return layer;
	}

	@Override
	public void setParentLayer(GraphicLayer parent) {
		layer=parent;
		
	}

}
