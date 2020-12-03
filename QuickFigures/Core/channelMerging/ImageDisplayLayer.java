package channelMerging;

import java.awt.Rectangle;
import java.util.ArrayList;

import genericMontageKit.PanelList;
import genericMontageKit.PanelSetter;
import graphicalObjects_FigureSpecific.PanelGraphicInsetDefiner;
import graphicalObjects_FigureSpecific.PanelManager;

/**A specialized object that contains a source multi-dimensional image
 * and a list of panels that display various parts of that image*/
public interface ImageDisplayLayer {
	
	/**Returns the source stack*/
	public String getTitle();
	public MultiChannelImage getMultiChannelImage() ;
	
	/**getter method for the panel manager */
	public PanelManager getPanelManager();
	public PanelList getPanelList();
	public void setPanelList(PanelList stack);
	
	/**updates the images in each panel based on the source image, the display colors and display ranges*/
	public void updatePanels();
	public void updateOnlyPanelsWithChannel(String realChannelName) ;
	
	/**returns the panel setter object used by this display layer*/
	public PanelSetter getSetter();
	
	/**returns all the insets inside of this display layer*/
	public ArrayList<PanelGraphicInsetDefiner> getInsets();

	/**returns an area that contains every image panel in the figure within its bounds*/
	public Rectangle getBoundOfUsedPanels();
	

	/**getter and setter methods for the crop, rotation and scaling*/
	public double getPreprocessScale();
	public MultiChannelImage setPreprocessScale(double s);
	PreProcessInformation getPreProcess();
	
	/**returns the slot that stores the source image*/
	MultiChannelSlot getSlot();
	
	

}
