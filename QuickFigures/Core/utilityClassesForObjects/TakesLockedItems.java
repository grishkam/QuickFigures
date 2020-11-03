package utilityClassesForObjects;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import graphicalObjectHandles.HasSmartHandles;

public interface TakesLockedItems extends Mortal, Selectable, HasSmartHandles {
	public void addLockedItem(LocatedObject2D l) ;
	public void removeLockedItem(LocatedObject2D l) ;
	
	
	public void snapLockedItems() ;
	public void snapLockedItem(LocatedObject2D l) ;
	
	public boolean hasLockedItem(LocatedObject2D l);
	public LockedItemList getLockedItems();
	public ArrayList<LocatedObject2D> getNonLockedItems();
	public Rectangle getBounds();
	public Rectangle2D getContainerForBounds(LocatedObject2D l);
	
	public ObjectContainer getTopLevelContainer();

}
