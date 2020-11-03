package applicationAdapters;
import infoStorage.MetaInfoWrapper;
import utilityClassesForObjects.ObjectContainer;

import java.awt.Window;

import genericMontageKit.SelectionManager;
import graphicalObjects.GraphicSetDisplayContainer;

/**a generan interface for images that have motnage layouts and can be modified with Montage Editor
  if the methods in this interface and superinterfaces work, the basics of the montage should work*/
public interface ImageWrapper extends ObjectContainer,PixelContainer, GraphicSetDisplayContainer, ImageFileWrapper{
	
	public void updateDisplay();
	public DisplayedImageWrapper getImageDisplay();
	
	public Window window();
	
	public void show();
	

//	public BasicMontageLayout createLayout() ;
	//public void saveLayout( BasicMontageLayout layout);
	
	public MetaInfoWrapper getMetadataWrapper();
	
	public SelectionManager getSelectionManagger();
	
	public ObjectCreator getDefaultObjectCreator();
	//public Dimension getDimensionsXY();

}
