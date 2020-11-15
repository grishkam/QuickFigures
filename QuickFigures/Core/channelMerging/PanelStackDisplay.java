package channelMerging;

import java.awt.Rectangle;
import java.util.ArrayList;

import genericMontageKit.PanelList;
import genericMontageKit.PanelSetter;
import graphicalObjects_FigureSpecific.PanelGraphicInsetDef;
import graphicalObjects_FigureSpecific.PanelManager;

public interface PanelStackDisplay {
	
	/**Returns the source stack*/
	public String getTitle();
	public MultiChannelWrapper getMultichanalWrapper() ;
	public PanelManager getPanelManager();
	
	public PanelList getPanelList();
	public void setPanelList(PanelList stack);
	
	public void updatePanels();
	public void updatePanelsWithChannel(String realChannelName) ;
	public PanelSetter getSetter();

	public Rectangle getBoundOfUsedPanels();
	

	/**getter and setter methods for the crop, rotatoin and scaling*/
	public double getPreprocessScale();
	public MultiChannelWrapper setPreprocessScale(double s);
	PreProcessInformation getPreProcess();
	MultiChannelSlot getSlot();
	
	public ArrayList<PanelGraphicInsetDef> getInsets();

}
