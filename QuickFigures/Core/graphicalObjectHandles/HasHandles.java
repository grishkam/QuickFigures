/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package graphicalObjectHandles;

import java.awt.Point;

import applicationAdapters.CanvasMouseEvent;


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
	
    public void handleMouseEvent(CanvasMouseEvent me, int handlenum, int button, int clickcount, int type, int... other);


		
 
    
    
}

