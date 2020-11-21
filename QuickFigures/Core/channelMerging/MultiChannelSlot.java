package channelMerging;

import java.io.Serializable;

import applicationAdapters.HasScaleInfo;

public interface MultiChannelSlot extends Serializable, HasScaleInfo {

	static final int loadFromFile=1,  loadEmbeded=0;
	
	
	static final String[] retrivalOptions=new String[] {"load from embedded (if failed try File)", "load from file (if fail, try embedded)", "Look in working directory"};


	
			
	public void addMultichannelUpdateListener(MultiChannelUpdateListener lis);
	public void removeMultichannelUpdateListener(MultiChannelUpdateListener lis);
	
	public MultiChannelImage getMultichannelImage() ;
	public int getRetrieval() ;
	public void setRetrival(int i);
	public void saveImageEmbed();
	public void setImageDialog();
	public void showImage();
	public void hideImage();
	public void hideImageWihtoutMessage();
	public void kill();
	
	/**methods regarding cropping and scaling the of the imate*/
	public PreProcessInformation getModifications();
	//void showCropDialogOfSize(Dimension recommmendation);
	//void showCropDialog(Rectangle recommmendation, double recAngle);
	public void applyCropAndScale(PreProcessInformation process) ;
	public MultiChannelImage getUnprocessedVersion();
	public void redoCropAndScale();
	
	public void setPanelStackDisplay( ImageDisplayLayer multichannelDisplayLayer);
	public ImageDisplayLayer getDisplayLayer();
	public MultiChannelSlot copy();
	 void setDisplaySlice(CSFLocation display);
	CSFLocation getDisplaySlice();
	
	
}
