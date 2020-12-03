package plotParts.DataShowingParts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

import dataSeries.DataSeries;
import graphicalObjects.CordinateConverter;

public abstract class AbstractDataLineShape extends DataShowingShape implements DataLineShape {

	public AbstractDataLineShape(DataSeries data) {
		super(data);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**returns the area that this item takes up for 
	  receiving user clicks*/
	@Override
	public Shape getOutline() {
	 return	new BasicStroke(3).createStrokedShape(this.getShape());

	}
	public Shape getOutline2() {
		 return	new BasicStroke(5).createStrokedShape(this.getShape());

		}

	public void drawHandesSelection(Graphics2D g2d, CordinateConverter<?> cords) {
		super.drawHandesSelection(g2d, cords);
		if (this.isSelected()) {
			g2d.setColor(new Color(0, 0, 0, 50));
			g2d.fill(cords.getAffineTransform().createTransformedShape(getOutline2()));
		}
	}

}
