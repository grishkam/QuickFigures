package genericMontageUIKit;

import java.awt.Color;
import java.util.ArrayList;

import channelMerging.MultiChannelImage;
import genericMontageKit.SubFigureOrganizer;
import logging.IssueLog;

/**an abstract class for dealing with channels.
  contains methods to reorder or recolor channels*/
public abstract class AbstractChannelHandler<ImageType> {
	
	public   String[] choicesforSwap= new String[] {"Swap Channels", "Swap Luts only", "Move Channels", "Move Channel Luts"};
	public final int SWAP_CHANNELS=0, SWAP_CHANNEL_COLORS=1, MOVE_CHANNELS=2, MOVE_CHANNEL_COLORS=3;

	public int option1=SWAP_CHANNELS;
	
	/**This method moved channel a into position b. all channels in between are shifted to the left or right depending on the type of move */
	public   void moveChannelsLuts(ImageType p, int a, int b) {
		if (a==b) return;
		if (a+1==b||b+1==a) {swapChannelLuts(p,  a, b); return;}
		if (a>b) {while (a>b) {swapChannelLuts(p,  a, a-1); a--;}}
		if (a<b) {while (a<b) {swapChannelLuts(p,  a, a+1); a++;}}
	}

	/**this method switches the channel colors of two channels */
	public abstract Boolean swapChannelLuts(ImageType p, int a, int b);
	
	/**this method swaps the order of two channels */
	public abstract void swapChannelsOfImage(ImageType p, int a, int b);

	/**this method sets the channel color of a channel */
	public abstract void setChannelColor(ImageType imp, Color lut, int chan);
	
	
	
	/**This method moves channel a into position b. all channels in between are shifted to the left or right depending on the type of move */
	public   void moveChannels(ImageType p, int a, int b) {
		if (a==b) return;
		if (a+1==b||b+1==a) {swapChannelsOfImage(p,  a, b); return;}
		if (a>b) {
			while (a>b) {swapChannelsOfImage(p,  a, a-1); a--;}
		}
		if (a<b) {
			while (a<b) {swapChannelsOfImage(p,  a, a+1); a++;}
		}
		
	}
	
	
	/**Performs the swap in each of the source stacks of the figure organizer given*/
	public void performUpdaterSwap(SubFigureOrganizer figure, int choice1, int choice2) {

		figure.supress();//do not want updates of figure panels from the source untill after all the edits of the source image are done
		try {
	
		ArrayList<MultiChannelImage> allImages=figure.getAllSourceImages();
		
		if (option1==SWAP_CHANNELS)			
			for(MultiChannelImage imp2: allImages) {if (imp2==null)continue; imp2.getChannelSwapper().swapChannelsOfImage(choice1, choice2); imp2.updateDisplay();};
		if (option1==SWAP_CHANNEL_COLORS)	
			for(MultiChannelImage imp2: allImages) {if (imp2==null)continue;imp2.getChannelSwapper().swapChannelLuts(choice1, choice2);imp2.updateDisplay();;} ;
		if (option1==MOVE_CHANNELS)			
			for(MultiChannelImage imp2: allImages) {if (imp2==null)continue;imp2.getChannelSwapper().moveChannelOfImage(choice1, choice2); imp2.updateDisplay();};
		if (option1==MOVE_CHANNEL_COLORS)	
			for(MultiChannelImage imp2: allImages) {if (imp2==null)continue;imp2.getChannelSwapper().moveChannelLutsOfImage(choice1, choice2);imp2.updateDisplay();};
		
		} catch (Exception e) {IssueLog.logT(e);}
		
		//not that edits are done, want to update the figure panels from the source images
		figure.release();		
		figure.updatePanelsAndLabelsFromSource();
		
	}
	



}
