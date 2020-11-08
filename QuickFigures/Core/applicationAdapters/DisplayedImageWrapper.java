package applicationAdapters;

import java.awt.Cursor;
import java.awt.Window;

import graphicalObjects.CordinateConverter;
import undo.UndoManagerPlus;
import utilityClassesForObjects.Selectable;

/**
  Created this interface. At the time I made this, the interface ImageWrapper
  was mostly for the data model of the image and not the window
  the user interface window and the data model for the image tend to be kept 
  separate in code. this interface is for objects that allow access to both.
  
  */
public interface DisplayedImageWrapper {

	public void updateDisplay() ;
	public ImageWrapper getImageAsWrapper() ;
	public CordinateConverter<?> getConverter();
	public Window getWindow();
	
	
	/**Resizes the window to fit its contents*/
	public void updateWindowSize();
	public UndoManagerPlus getUndoManager();
	
	public void setCursor(Cursor c);
	public void zoomOutToFitScreen();
	public void zoom(String st);
	
	public double getZoomLevel();
	public void setZoomLevel(double z);
	
	public void setEndFrame(int frame);
	public int getEndFrame();
	
	public int getCurrentFrame() ;
	public void setCurrentFrame(int currentFrame);
	
	public void scrollPane(double d, double e);
	void setScrollCenter(double dx, double dy);
	public void closeWindowButKeepObjects();
	
	public Selectable getSelectedItem() ;
	public void setSelectedItem(Selectable s) ;
	
	
}
