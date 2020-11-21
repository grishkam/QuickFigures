package appContext;

import java.util.ArrayList;

import channelMerging.MultiChannelImage;
import multiChannelFigureUI.MultiChannelDisplayCreator;

/**Interface for whatever package is used to open and display multidimensional images
   A programmer may implements this interface and set the CurrentAppContext
   to an instance of his implementation. As of composing this, there is an ImageJ implementation*/
public interface MultiDimensionalImageContext {
  public MultiChannelDisplayCreator getMultichannelOpener();
  
  public MultiChannelDisplayCreator createMultichannelDisplay();
 
  public ArrayList< MultiChannelImage> getallVisibleMultichanal();
  
  public MultiChannelImage getCurrentMultichanal();

  public String getDefaultDirectory();
  
  



  
}
