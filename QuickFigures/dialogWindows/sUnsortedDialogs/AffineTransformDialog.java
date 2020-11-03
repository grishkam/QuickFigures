package sUnsortedDialogs;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import standardDialog.AngleInputPanel;
import standardDialog.PointInputPanel;
import standardDialog.StandardDialog;

public class AffineTransformDialog extends StandardDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	static final int rotated=0, scales=1, sheared=2, translate=3;

	private int type;
	
	public AffineTransformDialog( double theta, Point2D about) {
		super("Transform", true);
		this.type=rotated;
		if (type==rotated) {
			this.add("theta", new AngleInputPanel("rotate", theta, true));
			this.add("about", new PointInputPanel("about ", about));
			this.setTitle("Rotate");
		}
		
	}
	
	public static AffineTransform showRotation(double theta, Point2D about) {
		AffineTransformDialog affineTransformDialog =	new AffineTransformDialog( theta, about);
		affineTransformDialog.showDialog();
		return affineTransformDialog.getTransform();
	}
	
	public AffineTransformDialog( int type, double theta, double about) {
		super("Transform", true);
		this.type=type;
		if (type==scales) {
			this.add("xy scale", new PointInputPanel("Scale X,Y", new Point2D.Double(theta, about)));
		}
		if (type==sheared) {
			this.add("xy shear", new PointInputPanel("Shear ", new Point2D.Double(theta, about)));
		}
		if (type==translate) {
			this.add("xy translate", new PointInputPanel("Translate XY", new Point2D.Double(theta, about)));
		}
	}
	
	
	
	public AffineTransform getTransform() {
		
		if (type==scales) {
			float[] ar = this.getNumberArray("xy scale");
			return AffineTransform.getScaleInstance(ar[0],ar[1]);
			
		}
		if (type==sheared) {
			float[] ar = this.getNumberArray("xy shear");
			return AffineTransform.getShearInstance(ar[0],ar[1]);
			
		}
		if (type==translate) {
			float[] ar = this.getNumberArray("xy translate");
			return AffineTransform.getTranslateInstance(ar[0],ar[1]);
			
		}
		
		if (type==rotated) {
			float[] ar = this.getNumberArray("about");
			double theta=this.getNumber("theta");
			return AffineTransform.getRotateInstance(-theta, ar[0],ar[1]);//.getTranslateInstance(ar[0],ar[1]);
			
		}
		
		return new AffineTransform();
			
	}

	public static AffineTransform showScale(Point point) {
		AffineTransformDialog affineTransformDialog = new AffineTransformDialog(scales, point.getX(), point.getY());
		affineTransformDialog.showDialog();
		return affineTransformDialog.getTransform();
		//return null;
	}
}
