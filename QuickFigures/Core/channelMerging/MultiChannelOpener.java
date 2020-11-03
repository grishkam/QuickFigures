package channelMerging;

import applicationAdapters.ImageWrapper;

/**Opens multidimensional images. depending on the software,
  these may take a few forms, but the MultiChannelWrapper
  allows some classes to work easily with them all */
public interface MultiChannelOpener<ObjectType> {
	
	public ObjectType openFile(String path) ;
	public ObjectType getImageFromChoiceIndex(int nextChoiceIndex);
	public MultiChannelWrapper openMultiChannel(String path) ;
	public  MultiChannelWrapper wrap(ObjectType ob);
	
	
	public MultiChannelWrapper getActiveWrapper();
	public ObjectType getActiveImage();
	public String[] getImageChoices();
	
	
	public ImageWrapper makeImageWrapper(ObjectType ot);
	public ObjectType unwrap(ImageWrapper montage);

	
	
	
	
}
