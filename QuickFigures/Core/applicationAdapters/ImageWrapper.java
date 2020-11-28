package applicationAdapters;
import infoStorage.MetaInfoWrapper;
import utilityClassesForObjects.ObjectContainer;

import java.awt.Window;

import genericMontageKit.OverlayObjectManager;
import graphicalObjects.FigureDisplayContainer;

/**a general interface for images.
 The methods in this interface and superinterfaces must work in order for the basics of the layouts
 and layout editing to work*/
public interface ImageWrapper extends ObjectContainer,PixelContainer, FigureDisplayContainer, OpenFileReference{
	
	public void updateDisplay();
	public DisplayedImage getImageDisplay();
	
	/**returns and shows the window (if there is one)*/
	public Window window();
	public void show();
	
	/**A way to refer to the medadata of the image*/
	public MetaInfoWrapper getMetadataWrapper();
	
	
	public OverlayObjectManager getOverlaySelectionManagger();
	
	public boolean setPrimarySelectionObject(Object d);
	
	

}
