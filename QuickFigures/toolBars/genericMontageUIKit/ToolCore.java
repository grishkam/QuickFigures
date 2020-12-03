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
package genericMontageUIKit;

import java.awt.Color;
import java.awt.Shape;

import applicationAdapters.CanvasMouseEvent;
import applicationAdapters.DisplayedImage;
import applicationAdapters.ImageWrapper;
import gridLayout.BasicMontageLayout;

/**Objects that implements this interface 
  store information relating to the tools in the toolbar
  Interface contains methods relating the the tools
  TODO: */
public interface ToolCore {

	
	public ImageWrapper currentlyInFocusWindowImage();
	
	/**Sets and returns the image being worked on by the tool*/
	public abstract DisplayedImage getClickedImage();
	public abstract void setClickedImage(DisplayedImage d);
	
	public abstract ImageWrapper getImageWrapperClick();

	public abstract void setImageWrapperClick(ImageWrapper imageWrapperClick);
	
	
	public abstract boolean shiftDown();
	public abstract boolean altKeyDown();
	
	/** Returns the difference in cordinates between the last mouse press
	  and the last drag (or release). Note, these are coordinates are
	  the Figure cordinates and NOT the JComponent coordinates of the MouseEvents.
	  
	 */
	public abstract int getXDisplaceMent();
	public abstract int getYDisplaceMent();



	public abstract void setMarkerRoi(int type);

	public abstract void setMarkerRoi(int index, int type);

	public abstract void setMarkerRoi(Shape s);

	public abstract void createIconSet(String... sts);

	public abstract int getClickedCordinateX();


	public abstract int getClickedCordinateY();

	public abstract void setClickedCordinateY(int clickedCordinateY);

	public abstract int getDragCordinateX();

	public abstract void setDragCordinateX(int dragCordinateX);

	public abstract int getDragCordinateY();

	public abstract void setDragCordinateY(int dragCordinateY);

	public abstract int getMouseDisplacementX();

	public abstract void setMouseDisplacementX(int mouseDisplacementX);

	public abstract int getMouseDisplacementY();

	public abstract void setMouseDisplacementY(int mouseDisplacementY);

	public abstract int getMouseXClick();

	public abstract void setMouseXClick(int mouseXClick);

	public abstract int getMouseYClick();

	public abstract void setMouseYClick(int mouseYClick);

	public abstract int getMouseXdrag();

	public abstract void setMouseXdrag(int mouseXdrag);

	public abstract int getMouseYdrag();

	public abstract void setMouseYdrag(int mouseYdrag);
	
	public abstract int getMouseXrelease();
	public abstract int getMouseYrelease();

	//Stores the index of the channel, frame of slice being dragged
	public abstract int getChannelClick();
	public abstract void setChannelClick(int channelClick);
	public abstract int getChannelDrag();
	public abstract void setChannelDrag(int channelDrag);
	public abstract int getFrameClick();
	public abstract void setFrameClick(int frameClick);
	public abstract int getFrameDrag();
	public abstract void setFrameDrag(int frameDrag);
	public abstract int getSliceClick();
	public abstract void setSliceClick(int sliceClick);
	public abstract int getSliceDrag();
	public abstract void setSliceDrag(int sliceDrag);

	
	public abstract int getPanelIndexClick();
	public abstract void setPanelIndexClick(int panelIndexClick);
	public abstract int getPanelIndexDrag();
	public abstract void setPanelIndexDrag(int panelIndexDrag);
	public abstract int getColIndexClick();
	public abstract void setColIndexClick(int colIndexClick);
	public abstract int getColIndexDrag();
	public abstract void setColIndexDrag(int colIndexDrag);
	public abstract int getRowIndexClick();
	public abstract void setRowIndexClick(int rowIndexClick);
	public abstract int getRowIndexDrag();
	public abstract void setRowIndexDrag(int rowIndexDrag);


	/**resets the tool to operate as if the original click point was in fact the current drag point
	 Needed for the function of certain tools*/
	public abstract void setClickPointToDragReleasePoint();

	public int getMouseButtonClick();
	public abstract int clickCount();

	
	public abstract Color getForeGroundColor();

	
	public abstract int getReleaseCordinateY();
	public abstract int getReleaseCordinateX();
	
	
	public void setRowColDragForLayout(BasicMontageLayout lay);
	public void setRowColClickForLayout(BasicMontageLayout lay);

	public CanvasMouseEvent getLastDragMouseEvent();
	public CanvasMouseEvent getLastMouseEvent();



	
	
	
	

}