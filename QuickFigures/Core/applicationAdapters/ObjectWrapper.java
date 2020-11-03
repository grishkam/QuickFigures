package applicationAdapters;

import java.awt.Point;

import utilityClassesForObjects.DrawnGraphic;
import utilityClassesForObjects.LocatedObject2D;

public interface ObjectWrapper<Roi> extends LocatedObject2D, DrawnGraphic{

	
	/**returns the wrapped object*/
	public Roi getObject();
	
	/**sets the wrapped object*/
	public void setWrappedObject(Roi roi);
	
	/**returns the points along a line or polygon outline*/
	public Point getPoint(int ind);

}
