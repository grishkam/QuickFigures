package standardDialog;

import java.awt.geom.Point2D;

public class PointInputPanel extends NumberArrayInputPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public PointInputPanel(String name, Point2D start) {
		super(2,2);
		this.setLabel(name);
		this.setArray(new float[] {(float) start.getX(), (float) start.getY()});
	}
	
	public Point2D getPoint() {
		return new Point2D.Double(getArray()[0], getArray()[1]);
	}
}


