package applicationAdapters;
import infoStorage.MetaInfoWrapper;
import utilityClassesForObjects.ObjectContainer;

import java.awt.Window;

import genericMontageKit.SelectionManager;
import graphicalObjects.GraphicSetDisplayContainer;

/**a general interface for images.
  if the methods in this interface and superinterfaces work, the basics of the montage should work*/
public interface ImageWrapper extends ObjectContainer,PixelContainer, GraphicSetDisplayContainer, OpenFileReference{
	
	public void updateDisplay();
	public DisplayedImage getImageDisplay();
	
	public Window window();
	
	public void show();
	

	public MetaInfoWrapper getMetadataWrapper();
	
	public SelectionManager getSelectionManagger();
	

}
