package genericMontageUIKit;

import java.awt.Color;
import java.awt.Shape;

import applicationAdapters.CanvasMouseEventWrapper;
import applicationAdapters.DisplayedImageWrapper;
import applicationAdapters.ImageWrapper;
import gridLayout.BasicMontageLayout;
import gridLayout.MontageEditorDialogs;

public interface ToolCore {

	//public abstract GenericMontageEditor getEditor();
	public ImageWrapper currentlyInFocusWindowImage();
	
	//public abstract ImagePlus getImageClick();

	//public abstract void setImageClick(ImagePlus imageClick);
	
	public abstract DisplayedImageWrapper getClickedImage();
	public abstract void setClickedImage(DisplayedImageWrapper d);
	
	//public AbstractMontageLayout<ImagePlus> getMainLayout();
	//public AbstractMontageLayout<ImagePlus> getLayoutForCurrentImage();
	public abstract ImageWrapper getImageWrapperClick();

	public abstract void setImageWrapperClick(ImageWrapper imageWrapperClick);
	
	
	/** Returns the difference in cordinates between the last mouse press
	  and the last drag (or release). Note, these are coordinates are
	  the Figure cordinates and NOT the JComponent coordinates of the MouseEvents.
	  
	 */
	public abstract int getXDisplaceMent();
	public abstract int getYDisplaceMent();


	public abstract MontageEditorDialogs getMontageEditorDialogs();

	public abstract void setMarkerRoi(int type);

	public abstract void setMarkerRoi(int index, int type);

	public abstract void setMarkerRoi(Shape s);

	public abstract void createIconSet(String... sts);

	//public abstract void updateImageDisplay(ImagePlus image);



	public abstract int getClickedCordinateX();

	//public abstract void setClickedCordinateX(int clickedCordinateX);

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
	
	public abstract int getSliceRelease() ;

	public abstract void setSliceDrag(int sliceDrag);

//	public abstract Image getCursorIcon();

//	public abstract void setCursorIcon(Image cursorIcon);

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


	public int getMouseButtonClick();


	public abstract boolean shiftDown();

	public abstract boolean altKeyDown();

	public abstract void setClickPointToDragReleasePoint();

	public abstract int clickCount();

	public abstract BasicMontageLayout getClickedLayout();

	//public abstract java.awt.event.MouseEvent getLastMouseEvent();

	public abstract Color getForeGroundColor();

	
	public abstract int getReleaseCordinateY();
	public abstract int getReleaseCordinateX();
	
	
	public void setRowColDragForLayout(BasicMontageLayout lay);
	public void setRowColClickForLayout(BasicMontageLayout lay);

	public CanvasMouseEventWrapper getLastDragMouseEvent();

	public CanvasMouseEventWrapper getLastMouseEvent();

	
	
	
	

}