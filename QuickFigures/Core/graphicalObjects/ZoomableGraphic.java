package graphicalObjects;

import java.awt.Graphics2D;
import java.io.Serializable;

public interface ZoomableGraphic extends Serializable, KnowsParentLayer{
	public void draw(Graphics2D graphics, CordinateConverter<?> cords);
}
