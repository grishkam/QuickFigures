package graphicalObjects_BasicShapes;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import graphicalObjects.CordinateConverter;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.PathItemRef;

public class FrameGraphic extends RectangularGraphic {

	
	{this.setName("Frame Graphic");
	this.setStrokeJoin(BasicStroke.JOIN_MITER);
	this.setAntialize(true);
	setFilled(false);
	hideStrokeHandle=true;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public FrameGraphic(Rectangle r) {
		super(r);
	}

	@Override
	public void draw(Graphics2D g, CordinateConverter<?> cords) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, this.isAntialize()?RenderingHints.VALUE_ANTIALIAS_ON: RenderingHints.VALUE_ANTIALIAS_OFF);
		
		 Shape r= cords.getAfflineTransform().createTransformedShape( getInsideFrameRect());
		 if (filled) {
				g.setColor(getFillColor()); 
				Shape r2 = cords.getAfflineTransform().createTransformedShape( this.getRotatedFrame(getBounds()));
				g.fill(r2);
			  }
		 /**
		  * 
		  if (angle!=0) {
			  double xr = r.getBounds().getCenterX();
			  double yr= r.getBounds().getCenterY();
			
			  r=AffineTransform.getRotateInstance(angle, xr, yr).createTransformedShape( getInsideFrameRect() );
			   
		   }*/
		   
		   if (this.getStrokeWidth()==0 )return;
		   g.setColor(getStrokeColor());
		   g.setStroke(cords.getScaledStroke(getStroke()));
		  
		   drawFrame(g, cords,this.getInsideFrameRect());
		   drawHandesSelection(g, cords);
		  
		 
		   }
	
	public Rectangle2D getInsideFrameRect() {

		Rectangle r =super.getBounds().getBounds();
		;
		;
		;
		;
		return new Rectangle2D.Double(r.x-this.getStrokeWidth()/2, r.y-this.getStrokeWidth()/2, r.width+this.getStrokeWidth(), r.height+this.getStrokeWidth());
	//return r;
	}

	
	public void drawFrame(Graphics2D g, CordinateConverter<?> cords, Rectangle2D r) {
	if(angle==0||angle%Math.PI==0)
		this.getGrahpicUtil().drawRectangle(g, cords, r, false);
	else this.getGrahpicUtil().drawStrokedShape(g, cords, getRotatedFrame(r));
	
	}
	
	private Shape getRotatedFrame(Rectangle2D r) {
		if(angle==0||angle%Math.PI==0) return r;
		return AffineTransform.getRotateInstance(-angle, r.getCenterX(), r.getCenterY()).createTransformedShape(r);
	}
	
	public void createShapeOnPathItem(ArtLayerRef aref, PathItemRef pi) {
		pi.createRectangle(aref, getInsideFrameRect());
		
	
	}
	
	/**frames should only have a stroke color and are not fillable by user*/
	public boolean isFillable() {
		return false;
	}
}
