package multiChannelFigureUI;

public interface DisplayRangeChangeListener {

	/**What needs to be done after min max is set*/
	public void minMaxSet(int channel, double min, double max);
	
	/**may be called by others classes to update the image panels. if set to null, updates everything regardless of channel names*/
	public void updateAllDisplaysWithRealChannel(String chanName);
	//public void colorSet(int channel, Color color);
}
