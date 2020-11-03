package multiChannelFigureUI;

import java.awt.Image;

import channelMerging.MultiChannelWrapper;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;

public interface MultiChannelDisplayCreator {
	static final String useActiveImage="ActiveImage";
	
	/**Creates a multiChannel Display for the user selected open image or file. if OpenFile is false, this will 
	  either open a dialog for the user to select an image or use the path string to find an open image*/
	public MultichannelDisplayLayer creatMultiChannelDisplayFromUserSelectedImage(boolean openFile, String path) ;
	public MultichannelDisplayLayer creatMultiChannelDisplayFromOpenImage() ;
	
	public MultiChannelWrapper creatMultiChannelFromImage(Image img) ;
	
	public String imageTypeName();
	
	public MultiChannelWrapper createFromImageSequence(String path, int[] dims);
	
}
