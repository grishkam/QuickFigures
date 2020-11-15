package applicationAdapters;

import java.awt.Component;
import java.awt.Point;
import java.io.Serializable;

import javax.swing.undo.AbstractUndoableEdit;

import selectedItemMenus.LayerSelector;

/**An interface with methods that return information about mouse events
 such as thEach figure has its own coordinate. 
 Broadly similar to java's MouseEvent class except with a few methods
 specifically relevant to QuickFigures images */
public interface CanvasMouseEventWrapper extends Serializable {

	/**The point clicked given in my coordinate system. Immediately useful for may tools*/
	Point getCoordinatePoint() ;
	/**The x and y for the point above*/
	int getCoordinateX();
	int getCoordinateY();
	
	/**The raw location of the click point as given by the MouseEvent.getX()*/
	int getClickedXScreen();
	/**The raw location of the click point as given by the MouseEvent.getX()*/
	int getClickedYScreen();
	
	
	/**method for converting the raw points into the coordinate system of my canvas */
	int convertClickedXImage(int x);
	int convertClickedYImage(int y);



	/**The image that is being clicked on */
	DisplayedImage getAsDisplay();
	/**Adds an undo to the appropriate undo manager*/
	public void addUndo(AbstractUndoableEdit... e);
	/**Returns the LayerSelection*/
	LayerSelector getSelectionSystem();

	/**Some implementations have a channel, slice and frame*/
	int getClickedSlice();
	int getClickedFrame();
	int getClickedChannel();

	/**returns the mouse event information*/
	java.awt.event.MouseEvent getAwtEvent();
	public boolean shfitDown();
	public int clickCount();
	public boolean altKeyDown();
	int mouseButton();
	Component getComponent();
	Object getSource();
	boolean isPopupTrigger();
	boolean isMetaDown();
	boolean isControlDown();
	
	

}
