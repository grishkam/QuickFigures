package appContext;

import java.util.ArrayList;

import channelMerging.MultiChannelWrapper;
import multiChannelFigureUI.MultiChannelDisplayCreator;

public interface MultichannelContext {
  public MultiChannelDisplayCreator getMultichannelOpener();
  
  public MultiChannelDisplayCreator createMultichannelDisplay();
 
  public ArrayList< MultiChannelWrapper> getallVisibleMultichanal();
  
  public MultiChannelWrapper getCurrentMultichanal();

public String getDefaultDirectory();
  
  



  
}
