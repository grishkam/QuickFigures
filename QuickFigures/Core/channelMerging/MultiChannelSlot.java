package channelMerging;

import java.io.Serializable;

import applicationAdapters.HasScaleInfo;

/**classes that implement this store and retrieve a particular 
 multi-dimensional image. */
public interface MultiChannelSlot extends Serializable, HasScaleInfo {

	/**returns the multidimensional image being kept*/
	public MultiChannelImage getMultichannelImage() ;
	
	
	
	static final int lOAD_FROM_SAVED_FILE=1,  LOAD_EMBEDDED_IMAGE=0;
	
	
	static final String[] retrivalOptions=new String[] {"load from embedded (if failed try File)", "load from file (if fail, try embedded)", "Look in working directory"};

	public int getRetrieval() ;
	public void setRetrival(int i);

	
			
	public void addMultichannelUpdateListener(MultiChannelUpdateListener lis);
	public void removeMultichannelUpdateListener(MultiChannelUpdateListener lis);
	

	
	/**stores the serialized image in an internal field*/
	public void saveImageEmbed();
	
	/**shows a dialog that the user can use to switch to a new image. 
	  will be deprecated eventually*/
	public void setImageDialog();
	
	/**makes the image visible in a window*/
	public void showImage();
	/**hides the image. might ask user to save*/
	public void hideImage();
	/**hides the image. does not ask the user to save*/
	public void hideImageWihtoutMessage();
	public void kill();
	
	/**returns the cropping and scaling applied*/
	public PreProcessInformation getModifications();
	
	/**returns the original version of the multidimensional image (without crop nor rotation nor scale) */
	public MultiChannelImage getUnprocessedVersion();

	/**applies a new set of processes (roate_crop+scale) to the original version of the multidimensional image
	 * creating a new multidimensional image.
	 the working version is replaced by the new one while the unmodified original remains*/
	public void applyCropAndScale(PreProcessInformation process) ;
	/**re-applies the processes (roate_crop+scale) to the original version of the multidimensional image
	 * * creating a new multidimensional image.
	 the working version is replaced by the new one while the unmodified original remains*/
	public void redoCropAndScale();
	
	/**Sets which display layer this slot belong to */
	public void setStackDisplayLayer( ImageDisplayLayer multichannelDisplayLayer);
	/**return the display layer that this slot belong to */
	public ImageDisplayLayer getDisplayLayer();
	
	
	public MultiChannelSlot copy();
	
	
	 
	 /**returns which location in the stack is selected by the user*/
	CSFLocation getDisplaySlice();
	 /**sets which location in the stack is selected by the user*/
	 void setDisplaySlice(CSFLocation display);
	
	
}
