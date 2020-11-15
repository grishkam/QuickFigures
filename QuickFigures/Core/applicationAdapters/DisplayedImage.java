package applicationAdapters;

import java.awt.Cursor;
import java.awt.Window;

import graphicalObjects.CordinateConverter;
import undo.UndoManagerPlus;
import utilityClassesForObjects.Selectable;

/**
 Interface with methods to return information related to the
 display window for figures , the layer set inside and what selections are made
 Also used for windows that display multichannel images
  */
public interface DisplayedImage {

	/**repaint the component used to display the item*/
	public void updateDisplay() ;
	
	/**returns the ImageWrapper*/
	public ImageWrapper getImageAsWrapper() ;
	public CordinateConverter<?> getConverter();
	/**returns the window used to display the image*/
	public Window getWindow();
	
	
	/**Resizes the window to fit its contents*/
	public void updateWindowSize();
	public UndoManagerPlus getUndoManager();
	
	/**Sets what cursor is drawn over the window*/
	public void setCursor(Cursor c);
	
	/**Methods to control the soom*/
	public void zoomOutToFitScreen();
	public void zoom(String st);
	public double getZoomLevel();
	public void setZoomLevel(double z);
	
	/**methods to control the scrolling if some method other than a JScrollPane is used*/
	public void scrollPane(double d, double e);
	void setScrollCenter(double dx, double dy);
	
	/**Methods to control which frame of an annimation is shown*/
	public void setEndFrame(int frame);
	public int getEndFrame();
	public int getCurrentFrame() ;
	public void setCurrentFrame(int currentFrame);
	
	/**closes the window*/
	public void closeWindowButKeepObjects();
	
	/**Sets the selected item*/
	public Selectable getSelectedItem() ;
	public void setSelectedItem(Selectable s) ;
	
	
}
