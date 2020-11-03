package channelMerging;

import java.awt.Rectangle;
import java.util.ArrayList;

import genericMontageKit.PanelList;
import genericMontageKit.PanelSetter;
import graphicalObjects_FigureSpecific.PanelGraphicInsetDef;
import graphicalObjects_FigureSpecific.PanelManager;
import undo.AbstractUndoableEdit2;
import undo.CompoundEdit2;
import undo.UndoAddManyItem;

public interface PanelStackDisplay {
	
	/**Returns the source stack*/
	public MultiChannelWrapper getMultichanalWrapper() ;
	public PanelManager getPanelManager();
	
	public PanelList getStack();
	public void setStack(PanelList stack);
	
	public void updatePanels();
	public void updatePanelsWithChannel(String realChannelName) ;
	public PanelSetter getSetter();
	
	/**
	public void eliminatePanels();
	public void generatePanelGraphics();
	public void setDefaultPanelLevelScale(double number);
	public double getDefaultPanelLevelScale();*/
	
	/**Channel label options
	 * @return */
	public AbstractUndoableEdit2 eliminateChanLabels();
	public AbstractUndoableEdit2 generateChannelLabels();
	
	
	public void eliminateAndRecreate();
	
	
	public Rectangle getBoundOfUsedPanels();
	

	public String getTitle();
	public double getPreprocessScale();
	public MultiChannelWrapper setPreprocessScale(double s);
	PreProcessInformation getPreProcess();
	MultiChannelSlot getSlot();
	
	public ArrayList<PanelGraphicInsetDef> getInsets();

}
