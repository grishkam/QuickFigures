package genericMontageKit;

import java.util.ArrayList;

import channelMerging.MultiChannelImage;

/**This interface is for all classes that maintain sets of panels and arrange them into figure*/
public interface SubFigureOrganizer {
	
	public ArrayList<MultiChannelImage> getAllSourceImages();

	public void updatePanelsAndLabelsFromSource();
	
	/**These allow any code to stop or set off the process of automatically updating the figure from 
	 * changes to the source stacks. not implemented in all subclasses */
	public void release();
	public void supress();
	
	
	
}
