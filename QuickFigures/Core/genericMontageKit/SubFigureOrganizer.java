package genericMontageKit;

import java.util.ArrayList;

import channelMerging.MultiChannelWrapper;

/**This interphase is for all classes that take a set of stacks and arrange them into figure*/
public interface SubFigureOrganizer {
	
	public ArrayList<MultiChannelWrapper> getAllSourceStacks();
	
	/**returns a panel list containing all panels from all the source images*/
	public PanelList getWorkingPanelList();

	public void updatePanelsAndLabelsFromSource();
	
	/**These allow any code to stop or set off the process of automatically updating the figure from 
	 * changes to the source stacks. */
	public void release();
	public void supress();
	
	
	
}
