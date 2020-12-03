package graphicalObjects;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.AffineTransform;

public class ReverseBasicCordinateConverter extends BasicCoordinateConverter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public ReverseBasicCordinateConverter(double x2, double y2,
			double scale) {
		setX(x2);
		setY(y2);
		setMagnification(scale);
	}

	@Override
	public double transformX(double ox) {
		return ox/getMagnification()+getX();
	}

	@Override
	public double transformY(double oy) {
		return oy/getMagnification()+getY();
	}
	
	@Override
	public Font getScaledFont(Font font) {
		return font.deriveFont((float)(font.getSize()/getMagnification()));
	}

	@Override
	public BasicStroke getScaledStroke(BasicStroke stroke) {    
	        double mag = getMagnification();
	        if (mag!=1.0) {
	            float width = (float)(stroke.getLineWidth()/mag);
	            float[] oldDash = stroke.getDashArray();
	            float[] newDash=new float[stroke.getDashArray().length] ;
	            for(int i=0; i<newDash.length; i++) {newDash[i]=(float) (oldDash[i]/mag);}
	            //return new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
	            return new BasicStroke(width, stroke.getEndCap(), stroke.getLineJoin(), stroke.getMiterLimit(), newDash, stroke.getDashPhase());
	        } else
	            return stroke;
	    }
	
	public AffineTransform getAffineTransform() {
		AffineTransform af = AffineTransform.getScaleInstance(1/getMagnification(), 1/getMagnification());
		//af.translate(-transformX(0), -transformY(0));
		af.translate(-getX(), -getY()); 
		return af;
	}
	
	
	public static void main(String[] args) {
		ReverseBasicCordinateConverter bb = new ReverseBasicCordinateConverter(100,30,1);
		bb.testConsistency(new Point(50,40));
		bb.testConsistency(new Point(-50,40));
		bb.testConsistency(new Point(50,4));
	}
	
}
