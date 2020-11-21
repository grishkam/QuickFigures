package applicationAdapters;
import infoStorage.MetaInfoWrapper;
import utilityClassesForObjects.ObjectContainer;

import java.awt.Window;

import genericMontageKit.OverlayObjectManager;
import graphicalObjects.FigureDisplayContainer;

/**a general interface for images.
  if the methods in this interface and superinterfaces work, the basics of the montage should work*/
public interface ImageWrapper extends ObjectContainer,PixelContainer, FigureDisplayContainer, OpenFileReference{
	
	public void updateDisplay();
	public DisplayedImage getImageDisplay();
	
	public Window window();
	
	public void show();
	

	public MetaInfoWrapper getMetadataWrapper();
	
	public OverlayObjectManager getSelectionManagger();
	
	public boolean setPrimarySelectionObject(Object d);
	
	

}
