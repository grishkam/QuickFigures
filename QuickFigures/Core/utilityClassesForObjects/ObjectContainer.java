package utilityClassesForObjects;

import java.util.ArrayList;

/**a container of located objects*/
public interface ObjectContainer {
	
	/**removes an objects from the image*/
	public void takeRoiFromImage(LocatedObject2D roi) ;
	/**adds an objects to the image*/
	public void addRoiToImage(LocatedObject2D roi) ;
	/**adds an objects to the image*/
	public void addRoiToImageBack(LocatedObject2D roi) ;
	
	public ArrayList<LocatedObject2D> getLocatedObjects();
	
	public LocatedObject2D getSelectionObject();
	
	

}
