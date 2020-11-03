package graphicalObjectHandles;

import java.awt.Point;

import applicationAdapters.CanvasMouseEventWrapper;


public interface HasHandles {
	/**The methods should work when given a display cordinate. (not the object's cordinates but the
	 grtaphics/canvas cordiantes). */
	
	/**returns which handle contains point (x,y). returns -1 if no handle has it
	  the x and y represent the cordinates of the canvas object and not the cordinates 
	 */
	public int handleNumber(int x, int y);
	
	
	//public generalSmartHandle getSmartHandleNumber(int i);
	
	public void handleMove(int handlenum, Point p1, Point p2);
	public void handleRelease(int pressedHandle, Point point, Point point2);
	public void handlePress(int handlenum,  Point p2);
	
    public void handleMouseEvent(CanvasMouseEventWrapper me, int handlenum, int button, int clickcount, int type, int... other);


		
 
    
    
}

