/**
 * Author: Greg Mazo
 * Date Created: April 25, 2021
 * Date Modified: April 25, 2021
 * Version: 2021.1
 */
package figureOrganizer;

import java.util.ArrayList;

import channelLabels.ChannelLabelManager;
import channelMerging.ChannelUseInstructions;
import channelMerging.ImageDisplayLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import layout.basicFigure.BasicLayout;

/**
 Since the inset panels and the main panels of a figure are managed somewhat 
 differently, they will use different implementations of this interface.
 
 @see PanelOrderCorrector
 @see ChannelPanelRemover 
 */
public interface CollectivePanelManagement {
	
	
	/**
	 * @return
	 */
	BasicLayout getUsedLayout() ;
	


	/**
	 * @return
	 */
	public ArrayList<ImageDisplayLayer> getDisplaysInOrder() ;
	
	/**
	 * @return
	 */
	ImageDisplayLayer getMultichannel() ;
	
	/**returns the layer that contains each panel*/
	public GraphicLayerPane getTargetLayer() ;
	
	/**
	 * @return
	 */
	ArrayList<ChannelUseInstructions> getChannelUserInformation() ;

	/**
	 sets the relative scale of the panel manager to match the scale of the panels
	 */
	public void updatePanelLevelScale() ;
	
	/**returns a list of all panel managers for the main panels in the figure*/
	public ArrayList< PanelManager> getPanelManagers() ;
	
	
	/**returns a list of panel managers in the same order as the figure layout*/
	public ArrayList<PanelManager> getPanelManagersInLayoutOrder();
	
	public ChannelLabelManager getChannelLabelManager() ;
	
	
	/**
	 returns the layout that contains the targetted panels
	 */
	public  DefaultLayoutGraphic getTargetLayout() ;
	
}
