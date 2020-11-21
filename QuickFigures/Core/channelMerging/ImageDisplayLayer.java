package channelMerging;

import java.awt.Rectangle;
import java.util.ArrayList;

import genericMontageKit.PanelList;
import genericMontageKit.PanelSetter;
import graphicalObjects_FigureSpecific.PanelGraphicInsetDefiner;
import graphicalObjects_FigureSpecific.PanelManager;

public interface ImageDisplayLayer {
	
	/**Returns the source stack*/
	public String getTitle();
	public MultiChannelImage getMultiChannelImage() ;
	public PanelManager getPanelManager();
	
	public PanelList getPanelList();
	public void setPanelList(PanelList stack);
	
	public void updatePanels();
	public void updatePanelsWithChannel(String realChannelName) ;
	public PanelSetter getSetter();

	public Rectangle getBoundOfUsedPanels();
	

	/**getter and setter methods for the crop, rotatoin and scaling*/
	public double getPreprocessScale();
	public MultiChannelImage setPreprocessScale(double s);
	PreProcessInformation getPreProcess();
	MultiChannelSlot getSlot();
	
	public ArrayList<PanelGraphicInsetDefiner> getInsets();

}
