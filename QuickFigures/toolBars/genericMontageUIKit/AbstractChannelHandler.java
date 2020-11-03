package genericMontageUIKit;

import java.awt.Color;
import java.util.ArrayList;

import channelMerging.MultiChannelWrapper;
import genericMontageKit.SubFigureOrganizer;
import logging.IssueLog;

/**an abstract class for dealing with channels*/
public abstract class AbstractChannelHandler<ImagePlus> {
	public int option1=0;
	
	/**This method moved channel a into position b. all channels in between are shifted to the left or right depending on the type of move */
	public   void moveChannelsLuts(ImagePlus p, int a, int b) {
		if (a==b) return;
		if (a+1==b||b+1==a) {swapChannelLuts(p,  a, b); return;}
		if (a>b) {while (a>b) {swapChannelLuts(p,  a, a-1); a--;}}
		if (a<b) {while (a<b) {swapChannelLuts(p,  a, a+1); a++;}}
	}

	public abstract Boolean swapChannelLuts(ImagePlus p, int a, int b);
	
	public   String[] choicesforSwap= new String[] {"Swap Channels", "Swap Luts only", "Move Channels", "Move Channel Luts"};
	public   final int swapChans=0;



	public final int swapLuts=1;



	public static final int moveChans=2, moveLuts=3;



	public abstract void setLutColor(ImagePlus imp, Color lut, int chan);

	public abstract void swapChannelsOfImage(ImagePlus p, int a, int b);
	
	/**This method moved channel a into position b. all channels in between are shifted to the left or right depending on the type of move */
	public   void moveChannels(ImagePlus p, int a, int b) {
		if (a==b) return;
		if (a+1==b||b+1==a) {swapChannelsOfImage(p,  a, b); return;}
		if (a>b) {
			while (a>b) {swapChannelsOfImage(p,  a, a-1); a--;}
		}
		if (a<b) {
			while (a<b) {swapChannelsOfImage(p,  a, a+1); a++;}
		}
		
	}
	

	
	

	
	public void setUpdaterChannelLut(SubFigureOrganizer imp, Color lut, int chan, boolean update) {	   
		if (lut==null || imp==null) return;
		for(MultiChannelWrapper imp2: imp.getAllSourceStacks()) {
			if (imp2==null) continue;	
			imp2.getChannelSwapper().setChannelColor(lut, chan);
			if (update) imp2.updateDisplay();
			
		}
	}

	public void updateAndDraw(ImagePlus imp2) {
		
	}
	
	/**Performs the swap in each of the source stacks of the montage*/
	public void performUpdaterSwap(SubFigureOrganizer abstractMontageUpdater, int choice1, int choice2) {
		 removeConflictingListeners();
		abstractMontageUpdater.supress();
		try {
	
		ArrayList<MultiChannelWrapper> impsit=abstractMontageUpdater.getAllSourceStacks();//.getAllSourceImages();
		
		//IJ.log("will perform operation on "+ impsit.size());
		if (option1==swapChans)	for(MultiChannelWrapper imp2: impsit) {if (imp2==null)continue; imp2.getChannelSwapper().swapChannelsOfImage(choice1, choice2); imp2.updateDisplay();};
		if (option1==swapLuts)	for(MultiChannelWrapper imp2: impsit) {if (imp2==null)continue;imp2.getChannelSwapper().swapChannelLuts(choice1, choice2);imp2.updateDisplay();;} ;
		if (option1==moveChans)	for(MultiChannelWrapper imp2: impsit) {if (imp2==null)continue;imp2.getChannelSwapper().moveChannelOfImage(choice1, choice2); imp2.updateDisplay();};
		if (option1==moveLuts)	for(MultiChannelWrapper imp2: impsit) {if (imp2==null)continue;imp2.getChannelSwapper().moveChannelLutsOfImage(choice1, choice2);imp2.updateDisplay();};
		//ImagePlus.addImageListener(up); up.updateMontageFromSource(up.reg.getMontage());
		//Montage_Updater.UpDatemode=true;
		} catch (Exception e) {IssueLog.log(e);}
		abstractMontageUpdater.release();		
		abstractMontageUpdater.updatePanelsAndLabelsFromSource();
		//AbstractMontageUpdater.UpDatemode=true;
	}
	
	/**
	public Color showLutSelectionDialog() {
		GenericDialog gd = new GenericDialog("Select Lut Color For Channel");
		gd.addChoice("Channel Colors Available ", IJdialogUse.colors, IJdialogUse.colors[0]);
		gd.showDialog();
		if (gd.wasOKed()) {
			return IJdialogUse.colorFromString( gd.getNextChoice());
		} 
		return null;
		
	}*/

	public void removeConflictingListeners() {	}

}
